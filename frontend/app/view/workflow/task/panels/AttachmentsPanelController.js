/**
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.view.workflow.task.panels.AttachmentsPanelController', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanelController',
    alias: 'controller.attachments',

    onAddItemClick: function (button) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        Ext.widget({
            xtype: 'form.window',
            title: viewModel.get('addItemText'),
            animateTarget: button,
            formParams: {
                method: 'POST',
                url: Unidata.Config.getMainUrl() + 'internal/data/workflow/attach',
                items: [
                    {
                        xtype: 'fileuploadfield',
                        msgTarget: 'under',
                        allowBlank: false,
                        maxSize: 60,
                        name: 'file'
                    }
                ],
                baseParams: {
                    processInstanceId: view.getProcessId()
                }
            },
            listeners: {
                submitstart: function () {
                    view.setLoading(true);
                },
                submitend: function (form, success) {
                    if (!success) {
                        view.setLoading(false);
                    } else {
                        view.updateProcessId(view.getProcessId());
                        view.expand();
                    }
                }
            }
        }).show();
    }

});
