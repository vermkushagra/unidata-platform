Ext.define('Unidata.locale.ru.form.field.Date', {
    override: 'Ext.form.field.Date',

    requires: [
        'Ext.locale.ru.form.field.Date'
    ],

    format: 'd.m.Y',
    altFormats : 'md|mdy|mdY|j',

    // не забываем предотвращение XSS
    invalidText: Ext.String.htmlEncode('{0} ' + Unidata.i18n.t('validation:invalidDateFormat') + ' dd.mm.YY'),

    startDay: 1,

    constructor: function () {
        var me = this,
            altFormats = [];

        // строим полное декартово приозведение всевозможных вариантов написания дат для Русских
        Ext.Array.each(['d', 'j'], function (d) {
            Ext.Array.each(['m', 'n'], function (m) {
                Ext.Array.each(['Y', 'y'], function (y) {
                    altFormats.push([d, m, y].join('.'));
                });
            });
        });

        this.altFormats += '|' + altFormats.join('|');

        me.callParent(arguments);
    }
});
