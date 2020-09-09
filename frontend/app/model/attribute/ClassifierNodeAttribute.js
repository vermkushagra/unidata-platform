/**
 * Модель атрибута узла классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.model.attribute.ClassifierNodeAttribute', {
    extend: 'Unidata.model.attribute.AbstractSimpleAttribute',

    fields: [
        {
            name: 'simpleDataType',
            type: 'string'
        },
        {
            name: 'enumDataType',
            type: 'string'
        },
        {
            name: 'value',
            type: 'simpleattributevalue',
            typeFieldName: 'simpleDataType',
            defaultValue: null,
            allowNull: true
        },
        {
            name: 'typeCategory',
            convert: function (value, record) {
                var type = '';

                if (record.get('simpleDataType')) {
                    type = 'simpleDataType';
                } else if (record.get('lookupEntityType')) {
                    type = 'lookupEntityType';
                } else if (record.get('enumDataType')) {
                    type = 'enumDataType';
                }

                return type;
            },

            depends: ['simpleDataType', 'lookupEntityType', 'enumDataType'],
            persist: false
        },
        {
            name: 'typeValue',

            /*здесь необходимо использовать convert вместо calculate т.к. зависимые данные могут изменяться*/
            convert: function (value, record) {
                return record.get(record.get('typeCategory'));
            },

            depends: ['typeCategory', 'simpleDataType', 'lookupEntityType', 'enumDataType'],

            persist: false
        },
        {
            name: 'lookupEntityType',
            type: 'string'
        },
        {
            name: 'lookupEntityCodeAttributeType',
            type: 'string',
            persist: true
        },
        {
            name: 'hasData',
            type: 'boolean'
        },
        {
            name: 'userAdded',
            defaultValue: false,
            persist: false
        },
        {
            name: 'order',
            defaultValue: 0,
            persist: true
        }
    ],

    hasMany: [
        {
            name: 'customProperties',
            model: 'KeyValuePair',
            storeConfig: {
                /*
                 * Блокируем загрузку store
                 * В случае если backend пришлет null вместо пустого массива ExtJS запрашивает данные
                 * по url с именем модели
                 *
                 * https://unidata.atlassian.net/browse/UN-1062
                 * https://www.sencha.com/forum/showthread.php?302601-Nested-Model-Data-Bind-resulting-in-server-request-for-data
                 */
                load: function () {
                    return;
                }
            }
        }
    ],

    isClassifierNodeAttribute: function () {
        return true;
    },

    /**
     * Проверяет корректность заполнения поля simpleDataType
     *
     * @returns {boolean}
     */
    isValidSimpleDataTypeField: function () {
        return this.isValidTypeField('simpleDataType');
    },

    isValidEnumTypeField: function () {
        return this.isValidTypeField('enumDataType');
    },

    isValidLookupTypeField: function () {
        return this.isValidTypeField('lookupEntityType');
    },

    isValidTypeField: function (typeFieldName) {
        var valid = true;

        if (Ext.isEmpty(this.get('typeCategory'))) {
            valid = false;
        }

        if (this.get('typeCategory') === typeFieldName && Ext.isEmpty(this.get(typeFieldName))) {
            valid = false;
        }

        return valid;
    },

    isDataTypeCategory: function (dataTypeCategory) {
        return this.get('typeCategory') === dataTypeCategory;
    },

    isSimpleDataType: function () {
        return this.isDataTypeCategory('simpleDataType');
    },

    isLookupEntityType: function () {
        return this.isDataTypeCategory('lookupEntityType');
    },

    isEnumDataType: function () {
        return this.isDataTypeCategory('enumDataType');
    }
});
