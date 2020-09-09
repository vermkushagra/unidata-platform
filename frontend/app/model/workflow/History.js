/**
 * История по рабочему процессу
 * @author Aleksandr Bavin
 * @date 2016-08-25
 */
Ext.define('Unidata.model.workflow.History', {

    extend: 'Unidata.model.Base',

    idProperty: 'id',

    fields: [
        {
            name: 'id',
            type: 'int',
            persist: false
        },
        {
            name: 'assignee',
            type: 'string'
        },
        {
            name: 'claimTime',
            type: 'date'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'endTime',
            type: 'date'
        },
        {
            name: 'filename',
            type: 'string'
        },
        {
            name: 'itemType', // COMMENT | ATTACH | WORKFLOW | ..
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'startTime',
            type: 'date'
        },
        {
            name: 'completedBy',
            type: 'string'
        }
    ],

    proxy: {
        type: 'workflow.history'
    }

});
