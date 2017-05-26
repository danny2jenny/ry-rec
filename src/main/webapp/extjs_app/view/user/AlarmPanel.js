/**
 * Created by 12793 on 2017/4/14.
 */


Ext.define('app.view.user.AlarmPanel', {
    title: '告警消息',
    extend: 'Ext.panel.Panel',
    alias: 'widget.user.alarm',
    //icon: 'res/toolbar/alarm.gif',

    width: 255,
    collapsible: true,
    collapsed: true,
    collapseDirection: Ext.Component.DIRECTION_RIGHT,
    //hidden: true,
    id: 'user.alarm.panel',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    // tools: [{
    //     type: 'close',
    //     closeAction: 'hide',
    //     handler: function (event, toolEl, panelHeader) {
    //         this.ownerCt.ownerCt.hide();
    //     }
    // }],

    items: [
        {
            xtype: 'gridpanel',
            flex: 1,
            store: 'Alarm',
            itemId: 'alarmGrid',
            //分组
            features: [Ext.create('Ext.grid.feature.Grouping', {
                groupHeaderTpl: ['日期：{groupValue:this.formatValue}: 共 ({rows.length}) 个', {
                    formatValue: function (value) {
                        return value
                    }
                }]
            })],

            // plugins: [{
            //     ptype: 'rowexpander',
            //     rowBodyTpl : new Ext.XTemplate(
            //         '<p><b>Company:</b> {id}</p>',
            //         '<p><b>Change:</b> {change:this.formatChange}</p><br>',
            //         '<p><b>Summary:</b> {device}</p>',
            //         {
            //             formatChange: function(v){
            //                 //var color = v >= 0 ? 'green' : 'red';
            //                 return '<span> ' + "Hello" + '</span>';
            //             }
            //         })
            // }],
            columns: [{
                text: '图标',
                dataIndex: 'icon',
                width: 40,
                renderer: function (val, column, row) {
                    return "<img src='" + val + "'>"
                }
            }, {
                text: '设备',
                dataIndex: 'device',
                flex: 1,
                renderer: function (val, column, row) {
                    return ry.devicesState[val].device.name
                }
            }, {
                header: '告警时间',
                dataIndex: 'hour',
                width: 100
            }],

            bbar: {
                xtype: 'pagingtoolbar',
                store: 'Alarm'
            }
        },

        // 这里是告警消息的详细信息
        {
            xtype: 'panel',
            title: '告警详细信息',
            height: 270,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },

            items: [{
                // 告警详细消息
                xtype: 'container',
                itemId: 'alarmDetail',
                style: {
                    lineHeight: '18px'
                },
                height: 60,
                tpl: '告警源：{device}<br>告警类型：{type}<br>告警参数：{value}'
            }, {
                // 关联摄像机
                xtype: 'gridpanel',
                itemId: 'alarmActions',
                store: 'AlarmVideo',
                title: '关联摄像机',
                flex: 1,
                columns: [{
                    text: '图标',
                    dataIndex: 'icon',
                    width: 40,
                    renderer: function (val, column, row) {
                        return "<img src='" + val + "'>"
                    }
                }, {
                    text: '设备',
                    dataIndex: 'target',
                    flex: 1,
                    renderer: function (val, column, row) {
                        return ry.devicesState[val].device.name
                    }
                }/*, {
                 text: '设备编号',
                 flex: 1,
                 dataIndex: 'target'
                 }*/]
            }]
        }
    ],

    // 播放告警声音
    playAudio: function (state) {
        var a = document.getElementById('wave.alarm');
        if (state) {
            a.play();
        } else {
            a.pause();
        }
    },

    // 告警处理
    alarm: function (msg) {
        // 播放声音
        this.expand();
        this.playAudio(true);

        // 播放视频
        for (var i in msg.msg.actions) {
            ry.realPlayInForm(msg.msg.actions[i].target);
        }
    },

    // 展开事件处理
    onExpand: function (p, eOpts) {
        // 刷新数据
        this.down('#alarmGrid').store.load();
    },

    // 折叠事件处理
    onCollapse: function (p, eOpts) {
        this.playAudio(false);
    },

    // 滑动展开
    onFloat: function (eOpts) {
        this.down('#alarmGrid').store.load();
    },

    // 滑动收起
    onUnfloat: function (eOpts) {
        this.playAudio(false);
    },

    // 当选择了一个Alarm
    onAlarmSelected: function (event, record, index, eOpts) {
        // 查询告警摄像机
        var actions = this.down('#alarmActions');
        actions.store.proxy.extraParams.device = record.data.device;
        actions.store.proxy.extraParams.sig = record.data.sig;
        actions.store.load();
        // 更新告警详细信息
        var detail = this.down('#alarmDetail');
        var data = {
            device: ry.devicesState[record.data.device].device.name,
            type: ry.trans(
                record.data.sig,
                ry['DEVICE_SIG_' + ry.devicesState[record.data.device].device.type]
            ),
            value: record.data.value
        };
        detail.update(data);
        // 地图上定位资源
        ry.gis.highlightDevice(record.data.device);
    },

    // Alarm Action 选择
    onAlarmActionSelected: function (event, record, index, eOpts) {
        ry.gis.highlightDevice(record.data.target);
    },

    initComponent: function () {
        this.callParent(arguments);

        // Panel 事件
        this.on('collapse', this.onCollapse, this);
        this.on('expand', this.onExpand, this);
        this.on('float', this.onFloat, this);
        this.on('unfloat', this.onUnfloat, this);



        // Alarm事件
        var alarm = this.down('#alarmGrid');
        alarm.on('select', this.onAlarmSelected, this);

        // AlarmAction 事件
        var actionsGrid = this.down('#alarmActions');
        actionsGrid.on('select', this.onAlarmActionSelected);

    }
});
