Ext.define('Unidata.model.data.DataRecordKey', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'etalonId',
            type: 'string'
        },
        {
            name: 'etalonDate',
            type: 'datetimeintervalfrom',
            allowNull: true
        }
    ]
});
