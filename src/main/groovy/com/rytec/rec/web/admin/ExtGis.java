package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.db.mapper.DeviceGisMapper;
import com.rytec.rec.db.mapper.GisMapper;
import com.rytec.rec.db.model.DeviceGis;
import com.rytec.rec.db.model.DeviceGisExample;
import com.rytec.rec.db.model.Gis;
import com.rytec.rec.db.model.GisExample;
import org.geojson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by danny on 16-12-29.
 * <p>
 * WEB GIS  数据库操作
 */

@Controller
public class ExtGis {

    @Autowired
    GisMapper gisMapper;

    @Autowired
    DeviceGisMapper deviceGisMapper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @param geoStr GeoJson的feature字符串
     * @return Gis 的数据库对象
     * <p>
     * 可以新建或者是更新或者是更新根据Feature的ID来确定是否需要新建
     */
    @ExtDirectMethod
    public Gis saveFeature(String geoStr) {

        Feature feature;

        try {
            feature = new ObjectMapper().readValue(geoStr, Feature.class);

            GeoJsonObject geometry = feature.getGeometry();

            int deviceId = feature.getProperty("deviceId");
            int layer = feature.getProperty("layer");

            String coordinateStr = "";
            int type = 1;
            //点
            if (geometry instanceof Point) {
                coordinateStr = new ObjectMapper().writeValueAsString(((Point) geometry).getCoordinates());
                type = 1;
            }

            // 线
            if (geometry instanceof LineString) {
                ((LineString) geometry).getCoordinates();
                coordinateStr = new ObjectMapper().writeValueAsString(((LineString) geometry).getCoordinates());
                type = 2;
            }

            // 面
            if (geometry instanceof Polygon) {
                coordinateStr = new ObjectMapper().writeValueAsString(((Polygon) geometry).getCoordinates());
                type = 3;
            }

            Gis gis = new Gis();
            gis.setData(coordinateStr);
            gis.setLayer(layer);
            gis.setType(type);
            gis.setDevice(deviceId);

            if (feature.getId() == null) {
                gisMapper.insert(gis);
            } else {
                gis.setId(Integer.parseInt(feature.getId()));
                gisMapper.updateByPrimaryKey(gis);
            }

            return gis;

        } catch (IOException e) {
            return null;
        }


    }

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Gis> list(ExtDirectStoreReadRequest request) {
        int deviceId = (Integer) request.getParams().getOrDefault("masterId", 0);
        if (deviceId == 0) {
            return gisMapper.selectByExample(null);
        } else {
            GisExample gisExample = new GisExample();
            gisExample.createCriteria().andDeviceEqualTo((Integer) request.getParams().get("masterId"));
            return gisMapper.selectByExample(gisExample);
        }
    }

    // 修改
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Gis update(Gis node) {
        gisMapper.updateByPrimaryKey(node);
        return node;
    }

    // 插入
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Gis create(Gis node) {
        gisMapper.insert(node);
        return node;
    }

    // 删除
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_MODIFY)
    public Gis delete(Gis node) {
        gisMapper.deleteByPrimaryKey(node.getId());
        return node;
    }
}
