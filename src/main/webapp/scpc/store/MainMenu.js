/**
 * Created by danny on 16-8-25.
 */

Ext.define('scpc.store.MainMenu', {
    extend: 'Ext.data.TreeStore',

    // 是否自动加载
    autoLoad: true,

    // store 与后台数据自动同步
    autoSync: true,

    // 这个属性是异步加载主要特征，通过该节点去请求子节点
    nodeParam: 'pid',
    // 根节点的参数值是0
    defaultRootId: 0,

    model: 'scpc.model.MainMenu',
    proxy: {
        type: 'ajax',
        url: 'menu',
        folderSort: true,
        reader: {
            type: 'json'
        },
        expanded: true
    },
    // 设置根节点
    root: {
        //默认展开
        expanded: false
    }


})