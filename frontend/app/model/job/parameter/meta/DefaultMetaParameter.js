/**
 * Мета модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-12
 */

Ext.define('Unidata.model.job.parameter.meta.DefaultMetaParameter', {

    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'id',
            type: 'auto'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'value',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string'
        }
    ],

    getName: function () {
        return this.get('name');
    },

    getValue: function () {
        return this.get('value');
    },

    getDataType: function () {
        return this.get('type');
    },

    getType: function () {
        return this.get('type');
    }

});
