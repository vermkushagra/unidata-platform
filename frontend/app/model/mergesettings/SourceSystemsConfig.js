Ext.define('Unidata.model.mergesettings.SourceSystemsConfig', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'weight',
            type: 'int'
        }
    ],

    hasOne: [],

    hasMany: []
});
