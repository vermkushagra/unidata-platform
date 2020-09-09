Ext.define('Unidata.model.user.SecurityLabelAttributeRole', {
    extend: 'Unidata.model.Base',

    idProperty: 'id',

    /*отрицательные идентификаторы для новых записей*/
    identifier: 'negative',

    fields: [
    {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'value',
            type: 'string'
        },
        {
            name: 'path',
            type: 'string'
        }
    ]
});
