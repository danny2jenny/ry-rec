/**
 * Created by danny on 17-5-2.
 *
 * 全景展示窗口
 *
 * https://github.com/pchen66/panolens.js
 *
 * http://pchen66.github.io/Panolens/#Example
 *
 * http://pchen66.github.io/Panolens/docs/index.html
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


    whenShow: function (w, o) {

        this.panorama = pannellum.viewer('panorama', {
            "type": "equirectangular",
            "panorama": "/upload/panorama/alma-correlator-facility.jpg",
            "autoLoad": true
        });

        this.panorama.on('mousedown', function (event, a, b, c) {
        }, this);
    },

    initComponent: function () {
        this.callParent(arguments);

        this.on('show', this.whenShow, this);
    }
});