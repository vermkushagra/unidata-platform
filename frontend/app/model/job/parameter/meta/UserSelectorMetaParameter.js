/**
 * Мета модель параметра операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-12
 */

Ext.define('Unidata.model.job.parameter.meta.UserSelectorMetaParameter', {

    extend: 'Unidata.model.job.parameter.meta.DefaultMetaParameter',

    getType: function () {
        return 'USER_SELECTOR';
    }

});
