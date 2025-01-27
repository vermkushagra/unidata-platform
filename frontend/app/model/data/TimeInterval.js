Ext.define('Unidata.model.data.TimeInterval', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'dateFrom',
            type: 'date',
            dateFormat: 'c',
            allowNull: true
        },
        {
            name: 'dateTo',
            type: 'date',
            dateFormat: 'c',
            allowNull: true
        }
    ],

    idProperty: 'etalonId',

    hasMany: [
        {
            name: 'contributors',
            model: 'data.Contributor'
        }
    ]
});
