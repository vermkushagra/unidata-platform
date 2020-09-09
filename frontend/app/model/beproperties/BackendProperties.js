Ext.define('Unidata.model.beproperties.BackendProperties', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'tempId',
            type: 'auto',
            persist: false
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'group',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string'
        },
        {
            name: 'value',
            type: 'auto'
        },
        {
            name: 'meta',
            tpe: 'auto'
        }
    ]
});
