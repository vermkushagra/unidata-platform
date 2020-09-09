Ext.define('Unidata.locale.ru.grid.PropertyColumnModel', {
    override: 'Ext.grid.PropertyColumnModel',

    requires: [
        'Ext.locale.ru.grid.PropertyColumnModel'
    ],

    dateFormat: 'd.m.Y'
});
