/**
 * Created by danny on 17-1-5.
 */

Ext.define('app.view.admin.panel.FormLayerUpload', {
    extend: 'Ext.form.Panel',
    alias: 'widget.admin.panel.formlayerupload',
    fileUpload: true,
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
    buttons: [{
        text: "Upload",
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
    api: {
        submit: uploadService.gisLayer
    }
})