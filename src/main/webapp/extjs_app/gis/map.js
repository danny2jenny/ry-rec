/**
 * Created by danny on 16-12-27.
 *
 * GIS 界面
 *
 * 参考：
 * http://openlayers.org/workshop/en/index.html
 * http://viglino.github.io/ol3-ext/
 *
 */

gis = new Object();

/*******************************************************
 * Map 属性                                             *
 *******************************************************/

gis.extent = [-180, -90, 180, 90];

gis.projection = new ol.proj.Projection({
    code: 'EPSG:4326',
    units: 'm',
    extent: [-180.0000, -90.0000, 180.0000, 90.0000]
});

/*******************************************************
 * Style                                               *
 *******************************************************/

gis.style = new Object();

gis.style.getIconPath = function (cls, mod) {
    return "icon/device_icon/" + cls + "-" + mod + ".gif";
};

gis.style.fill = new ol.style.Fill({
    color: 'rgba(255, 255, 255, 0.2)'
});

gis.style.stroke = new ol.style.Stroke({
    color: '#ffcc33',
    width: 2
});

gis.style.icon = new ol.style.Circle({
    radius: 7,
    fill: new ol.style.Fill({
        color: '#ffcc33'
    })
});

gis.style.default = new ol.style.Style({
    fill: gis.style.fill,
    stroke: gis.style.stroke,
    image: gis.style.icon
});

gis.style.cache = new ol.Collection();

//动态Style函数
gis.style.styleFun = function (feature, resolution) {

    var styleKey = feature.getProperties().icon + '-' + 11;

    var cachedStyle = gis.style.cache.get(styleKey);

    if (!cachedStyle) {
        var cachedStyle = new ol.style.Style({
            fill: gis.style.fill,
            stroke: gis.style.stroke,
        });

        var icon = new ol.style.Icon({
            src: gis.style.getIconPath(feature.getProperties().icon, 11)
        });

        cachedStyle.setImage(icon);
        gis.style.cache.set(styleKey, cachedStyle);
    }


    return cachedStyle;
}

/*******************************************************
 * Map 对象                                             *
 *******************************************************/


gis.map = new ol.Map({
    view: new ol.View({
        projection: gis.projection,
        center: ol.extent.getCenter(gis.extent),
        extent: gis.extent,
        zoom: 4,
        maxZoom: 4
    })
});
gis.map.addControl(new ol.control.LayerSwitcherImage());


/*******************************************************
 * Map 工具                                             *
 *******************************************************/
gis.interaction = new Object();

// 当前是否在作图过程中，例如存储过程
gis.editing = false;

// 作图完成后的回调
gis.interaction.drawFun = function (event) {
    var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
    var deviceId = deviceGrid.getSelectionModel().selected.get(1).get('id');
    var icon = deviceGrid.getSelectionModel().selected.get(1).get('icon');
    var layer = gis.getActiveLayer().id;

    // 设置Feature 的属性
    event.feature.setProperties(
        {
            'deviceId': deviceId,
            'icon': icon,
            'layer': layer
        });

    var layerSource = gis.getActiveVectorLayer().getSource();
    layerSource.addFeature(event.feature);

    //一个新的对象被添加
    var parser = new ol.format.GeoJSON();
    var geoJson = parser.writeFeature(event.feature);

    //保存当前作图的状态
    gis.editing = true;
    gis.lastFeature = event.feature;

    // 保存，回调
    extGis.saveFeature(geoJson, function (result, e) {
        gis.editing = false;
        gis.lastFeature.setId(result.id);
    });


};

// 修改完成后的事件
gis.interaction.editFun = function (event, a, b, c) {
    var feature = event.target.dragSegments_[0][0].feature;

    var parser = new ol.format.GeoJSON();
    var geoJson = parser.writeFeature(feature);

    //保存当前作图的状态
    gis.editing = true;
    gis.lastFeature = feature;

    // 保存，回调
    extGis.saveFeature(geoJson, function (result, e) {
        gis.editing = false;
        debugger;
    });

    debugger;
};

// 是否可以作图的判断
gis.interaction.drawConditionFun = function (event) {
    var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
    if (!deviceGrid.getSelectionModel().selected.length) {
        return false;
    } else {
        return !gis.editing;
    }
};

// 画点工具
gis.interaction.drawPoint = new ol.interaction.Draw({
    type: "Point",
    condition: gis.interaction.drawConditionFun
});
gis.interaction.drawPoint.on('drawend', gis.interaction.drawFun);

