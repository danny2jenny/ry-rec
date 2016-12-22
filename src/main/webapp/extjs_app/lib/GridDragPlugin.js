/**
 * Created by danny on 16-12-21.
 *
 * 用于拖动的扩展
 */

Ext.define('ryrec.lib.GridDragPlugin', {
    extend : 'Ext.grid.plugin.DragDrop',
    alias : 'plugin.gridDragPlugin',

    onViewRender : function() {
        this.callParent(arguments);

        //释放后的事件
        this.dropZone._handleNodeDrop = this.dropZone.handleNodeDrop;

        this.dropZone.handleNodeDrop = function(data, targetNode, position) {
            //首先进行数据库更新，数据库更新后再进行界面的体现
            this.view.ownerCt.fireEvent('onDragDrop', data, targetNode, position);
        };

    }
});