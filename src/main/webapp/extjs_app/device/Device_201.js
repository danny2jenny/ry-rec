/**
 * Created by danny on 17-2-16.
 * 模拟输入
 */

// 配置面板
Ext.define('app.device.Device_201', {

    configPanel: Ext.create('Ext.panel.Panel', {
        itemId: 'device_config_201',
        title: '设备参数配置',
        bodyPadding: '5 5 0',
        hidden: true,
        height: 200,
        items: [
            {
                xtype: 'numberfield',
                fieldLabel: '+高告警门限',
                name: 'GATE_HIGH_2',
                itemId: 'GATE_HIGH_2'
            },
            {
                xtype: 'numberfield',
                fieldLabel: '+高联动门限',
                name: 'GATE_HIGH_1',
                itemId: 'GATE_HIGH_1'
            }, {
                xtype: 'numberfield',
                fieldLabel: '-低联动门限',
                name: 'GATE_LOW_1',
                itemId: 'GATE_LOW_1'
            }, {
                xtype: 'numberfield',
                fieldLabel: '-低告警门限',
                name: 'GATE_LOW_2',
                itemId: 'GATE_LOW_2'
            }
        ],
        buttons: [{
            text: "更新",
            handler: function () {
                debugger;
                var pa = this.ownerCt.ownerCt;
                var selection = pa.selection;
                var opt = {};

                selection.beginEdit();

                opt.GATE_HIGH_2 = pa.down('#GATE_HIGH_2').getValue();
                opt.GATE_HIGH_1 = pa.down('#GATE_HIGH_1').getValue();
                opt.GATE_LOW_1 = pa.down('#GATE_LOW_1').getValue();
                opt.GATE_LOW_2 = pa.down('#GATE_LOW_2').getValue();

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
                this.down('#GATE_HIGH_2').setValue(opt.GATE_HIGH_2);
                this.down('#GATE_HIGH_1').setValue(opt.GATE_HIGH_1);
                this.down('#GATE_LOW_1').setValue(opt.GATE_LOW_1);
                this.down('#GATE_LOW_2').setValue(opt.GATE_LOW_2);
            } else {
                this.down('#GATE_HIGH_2').setValue(9999);
                this.down('#GATE_HIGH_1').setValue(8888);
                this.down('#GATE_LOW_1').setValue(-8888);
                this.down('#GATE_LOW_2').setValue(-9999);
            }
        }

    })
});

// 加入到全局变量
ry.devices['device_201'] = Ext.create('app.device.Device_201', {});

ry.deviceEditor['sig_201'] = Ext.create('Ext.form.field.ComboBox', {
    forceSelection: true,
    allowBlank: false,
    editable: false,
    autoSelect: true,
    triggerAction: 'all',
    store: ry.DEVICE_SIG_201
});