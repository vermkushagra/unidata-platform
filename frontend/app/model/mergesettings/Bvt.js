Ext.define('Unidata.model.mergesettings.Bvt', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.mergesettings.Attribute'
    ],

    hasOne: [],

    hasMany: [
        {
            name: 'attributes',
            model: 'mergesettings.Attribute'
        }
    ]
});
