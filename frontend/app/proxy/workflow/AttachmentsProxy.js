/**
 * Прокси для работы с аттачами процесса
 * @author Aleksandr Bavin
 * @date 2016-08-17
 */
Ext.define('Unidata.proxy.workflow.AttachmentsProxy', {

    extend: 'Ext.data.proxy.Rest',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.workflow.attachments',

    model: 'Unidata.model.workflow.Attachment',

    limitParam: '',
    startParam: '',
    pageParam: '',
    paramsAsJson: true,

    baseUrl: Unidata.Config.getMainUrl(),

    url: 'internal/data/workflow/attach',

    api: {
        create:  'internal/data/workflow/attach',
        read:    'internal/data/workflow/attach',
        update:  'internal/data/workflow/attach',
        destroy: 'internal/data/workflow/attach'
    },

    extraParams: {
        // taskId: null,
        // processInstanceId: null,
        // sortDateAsc: true
    },

    reader: {
        type: 'json',
        rootProperty: 'content'
    },

    writer: {
        type: 'json',
        writeRecordId: false
    }

});
