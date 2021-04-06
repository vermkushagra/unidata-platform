/**
 * Модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.model.job.parameter.data.DefaultParameter', {

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

    getType: function () {
        return this.getMeta().getType();
    },

    setValue: function (value) {
        this.set('value', value);
    },

    setMeta: function (meta) {
        this.meta = meta;
        this.set('type', meta.getDataType());
    },

    /**
     * @returns {Unidata.model.job.parameter.meta.DefaultMetaParameter}
     */
    getMeta: function () {
        return this.meta;
    },

    getData: function () {
        var result = this.callParent(arguments);

        if (result.id < 0) {
            delete result.id;
        }

        return result;
    }

});
