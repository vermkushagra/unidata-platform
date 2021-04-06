Ext.define('Unidata.locale.ru.picker.Date', {
    override: 'Ext.picker.Date',

    requires: [
        'Ext.locale.ru.picker.Date'
    ],

    format: 'd.m.Y',

    startDay: 1,

    todayTip: '{0}'
});
