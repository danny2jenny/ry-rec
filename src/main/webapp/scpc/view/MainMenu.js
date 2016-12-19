/**主界面左菜单
 *
 */
Ext.define('scpc.view.MainMenu', {

    extend: 'Ext.tree.Panel',
    alias: 'widget.mainMenu',
    title: '菜单',
    rootVisible: false,  //隐藏根节点
    containerScroll: true,
    collapsible: true, //panel 可以收方的动画效果
    autoScroll: true,
    width: 300,

    store: 'MainMenu',

    initComponent: function () {
        this.callParent(arguments);

    }
})


