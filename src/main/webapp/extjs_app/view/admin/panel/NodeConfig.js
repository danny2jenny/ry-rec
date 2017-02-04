/**
 * Created by danny on 17-2-3.
 * Node 的参数配置
 * 生成一个json存放在Node表的opt字段
 */


Ext.define('app.view.admin.panel.NodeConfig', {
    extend: 'Ext.form.Panel',
    alias: 'widget.admin.panel.nodeconfig',
    itemId: 'admin_panel_nodeconfig',
    title: '节点参数配置',
    icon: 'icon/toolbar/node.png',
    hidden: true,


    bodyPadding: '5 5 0',
    //layout: 'form',
    //frame: true,
    fileUpload: true,
    items: [
        {
            xtype: 'numberfield',
            fieldLabel: '灵敏度：',
            name: 'sensitive'
        },
        {
            xtype: 'numberfield',
            fieldLabel: '线性参数A',
            name: 'pA',
        }, {
            xtype: 'numberfield',
            fieldLabel: '线性参数B',
            name: 'pB',
        }],
    buttons: [{
        text: "更新",
        handler: function () {

            var form = this.up('form').getForm();
            if (form.isValid()) {
                form.submit({
                    waitMsg: 'Uploading your files...',
                    success: function (form, action) {
                        debugger;
                        //textArea1.setValue(action.result.fileContents);
                    }
                });
            }

        }
    }],
    // api: {
    //     submit: uploadService.gisLayer
    // }

});