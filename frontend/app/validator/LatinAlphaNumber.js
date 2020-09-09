/**
 * Валидатор для идентификаторов записей, разрешает вводить только латинские буквы, цифры, _ и -
 *
 * @author Cyril Sevastyanov
 */
Ext.define('Unidata.validator.LatinAlphaNumber', {
    extend: 'Ext.data.validator.Format',

    alias: 'data.validator.latinalphanumber',

    type: 'latinalphanumber',

    config: {
        matcher: /^[a-z][a-z0-9_-]*$/i,
        message: Unidata.i18n.t('validation:onlyAlphaAndSlashes')
    }
});
