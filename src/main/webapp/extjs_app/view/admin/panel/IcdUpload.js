/**
 * Created by danny on 17-5-9.
 */


Ext.define('app.view.admin.panel.IcdUpload', {
    extend: 'Ext.form.Panel',
    alias: 'widget.admin.panel.icdupload',
    bodyPadding: '5 5 0',
    fileUpload: true,
    icon: "res/toolbar/upload.png",
    items: [
        {
            xtype: 'textfield',
            fieldLabel: 'Ied 名称',
            name: 'iedName'
        },
        {
            xtype: 'textfield',
            fieldLabel: 'AP 名称',
            name: 'apName'
        },
        {
            xtype: 'filefield',
            buttonOnly: false,
            fieldLabel: '场景文件：',
            name: 'icdFile',
            buttonText: '选择上传文件...',
        }],
    buttons: [
        {
            text: "上传并生成配置文件",
            itemId: "btn_new",
            handler: function () {

                var form = this.up('form').getForm();
                if (form.isValid()) {
                    form.submit({
                        waitMsg: 'Uploading your files...',
                        success: function (form, action) {
                        }
                    });
                }

            }
        }],
    api: {
        submit: uploadService.iec61850icd
    },

    // 初始化
    initComponent: function () {
        this.callParent(arguments);
    }
});