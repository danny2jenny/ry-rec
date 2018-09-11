package com.rytec.rec.service.heshen;

import com.rytec.rec.service.iec60870.FileManager;
import com.rytec.rec.service.iec60870.Iec60870Listener;
import com.rytec.rec.service.iec60870.Iec60870Server;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.openmuc.j60870.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;

@Service
@Order(400)
public class HeShen80870Service extends Iec60870Server {

    @Value("${fun.hs.104}")
    boolean enable;

    @Autowired
    FileManager fileManager;

    FTPClient ftpClient = null;

    @Override
    public void start() {
        if (!enable) {
            return;
        }
        // 初始化配置
        Iec60870Addr = Integer.parseInt(dbConfig.getCfg("iec60870.addr"));

        builder = new Server.Builder();
        builder.setPort(Integer.parseInt(dbConfig.getCfg("heshen104.port")));
        // TODO 可以设置不同的超时

        server = builder.build();

        try {
            server.start(this);
        } catch (IOException e) {
            debug("Unable to start listening: \"" + e.getMessage() + "\". Will quit.");
            return;
        }
    }

    /**
     * 每一个连接生成一个连接对象
     *
     * @param connection
     */
    @Override
    public void connectionIndication(Connection connection) {
        int myConnectionId = connectionIdCounter++;
        debug("客户端连接: " + myConnectionId);

        try {
            Iec60870Listener iec60870Listener = new HeShenListener(connection, myConnectionId);
            // 为非托管类传递一些参数
            iec60870Listener.setServer(this);
            crtListener = iec60870Listener;
            connection.waitForStartDT(iec60870Listener, 5000);
            ASdu aSdu = new ASdu(
                    TypeId.M_EI_NA_1,
                    1,
                    CauseOfTransmission.INITIALIZED,
                    false, false,
                    0,
                    Iec60870Addr,
                    new InformationObject[]{
                            new InformationObject(
                                    0,
                                    new InformationElement[][]{
                                            {
                                                    new IeChecksum(0)
                                            }
                                    })
                    }
            );
            connection.send(aSdu);
        } catch (IOException e) {
            debug("连接： (" + myConnectionId + ") 中断 StartDT: " + e.getMessage());
            return;
        } catch (TimeoutException e) {

        }
    }


    /**
     * 检查FTP连接
     */
    @Scheduled(fixedDelay = 3000)
    void checkFtp() {

        if (!enable) {
            return;
        }

        if (ftpClient == null) {
            ftpClient = new FTPClient();
            ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        }

        if (ftpClient.isConnected()) {
            return;
        }

        try {
            ftpClient.connect(dbConfig.getCfg("heshen104.ftp.host"), Integer.parseInt(dbConfig.getCfg("heshen104.ftp.port")));
            ftpClient.enterLocalPassiveMode();
            ftpClient.login("anonymous", "");

            int stationId = Integer.parseInt(dbConfig.getCfg("iec60870.addr"));
            String stationHex = Integer.toHexString(stationId);
            int def = 4 - stationHex.length();
            for (int i = 0; i < def; i++) {
                stationHex = "0" + stationHex;
            }

            FTPFile[] files = ftpClient.listFiles("/");
            boolean hasDir = false;
            for (FTPFile file : files) {
                if (stationHex.equals(file.getName())) {
                    hasDir = true;
                }
            }
            // 判断是否要建立目录
            if (!hasDir) {
                ftpClient.makeDirectory(stationHex);
            }
        } catch (IOException e) {
            return;
        }


    }

    /**
     * ftp 上传
     *
     * @param path
     * @param fileName
     * @param f
     */
    private void uploadFile(String path, String fileName, String f) {
        if (ftpClient.isConnected()) {
            InputStream inStream = new ByteArrayInputStream(f.getBytes());
            try {
                ftpClient.storeFile(path + '/' + fileName, inStream);
                debug("上传成功");
            } catch (IOException e) {
                debug("上传失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传配置文件
     */
    public void uploadCfg() {
        String s = fileManager.genJson();
        debug(s);
        uploadFile("ffff", "cfg.json", s);
    }

}
