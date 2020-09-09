/**
 * Структура для удаления связи типа Reference
 *
 * @author Sergey Shishigin
 * @date 2018-06-22
 */
Ext.define('Unidata.model.data.RelationReferenceDelete', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'etalonId',
            type: 'string'
        },
        {
            name: 'originId',
            type: 'string',
            allowNull: true,
            defaultValue: null
        },
        {
            name: 'validFrom',
            type: 'datetimeintervalfrom',
            allowNull: true
        },
        {
            name: 'validTo',
            type: 'datetimeintervalto',
            allowNull: true
        },
        {
            name: 'wipe',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'relName',
            type: 'string'
        }
    ]
});
