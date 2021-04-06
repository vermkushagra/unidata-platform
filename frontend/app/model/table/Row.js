/**
 * @author Aleksandr Bavin
 * @date 2017-03-29
 */
Ext.define('Unidata.model.table.Row', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'cells',
            model: 'table.Cell'
        }
    ]

});