// 画线工具
gis.interaction.drawLine = new ol.interaction.Draw({
    type: "LineString",
    condition: gis.interaction.drawConditionFun
});
gis.interaction.drawLine.on('drawend', gis.interaction.drawFun);

// 画面工具
gis.interaction.drawPolygon = new ol.interaction.Draw({
    type: "Polygon",
    condition: gis.interaction.drawConditionFun
});
gis.interaction.drawPolygon.on('drawend', gis.interaction.drawFun);

// 修改工具
gis.interaction.modify = new ol.interaction.Modify({
    features: new ol.Collection()
});
gis.interaction.modify.on('modifyend', gis.interaction.editFun);

// 选择工具
gis.interaction.select = new ol.interaction.Select();


/*******************************************************
 * 控件管理                                             *
 *******************************************************/
gis.editBar = new ol.control.Bar();
gis.map.addControl(gis.editBar);

/* Nested toobar with one control activated at once */
gis.editBar.barItems = new ol.control.Bar({
    toggleOne: true, group: true
});

gis.editBar.addControl(gis.editBar.barItems);

// 添加画点
gis.editBar.barItems.addControl(new ol.control.Toggle(
    {
        html: "<img src= 'icon/24/Point.png' />",
        className: "point",
        title: '点',
        interaction: gis.interaction.drawPoint,
        onToggle: function (active) {

        }
    })
);

// 添加画线
gis.editBar.barItems.addControl(new ol.control.Toggle(
    {
        html: "<img src= 'icon/24/Line.png' />",
        className: "line",
        title: '线',
        interaction: gis.interaction.drawLine,
        onToggle: function (active) {

        }
    })
);

// 添加画面
gis.editBar.barItems.addControl(new ol.control.Toggle(
    {
        html: "<img src= 'icon/24/Polygon.png' />",
        className: "polygon",
        title: '面',
        interaction: gis.interaction.drawPolygon,
        onToggle: function (active) {

        }
    })
);

// 添加修改
gis.editBar.barItems.addControl(new ol.control.Toggle(
    {
        html: "<img src= 'icon/24/Move.png' />",
        className: "modify",
        title: '移动',
        interaction: gis.interaction.modify,
        //interaction: new ol.interaction.Select(),
        onToggle: function (active) {
            gis.interaction.modify.features_.clear();
            var features = gis.getActiveVectorLayer().getSource().getFeatures();
            for (i in features) {
                var feature = features[i];
                gis.interaction.modify.addFeature_(feature);
            }
        }
    })
);


/*******************************************************
 * 图层管理                                             *
 *******************************************************/

gis.layers = new Ext.util.HashMap();

// 删除所有的图层
gis.clearLayers = function () {
    gis.map.getLayers().clear();
    gis.layers.clear();
};

// 删除一个图层
gis.delLayer = function (layerId) {
    var layer = gis.layers.get(layerId);
    gis.map.removeLayer(layer);
    gis.layers.removeAtKey(layerId);
};

// 得到当前激活的图层
gis.getActiveLayer = function () {
    var r = null;
    gis.layers.each(function (key, value, length) {
        if (value.getVisible()) {
            r = value;
        }
    }, this);
    return r;
};

gis.getActiveVectorLayer = function () {
    var layer = gis.getActiveLayer();
    if (layer) {
        return layer.getLayers().getArray()[1];
    } else {
        return null;
    }
}

/**
 *
 * @param layerId
 * @param layerName
 * @param layerFile
 */
gis.addLayer = function (layerId, layerName, layerFile) {

    var newLayer = new ol.layer.Group({
        baseLayer: true,
        name: layerName,
        preview: '/upload/gis/layer/_' + layerFile,
        visible: false,
        layers: [
            new ol.layer.Image({
                source: new ol.source.ImageStatic({
                    url: '/upload/gis/layer/' + layerFile,
                    projection: gis.projection,
                    imageExtent: gis.extent
                })
            }),
            new ol.layer.Vector({
                source: new ol.source.Vector({
                    url: "srv/gis/getFeaturesByLayer/" + layerId,
                    format: new ol.format.GeoJSON()
                }),
                style: gis.style.styleFun
            })
        ]
    });

    gis.layers.add(layerId, newLayer);

    newLayer.id = layerId;
    gis.map.addLayer(newLayer);
};

