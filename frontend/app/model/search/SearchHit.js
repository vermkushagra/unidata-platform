Ext.define('Unidata.model.search.SearchHit', {
    extend: 'Unidata.model.Base',

    idProperty: 'id',

    findBy: function (array, field) {
        var data = Ext.Array.findBy(array, function (d) { return d.field === field; });

        return data ? data.value : null;
    },

    fields: [
        {
            name: 'source',
            type: 'auto'
        },
        {
            //for using combobox
            name: 'keyValue',
            convert: function (value, record) {
                return record.findBy(record.data.preview, 'name');
            }
        },
        {
            //for using combobox
            name: 'displayValue',
            convert: function (value, record) {
                return record.findBy(record.data.preview, 'displayName');
            }
        },
        {
            name: 'value',
            convert: function (value, record) {
                return record.findBy(record.data.preview, 'value');
            }
        },
        {
            name: 'searchObject',
            convert: function (value, record) {
                return record.findBy(record.data.preview, '$search_object');
            }
        },
        {
            name: 'etalonId',
            convert: function (value, record) {
                return record.findBy(record.data.preview, '$etalon_id');
            }
        }
    ],

    hasMany: [
        {
            name: 'preview',
            model: 'search.SearchPreview'
        }
    ],

    mapToObject: function () {
        var obj = {};

        this.preview().each(function (item) {
            obj[item.get('field')] = item.get('value');
        });

        return obj;
    },

    mapToObjectValues: function () {
        var obj = {};

        this.preview().each(function (item) {
            obj[item.get('field')] = item.get('values');
        });

        return obj;
    },

    getValueFromObjectMap: function (fieldName) {
        var map = this.mapToObject(),
            result = undefined;

        if (map.hasOwnProperty(fieldName)) {
            result = map[fieldName];
        }

        return result;
    },

    getValuesFromObjectMap: function (fieldName) {
        var map = this.mapToObjectValues(),
            result = undefined;

        if (map.hasOwnProperty(fieldName)) {
            result = map[fieldName];
        }

        return result;
    }
});
