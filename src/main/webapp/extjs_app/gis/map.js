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
    style: new ol.style.Style({
        fill: new ol.style.Fill({
            color: 'rgba(255, 255, 255, 0.2)'
        }),
        stroke: new ol.style.Stroke({
            color: '#ffcc33',
            width: 2
        }),
        image: new ol.style.Circle({
            radius: 7,
            fill: new ol.style.Fill({
                color: '#ffcc33'
            })
        })
    })
});

gis.layers.device.setMap(gis.map);

gis.draw = new ol.interaction.Draw({
    features: gis.devices,
    //type: "Point",
    type: "LineString",
});

gis.draw.on('drawend', function (event) {
    //一个新的对象被添加
    event.feature
});

gis.map.addInteraction(gis.draw);