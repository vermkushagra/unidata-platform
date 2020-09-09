/**
 * Модель array атрибута узла классификатора
 *
 * @author Ivan Marshalkin
 * @date 2018-05-11
 */
Ext.define('Unidata.model.attribute.ClassifierNodeArrayAttribute', {
    extend: 'Unidata.model.attribute.AbstractSimpleAttribute',

    fields: [
        {
            name: 'value',
            type: 'auto',
            // type: 'simpleattributevalue',
            // typeFieldName: 'simpleDataType',
            defaultValue: null,
            allowNull: true
        },
        {
            name: 'arrayDataType',
            type: 'string'
        },
        {
            name: 'typeCategory',
            convert: function (value, record) {
                var type = '';

                if (Boolean(record.get('lookupEntityType'))) {
                    type = 'lookupEntityType';
                } else if (Boolean(record.get('arrayDataType'))) {
                    type = 'arrayDataType';
                }

                return type;
            },

            depends: ['arrayDataType', 'lookupEntityType'],
            persist: false
        },
        {
            name: 'typeValue',

            /*здесь необходимо использовать convert вместо calculate т.к. зависимые данные могут изменяться*/
            convert: function (value, record) {
                return record.get(record.get('typeCategory'));
            },

            depends: ['typeCategory', 'arrayDataType', 'lookupEntityType'],

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
        return this.isValidTypeField('arrayDataType');
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

    isArrayDataType: function () {
        return true;
    }
});
