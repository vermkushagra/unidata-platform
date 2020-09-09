Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.presentation.attributegroup',

    data: {
        readOnly: false,
        settings: null
    },

    stores: {
        attributes: {
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'displayName',
                    type: 'string'
                }
            ]
        }
    },

    formulas: {}
});
