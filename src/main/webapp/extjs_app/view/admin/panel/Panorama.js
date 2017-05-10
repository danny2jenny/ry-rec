/**
 * Created by danny on 17-5-9.
 *
 * 全景编辑界面
 */

Ext.define('app.view.admin.panel.Panorama', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.panel.Panorama',
    id: 'panel.panorama.editor',
    layout: 'fit',
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

    onHotSpotClick: function (event) {
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

    afterRender: function (panel, eOpts) {

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
        this.panorama.onHotSpotClick = this.onHotSpotClick;


        // 鼠标事件
        this.panorama.on('mousedown', function (event) {
            var a = ry.panorama.panorama.mouseEventToCoords(event);
            console.log(a);
        }, this);

        // 图片加载完成后的事件
        this.panorama.on('load', function () {
            ry.panorama.panorama.addHotSpot(ry.panorama.hotspots[31]);
        })
    },

    onResize: function (panel, layout, eOpts) {
        //ry.panorama.panorama.resize();
    },

    initComponent: function () {
        this.callParent(arguments);
        //this.on('render', this.afterRender, this);
        this.on('afterlayout', this.onResize, this);
        ry.panorama = this;
    }
});
