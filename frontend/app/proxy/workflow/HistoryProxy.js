/**
 * Прокси для работы с историей процесса
 * @author Aleksandr Bavin
 * @date 2016-08-25
 */
Ext.define('Unidata.proxy.workflow.HistoryProxy', {

    extend: 'Ext.data.proxy.Ajax',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.workflow.history',

    model: 'Unidata.model.workflow.Comment',

    limitParam: '',
    startParam: '',
    pageParam: '',
    paramsAsJson: true,

    baseUrl: Unidata.Config.getMainUrl(),

    url: 'internal/data/workflow/history',

    extraParams: {
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
