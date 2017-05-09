/**
 * Created by danny on 17-1-5.
 *
 * 图片上传，GIS 和 全景
 *
 */

Ext.define('app.view.admin.panel.FormLayerUpload', {
    extend: 'Ext.form.Panel',
    alias: 'widget.admin.panel.formlayerupload',
    itemId: 'formLayerUpload',
    bodyPadding: '5 5 0',
    //layout: 'form',
    //frame: true,
    fileUpload: true,
    icon: "res/toolbar/upload.png",
    items: [
        {
            xtype: 'textfield',
            fieldLabel: '图层名称',
            name: 'layerName'
        },
        {
            xtype: 'filefield',
            buttonOnly: false,
            fieldLabel: '图层文件：',
            name: 'fileUpload',
            buttonText: '选择上传文件...',
        }],
    buttons: [
        {
            text: "新建图层",

            handler: function () {

                var form = this.up('form').getForm();
                if (form.isValid()) {
                    form.submit({
                        waitMsg: 'Uploading your files...',
                        // 上传的附加参数，表明GIS还是全景，新增加还是替换
                        params: {
                            layer: 0,
                            replace: false
                        },
                        success: function (form, action) {
                            // 不能在这个地方刷新，时序不对
                            //Ext.getCmp('admin.panel.gislayer').editPlugin.reload();
                        }
                    });
                }

            }
        }, {
            text: "更新图层",
            itemId: "btn_replace",
            disabled: true,
            handler: function () {

                var form = this.up('form').getForm();
                if (form.isValid()) {

                    form.submit({
                        waitMsg: 'Uploading your files...',
                        // 上传的附加参数，表明GIS还是全景，新增加还是替换
                        params: {
                            layer: this.ownerCt.ownerCt.layerId,
                            replace: true
                        }
                    });
                }

            }
        }],
    api: {
        submit: uploadService.gisLayer
    },

    // 初始化
    initComponent: function () {
        this.callParent(arguments);

        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            // 上传图层后，刷新图层列表
            if (event.action == "uploadService" && event.method == "gisLayer") {
                Ext.getCmp('admin.panel.gislayer').editPlugin.reload();
            }
        });
    }
});