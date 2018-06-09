package com.rytec.rec.service.iec60870;

import com.rytec.rec.app.ManageableInterface;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.device.AbstractOperator;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.device.DeviceRuntimeBean;
import com.rytec.rec.util.ConstantFromWhere;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.Server;
import org.openmuc.j60870.ServerEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 60870 的服务器构建，并管理对应的连接
 */
@Service
@Order(400)
public class Iec60870Server extends RecBase implements ServerEventListener, ManageableInterface {

    private int connectionIdCounter = 1;

    //端口配置
    @Value("${iec60870.port}")
    private int port;

    @Value("${iec60870.xml}")
    public String xmlFileName;

    @Value("${iec60870.addr}")
    public int Iec60870Addr;            // 60870 地址

    @Autowired
    AddrConvert addrConvert;

    @Autowired
    public FileManager fileManager;

    @Autowired
    public DeviceManager deviceManager;

    Server.Builder builder;
    Server server = null;

    private Iec60870Listener crtListener = null;       // 当前的监听对象

    public long timerOffset;                    // 时间差


    /**
     * 客户端断开
     *
     * @param client
     */
    public void clientClosed(Iec60870Listener client) {
        if (client == crtListener) {
            crtListener = null;
            debug("当前活动链接关闭");
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
            Iec60870Listener iec60870Listener = new Iec60870Listener(connection, myConnectionId);
            // 为非托管类传递一些参数
            iec60870Listener.setServer(this);
            crtListener = iec60870Listener;
            connection.waitForStartDT(iec60870Listener, 5000);
        } catch (IOException e) {
            debug("连接： (" + myConnectionId + ") 中断 StartDT: " + e.getMessage());
            return;
        } catch (TimeoutException e) {

        }

    }

    @Override
    public void serverStoppedListeningIndication(IOException e) {
        debug("服务器停止 : \"" + e.getMessage() + "\"");
    }

    @Override
    public void connectionAttemptFailed(IOException e) {
        debug("连接失败: " + e.getMessage());
    }

    @Override
    public void start() {
        builder = new Server.Builder();
        builder.setPort(port);
        // TODO 可以设置不同的超时

        server = builder.build();

        try {
            server.start(this);
        } catch (IOException e) {
            debug("Unable to start listening: \"" + e.getMessage() + "\". Will quit.");
            return;
        }
    }

    @PreDestroy
    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
        server = null;
    }

    /**
     * 更新设备状态
     *
     * @param deviceRuntimeBean
     */
    public void update(DeviceRuntimeBean deviceRuntimeBean) {

        if (crtListener != null) {
            crtListener.updateDevice(deviceRuntimeBean);
        }

    }

    /**
     * 控制开关
     *
     * @param device
     * @param ste
     */
    public void devCtl(int device, boolean ste) {
        AbstractOperator deviceOperator = deviceManager.getOperatorByDeviceId(device);
        if (deviceOperator != null) {
            if (ste) {
                deviceOperator.operate(ConstantFromWhere.FROM_USER, device, 101, null);
            } else {
                deviceOperator.operate(ConstantFromWhere.FROM_USER, device, 100, null);
            }

        }
    }

}
