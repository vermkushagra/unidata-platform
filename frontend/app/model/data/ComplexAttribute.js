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
    ],

    constructor: function () {
        this.callParent(arguments);

        this.initAttributePaths();
    },

    initAttributePaths: function () {
        var nestedRecordsStore = this.nestedRecords();

        nestedRecordsStore.on('add', function (store, records) {
            Ext.Array.each(records, function (record) {
                record.setParentPathRecord(this);
            }, this);

            this.updateNestedIndexes();
        }, this);
    },

    updateNestedIndexes: function () {
        var nestedRecordsStore = this.nestedRecords();

        nestedRecordsStore.each(function (record, index) {
            record.set('index', index);
        });
    }

});
