/**
 * Created by danny on 17-1-5.
 * <p>
 * 文件上传管理
 */

package com.rytec.rec.web.util;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;
import com.libiec61850.tools.DynamicModelGenerator;
import com.rytec.rec.app.RecBase;
import com.rytec.rec.db.mapper.GisLayerMapper;
import com.rytec.rec.db.mapper.PanoramaMapper;
import com.rytec.rec.db.model.GisLayer;
import com.rytec.rec.db.model.Panorama;
import com.rytec.rec.service.IEC61850Service;
import com.rytec.rec.util.RyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
public class UploadService extends RecBase {

    @Autowired
    GisLayerMapper gisLayerMapper;

    @Autowired
    PanoramaMapper panoramaMapper;

    @Autowired
    IEC61850Service iec61850Service;


    /**
     * GIS 图层上传
     *
     * @param layerName
     * @param upFile
     * @param layer
     * @param replace
     * @return
     * @throws IOException
     */
    @ExtDirectMethod(ExtDirectMethodType.FORM_POST)
    public ExtDirectFormPostResult gisLayer(
            @RequestParam("layerName") String layerName,
            @RequestParam("fileUpload") MultipartFile upFile,
            @RequestParam("layer") Integer layer,
            @RequestParam("replace") Boolean replace) throws IOException {


        //文件上传的路径
        String layerPath = System.getProperty("web.root") + "/upload/gis/layer/";

        ExtDirectFormPostResult resp = new ExtDirectFormPostResult(true);

        if (upFile == null || upFile.isEmpty()) {
            return resp;
        }

        GisLayer gisLayer;
        if (replace) {
            // 更新
            gisLayer = gisLayerMapper.selectByPrimaryKey(layer);
            // 得到以前的文件
            if (gisLayer != null) {
                String orgFile = layerPath + gisLayer.getFile();
                RyFile.deleteFile(orgFile);
            } else {
                return resp;
            }

        } else {
            // 新建

            //首先写入数据库
            gisLayer = new GisLayer();
            gisLayer.setName(layerName);
            gisLayer.setZoom(0);
            gisLayerMapper.insert(gisLayer);

        }


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
        RyFile.makeThumb(outFullFileName, extName);

        return resp;
    }


    /**
     * 全景场景上传
     *
     * @param name
     * @param upFile
     * @param scene
     * @param replace
     * @return
     * @throws IOException
     */
    @ExtDirectMethod(ExtDirectMethodType.FORM_POST)
    public ExtDirectFormPostResult panoramaScene(
            @RequestParam("sceneName") String name,
            @RequestParam("fileUpload") MultipartFile upFile,
            @RequestParam("scene") Integer scene,
            @RequestParam("device") Integer device,
            @RequestParam("replace") Boolean replace) throws IOException {

        ExtDirectFormPostResult resp = new ExtDirectFormPostResult(true);

        //文件上传的路径
        String layerPath = System.getProperty("web.root") + "/upload/panorama/";


        if (upFile == null || upFile.isEmpty()) {
            return resp;
        }

        Panorama panorama;

        if (replace) {
            // 更新
            panorama = panoramaMapper.selectByPrimaryKey(scene);
            // 得到以前的文件
            if (panorama != null) {
                String orgFile = layerPath + panorama.getFile();
                RyFile.deleteFile(orgFile);
            } else {
                return resp;
            }

        } else {
            // 新建
            //首先写入数据库
            panorama = new Panorama();
            panorama.setName(name);
            panorama.setDevice(device);
            panoramaMapper.insert(panorama);
        }


        //得到文件名和扩展名
        String srcFileName = upFile.getOriginalFilename();
        String extName = srcFileName.substring(srcFileName.lastIndexOf(".") + 1);
        String outFullFileName = layerPath + panorama.getId() + '.' + extName;

        //保存文件
        File convFile = new File(outFullFileName);
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(upFile.getBytes());
        fos.close();

        //更新文件名
        panorama.setFile("" + panorama.getId() + '.' + extName);
        panoramaMapper.updateByPrimaryKey(panorama);

        return resp;
    }

    /**
     * 61850 icd 文件上传，并生成配置
     * 1、保存61850模型文件
     * 2、生成配置文件
     *
     * @param icdFile
     * @return
     */
    @ExtDirectMethod(ExtDirectMethodType.FORM_POST)
    public ExtDirectFormPostResult iec61850icd(@RequestParam("icdFile") MultipartFile icdFile) {
        String filePath = System.getProperty("web.root") + "/upload/61850.icd";

        if (icdFile == null || icdFile.isEmpty()) {
            return new ExtDirectFormPostResult(false);
        }

        //保存文件
        File convFile = new File(filePath);
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(icdFile.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            PrintStream outputStream = new PrintStream(new FileOutputStream(new File(iec61850Service.iecCfgFile)));
            new DynamicModelGenerator(icdFile.getInputStream(), null, outputStream, null, null);
            return new ExtDirectFormPostResult(true);
        } catch (Exception e) {
            // 错误处理
            return new ExtDirectFormPostResult(false);
        }


    }

}
