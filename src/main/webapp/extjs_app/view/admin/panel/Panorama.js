/**
 * Created by danny on 17-5-9.
 *
 * 全景编辑界面
 *
 */

Ext.define('app.view.admin.panel.Panorama', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.panel.Panorama',
    id: 'panel.panorama.editor',
    layout: 'fit',
    html: "<div id='panorama' style='width: 100%;height: 100%'></div>",

    editable: true,      // 缺省可以编辑
    ctlPanel: null,      // 控制面板

    // 所有的热点
    hotspots: new Ext.util.HashMap(),

    /**
     * 鼠标进入
     * @param event
     */
    onHotSpotEnter: function (event) {
        event.currentTarget.id;

        // 首先关闭以前的面板
        if (ry.panorama.ctlPanel) {
            ry.panorama.ctlPanel.close();
            ry.panorama.ctlPanel = null;
        }

        var hotspot = ry.panorama.hotspots.get(event.currentTarget.id);

        if (!hotspot) {
            return;
        }

        // 判断是否有控制面板
        if (!Ext.isDefined(app.view.device.control['_' + hotspot.type])) {
            return;
        }

        ry.panorama.ctlPanel = Ext.create('app.view.device.control._' + hotspot.type, {
            renderTo: hotspot.div.children[0]
        });

        ry.panorama.ctlPanel.updateState(ry.devicesState[hotspot.device]);

        ry.panorama.ctlPanel.show();

    },

    /**
     * 鼠标离开
     * @param event
     */
    onHotSpotLeave: function (event) {
        event.currentTarget.id

        // 首先关闭以前的面板
        if (ry.panorama.ctlPanel) {
            ry.panorama.ctlPanel.close();
            ry.panorama.ctlPanel = null;
        }

    },

    /**
     * 热点上的鼠标点击
     * @param event
     */
    onHotSpotClick: function (event) {

        var hotspot = ry.panorama.hotspots.get(event.currentTarget.id);

        switch (event.button) {
            case 0:     // 左键----执行动作
                if (ry.devices['device_' + hotspot.type].gisClick) {
                    ry.devices['device_' + hotspot.type].gisClick(hotspot.device);
                }
                break;
            case 2:     // 右键----删除
                if (ry.panorama.editable) {
                    ry.panorama.delHotspot(hotspot);
                }
                break;
        }
    },


    /**
     * 删除热点
     * @param hotspot
     */
    delHotspot: function (hotspot) {
        //从界面删除
        ry.panorama.panorama.removeHotSpot(hotspot.id);
        //从数据库删除
        extPanoramaDevice.delPanoramaDevices(hotspot.id);
    },


    // 改变大小，需要重绘
    onResize: function (panel, layout, eOpts) {
        if (ry.panorama.panorama && ry.panorama.panorama.getRenderer()) {
            ry.panorama.panorama.resize();
        }
    },

    /**
     * 用于加载一个场景
     * @param deviceId  对应的Device
     */
    loadPanorama: function (deviceId) {

        var store = Ext.StoreMgr.get('Panorama');
        var record = store.findRecord('device', deviceId);

        if (!record) {
            return;
        }

        // 销毁以前的全景
        if (this.panorama) {
            this.panorama.destroy();
            this.panorama = null;
        }

        this.sceneId = record.data.id;

        // 显示全景
        this.panorama = pannellum.viewer('panorama', {
            "type": "equirectangular",
            panorama: "/upload/panorama/" + record.data.file,
            "hotSpots": [],
            "autoLoad": true
        });

        this.panorama.onHotSpotEnter = this.onHotSpotEnter;
        this.panorama.onHotSpotLeave = this.onHotSpotLeave;
        this.panorama.onHotSpotClick = this.onHotSpotClick;


        // 鼠标事件
        this.panorama.on('mousedown', this.onNewHotspot);

        // 图片加载完成后的事件
        this.panorama.on('load', this.afterSceneLoad);
    },

    // 添加新的Hotspot
    onNewHotspot: function (event) {

        if (!ry.panorama.editable) {
            return;
        }

        if (!event.altKey) {
            return;
        }

        // [pitch, yaw]
        var position = ry.panorama.panorama.mouseEventToCoords(event);

        var deviceGrid = Ext.getCmp('admin.device.grid');
        if (!deviceGrid.getSelectionModel().selected.length) {
            return;
        }

        var device = deviceGrid.getSelectionModel().getSelection()[0];
        if (device.get('type') >= 9000) {
            return;
        }
        var device = device.get('id');
        var scene = ry.panorama.sceneId;

        extPanoramaDevice.savePanoramaDevice(device, scene, position[0], position[1], function (result, e) {
            if (!result.id) {
                return;
            }
            ry.panorama.addHotspot(result);
        }, this);

    },

    /**
     * 通过数据库返回记录，添加一个单一的Hotspot
     */
    addHotspot: function (result) {
        var device = ry.devicesState[result.device].device;

        var hotspot = {};
        hotspot.id = result.id;
        hotspot.device = result.device;
        hotspot.pitch = result.pitch;
        hotspot.yaw = result.yaw;
        hotspot.icon = device.icon;
        hotspot.name = device.name;
        hotspot.type = device.type;

        ry.panorama.hotspots.add(hotspot.id, hotspot);
        ry.panorama.panorama.addHotSpot(hotspot);

        // 更新图标状态
        if (!ry.devicesState) {
            return;
        }
        var state = ry.devicesState[hotspot.device];

        if (state) {
            hotspot.div.style.backgroundImage = "url(" + ry.getDeviceStateIcon(hotspot.icon, state.runtime.iconState) + ")";
        }

    },

    /**
     * 更新 一个 Icon 图标
     */
    updateIconState: function (msg) {
        this.hotspots.each(function (key, hotspot, length) {
            if (hotspot.device == msg.device.id) {
                hotspot.div.style.backgroundImage = "url("
                    + ry.getDeviceStateIcon(msg.device.icon, msg.runtime.iconState)
                    + ")";
            }
        });
    },

    /**
     * 更新所有的图标
     */
    updateAllIcon: function () {
        this.hotspots.each(function (key, hotspot, length) {
            var state = ry.devicesState[hotspot.device];
            if (state) {
                hotspot.div.style.backgroundImage = "url("
                    + ry.getDeviceStateIcon(hotspot.icon, state.runtime.iconState)
                    + ")";
            }
        });
    },

    // 全景场景加载完成，可以添加　hotspot
    afterSceneLoad: function () {

        ry.panorama.hotspots.clear();
        extPanoramaDevice.getPanoramaDevices(ry.panorama.sceneId, function (result, e) {
            for (var i in result) {
                ry.panorama.addHotspot(result[i]);
            }
        });
    },

    // 收到stom消息，更新状态
    updateDeviceState: function (msg) {
        // 更新图标
        this.updateIconState(msg);

        // 更新控制面板
        if (this.ctlPanel) {
            this.ctlPanel.refreshState(msg);
        }
    },

    initComponent: function () {
        this.callParent(arguments);
        this.on('resize', this.onResize, this);
        ry.panorama = this;
    }
});
