/**
 * Created by danny on 16-12-19.
 * 主表和子表的编辑
 * 通用编辑器
 * 初始化需要
 * -------------------------------------------
 * 1 columns 配置对象，显示列和验证条件
 * 2 store 字符串 对应的Store
 * 3 autoload 布尔，是否自动装载
 * *********************
 * 4 itemid 自身的ID （主表设置）
 * 5 master 主表的ID（从表设置）
 * 6 fkey 外键名称（从表设置）
 * ******************************
 * 7 newRecord 需要插入的缺省值
 */

Ext.define('ryrec.lib.MasterSlaveGride', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridEditBase',

    //设置没有数据显示文本
    viewConfig: {
        emptyText: '<div style="text-align:center; padding:50px; color:gray">没有数据可显示</div>',
        deferEmptyText: false
    },

    // 工具条
    tbar: [
        //工具按钮：添加
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

                debugger;
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
                        var ctlType = this.ownerCt.ownerCt.ownerCt.xtype;
                        var temp = this.ownerCt.ownerCt;
                        if (rst != 'no') {
                            var sm = this.ownerCt.ownerCt.getSelectionModel();
                            this.ownerCt.ownerCt.rowEditing.cancelEdit();
                            this.ownerCt.ownerCt.store.remove(sm.getSelection());
                            if (this.ownerCt.ownerCt.store.getCount() > 0) {
                                sm.select(0);
                            }
                        }

                        //级联刷新资源列表
                        if (temp.xtype == 'gridEditRfid') {
                            ctlType = 'gridEditRfid';
                        }

                        switch (ctlType) {
                            case 'gridEditController'://控制器
                            case 'gridEditEncoder'://视频编辑
                            case 'gridEditRfid'://定位点
                                var resGrid = Ext.ComponentQuery.query('resGrid')[0];
                                resGrid.store.load();
                                break;
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
        }],

    /**
     * 当没有选择记录的时候，设置删除按钮不可用
     */
    listeners: {
        'selectionchange': function (view, records) {
            this.down('#buttonDelete').setDisabled(!records.length);
        }
    },

    /**
     * 通过外键装载子表
     * @param resid
     * @param type
     */
    loadByMasterId: function (masterId) {
        this.store.proxy.extraParams.masterId = masterId;
        this.store.load();
    },


    /**
     * 界面初始化的时候调用一次
     * 因为只有界面初始化后，才能确定主表的对象实体
     * @param me
     * @param eOpts
     */
    onAfterMasterRender: function (me, eOpts) {
        // 得到主表
        var master = this.ownerCt.queryById(this.master);
        master.on('selectionchange', this.onMasterSelectChange, this);
        // 子表按钮初始状态
        this.down('#buttonAdd').setDisabled(true);
        this.down('#buttonRefresh').setDisabled(true);
    },

    /**
     * 主表选择变化
     * @param view
     * @param selections
     * @param options
     */
    onMasterSelectChange: function (view, selections, options) {

        /**
         * this 是指子表对象
         * selections 是主表选中的对象
         * 因为传进来的scop任然是子表，这里需要得到主表对象
         */

        var master = this.ownerCt.queryById(this.master);

        // 设置子表添新字段时的初始值，主要是外键
        if ((selections[0]) && (selections[0].data.id != undefined)) {
            this.newRecord[this.fkey] = selections[0].data.id;
        }

        // 判断主表是否有选中记录
        if (selections.length && selections[0].data.id) {
            this.loadByMasterId(selections[0].data.id);
            this.down('#buttonAdd').setDisabled(false);
            this.down('#buttonRefresh').setDisabled(false);
        } else {
            this.store.removeAll();
            this.down('#buttonAdd').setDisabled(true);
            this.down('#buttonRefresh').setDisabled(true);
        }

    },

    /**
     * 控件初始化
     */
    initComponent: function () {

        //保存数据后，自动刷新列表
        /*this.on('edit', function(editor, e) {
         editor.grid.store.load();
         });*/

        // 1 编辑器插件
        this.rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
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

        // 3 添加插件
        this.plugins = [this.rowEditing];

        /**
         * 如果是子表：master不为空
         */
        if (this.master) {
            this.on('afterrender', this.onAfterMasterRender, this);
        }

        // 6 需要新增加的记录对象
        if (!this.newRecord) {
            this.newRecord = {};
        }


        this.callParent(arguments);

        // 拖动
        //this.addEvents('onDragDrop');
    }
});
