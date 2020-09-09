Ext.define('Unidata.view.admin.entity.metarecord.model.Model', {
    extend: 'Ext.container.Container',

    controller: 'admin.entity.metarecord.model',
    viewModel: {
        type: 'admin.entity.metarecord.model'
    },

    requires: [
        'Unidata.view.admin.entity.metarecord.model.ModelController',
        'Unidata.view.admin.entity.metarecord.model.ModelModel'
    ],

    alias: 'widget.admin.entity.metarecord.model',
    layout: {
        type: 'fit',
        align: 'stretch'
    },

    draftMode: null,                                  // режим работы с черновиком

    initItems: function () {
        var draftMode = this.draftMode;

        this.callParent(arguments);

        this.diagram = this.add({
            xtype: 'diagrammcomponent',
            draftMode: draftMode,
            showNested: true,
            bind: {
                data: '{entities}',
                relations: '{relations}'
            }
        });
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.diagram = null;
    }

});
