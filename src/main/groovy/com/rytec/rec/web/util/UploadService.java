/**
 * Created by danny on 17-1-5.
 * <p>
 * 文件上传管理
 */

package com.rytec.rec.web.util;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;
import com.rytec.rec.db.mapper.GisLayerMapper;
import com.rytec.rec.db.model.GisLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
public class UploadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static private int thumbWidth = 128;

    @Autowired
    GisLayerMapper gisLayerMapper;

    @ExtDirectMethod(ExtDirectMethodType.FORM_POST)
    public ExtDirectFormPostResult gisLayer(@RequestParam("layerName") String layerName, @RequestParam("fileUpload") MultipartFile upFile) throws IOException {

        //文件上传的路径
        String layerPath = System.getProperty("web.root") + "/upload/gis/layer/";

        ExtDirectFormPostResult resp = new ExtDirectFormPostResult(true);

        if (upFile != null && !upFile.isEmpty()) {
            //首先写入数据库
            GisLayer gisLayer = new GisLayer();
            gisLayer.setName(layerName);
            gisLayerMapper.insert(gisLayer);
            //得到文件名和扩展名
            String srcFileName = upFile.getOriginalFilename();
            String extName = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
            String outFullFileName = layerPath + gisLayer.getId() + '.' + extName;
            //保存文件
            File convFile = new File(outFullFileName);
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(upFile.getBytes());
            fos.close();
            //更新文件名
            gisLayer.setFile("" + gisLayer.getId() + '.' + extName);
            gisLayerMapper.updateByPrimaryKey(gisLayer);
            //生成略缩图
            makeThumb(outFullFileName, extName);
        }
        return resp;
    }

    private void makeThumb(String infile, String extName) {
        try {

            // 大图文件
            File fiBig = new File(infile);
            // 将要转换出的小图文件
            File foSmall = new File(fiBig.getParent() + "/_" + fiBig.getName());

            AffineTransform transform = new AffineTransform();

            //读取图片
            BufferedImage bis = ImageIO.read(fiBig);
            //获得图片原来的高宽
            int w = bis.getWidth();
            int h = bis.getHeight();

            double scale = (double) w / h;

            int newWidth = thumbWidth;
            int newHeight = (newWidth * h) / w;

            if (newHeight > thumbWidth) {
                newHeight = thumbWidth;
                newWidth = (newHeight * w) / h;
            }

            double sx = (double) newWidth / w;
            double sy = (double) newHeight / h;

            transform.setToScale(sx, sy);

            AffineTransformOp ato = new AffineTransformOp(transform, null);
            BufferedImage bid = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_3BYTE_BGR);
            ato.filter(bis, bid);

            ImageIO.write(bid, extName, foSmall);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
