Ext.define('Unidata.model.mergesettings.Attribute', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.mergesettings.SourceSystemsConfig'
    ],

    fields: [
        {
            name: 'name',
            type: 'string'
        }
    ],

    hasOne: [],

    hasMany: [
        {
            name: 'sourceSystemsConfig',
            model: 'mergesettings.SourceSystemsConfig'
        }
    ]
});
