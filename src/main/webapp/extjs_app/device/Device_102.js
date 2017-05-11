/**
 * Created by danny on 17-2-16.
 *
 * 开关输入
 */

// 没有控制面板


// 必须有
ry.devices['device_102'] = {
    gisClick: function (device) {

    }
};

ry.deviceEditor['sig_102'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_SIG_102
});

