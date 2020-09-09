Ext.define('Unidata.model.data.AbstractAttribute', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        }
    ],

    parentPathRecord: null,

    setParentPathRecord: function (parentPathRecord) {
        this.parentPathRecord = parentPathRecord;
    },

    getPath: function () {
        var parentPathRecord = this.parentPathRecord,
            path = this.get('name');

        if (parentPathRecord) {
            path = parentPathRecord.getPath() + '.' + path;
        }

        return path;
    },

    validators: {
        name: [
            {
                type: 'format', matcher: /([A-Za-z]+)[0-9]/i
            }
        ]
    }
});
