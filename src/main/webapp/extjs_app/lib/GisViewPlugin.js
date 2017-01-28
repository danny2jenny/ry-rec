/**
 * Created by danny on 17-1-24.
 *
 * GisView 的Plugin，用于一个Panel, layout: 'fit' 必须
 *
 * 参考：
 * http://openlayers.org/workshop/en/index.html
 * http://viglino.github.io/ol3-ext/
 * https://openlayersbook.github.io/
 * http://www.acuriousanimal.com/thebookofopenlayers3/
 * http://www.acuriousanimal.com/thebookofopenlayers3/chapter06_02_markers_overlays.html
 * https://github.com/anzhihun/OpenLayers3Primer
 *
 *
 *
 * 配置：
 * layerStore：字符串， GisLayer对应 app.store.GisLayer
 * editable: boolean 是否允许编辑
 */

Ext.define('app.lib.GisViewPlugin', {
    alias: 'plugin.gis.view',                   //使用 ptypt:'gis.view' 方式建立插件
    extend: 'Ext.AbstractPlugin',

    mixins: {
        observable: 'Ext.util.Observable'       //具备事件的处理能力
    },

    /*******************************************************
     * Map 属性                                             *
     *******************************************************/

    extent: [-180, -90, 180, 90],

    projection: new ol.proj.Projection({
        code: 'EPSG:4326',
        units: 'm',
        extent: [-180.0000, -90.0000, 180.0000, 90.0000]
    }),

    /*******************************************************
     * 基本的Style                                          *
     *******************************************************/
    style: {
        /**
         *
         * @param cls               图标的代码
         * @param mod               图标模式
         * @returns {string}        返回图标的路径
         */
        getIconPath: function (cls, mod) {
            return "icon/device_icon/" + cls + "-" + mod + ".gif";
        },

        // 区域填充的样式
        fill: new ol.style.Fill({
            color: 'rgba(255, 255, 255, 0.2)'
        }),

        // 线条填充样式
        stroke: new ol.style.Stroke({
            color: '#ffcc33',
            width: 2
        }),

        // 点填充样式
        icon: new ol.style.Circle({
            radius: 7,
            fill: new ol.style.Fill({
                color: '#ffcc33'
            })
        }),

        // 样式的缓存
        cache: {}
    },

    // 是否在编辑
    editing: false,

    /**
     * 初始化函数
     * client 对应父控件，一般是一个panel
     * @param client
     */
    init: function (client) {

        client.layout = 'fit';          // 强制使用fit
        this.callParent(arguments);
        var me = this;


        /*******************************************************
         * 样式的集合Style                                          *
         *******************************************************/

        // 缺省的样式集
        me.style.default = new ol.style.Style({
            fill: me.style.fill,
            stroke: me.style.stroke,
            image: me.style.icon
        });

        // 选中的样式
        me.style.selected = new ol.style.Style({
            fill: me.style.fill,
            stroke: me.style.stroke,
            image: new ol.style.Icon({
                src: '/icon/mark/light.gif'
            })
        });

        // Style 初始化函数
        me.style.styleFun = function (feature, resolution) {

            // 缓冲样式的 key，用于 gis.style.chace 的索引
            var styleKey = feature.getProperties().icon + '-' + 11;

            // 是否有缓冲
            var cachedStyle = me.style.cache[styleKey];

            if (!cachedStyle) {
                // 没有缓冲，新建
                var cachedStyle = new ol.style.Style({
                    fill: me.style.fill,
                    stroke: me.style.stroke,
                });

                // 建立点的样式
                var icon = new ol.style.Icon({
                    src: me.style.getIconPath(feature.getProperties().icon, 11)
                });

                // 点样式加入到样式
                cachedStyle.setImage(icon);

                me.style.cache[styleKey] = cachedStyle;
            }

            return cachedStyle;
        };

        // 改变一组 Feature 的样式
        me.style.changeStyle = function (features, type) {

            // 缓冲样式的 key，用于 gis.style.chace 的索引

            for (var i in features) {
                var feature = features[i];
                var styleKey = feature.getProperties().icon + '-' + type;
                var cachedStyle = me.style.cache[styleKey];


                if (!cachedStyle) {
                    // 没有缓冲，新建
                    var cachedStyle = new ol.style.Style({
                        fill: me.style.fill,
                        stroke: me.style.stroke,
                    });

                    // 建立点的样式
                    var icon = new ol.style.Icon({
                        src: me.style.getIconPath(feature.getProperties().icon, type)
                    });

                    // 点样式加入到样式
                    cachedStyle.setImage(icon);

                    me.style.cache[styleKey] = cachedStyle;
                }

                feature.setStyle(cachedStyle);
            }
        };

        // 改变 Device 的 Style
        me.setDeviceStyle = function (device, state) {
            var features = me.getFeaturesByIdofLayer(gis.getActiveVectorLayer(), device);
            me.style.changeStyle(features, state);
        };


        /*******************************************************
         * Map 对象                                             *
         *******************************************************/
        me.map = new ol.Map({
            renderer: 'canvas',
            view: new ol.View({
                projection: me.projection,
                center: ol.extent.getCenter(me.extent),
                extent: me.extent,
                zoom: 4,
                maxZoom: 4
            })
        });
        me.map.owner = me;

        me.map.on('change', function (event) {
            debugger;
        });

        // 添加图层切换
        me.map.addControl(new ol.control.LayerSwitcherImage());

        // 重新设置mapView
        me.maxExtent = function () {
            me.map.getView().fit(me.extent, me.map.getSize());
        };

        // 缩放到Features对应的区域
        me.zoomToFeatures = function (features) {
            var a = [];
            for (var i in features) {
                a.push(features[i].getGeometry());
            }
            var gc = new ol.geom.GeometryCollection(a);
            me.map.getView().fit(gc.getExtent(), me.map.getSize());

        };

        /*******************************************************
         * 图层管理                                             *
         *******************************************************/

        me.layers = new Ext.util.HashMap();

        // 删除所有的图层
        me.clearLayers = function () {
            me.map.getLayers().clear();
            me.layers.clear();
        };

        // 删除一个图层
        me.delLayer = function (layerId) {
            var layer = me.layers.get(layerId);
            me.map.removeLayer(layer);
            me.layers.removeAtKey(layerId);
        };

        // 得到当前激活的图层组
        me.getActiveLayer = function () {
            var r = null;
            me.layers.each(function (key, value, length) {
                if (value.getVisible()) {
                    r = value;
                }
            }, this);
            return r;
        };

        // 得到当前激活的设备显示图层
        me.getActiveVectorLayer = function () {
            var layer = me.getActiveLayer();
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
        me.addLayer = function (layerId, layerName, layerFile) {

            var newLayer = new ol.layer.Group({
                baseLayer: true,
                name: layerName,
                preview: '/upload/gis/layer/_' + layerFile,
                visible: false,
                layers: [
                    new ol.layer.Image({
                        source: new ol.source.ImageStatic({
                            url: '/upload/gis/layer/' + layerFile,
                            projection: me.projection,
                            imageExtent: me.extent
                        })
                    }),
                    new ol.layer.Vector({
                        source: new ol.source.Vector({
                            url: "srv/gis/getFeaturesByLayer/" + layerId,
                            format: new ol.format.GeoJSON()
                        }),
                        style: me.style.styleFun
                    })
                ]
            });

            // Layer切换的事件
            newLayer.on('change:visible', function (event) {

                event.target;       //layer group
                event.oldValue;     // 以前的状态 true false

                debugger;
                // 当前激活的层
                if (!event.oldValue) {
                    gisDevice.getFeaturesState(function (data, event, rst) {
                        this.overlay.states = data;
                        this.overlay.updateDevice(this);
                    }, this)
                }
            }, me);

            me.layers.add(layerId, newLayer);

            newLayer.id = layerId;
            me.map.addLayer(newLayer);
        };


        /*******************************************************
         * Feature 管理                                         *
         *******************************************************/

        me.overlay = {
            states: null,
            /**
             * 通过Feature生成一个overlay
             * @param feature
             * @returns {ol.Overlay}
             */
            createFeatureOverlay: function (feature, state, me) {
                var elem = document.createElement('div');
                elem.style.width = '16px';
                elem.style.height = '16px';
                elem.style.backgroundImage = "url(" + me.style.getIconPath(feature.getProperties().icon, state) + ")";

                var overlay = new ol.Overlay({
                    id: feature.getId(),
                    element: elem,
                    position: feature.getGeometry().getCoordinates(),
                    positioning: 'center-center'
                });
                return overlay;
            },
            devicesOverlay: new ol.Collection,    // Device 的 overlay

            updateDevice: function (me) {
                me.map.overlays_.clear();
                var features = me.getActiveVectorLayer().getSource().getFeatures();
                for (i in features) {
                    var feature = features[i];
                    var state = this.states[feature.getProperties().deviceId].state;
                    var overlay = me.overlay.createFeatureOverlay(feature, state, me);
                    me.map.addOverlay(overlay);
                }

            }
        };

        /**
         * 得到一个Layer中对应的id的Feature
         * @param id
         * @param layer
         */
        me.getFeaturesByIdofLayer = function (layer, id) {
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

        me.hightlightFeatures = function (features, zoom) {
            for (var i in features) {
                var position = features[i].getGeometry().getCoordinates();
                var elem = document.createElement('div');

                var overlay = new ol.Overlay({
                    element: elem,
                    position: position,
                    positioning: 'center-center'
                });
                me.map.addOverlay(overlay)
                elem.setAttribute('class', 'circle');
            }

            if (zoom) {
                me.map.zoomToFeatures(features);
            }
        };

        // 清除Overlay
        me.overlayClean = function () {
            var overLays = me.map.getOverlays().getArray();
            var len = overLays.length;
            for (var i = 0; i < len; i++) {
                var overlay = overLays[0];
                me.map.removeOverlay(overlay);
            }
        };

        /*******************************************************
         * Map 编辑工具                                         *
         *******************************************************/
        if (me.editable) {
            me.interaction = {

                // 判断是否可以编辑的函数
                drawConditionFun: function (event) {
                    // todo：这个需要添加到配置
                    var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
                    if (!deviceGrid.getSelectionModel().selected.length) {
                        return false;
                    } else {
                        return !me.editing;
                    }
                },

                // 作图完成后的事件
                onDrawEnd: function (event) {
                    // todo：这个需要添加到配置
                    var deviceGrid = Ext.ComponentQuery.query('#adminDeviceGrid')[0];
                    var deviceId = deviceGrid.getSelectionModel().selected.get(0).get('id');
                    var icon = deviceGrid.getSelectionModel().selected.get(0).get('icon');
                    var layer = me.getActiveLayer().id;

                    // 设置Feature 的属性
                    event.feature.setProperties(
                        {
                            'deviceId': deviceId,
                            'icon': icon,
                            'layer': layer
                        });

                    var layerSource = me.getActiveVectorLayer().getSource();
                    layerSource.addFeature(event.feature);

                    //一个新的对象被添加
                    var parser = new ol.format.GeoJSON();
                    var geoJson = parser.writeFeature(event.feature);

                    //保存当前作图的状态
                    me.editing = true;
                    me.lastFeature = event.feature;

                    // 保存，回调
                    extGis.saveFeature(geoJson, function (result, e) {
                        this.editing = false;
                        this.lastFeature.setId(result.id);
                    }, me);


                },

                // 编辑完成后的事件
                onModifyEnd: function (event) {
                    var feature = event.target.dragSegments_[0][0].feature;

                    var parser = new ol.format.GeoJSON();
                    var geoJson = parser.writeFeature(feature);

                    //保存当前作图的状态
                    this.editing = true;
                    this.lastFeature = feature;

                    // 保存，回调
                    extGis.saveFeature(geoJson, function (result, e) {
                        // todo: 更新 gis grid
                        this.editing = false;
                    }, me);
                }
            };

            // 画点工具
            me.interaction.drawPoint = new ol.interaction.Draw({
                type: "Point",
                condition: me.interaction.drawConditionFun
            });
            me.interaction.drawPoint.on('drawend', me.interaction.onDrawEnd, me);

            // 画线工具
            me.interaction.drawLine = new ol.interaction.Draw({
                type: "LineString",
                condition: gis.interaction.drawConditionFun
            });
            me.interaction.drawLine.on('drawend', me.interaction.onDrawEnd, me);

            // 画面工具
            me.interaction.drawPolygon = new ol.interaction.Draw({
                type: "Polygon",
                condition: gis.interaction.drawConditionFun
            });
            me.interaction.drawPolygon.on('drawend', me.interaction.onDrawEnd, me);

            // 修改工具
            me.interaction.modify = new ol.interaction.Modify({
                features: new ol.Collection()
            });
            me.interaction.modify.on('modifyend', me.interaction.onModifyEnd, me);

            // 编辑工具条
            me.editBar = new ol.control.Bar();
            me.map.addControl(me.editBar);
            me.editBar.barItems = new ol.control.Bar({
                toggleOne: true, group: true
            });
            me.editBar.addControl(me.editBar.barItems);


            // 添加画点
            me.editBar.barItems.addControl(new ol.control.Toggle(
                {
                    html: "<img src= 'icon/24/Point.png' />",
                    className: "point",
                    title: '点',
                    interaction: me.interaction.drawPoint,
                    onToggle: function (active) {
                        me.setEditing();
                    }
                })
            );

            // 添加画线
            me.editBar.barItems.addControl(new ol.control.Toggle(
                {
                    html: "<img src= 'icon/24/Line.png' />",
                    className: "line",
                    title: '线',
                    interaction: me.interaction.drawLine,
                    onToggle: function (active) {
                        me.setEditing();
                    }
                })
            );

            // 添加画面
            me.editBar.barItems.addControl(new ol.control.Toggle(
                {
                    html: "<img src= 'icon/24/Polygon.png' />",
                    className: "polygon",
                    title: '面',
                    interaction: me.interaction.drawPolygon,
                    onToggle: function (active) {
                        me.setEditing();
                    }
                })
            );

            // 添加修改
            me.editBar.barItems.addControl(new ol.control.Toggle(
                {
                    html: "<img src= 'icon/24/Move.png' />",
                    className: "modify",
                    title: '移动',
                    interaction: me.interaction.modify,
                    onToggle: function (active) {
                        me.setEditing();
                        // 把当前激活图层的features添加道modify控件当中
                        me.interaction.modify.features_.clear();
                        var features = me.getActiveVectorLayer().getSource().getFeatures();
                        for (i in features) {
                            var feature = features[i];
                            me.interaction.modify.addFeature_(feature);
                        }
                    }
                })
            );

            // 判断是否Edit工具使能
            me.setEditing = function () {

                var active = false;
                for (i in this.editBar.barItems.controls_) {
                    var control = this.editBar.barItems.controls_[i];
                    if (control.getActive()) {
                        active = true;
                        break;
                    }
                }

                if (active) {
                    this.map.overlays_.clear();
                } else {
                    this.overlay.updateDevice(this);
                }
            };
        }

        /*******************************************************
         * Client 的事件                                        *
         *******************************************************/
        client.on('resize', function (client) {
            this.map.updateSize();
        }, me);

        client.on('afterLayout', function (client) {
            this.map.setTarget(client.body.dom);
            // 窗口改变大小后，需要重新缩放图层
            this.maxExtent();
        }, me);

        client.on('focus', function (panel, eOpts) {
            debugger;
        }, me);

        // 当Layer Store 刷新时触发
        me.onLayerFresh = function (store, opt) {

            //首删除所有的图层
            this.clearLayers();

            //添加相应的图层
            for (var i = 0; i < store.data.getCount(); i++) {
                var item = store.data.getAt(i).data;
                me.addLayer(item.id, item.name, item.file);
            }

            //把第一个层作为显示层
            this.layers.getValues()[0].setVisible(true);

        };

        me.layerStore = Ext.StoreMgr.get(me.layerStore);
        me.layerStore.on('refresh', me.onLayerFresh, me);
    }
});
