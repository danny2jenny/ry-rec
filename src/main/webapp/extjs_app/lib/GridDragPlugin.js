/**
 * Created by danny on 16-12-21.
 *
 * 用于拖动的扩展
 *
 * 继承至Ext.grid.plugin.DragDrop
 * 两个子对象
 * 1、'Ext.view.DragZone'        拖动
 * 2、'Ext.grid.ViewDropZone'    释放
 *
 * 新增属性：
 * 1 showField 用于显示表格中那个字段用于在拖放时进行显示
 */

Ext.define('app.lib.GridDragPlugin', {
    extend: 'Ext.grid.plugin.DragDrop',
    alias: 'plugin.gridDragPlugin',

    onViewRender: function () {

        var me = this;

        // 初始化父类
        this.callParent(arguments);


        if (this.enableDrop) {
            //释放后的事件
            this.dropZone._handleNodeDrop = this.dropZone.handleNodeDrop;

            /**
             * 调用父控件的事件，所对应的GridPanel的事件。
             * @param data
             * @param targetNode
             * @param position
             */
            this.dropZone.handleNodeDrop = function (data, targetNode, position) {
                //首先进行数据库更新，数据库更新后再进行界面的体现
                this.view.ownerCt.fireEvent('onDragDrop', data, targetNode, position);
            };
        }


        if (this.enableDrag) {
            this.dragZone.getDragText = function () {
                if (this.dragField) {
                    var fieldValue = this.dragData.records[0].get(this.dragField);
                    return Ext.String.format(this.dragText, fieldValue);
                } else {
                    return this.dragData.records[0].data[me.showField];
                }
            }
        }
    }
});