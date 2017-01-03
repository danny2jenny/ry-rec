package com.rytec.rec.web.admin;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rytec.rec.db.mapper.GisMapper;
import com.rytec.rec.db.model.Gis;
import com.rytec.rec.db.model.GisExample;
import org.geojson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 保存单个的GIS数据
    @ExtDirectMethod
    public boolean saveFeature(String geoStr) {
        Feature feature;

        try {
            feature = new ObjectMapper().readValue(geoStr, Feature.class);

            GeoJsonObject geometry = feature.getGeometry();

            int deviceId = feature.getProperty("deviceId");

            logger.debug("DeviceID:" + deviceId);

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
            gis.setLayer(1);
            gis.setType(type);
            gis.setDevice(deviceId);

            gisMapper.insert(gis);

            logger.debug(coordinateStr);

        } catch (IOException e) {
            return false;
        }

        return true;
    }

    // 列表
    @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ)
    public List<Gis> list(ExtDirectStoreReadRequest request) {
        int deviceId = (Integer) request.getParams().getOrDefault("device", 0);
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
