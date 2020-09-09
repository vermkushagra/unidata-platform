Ext.define('Unidata.model.cleansefunction.thirdparty.CleanseFunctionLoadAction', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'state',
            type: 'string'
        }
    ]
});
