/**
 * Аттач к задаче или рабочему процессу
 * @author Aleksandr Bavin
 * @date 2016-08-17
 */
Ext.define('Unidata.model.workflow.Attachment', {

    extend: 'Unidata.model.Base',

    idProperty: 'id',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'username',
            type: 'string'
        },
        {
            name: 'taskId',
            type: 'int'
        },
        {
            name: 'processInstanceId',
            type: 'int'
        },
        {
            name: 'type',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'url',
            type: 'string'
        },
        {
            name: 'dateTime',
            type: 'date'
        }
    ],

    proxy: {
        type: 'workflow.attachments'
    }

});
