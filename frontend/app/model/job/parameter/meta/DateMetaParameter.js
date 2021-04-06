/**
 * Мета модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-12
 */

Ext.define('Unidata.model.job.parameter.meta.DateMetaParameter', {

    extend: 'Unidata.model.job.parameter.meta.DefaultMetaParameter',

    statics: {
        FORMAT: 'Y-m-d\\TH:i:sP',
        FORMAT_WITH_MS: 'Y-m-d\\TH:i:s.uP'
    },

    getType: function () {
        return 'DATE';
    }

});
