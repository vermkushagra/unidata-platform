/**
 * @author Aleksandr Bavin
 * @date 2017-03-29
 */
Ext.define('Unidata.model.table.Cell', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.table.Row',
        'Unidata.model.table.Column'
    ],

    fields: [
        {
            name: 'row',
            type: 'string'
        },
        {
            name: 'column',
            type: 'string'
        },
        {
            name: 'value',
            type: 'string'
        },
        {
            name: 'displayValue',
            type: 'string'
        }
    ]

});
