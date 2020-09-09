Ext.define('Unidata.model.user.SecuredResource', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'isSystemResource',
            type: 'boolean',
            calculate: function (value) {
                var result         = false,
                    systemResource = [
                        'ADMIN_CLASSIFIER_MANAGEMENT',
                        'ADMIN_SYSTEM_MANAGEMENT',
                        'ADMIN_DATA_MANAGEMENT',
                        'BULK_OPERATIONS_OPERATOR',
                        'ADMIN_MATCHING_MANAGEMENT'
                    ];

                if (Ext.Array.contains(systemResource, value['name'])) {
                    result = true;
                }

                return result;
            },
            persist: true,
            depends: ['name']
        },
        {
            name: 'isSystemManagement',
            type: 'boolean',
            calculate: function (value) {
                return value['name'] === 'ADMIN_SYSTEM_MANAGEMENT';
            },
            persist: true,
            depends: ['name']
        },
        {
            name: 'isDataManagement',
            type: 'boolean',
            calculate: function (value) {
                return value['name'] === 'ADMIN_DATA_MANAGEMENT';
            },
            persist: true,
            depends: ['name']
        },
        {
            name: 'isBulkOperator',
            type: 'boolean',
            calculate: function (value) {
                return value['name'] === 'BULK_OPERATIONS_OPERATOR';
            },
            persist: true,
            depends: ['name']
        }
    ],

    proxy: {
        writer: {
            type: 'json',
            writeRecordId: false
        }
    }
});
