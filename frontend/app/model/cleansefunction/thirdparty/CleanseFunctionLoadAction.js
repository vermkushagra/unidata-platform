Ext.define('Unidata.model.cleansefunction.thirdparty.CleanseFunctionLoadStatus', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'action',
            type: 'string'
        }
    ]
});
