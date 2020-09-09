Ext.define('Unidata.model.data.NestedRecord', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'tempId',
            type: 'auto',
            persist: false
        },
        {
            name: 'index', // порядковый номер нестеда в сторе
            type: 'number',
            persist: false
        }
    ],

    hasMany: [
        {
            name: 'simpleAttributes',
            model: 'data.SimpleAttribute'
        },
        {
            name: 'complexAttributes',
            model: 'data.ComplexAttribute'
        },
        {
            name: 'arrayAttributes',
            model: 'data.ArrayAttribute'
        }
    ],

    parentPathRecord: null,

    setParentPathRecord: function (parentPathRecord) {
        this.parentPathRecord = parentPathRecord;
    },

    getPath: function () {
        var parentPathRecord = this.parentPathRecord,
            index = this.get('index'),
            path = '';

        if (parentPathRecord) {
            path = parentPathRecord.getPath() + '[' + index + ']';
        }

        return path;
    },

    constructor: function () {
        this.callParent(arguments);

        this.initNestedAttributePaths();
    },

    initNestedAttributePaths: function () {
        var simpleAttributesStore = this.simpleAttributes(),
            complexAttributesStore = this.complexAttributes(),
            arrayAttributesStore = this.arrayAttributes();

        simpleAttributesStore.on('add', this.onAttributesAdd, this);
        complexAttributesStore.on('add', this.onAttributesAdd, this);
        arrayAttributesStore.on('add', this.onAttributesAdd, this);
    },

    onAttributesAdd: function (store, attributes) {
        this.updateParentRecord(attributes);
    },

    updateParentRecord: function (attributes) {
        Ext.Array.each(attributes, function (attribute) {
            attribute.setParentPathRecord(this);
        }, this);
    }

});
