/**
 * Created by danny on 16-12-27.
 *
 * GIS 界面
 */

gis = new Object();

gis.extent = [-180, -90, 180, 90];

gis.projection = new ol.proj.Projection({
    code: 'EPSG:4326',
    units: 'm',
    extent: [-180.0000, -90.0000, 180.0000, 90.0000]
});

//------------------------------------------------------------------
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

//------------------------------------------------------------------


gis.map = new ol.Map({
    layers: [
        new ol.layer.Image({
            source: new ol.source.ImageStatic({
                url: '/upload/gis/layer/base.jpg',
                projection: gis.projection,
                imageExtent: gis.extent
            })
        })
    ],

    view: new ol.View({
        projection: gis.projection,
        center: ol.extent.getCenter(gis.extent),
        extent: gis.extent,
        zoom: 4,
        maxZoom: 4
    })
});

// 存放所有的Device对象
gis.devices = new ol.Collection();

gis.layers = new Object();

gis.layers.device = new ol.layer.Vector({
    source: new ol.source.Vector({features: gis.devices}),
    style: gis.style.styleFun
});

gis.layers.device.setMap(gis.map);

gis.draw = new ol.interaction.Draw({
    features: gis.devices,
    type: "Point",
    //type: "LineString",
    condition: function (event) {
        var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
        if (!deviceGrid.getSelectionModel().selected.length) {
            return false;
        } else {
            return true;
        }
    }
});

gis.draw.on('drawend', function (event) {
    var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
    var deviceId = deviceGrid.getSelectionModel().selected.get(1).get('id');
    var icon = deviceGrid.getSelectionModel().selected.get(1).get('icon');

    event.feature.setProperties({'deviceId': deviceId});
    event.feature.setProperties({'icon': icon});

    //一个新的对象被添加
    var parser = new ol.format.GeoJSON();
    var geoJson = parser.writeFeature(event.feature);
    extGisDevice.saveFeature(geoJson);
});

gis.map.addInteraction(gis.draw);

