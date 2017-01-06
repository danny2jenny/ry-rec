package com.rytec.gis;

import org.geojson.FeatureCollection;

/**
 * Created by danny on 17-1-5.
 * <p>
 * 为 Openlayers 扩展的 FeatureCollection 类，添加了Type描述
 */
public class OlFeatureCollection extends FeatureCollection {

    private static String type = "FeatureCollection";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
