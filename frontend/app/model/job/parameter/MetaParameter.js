/**
 * Мета модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-12
 */

Ext.define('Unidata.model.job.parameter.MetaParameter', {

    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.job.parameter.meta.DefaultMetaParameter',
        'Unidata.model.job.parameter.meta.DateMetaParameter',
        'Unidata.model.job.parameter.meta.UserSelectorMetaParameter',
        'Unidata.model.job.parameter.meta.EnumMetaParameter'
    ],

    constructor: function (data) {

        // костыль для UN-6788
        if (data.name && data.name === 'usersSelector') {
            return new Unidata.model.job.parameter.meta.UserSelectorMetaParameter(data);
        }

        if (Ext.isArray(data.value)) {
            return new Unidata.model.job.parameter.meta.EnumMetaParameter(data);
        }

        switch (data.type) {
            case 'DATE':
                return new Unidata.model.job.parameter.meta.DateMetaParameter(data);
            default:
                return new Unidata.model.job.parameter.meta.DefaultMetaParameter(data);
        }
    }

});
