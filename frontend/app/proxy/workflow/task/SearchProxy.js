/**
 * Прокси для поиска задач
 * @author Aleksandr Bavin
 * @date 14.06.2016
 */
Ext.define('Unidata.proxy.workflow.task.SearchProxy', {

    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.workflow.task.search',

    model: 'Unidata.model.workflow.Task',

    actionMethods: {
        create:  'POST',
        read:    'POST',
        update:  'POST',
        destroy: 'POST'
    },

    limitParam: 'count',
    startParam: '',
    pageParam: 'page',
    paramsAsJson: true,

    url: Unidata.Config.getMainUrl() + 'internal/data/workflow/tasks',

    extraParams: {
        taskId: null,
        historical: false,
        taskCompletedBy: null,      // Выполнивший task
        processStartAfter: null,    // Период создания workflow
        processStartBefore: null,
        taskStartAfter: null,       // Период создания task
        taskStartBefore: null,
        taskEndAfter: null,         // Период завершения task
        taskEndBefore: null,
        initiator: null,            // Инициатор
        approvalState: null,
        candidateUser: null,
        assignedUser: null,
        candidateOrAssignee: null,
        variables: null // {entityName: 'someName'},
    },

    reader: {
        type: 'json',
        rootProperty: 'content.tasks',
        totalProperty: 'content.total_count'
    }

});
