/**
 * Created by danny on 17-2-16.
 *
 * 开关控制
 */

// 配置面板
Ext.define('app.device.Device_101', {

    configPanel: Ext.create('Ext.panel.Panel', {

        itemId: 'device_config_101',
        title: '设备参数配置',
        hidden: true,

        items: [
            {
                xtype: 'numberfield',
                fieldLabel: '+高告警门限：',
                name: 'limit_h_2',
                itemId: 'limit_h_2'
            },
            {
                xtype: 'numberfield',
                fieldLabel: '+高联动门限',
                name: 'limit_h_1',
                itemId: 'limit_h_1'
            }, {
                xtype: 'numberfield',
                fieldLabel: '-低联动门限',
                name: 'limit_l_1',
                itemId: 'pB'
            }, {
                xtype: 'textfield',
                fieldLabel: '-低告警门限',
                name: 'limit_l_2',
                itemId: 'limit_l_2'
            }
        ],
        buttons: [{
            text: "更新",
            handler: function () {

                var form = this.up('form').getForm();

                debugger;
                var pa = this.ownerCt.ownerCt;
                var selection = pa.selection;
                var opt = {};

                selection.beginEdit();

                opt.sensitive = pa.down('#sensitive').getValue();
                opt.pA = pa.down('#pA').getValue();
                opt.pB = pa.down('#pB').getValue();
                opt.unit = pa.down('#unit').getValue();

                selection.set('opt', JSON.stringify(opt))
                selection.endEdit();
                selection.commit();
            }
        }],

        /**
         * 根据
         * @param store
         * @param field
         */
        readConfig: function (selection, field) {
            this.show();
            var optStr = selection.get(field);
            this.selection = selection;
            //读取数据
            if (optStr.length) {
                var opt = JSON.parse(optStr);
                this.down('#sensitive').setValue(opt.sensitive);
                this.down('#pA').setValue(opt.pA);
                this.down('#pB').setValue(opt.pB);
            } else {
                this.down('#sensitive').setValue(null);
                this.down('#pA').setValue(null);
                this.down('#pB').setValue(null);
            }
        }

    })


});

ry.devices['device_101'] = Ext.create('app.device.Device_101', {});

ry.deviceEditor['act_101'] =Ext.create('Ext.form.field.ComboBox',{
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_ACT_101
});