/**
 * Created by danny on 17-5-9.
 */


Ext.define('app.view.admin.panel.PanoramaUpload', {
    extend: 'Ext.form.Panel',
    alias: 'widget.admin.panel.panoramaupload',
    itemId: 'admin_panel_panoramaupload',
    bodyPadding: '5 5 0',
    fileUpload: true,
    icon: "res/toolbar/upload.png",
    items: [
        {
            xtype: 'textfield',
            fieldLabel: '场景名称：',
            name: 'sceneName'
        },
        {
            xtype: 'filefield',
            buttonOnly: false,
            fieldLabel: '场景文件：',
            name: 'fileUpload',
            buttonText: '选择上传文件...',
        }],
    buttons: [
        {
            text: "新建场景",
            itemId: "btn_new",
            disabled: true,
            handler: function () {

                var form = this.up('form').getForm();
                if (form.isValid()) {
                    form.submit({
                        waitMsg: 'Uploading your files...',
                        // 上传的附加参数
                        params: {
                            scene: 0,
                            device: this.ownerCt.ownerCt.deviceId,
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
            text: "更新场景",
            itemId: "btn_replace",
            disabled: true,
            handler: function () {

                var form = this.up('form').getForm();
                if (form.isValid()) {

                    form.submit({
                        waitMsg: 'Uploading your files...',
                        // 上传的附加参数
                        params: {
                            scene: this.ownerCt.ownerCt.sceneId,
                            device: this.ownerCt.ownerCt.deviceId,
                            replace: true
                        }
                    });
                }
            }
        }],
    api: {
        submit: uploadService.panoramaScene
    },

    // 初始化
    initComponent: function () {
        this.callParent(arguments);

        Ext.direct.Manager.on('event', function (event, provider, eOpts) {

            // 上传图层后，刷新图层列表
            if (event.action == "uploadService" && event.method == "panoramaScene") {
                Ext.getCmp('admin.panel.panoramagrid').editPlugin.reload();
            }
        });
    }
});