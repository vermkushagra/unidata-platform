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
        // Поддержка перечислений временно скрыта на UI, т.к. не поддерживается на BE
        // См. UN-3291 Экран моделирования классификатора должен поддерживать перечисления
        // {
        //     name: 'enumDataType',
        //     type: 'string'
        // },
        {
            name: 'value',
            type: 'simpleattributevalue',
            typeFieldName: 'simpleDataType'
        },
        {
            name: 'typeCategory',
            convert: function (value, rec) {
                var type = '';

                if (rec.get('simpleDataType')) {
                    type = 'simpleDataType';
                } else if (rec.get('lookupEntityType')) {
                    type = 'lookupEntityType';
                    // Поддержка перечислений временно скрыта на UI, т.к. не поддерживается на BE
                    // См. UN-3291 Экран моделирования классификатора должен поддерживать перечисления
                    // } else if (rec.get('enumDataType')) {
                    //     type = 'enumDataType';
                }

                return type;
            },

            depends: ['simpleDataType', 'lookupEntityType'],
            // depends: ['simpleDataType', 'lookupEntityType', 'enumDataType'],
            persist: false
        },
        {
            name: 'typeValue',

            /*здесь необходимо использовать convert вместо calculate т.к. зависимые данные могут изменяться*/
            convert: function (v, rec) {
                return rec.get(rec.get('typeCategory'));
            },

            depends: ['typeCategory', 'simpleDataType', 'lookupEntityType'],
            // depends: ['typeCategory', 'simpleDataType', 'lookupEntityType', 'enumDataType'],

            persist: false
        },
        {
            name: 'lookupEntityType',
            type: 'string'
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
        // Поле order пока не поддерживается backend. Заложили на будущее
        {
            name: 'order',
            defaultValue: 0,
            persist: false
        }
    ],

    /**
     * Проверяет корректность заполнения поля simpleDataType
     *
     * @returns {boolean}
     */
    isValidSimpleDataTypeField: function () {
        var valid = true;

        if (Ext.isEmpty(this.get('typeCategory'))) {
            valid = false;
        }

        if (this.get('typeCategory') === 'simpleDataType' && Ext.isEmpty(this.get('simpleDataType'))) {
            valid = false;
        }

        return valid;
    }
});
