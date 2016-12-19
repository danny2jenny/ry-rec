Ext.define('scpc.view.TopPanel', {
        extend: 'Ext.panel.Panel',
        alias: 'widget.topPanel',
        region: 'center',

        frame: false,
        header: false,
        //height: '15%', //采用百分比的方式
        height: 110, //固定高度
        //width :3605,
        //设置背景色或者图片
        //html:'<img src="res/img/top.jpg" height=100% , width=100%></img>'

        //设置背景图片
        bodyStyle: {
            //background: '#ffc',
            background: 'url(/static/img/top.jpg) no-repeat #00FFFF ',
            padding: '10px'
        }

    }
);