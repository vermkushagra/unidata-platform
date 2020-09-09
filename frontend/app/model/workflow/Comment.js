/**
 * Комментарий к задаче или рабочему процессу
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.model.workflow.Comment', {

    extend: 'Unidata.model.Base',

    idProperty: 'id',

    fields: [
        {
            name: 'id',
            type: 'int',
            persist: false
        },
        {
            name: 'username',
            type: 'string',
            persist: false
        },
        {
            name: 'taskId',
            type: 'string'
        },
        {
            name: 'processInstanceId',
            type: 'string'
        },
        {
            name: 'type', // comment
            type: 'string',
            persist: false
        },
        {
            name: 'message',
            type: 'string'
        },
        {
            name: 'dateTime',
            type: 'date',
            persist: false
        }
    ],

    proxy: {
        type: 'workflow.comments'
    }

});
