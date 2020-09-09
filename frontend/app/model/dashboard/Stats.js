Ext.define('Unidata.model.dashboard.Stats', {
    extend: 'Unidata.model.Base',

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
