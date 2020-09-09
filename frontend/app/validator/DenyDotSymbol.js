/**
 * Валидатор, запрещающий использование символа "точка"
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.validator.DenyDotSymbol', {
    extend: 'Ext.data.validator.Format',

    alias: 'data.validator.denydotsymbol',

    type: 'denydotsymbol',

    config: {
        matcher: /^[^.]*$/i,
        message: Unidata.i18n.t('validation:valueCantBeDot')
    }
});

