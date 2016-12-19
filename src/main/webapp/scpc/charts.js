/*
 图表生成
 输入参数：
 store：对应的Store
 unit: 计量单位名称
 xName: 横轴名称
 yName: 纵轴名称
 column：列名
 columName: 列的显示名称
 range: 数据分类的数组 [[min,max],[min,max]]
 */


function greateChart(opt) {

    var me = this;

    /*
     {
     name: '名称',
     value: '值',
     desc： '描述'
     }
     */
    var chartData = new Array();

    //分组数据，以及最后的显示数据
    opt.range.forEach(function (ri) {
        var vi = (ri[0] == -999999999) ? '以上' : Math.abs(ri[0]);
        var va = (ri[1] == 999999999) ? '以上' : Math.abs(ri[1]);

        var vv = '';

        if (Math.abs(ri[0]) < Math.abs(ri[1])) {
            vv = vi + '-' + va + opt.unit;
        } else {
            vv = va + '-' + vi + opt.unit;
        }

        chartData.push(
            {
                vMin: ri[0],        //最小值
                vMax: ri[1],        //最大值
                vName: vv,          //名称
                vValue: 0,          //值
                vDesc: '<ol>'       //描述
            }
        );

    })


    //把原始数据进行分组
    var store = Ext.StoreMgr.get(opt.store);

    if (!store.data.length) {
        store.load();
    }


    store.each(function (item, scope) {
        chartData.forEach(function (di) {
            if ((item.data[opt.column] >= di.vMin) && (item.data[opt.column] < di.vMax)) {
                di.vValue++;
                di.vDesc += '<li>' + item.data[opt.columnName] + '</li>';
            }
        })
    }, me);

    var chartStore = Ext.create('Ext.data.JsonStore', {
        fields: ['vMin', 'vMax', 'vName', 'vValue', 'vDesc'],
        data: chartData
    });


    //构造图表
    switch (opt.type) {

        //柱状图
        case 'column':
            var chartAxes = [
                // 纵坐标
                {
                    type: 'Numeric',
                    position: 'left',
                    fields: ['vValue'],
                    minimum: 0,
                    grid: true,
                    title: opt.yName
                },

                //横坐标
                {
                    type: 'Category',  //'Category',
                    position: 'bottom',
                    fields: ['vName'],
                    title: opt.xName
                }];

            var chartSeries = [
                {
                    type: 'column',
                    axis: 'bottom',
                    highlight: true,
                    xField: 'vName',
                    yField: 'vValue',
                    title: opt.yName,
                    renderer: function (sprite, record, attr, index, store) {
                        return Ext.apply(attr, {
                            fill: opt.color ? opt.color : '#A9BE3B'
                        });
                    },
                    label: {
                        display: 'insideEnd',
                        field: 'vValue',
                        orientation: 'horizontal',
                        color: '#333',
                        'text-anchor': 'middle'
                    }
                    ,
                    tips: {
                        trackMouse: true,
                        layout: 'fit',
                        items: [{
                            xtype: 'container'
                        }],
                        renderer: function (storeItem, item) {
                            this.down('container').update(storeItem.data.vDesc);
                            //this.setTitle();
                        }
                    }
                }];

            var chart = Ext.create(
                'Ext.chart.Chart',
                {
                    title: opt.xName,
                    store: chartStore,
                    animate: true,
                    axes: chartAxes,
                    series: chartSeries
                });

            break;

        //饼图
        case 'pie':
            var chartSeries = [{
                type: 'pie',
                field: 'vValue',
                showInLegend: true,
                donut: 10,
                tips: {
                    trackMouse: true,
                    layout: 'fit',
                    items: [{
                        xtype: 'container'
                    }],
                    renderer: function (storeItem, item) {
                        //calculate percentage.
                        var total = 0;
                        chartStore.each(function (rec) {
                            total += rec.get('vValue');
                        });
                        this.down('container').update(storeItem.get('vName') + '<br>' + Math.round(storeItem.get('vValue') / total * 100) + '%<br>' + storeItem.get('vDesc'));
                        //this.setTitle(storeItem.get('vName') + '<br>' + Math.round(storeItem.get('vValue') / total * 100) + '%<br><br>' + storeItem.get('vDesc'));
                    }
                },
                highlight: {
                    segment: {
                        margin: 20
                    }
                },
                label: {
                    field: 'vName',
                    display: 'rotate',
                    contrast: true,
                    font: '18px Arial',
                    renderer: function (label, storeItem, item, i, display, animate, index) {
                        var total = 0;
                        chartStore.each(function (rec) {
                            total += rec.get('vValue');
                        });
                        return Math.round((item.get('vValue') / total * 100)) + '%';
                    }
                }
            }]

            var chart = Ext.create(
                'Ext.chart.Chart',
                {
                    legend: {
                        position: 'right'
                    },
                    background: '#0000',
                    store: chartStore,
                    animate: true,
                    series: chartSeries
                });
            break
    }


    return chart;
}


function testChart() {
    var chart = greateChart({
        type: 'pie',
        store: 'Enterprise',
        xName: '企业产值范围',
        yName: '数量',
        unit: '万',
        column: 'output',
        columnName: 'shortName',
        range: [[0, 3000], [3000, 10000]]
    });

    var win = Ext.create('Ext.Window', {
        width: 800,
        height: 600,
        maximizable: true,
        renderTo: Ext.getBody(),
        layout: 'fit',
        items: chart
    })

    win.show();
}

// Store 的回调函数
function storeCallback(records, operation, success) {
    //得到图表的显示区域
    var cp = Ext.ComponentQuery.query('[itemId=chartPanel]')[0];

    cp.removeAll(true);

    if (this.chartCfg) {
        this.chartCfg.forEach(function (cd) {
            var chart = greateChart(cd);
            cp.add(chart);
            cp.setActiveTab(chart);
        })
        cp.expand();
    } else {
        cp.collapse();
    }

}