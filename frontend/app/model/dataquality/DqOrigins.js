Ext.define('Unidata.model.dataquality.DqOrigins', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'all',
            type: 'boolean'
        },
        {
            name: 'sourceSystems'
        }
    ],

    manyToMany: {
        sourceSystems: {
            type: 'sourcesystem.SourceSystem',
            role: 'sourceSystems',
            field: 'name',
            right: true
        }
    }

});
