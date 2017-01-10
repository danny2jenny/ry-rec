/**
 * Created by danny on 16-12-22.
 *
 * 开关量的功能端口配置UI
 */


Ext.define('app.view.device.FunSwitch', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.airVel',
    title: '最新风速信息',
    height: 150,
    iconCls: 's_wind',
    styleHtmlContent: true,
    styleHtmlCls: 'control_tpl',
    tpl: '更新日期:{time}<br>当前风速:{val1}m/s',

    interaction: [{
        type: 'refresh',
        fun: 1,
        tooltip: '刷新当前数据'
    }, {
        type: 'search',
        fun: 2,
        tooltip: '查看历史数据'
    }],
    bbar: [{
        xtype: 'label',
        name: 'tip',
        text: '系统提示',
        style: 'color:red;',
        hidden: true
    }],

    show: function () {
        this.callParent(arguments);
        this.fireEvent('avtShow', this);
    },
    initComponent: function () {
        this.callParent(arguments);
        this.addEvents('avtShow');
    }
});