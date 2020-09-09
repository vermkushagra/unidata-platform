/**
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.view.workflow.task.panels.AttachmentsPanel', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanel',

    requires: [
        'Unidata.view.workflow.task.panels.AttachmentsPanelController',
        'Unidata.view.workflow.task.panels.AttachmentsPanelModel'
    ],

    alias: 'widget.workflow.task.panels.attachments',

    viewModel: {
        type: 'attachments'
    },

    controller: 'attachments',

    /**
     * @param {Unidata.model.workflow.Attachment} attachment
     */
    addPanelItem: function (attachment) {
        var dateFormat = Unidata.Config.getDateFormat(),
            titleDateFormat = dateFormat + ' H:i',
            title,
            url;

        title = attachment.get('username') + ' ' + Ext.Date.format(attachment.get('dateTime'), titleDateFormat);

        url = Ext.String.format(
            '{0}internal/data/workflow/attach/{1}/download?token={2}',
            Unidata.Config.getMainUrl(),
            attachment.get('id'),
            Unidata.Config.getToken()
        );

        this.add({
            xtype: 'workflow.task.property',
            title: title,
            value: {
                xtype: 'container',
                data: {
                    url: url,
                    fileName: attachment.get('name')
                },
                tpl: '<a class="un-attach-fileName" href="{url}">{fileName}</a>'
            }
        });
    }

});
