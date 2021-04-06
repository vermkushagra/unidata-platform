Ext.define('Unidata.model.notification.Notification', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'binaryDataId',
            type: 'string'
        },
        {
            name: 'characterDataId',
            type: 'string'
        },
        {
            name: 'content',
            type: 'string'
        },
        {
            name: 'createDate',
            type: 'date',
            dateFormat: 'c'
        },
        {
            name: 'createdBy',
            type: 'string'
        },
        {
            name: 'id',
            type: 'string'
        },

        {
            name: 'type',
            type: 'string'
        }
    ]
});
