/**
 * @author Ivan Marshalkin
 * @date 2016-12-07
 */

Ext.define('Unidata.view.admin.enumeration.EnumerationModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.enumeration',

    data: {
        draftMode: null
    },

    stores: {
        enumerationTreeStore: {
            type: 'tree',
            fields: [
                {
                    name: 'text',
                    type: 'string'
                },
                {
                    name: 'record',
                    type: 'auto'
                }
            ],
            root: {
                id: null
            }
        },
        enumerationStore: {
            type: 'un.enumeration'
        }
    },

    formulas: {}
});
