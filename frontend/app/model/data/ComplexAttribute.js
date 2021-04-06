Ext.define('Unidata.model.data.ComplexAttribute', {
    extend: 'Unidata.model.data.AbstractAttribute',

    allDataOptions: {
        writeAllFields: false,
        writeRecordId: false
    },

    hasMany: [
        {
            name: 'nestedRecords',
            model: 'data.NestedRecord'
        }
    ]
});
