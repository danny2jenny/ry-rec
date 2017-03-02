package com.rytec.rec.web.gis;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.db.mapper.DeviceGisMapper;
import com.rytec.rec.db.model.DeviceGis;
import com.rytec.rec.db.model.DeviceGisExample;
import com.rytec.rec.device.DeviceManager;
import com.rytec.rec.device.DeviceStateBean;
import org.geojson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danny on 17-1-25.
 * <p>
 * GIS View 的管理
 */
@Controller
public class GisDevice {

    @Autowired
    DeviceGisMapper deviceGisMapper;

    @Autowired
    DeviceManager deviceManager;

    /**
     * @param layer // 层ID
     * @return // FeatureCollection 的json字符串
     * <p>
     * feature 中包含的属性：
     * 1、icon
     * 2、deviceId
     * 3、layer
     */
    @RequestMapping("/gis/features")
    @ResponseBody
    public String getFeaturesByLayer(@RequestParam int layer) {

        // 返回的FeatureCollection
        FeatureCollection featureCollection = new FeatureCollection();
        Feature feature;

        // 数据库操作
        List<DeviceGis> giss;
        DeviceGisExample deviceGisExample = new DeviceGisExample();

        // json转换
        GeoJsonObject geoJsonObject;
        ObjectMapper objectMapper = new ObjectMapper();


        // 查询数据
        if (layer > 0) {
            // 查询指定层的数据
            deviceGisExample.createCriteria().andLayerEqualTo(layer).andGidIsNotNull();
            giss = deviceGisMapper.selectByExample(deviceGisExample);
        } else {
            // 查询所有的数据
            deviceGisExample.createCriteria().andGidIsNotNull();
            giss = deviceGisMapper.selectByExample(deviceGisExample);
        }

        // 生成Feature
        for (DeviceGis gisItem : giss) {
            feature = new Feature();

            feature.setId(gisItem.getGid().toString());
            feature.setProperty("icon", gisItem.getIcon());
            feature.setProperty("deviceId", gisItem.getDid());
            feature.setProperty("layer", gisItem.getLayer());

            switch (gisItem.getGtype()) {
                case 1:     //点
                    geoJsonObject = new Point();
                    try {
                        LngLatAlt coord = objectMapper.readValue(gisItem.getData(), LngLatAlt.class);
                        ((Point) geoJsonObject).setCoordinates(coord);
                    } catch (IOException e) {

                    }
                    feature.setGeometry(geoJsonObject);
                    break;
                case 2:     //线
                    geoJsonObject = new LineString();
                    try {
                        List<LngLatAlt> coord = objectMapper.readValue(gisItem.getData(), List.class);
                        ((LineString) geoJsonObject).setCoordinates(coord);
                    } catch (IOException e) {

                    }
                    feature.setGeometry(geoJsonObject);
                    break;
                case 3:     //面
                    geoJsonObject = new Polygon();
                    try {
                        List<List<LngLatAlt>> coord = objectMapper.readValue(gisItem.getData(), List.class);
                        ((Polygon) geoJsonObject).setCoordinates(coord);
                    } catch (IOException e) {

                    }
                    feature.setGeometry(geoJsonObject);
                    break;
            }
            featureCollection.add(feature);
        }

        // 转换成 json
        try {
            return objectMapper.writeValueAsString(featureCollection);
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    /**
     * @return Device 的状态Hash 列表
     */
    @ExtDirectMethod
    public HashMap<Integer, DeviceStateBean> getFeaturesState() {
        return deviceManager.getDeviceStateList();
    }

}
