/**
 * Created by danny on 17-4-11.
 *
 * 摄像机
 */


ry.devices['device_401'] = {
    gisClick: function (fProperties) {
        var deviceId = fProperties.deviceId;
        // 找到相应的adr和cid
        ry.realPlayInGrid(deviceId);
    }
};

// 设备动作
ry.deviceEditor['act_401'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_ACT_401
});