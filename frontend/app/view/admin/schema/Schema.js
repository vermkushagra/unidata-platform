Ext.define('Unidata.view.admin.schema.Schema', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.admin.schema.SchemaController',
        'Unidata.view.admin.schema.SchemaModel'
    ],

    alias: 'widget.admin.schema',

    viewModel: {
        type: 'admin.schema'
    },
    controller: 'admin.schema',

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'diagrammcomponent',
            draftMode: false,
            bind: {
                data: '{entities}',
                relations: '{relations}'
            }
        }
    ],

    listeners: {
        added: 'onComponentAdded'
    }
});
