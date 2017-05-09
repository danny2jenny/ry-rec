/**
 * Created by danny on 17-5-2.
 *
 * 全景展示窗口
 * https://github.com/mpetroff/pannellum
 *
 * 每个Device包含以下关键变量
 * hotspot-id
 * device-id
 * name->text
 * icon
 * type
 *
 * 通过DeviceState来更新状态图标
 *
 */

Ext.define('app.view.window.Panorama', {
    extend: 'Ext.window.Window',
    alias: 'widget.window.panorama',
    id: 'window.panorama',
    width: 640,
    height: 480,
    layout: 'fit',
    maximized: true,
    html: "<div id='panorama' style='width: 100%;height: 100%'></div>",

    onHotSpotEnter: function (node) {
        node.currentTarget.id;

        // 首先关闭以前的面板
        if (ry.panorama.ctlPanel) {
            ry.panorama.ctlPanel.close();
            ry.panorama.ctlPanel = null;
        }

        var hotsport = ry.panorama.hotspots[node.currentTarget.id];

        if (!hotsport) {
            return;
        }

        // 判断是否有控制面板
        if (!Ext.isDefined(app.view.device.control['_' + hotsport.type])) {
            return;
        }

        ry.panorama.ctlPanel = Ext.create('app.view.device.control._' + hotsport.type, {
            renderTo: hotsport.div.children[0]
        });

        ry.panorama.ctlPanel.show();

    },

    onHotSpotLeave: function (node) {
        node.currentTarget.id

        // 首先关闭以前的面板
        // if (ry.panorama.ctlPanel) {
        //     ry.panorama.ctlPanel.close();
        //     ry.panorama.ctlPanel = null;
        // }


    },

    ctlPanel: null,      // 控制面板

    // 所有的热点
    hotspots: {
        "31": {
            id: 31,
            icon: 201,
            name: '测试点',
            type: 101,
            pitch: 5,
            yaw: 9
        }
    },

    whenShow: function (w, o) {

        // 关闭 GIS 的POPUP
        ry.gis.interaction.popup.hide();

        // 显示全景
        this.panorama = pannellum.viewer('panorama', {
            "type": "equirectangular",
            panorama: "/upload/panorama/alma-correlator-facility.jpg",
            "hotSpots": [],
            "autoLoad": true
        });

        this.panorama.onHotSpotEnter = this.onHotSpotEnter;
        this.panorama.onHotSpotLeave = this.onHotSpotLeave;


        // 鼠标事件
        this.panorama.on('mousedown', function (event) {
            var a = ry.panorama.panorama.mouseEventToCoords(event);
            console.log(a);
        }, this);

        // 图片加载完成后的事件
        this.panorama.on('load', function (a, b, c) {
            ry.panorama.panorama.addHotSpot(ry.panorama.hotspots[31]);
        })
    },

    initComponent: function () {
        this.callParent(arguments);

        this.on('show', this.whenShow, this);
        ry.panorama = this;
    }
});