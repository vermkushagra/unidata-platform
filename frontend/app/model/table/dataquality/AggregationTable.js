/**
 * Данные для построения таблицы
 *
 * @author Aleksandr Bavin
 * @date 2017-03-31
 */
Ext.define('Unidata.model.table.dataquality.AggregationTable', {
    extend: 'Unidata.model.table.Table',

    fields: [
        {
            name: 'entityName',
            type: 'string'
        },
        {
            name: 'columnSearchName',
            type: 'string'
        },
        {
            name: 'rowSearchName',
            type: 'string'
        }
    ],

    hasOne: [
        {
            name: 'variables',
            model: 'table.Variables'
        }
    ],

    hasMany: [
        {
            name: 'rows',
            model: 'table.Row'
        },
        {
            name: 'columns',
            model: 'table.Column'
        },
        {
            name: 'cells',
            model: 'table.Cell'
        }
    ]

});
