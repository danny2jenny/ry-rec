/**
 * Created by danny on 16-10-14.
 */

Ext.define('scpc.view.DynamicMenu', {
    extend: 'Ext.toolbar.Toolbar',
    mixins: {
        bindable: 'Ext.util.Bindable'
    },
    alias: 'widget.dynamic.menu',
    border: true,
    store: 'MainMenu',
    initComponent: function () {
        var me = this;
        if (Ext.isString(me.store))
            me.store = Ext.data.StoreManager.lookup(me.store || 'ext-empty-store');
        me.store.addListener('load', me.onLoad, me);
        this.callParent();
    },

    doClick: function (menu, item, e, eOpts) {
        var centerPanel = Ext.getCmp('centerPanel');
        var n = centerPanel.getComponent(menu.action);

        if (!n) { // 判断是否已经打开该面板 (为叶子节点时执行点击事件)
            n = centerPanel.add({
                xtype: menu.action,
                itemId: menu.action,
                icon: menu.icon,
                title: menu.text,
                closable: true // 通过html载入目标页  ,显示关闭按钮
            });
        }


        if (n) {
            centerPanel.setActiveTab(n);
            n.store.load({
                scope: n.store,
                callback: storeCallback
            });
        }

    },

    onLoad: function (ts, parent, records) {
        // tb should be the toolbar
        var tb = this, container;
        //for the toolbar first level buttons
        if (parent.isRoot()) {
            Ext.Array.each(records, function (node) {
                //
                tb.doAdd(tb, node, tb);
            });
            return;
        }

        // for submenu
        // get container component from itemId
        container = tb.down('[itemId=' + parent.getId() + ']');
        if (!container)return;
        Ext.Array.each(records, function (node) {
            tb.doAdd(container, node, tb);
        });
    },


    doAdd: function (container, node, tb) {
        var me = this;
        if (node.isLeaf()) {
            me.addLeafNode(container, node, tb);
        } else {
            me.addParentNode(container, node, false);
        }
    },

    //添加父节点
    addParentNode: function (container, node, split) {
        var menuItem = {
            height: 32,
            scale: 'medium',
            icon: '/static/icon/16/' + node.raw.icon + '.png',
            text: node.data.text,
            itemId: node.getId(),
            menu: []
        };
        //将菜单添加到父容器中

        if (container.xtype == 'dynamic.menu') {

            container.insert(container.items.indexOf(container.down('[itemId=yearSelect]')) - 1, menuItem);
        } else {
            menuItem.height = 32;
            container.menu.add(menuItem);
        }
        if (split) container.add('-');
        //展开节点
        node.expand();
    },

    //添加子节点
    addLeafNode: function (container, node, tb) {
        var item,
            menuItem = {
                height: 24,
                scale: 'medium',
                icon: '/static/icon/16/' + node.raw.icon + '.png',
                text: node.data.text,
                itemId: node.getId(),
                action: node.raw.action,
            };
        if (container.xtype == 'dynamic.menu') {
            item = container.add(menuItem);
        } else {
            item = container.menu.add(menuItem);
        }

        item.addListener('click', tb.doClick, tb);
    },

    items: [
        '->',
        {
            xtype: 'combobox',
            fieldLabel: '计算年份',
            itemId: 'yearSelect',
            store: 'YearList',
            displayField: 'name',
            valueField: 'value',
            value: (new Date).getFullYear()
        }
    ]
})