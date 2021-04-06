/**
 * Прокси для работы с комментариями процесса
 * @author Aleksandr Bavin
 * @date 2016-08-17
 */
Ext.define('Unidata.proxy.workflow.CommentsProxy', {

    extend: 'Ext.data.proxy.Rest',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.workflow.comments',

    model: 'Unidata.model.workflow.Comment',

    limitParam: '',
    startParam: '',
    pageParam: '',
    paramsAsJson: true,

    baseUrl: Unidata.Config.getMainUrl(),

    url: 'internal/data/workflow/comment',

    api: {
        create:  'internal/data/workflow/comment',
        read:    'internal/data/workflow/comment',
        update:  'internal/data/workflow/comment',
        destroy: 'internal/data/workflow/comment'
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
