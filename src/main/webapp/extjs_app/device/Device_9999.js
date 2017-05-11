/**
 * Created by danny on 17-5-10.
 *
 *  全景设备
 */


ry.devices['device_9999'] = {
    gisClick: function (device) {
        var pView = Ext.create('app.view.window.Panorama', {});
        pView.show();
        pView.items.items[0].loadPanorama(device);

        var store = Ext.StoreMgr.get('Panorama');
        var record = store.findRecord('device', device);
        pView.setTitle("全景：" + record.get('name'));
    }
};

