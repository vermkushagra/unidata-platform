/**
 * @author Aleksandr Bavin
 * @date 2017-02-02
 */
Ext.define('Unidata.model.entity.modelimport.SettingsTreeNode', {

    extend: 'Ext.data.TreeModel',

    idProperty: 'tempId',

    fields: [
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'action',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string'
        }
    ]

});
