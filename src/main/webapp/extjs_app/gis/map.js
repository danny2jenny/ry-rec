/**
 * Created by danny on 16-12-27.
 *
 * GIS 界面
 *
 * 参考：
 * http://openlayers.org/workshop/en/index.html
 * http://viglino.github.io/ol3-ext/
 * https://openlayersbook.github.io/
 *
 * http://www.acuriousanimal.com/thebookofopenlayers3/
 * http://www.acuriousanimal.com/thebookofopenlayers3/chapter06_02_markers_overlays.html
 * https://github.com/anzhihun/OpenLayers3Primer
 *
 */

gis = {};

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

gis.style = {};

/**
 *
 * @param cls               图标的代码
 * @param mod               图标模式
 * @returns {string}        返回图标的路径
 */
gis.style.getIconPath = function (cls, mod) {
    return "icon/device_icon/" + cls + "-" + mod + ".gif";
};

// 区域填充的样式
gis.style.fill = new ol.style.Fill({
    color: 'rgba(255, 255, 255, 0.2)'
});

// 线条填充样式
gis.style.stroke = new ol.style.Stroke({
    color: '#ffcc33',
    width: 2
});

// 点填充样式
gis.style.icon = new ol.style.Circle({
    radius: 7,
    fill: new ol.style.Fill({
        color: '#ffcc33'
    })
});

// 缺省的样式集
gis.style.default = new ol.style.Style({
    fill: gis.style.fill,
    stroke: gis.style.stroke,
    image: gis.style.icon
});

// 选中的样式
gis.style.selected = new ol.style.Style({
    fill: gis.style.fill,
    stroke: gis.style.stroke,
    image: new ol.style.Icon({
        src: '/icon/mark/light.gif'
    })
});

/**
 * 样式的一个Index，缓冲
 * index: icon-state 的方式，例如 101-10
 */

gis.style.cache = {};

/**
 * 动态Style函数 通过 Feature 的 properties 来确定样式
 * 只针对点样式进行动态
 * @param feature           传递的feature
 * @param resolution        当前的分辨率
 */

gis.style.styleFun = function (feature, resolution) {

    // 缓冲样式的 key，用于 gis.style.chace 的索引
    var styleKey = feature.getProperties().icon + '-' + 11;

    // 是否有缓冲
    var cachedStyle = gis.style.cache[styleKey];

    if (!cachedStyle) {
        // 没有缓冲，新建
        var cachedStyle = new ol.style.Style({
            fill: gis.style.fill,
            stroke: gis.style.stroke,
        });

        // 建立点的样式
        var icon = new ol.style.Icon({
            src: gis.style.getIconPath(feature.getProperties().icon, 11)
        });

        // 点样式加入到样式
        cachedStyle.setImage(icon);

        gis.style.cache[styleKey] = cachedStyle;
    }

    return cachedStyle;
};

/**
 * 改变一个feature的风格
 * @param feature       //Feature 的Array
 * @param type          //新的状态编码
 */
gis.style.changeStyle = function (features, type) {
// 缓冲样式的 key，用于 gis.style.chace 的索引

    for (var i in features) {
        var feature = features[i];
        var styleKey = feature.getProperties().icon + '-' + type;
        var cachedStyle = gis.style.cache[styleKey];


        if (!cachedStyle) {
            // 没有缓冲，新建
            var cachedStyle = new ol.style.Style({
                fill: gis.style.fill,
                stroke: gis.style.stroke,
            });

            // 建立点的样式
            var icon = new ol.style.Icon({
                src: gis.style.getIconPath(feature.getProperties().icon, type)
            });

            // 点样式加入到样式
            cachedStyle.setImage(icon);

            gis.style.cache[styleKey] = cachedStyle;
        }

        feature.setStyle(cachedStyle);
    }
};

/**
 * 改变 device 的图标
 * @param device
 * @param state
 */
gis.style.featureStyle = function (device, state) {
    var features = gis.getFeaturesByDeviceOfLayer(gis.getActiveVectorLayer(), device);
    gis.style.changeStyle(features, state);
};

/*******************************************************
 * Map 对象                                             *
 *******************************************************/


gis.map = new ol.Map({
    renderer: 'canvas',
    view: new ol.View({
        projection: gis.projection,
        center: ol.extent.getCenter(gis.extent),
        extent: gis.extent,
        zoom: 4,
        maxZoom: 4
    })
});

