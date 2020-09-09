Ext.define('Unidata.model.cleansefunction.Group', {
    extend: 'Unidata.model.ExtendedBase',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'functions',
            model: 'cleansefunction.CleanseFunction'
        },
        {
            name: 'groups',
            model: 'cleansefunction.Group'
        }
    ]
});
