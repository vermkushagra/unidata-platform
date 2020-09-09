/**
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.view.workflow.task.panels.AttachmentsPanelModel', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanelModel',
    alias: 'viewmodel.attachments',

    data: {
        title: Unidata.i18n.t('workflow>attachments'),
        addItemText: Unidata.i18n.t('workflow>addAttachment')
    },

    stores: {
        itemsStore: {
            model: 'Unidata.model.workflow.Attachment',
            proxy: 'workflow.attachments'
        }
    }
});
