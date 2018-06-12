/**
 * Created by danny on 17-2-3.
 * Node 的参数配置
 * 生成一个json存放在Node表的opt字段
 */


Ext.define('app.view.admin.panel.NodeConfig', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.admin.panel.nodeconfig',
    itemId: 'admin_panel_nodeconfig',
    title: '节点参数配置',
    icon: 'res/toolbar/config.png',
    hidden: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },


    bodyPadding: '5 5 0',
    items: [
        {
            xtype: 'numberfield',
            fieldLabel: '灵敏度',
            name: 'sensitive',
            decimalPrecision: 10,
            itemId: 'sensitive'
        },
        {
            xtype: 'numberfield',
            fieldLabel: '线性参数A',
            name: 'pA',
            decimalPrecision: 10,
            itemId: 'pA'
        }, {
            xtype: 'numberfield',
            fieldLabel: '线性参数B',
            decimalPrecision: 10,
            name: 'pB',
            itemId: 'pB'
        }, {
            xtype: 'textfield',
            fieldLabel: '计量单位',
            name: 'unit',
            itemId: 'unit'
        }, {
            xtype: 'numberfield',
            fieldLabel: '更新周期',
            name: 'interval',
            itemId: 'interval'
        }],
    buttons: [{
        text: "更新",
        handler: function () {
            var pa = this.ownerCt.ownerCt;
            var selection = pa.selection;
            var opt = {};

            selection.beginEdit();

            opt.sensitive = pa.down('#sensitive').getValue();
            opt.pA = pa.down('#pA').getValue();
            opt.pB = pa.down('#pB').getValue();
            opt.unit = pa.down('#unit').getValue();
            opt.interval = pa.down('#interval').getValue();

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
            this.down('#unit').setValue(opt.unit);
            this.down('#interval').setValue(opt.interval);

        } else {
            this.down('#sensitive').setValue(null);
            this.down('#pA').setValue(null);
            this.down('#pB').setValue(null);
            this.down('#unit').setValue(null);
            this.down('#interval').setValue(null);
        }
    }

});