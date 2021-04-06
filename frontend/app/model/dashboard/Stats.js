Ext.define('Unidata.model.dashboard.Stats', {
    extend: 'Ext.data.Model',

    fields: [
        {
            name: 'type',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'series',
            model: 'Unidata.model.dashboard.Series'
        }
    ]
});
