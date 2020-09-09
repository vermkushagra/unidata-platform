/**
 * Прокси для поиска процессов
 *
 * @author Ivan Marshalkin
 * @date 2018-05-25
 */

Ext.define('Unidata.proxy.workflow.ProcessSearchProxy', {

    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.workflow.process.search',

    model: 'Unidata.model.workflow.Task',

    actionMethods: {
        create: 'POST',
        read: 'POST',
        update: 'POST',
        destroy: 'POST'
    },

    limitParam: 'count',
    startParam: '',
    pageParam: 'page',
    paramsAsJson: true,

    url: Unidata.Config.getMainUrl() + 'internal/data/workflow/processes',

    extraParams: {
        skipVariables: false,
        processStartAfter: null,               // Период создания workflow
        processStartBefore: null,
        status: null,                          // ALL | COMPLETED | DECLINED | RUNNING
        initiator: null,                       // Инициатор
        variables: null                        // {entityName: 'someName'},
    },

    reader: {
        type: 'json',
        rootProperty: 'content.processes',
        totalProperty: 'content.total_count'
    }

});
