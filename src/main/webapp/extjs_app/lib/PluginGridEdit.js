/**
 * Created by danny on 17-1-3.
 *
 * 可以编辑表格的插件
 * 1、标准的给予行的编辑插件
 * 2、可以建立主表和子表的关系
 *
 * *********** 配置信息 ****************
 * ptype: 'grid.editing',
 * autoLoad: true,
 * masterGrid: '主表的itemId',
 * fKey: '外键名',
 * newRec: {}          //新建记录的缺省值
 * ************************************
 */

Ext.define('app.lib.PluginGridEdit', {
    alias: 'plugin.grid.editing',  //使用 ptypt:'grid.editing' 方式建立插件
    extend: 'Ext.AbstractPlugin',

    mixins: {
        observable: 'Ext.util.Observable'
    },


    // 构造函数
    constructor: function () {
        var me = this;
        me.callParent(arguments);
    },


    // 销毁函数
    destroy: function () {
        this.callParent(arguments);
    },

    // 通过外键加载
    loadByMasterId: function (masterId) {
        var grid = this.getCmp();
        grid.store.proxy.extraParams.masterId = masterId;
        grid.store.load();
    },

    // 主表选择改变的事件
    onMasterSelectChange: function (view, selections, options) {
        var me = this;

        debugger;
        if (selections.length && selections[0].get('id')) {
            var fkey = selections[0].get('id');
            me.newRec[this.fkey] = fkey;
            me.loadByMasterId(fkey);

            me.tbar.down('#buttonAdd').setDisabled(false);
            me.tbar.down('#buttonRefresh').setDisabled(false);
        } else {
            me.tbar.down('#buttonAdd').setDisabled(true);
            me.tbar.down('#buttonRefresh').setDisabled(true);
        }
        debugger;
    },

    // 所属的Grid的选择事件
    onSelectChange: function (view, selections, options) {
        var me = this;
        me.tbar.down('#buttonDelete').setDisabled(!selections.length);
    },

    // 初始化函数
    init: function (client) {
        var me = this;

        if (!me.newRec) {
            me.newRec = {};
        }

        // 建立工具条
        me.tbar = Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    text: '添加',
                    icon: '/icon/toolbar/add.png',
                    itemId: 'buttonAdd',
                    handler: function () {
                        // this.ownerCt.ownerCt.rowEditing.cancelEdit();
                        var editGrid = this.ownerCt.ownerCt;

                        // 如果是子表需要按需把外键进行添加
                        var insertRecord = {};

                        if (editGrid.newRecord != undefined) {
                            insertRecord = editGrid.newRecord;
                        }

                        editGrid.store.insert(0, insertRecord);
                        this.ownerCt.ownerCt.rowEditing.startEdit(0, 0);
                    }
                },

                //工具按钮：删除
                {
                    itemId: 'buttonDelete',
                    text: '删除',
                    icon: '/icon/toolbar/delete.png',
                    handler: function () {
                        Ext.Msg.show({
                            title: '删除警告',
                            msg: '请再次确认删除，删除该数据将删除与之相关联的数据',
                            buttons: Ext.Msg.YESNO,
                            fn: function (rst) {
                                if (rst != 'no') {
                                    var sm = this.ownerCt.ownerCt.getSelectionModel();
                                    this.ownerCt.ownerCt.rowEditing.cancelEdit();
                                    this.ownerCt.ownerCt.store.remove(sm.getSelection());
                                    if (this.ownerCt.ownerCt.store.getCount() > 0) {
                                        sm.select(0);
                                    }
                                }
                            },
                            scope: this,
                            icon: Ext.window.MessageBox.QUESTION
                        });

                    },
                    disabled: true
                },

                //工具按钮：刷新
                {
                    itemId: 'buttonRefresh',
                    text: '刷新',
                    icon: '/icon/toolbar/sync.png',
                    handler: function () {
                        this.ownerCt.ownerCt.store.load();
                    }
                }
            ]
        });
        client.addDocked(me.tbar, 'top');

        // 设置是否自动加载数据
        client.store.autoLoad = me.autoLoad;

        // 添加编辑控件
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            errorSummary: false,

            /**
             * 是否在取消编辑的时候自动删除添加的记录 if true, auto remove phantom record on
             * cancel,default is true.
             *
             * @cfg {Boolean}
             */
            autoRecoverOnCancel: true,

            /**
             * 取消编辑 1.fireEvent 'canceledit' 2.when autoRecoverOnCancel is true,
             * if record is phantom then remove it
             *
             * @private
             * @override
             */
            cancelEdit: function () {
                var me = this;
                if (me.editing) {
                    me.getEditor().cancelEdit();
                    me.editing = false;
                    me.fireEvent('canceledit', me.context);
                    if (me.autoRecoverOnCancel) {
                        me.grid.store.load();
                    }
                }
            },

            saveBtnText: '保存',
            cancelBtnText: '取消',
            errorsText: "<font color='red'>错误信息</font>",
            dirtyText: "已修改,你需要提交或取消变更"
        });
        client.addPlugin(rowEditing);

        //通过 masterGrid 指定的itemId，寻找相应的Grid
        if (me.masterGrid) {
            me.master = Ext.ComponentQuery.query('#' + me.masterGrid)[0];
        }

        // 给主表增加事件
        if (me.master) {
            me.master.on('selectionchange', me.onMasterSelectChange, this);
        }

        // 给自己的Grid添加事件

        me.getCmp().on('selectionchange', me.onSelectChange, this);
    }

})
