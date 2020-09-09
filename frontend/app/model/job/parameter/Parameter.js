/**
 * Модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.model.job.parameter.Parameter', {

    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.job.parameter.data.DefaultParameter',
        'Unidata.model.job.parameter.data.EnumParameter'
    ],

    constructor: function (data) {
        switch (data.type) {
            case 'ENUM':
                return new Unidata.model.job.parameter.data.EnumParameter(data);
            default:
                return new Unidata.model.job.parameter.data.DefaultParameter(data);
        }
    }

});
