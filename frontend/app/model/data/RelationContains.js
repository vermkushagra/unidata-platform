Ext.define('Unidata.model.data.RelationContains', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'createDate',
            type: 'string'
        },
        {
            name: 'createdBy',
            type: 'string'
        },
        {
            name: 'etalonId',
            type: 'string'
        },
        {
            name: 'relName',
            type: 'string'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'updateDate',
            type: 'string'
        },
        {
            name: 'updatedBy',
            type: 'string'
        }
    ],

    hasOne: [{
        name: 'etalonRecord',
        model: 'data.Record'
    }],

    proxy: {
        type: 'data.relationproxy',
        actionMethods: {
            create: 'POST',
            read: 'GET',
            update: 'POST',
            destroy: 'DELETE'
        }
    }
});
