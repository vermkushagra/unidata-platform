/**
 * Модель записи из набора записей
 *
 * @author Sergey Shishigin
 * @date 2017-02-02
 */
Ext.define('Unidata.model.etaloncluster.EtalonClusterRecord', {
    extend: 'Unidata.model.Base',

    idProperty: 'etalonId',

    fields: [
        {
            name: 'etalonId',
            type: 'string'
        },
        {
            name: 'etalonDate',
            type: 'string',
            allowNull: true
        },
        {
            name: 'displayValue',
            type: 'string'
        }
    ]
});
