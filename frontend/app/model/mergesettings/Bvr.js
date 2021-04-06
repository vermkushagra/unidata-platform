Ext.define('Unidata.model.mergesettings.Bvr', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.mergesettings.SourceSystemsConfig'
    ],

    hasOne: [],

    hasMany: [
        {
            name: 'sourceSystemsConfig',
            model: 'mergesettings.SourceSystemsConfig'
        }
    ]
});
