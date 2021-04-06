/**
 * Мета модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-12
 */

Ext.define('Unidata.model.job.parameter.meta.EnumMetaParameter', {

    extend: 'Unidata.model.job.parameter.meta.DefaultMetaParameter',

    fields: [
        {
            name: 'value',
            type: 'auto'
        }
    ],

    getType: function () {
        return 'ENUM';
    },

    getDataType: function () {
        return this.get('type');
    },

    getValue: function () {
        return this.get('value')[0];
    },

    formatValueTitle: function (value) {

        var dt;

        switch (this.getDataType()) {
            case 'STRING':
            case 'LONG':
            case 'DOUBLE':
                return value;
            case 'BOOLEAN':
                return value ? Unidata.i18n.t('common:yes') : Unidata.i18n.t('common:no');
            case 'DATE':
                dt = Ext.Date.parse(value, Unidata.model.job.parameter.meta.DateMetaParameter.FORMAT);

                if (!dt) {
                    // на бэкенде ошибка с форматом времени-даты, временный фикс
                    dt = Ext.Date.parse(value, Unidata.model.job.parameter.meta.DateMetaParameter.FORMAT_WITH_MS);
                }

                return Ext.Date.format(dt, Unidata.Config.getDateTimeFormat());
        }
    },

    getValues: function () {

        var values = this.get('value'),
            result = [],
            i;

        for (i = 0; i < values.length; i++) {
            result.push({
                value: values[i],
                name: this.formatValueTitle(values[i])
            });
        }

        return result;

    }

});
