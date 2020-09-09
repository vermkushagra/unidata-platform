Ext.define('Unidata.model.mergesettings.MergeSettings', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.mergesettings.Bvt',
        'Unidata.model.mergesettings.Bvr'
    ],

    hasOne: [
        {
            name: 'bvtMergeSettings',
            model: 'mergesettings.Bvt'
        },
        {
            name: 'bvrMergeSettings',
            model: 'mergesettings.Bvr'
        }
    ]
});