// 添加图层切换
gis.map.addControl(new ol.control.LayerSwitcherImage());

gis.map.maxExtent = function () {
    gis.map.getView().fit(gis.extent, gis.map.getSize());
};

// 缩放到Features对应的区域
gis.map.ZoomToFeatures = function (features) {
    var a = [];
    for (var i in features) {
        a.push(features[i].getGeometry());
    }
    var gc = new ol.geom.GeometryCollection(a);
    gis.map.getView().fit(gc.getExtent(), gis.map.getSize());

};

/*******************************************************
 * Map 工具                                             *
 *******************************************************/
gis.interaction = {};

// 当前是否在作图过程中，例如存储过程
gis.editing = false;

// 作图完成后的回调
gis.interaction.drawFun = function (event) {
    var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
    var deviceId = deviceGrid.getSelectionModel().selected.get(0).get('id');
    var icon = deviceGrid.getSelectionModel().selected.get(0).get('icon');
    var layer = gis.getActiveLayerGroup().id;

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
gis.interaction.editFun = function (event) {
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


// Hover选择工具
gis.interaction.hoverSelect = new ol.interaction.Select({
    condition: ol.events.condition.pointerMove,
    multi: false,
    //style: gis.style.styleFun
});

gis.interaction.hoverSelect.on('select', function (event) {
    if (event.selected.length) {
        gis.map.addOverlay(gis.interaction.popup);
        gis.interaction.popup.show(event.selected[0].getGeometry().getCoordinates(), '高宏宇');
    } else {
        gis.interaction.popup.hide();
        //gis.map.removeOverlay(gis.interaction.popup);
    }
});

gis.map.addInteraction(gis.interaction.hoverSelect);

// Click 选择工具
gis.interaction.clickSelect = new ol.interaction.Select({
    condition: ol.events.condition.click,
    multi: false,
    //style: gis.style.styleFun
});

gis.interaction.clickSelect.on('select', function (event) {
    if (event.selected.length) {
        //gis.overlayClean();
    } else {
        //gis.overlayClean();
    }
});

gis.map.addInteraction(gis.interaction.clickSelect);

//弹出的Overlay
gis.interaction.popup = new ol.Overlay.Popup({
    popupClass: "default", //"tooltips", "warning" "black" "default", "tips", "shadow",
    positioning: 'auto',
    autoPan: true,
    autoPanAnimation: {duration: 250}
});


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
        onToggle: function (active) {
            // 把当前激活图层的features添加道modify控件当中
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
gis.getActiveLayerGroup = function () {
    var r = null;
    gis.layers.each(function (key, value, length) {
        if (value.getVisible()) {
            r = value;
        }
    }, this);
    return r;
};

// 得到当前激活的设备显示图层
gis.getActiveVectorLayer = function () {
    var layer = gis.getActiveLayerGroup();
    if (layer) {
        return layer.getLayers().getArray()[1];
    } else {
        return null;
    }
};

/**
 * 添加图层
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

/**
 * 得到一个Layer中对应的id的Feature
 * @param id
 * @param layer
 */
gis.getFeaturesByDeviceOfLayer = function (layer, id) {
    var out = [];
    var features = layer.getSource().getFeatures();
    for (var index in features) {
        if (features[index].getProperties().deviceId == id) {
            out.push(features[index]);
        }
    }

    return out;
};


/**
 * 高亮Feature，通过Overlayer
 * @param features          features的 数组
 * @param zoom              是否放大到该区域
 */

gis.highlightFeatures = function (features, zoom) {
    for (var i in features) {
        var position = features[i].getGeometry().getCoordinates();
        var elem = document.createElement('div');

        var overlay = new ol.Overlay({
            element: elem,
            position: position,
            positioning: 'center-center'
        });
        gis.map.addOverlay(overlay)
        elem.setAttribute('class', 'circle');
    }

    if (zoom) {
        gis.map.ZoomToFeatures(features)
    }
};

// 清除Overlay
gis.overlayClean = function () {
    var overLays = gis.map.getOverlays().getArray();
    var len = overLays.length;
    for (var i = 0; i < len; i++) {
        var overlay = overLays[0];
        gis.map.removeOverlay(overlay);
    }
}

