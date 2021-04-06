Ext.define('Unidata.locale.ru.form.field.Date', {
    override: 'Ext.form.field.Date',

    requires: [
        'Ext.locale.ru.form.field.Date'
    ],

    format: 'd.m.Y',

    // не забываем предотвращение XSS
    invalidText: '{0:htmlEncode} ' + Unidata.i18n.t('validation:invalidDateFormat') + ' {1}',

    startDay: 1
});
