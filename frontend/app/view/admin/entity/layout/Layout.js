Ext.define('Unidata.view.admin.entity.layout.Layout', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.layout.LayoutController',
        'Unidata.view.admin.entity.layout.LayoutModel',

        'Unidata.view.admin.entity.resultset.Resultset',
        'Unidata.view.admin.entity.recordshow.Recordshow',

        'Unidata.view.admin.entity.catalog.Catalog'
    ],

    alias: 'widget.admin.entity.layout',

    viewModel: {
        type: 'admin.entity.layout'
    },
    controller: 'admin.entity.layout',

    config: {
        draftMode: false
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        }
    ],

    cls: 'animated fadeIn',

    referenceHolder: true,

    resultsetPanel: null,
    resultsetStore: null,
    recordshowTabPanel: null,
    catalogEditor: null,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.setDraftMode(DraftModeNotifier.getDraftMode());

        this.callParent(arguments);

        this.initComponentReference();

        this.setDraftMode(DraftModeNotifier.getDraftMode());
        this.resultsetPanel.setDraftMode(DraftModeNotifier.getDraftMode());

        this.resultsetStore = this.resultsetPanel.getViewModel().getStore('resultsetStore');

        this.resultsetStore.on('datachanged', this.refreshCatalog, this);

        DraftModeNotifier.subscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
    },

    initComponentReference: function () {
        var me = this;

        me.resultsetPanel = this.lookupReference('resultsetPanel');
        me.recordshowTabPanel = this.lookupReference('recordshowTabPanel');
        me.catalogEditor = this.lookupReference('catalogEditor');
    },

    onDestroy: function () {
        var me = this,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        me.resultsetPanel = null;
        me.recordshowTabPanel = null;
        me.catalogEditor = null;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);

        me.callParent(arguments);
    },

    onGlobalDraftModeChange: function () {
        var view = this,
            controller = this.getController();

        if (view.isDestroyed || view.destroying) {
            return;
        }

        return controller.draftModeChangeHandler.apply(controller, arguments);
    },

    items: [
        {
            xtype: 'admin.entity.resultset',
            reference: 'resultsetPanel',
            listeners: {
                addrecord: 'onAddRecord',
                showCatalogEditor: 'onShowCatalogEditor',
                modeluploadsuccess: 'onModeluploadsuccess',
                applydraft: 'onApplyDraft',
                removedraft: 'onRemoveDraft'
            }
        },
        {
            flex: 1,
            reference: 'recordshowTabPanel',
            xtype: 'admin.entity.recordshow'
        },
        {
            xtype: 'admin.entity.catalog',
            reference: 'catalogEditor',
            flex: 1,
            hidden: true,
            listeners: {
                needReloadCatalog: 'onNeedReloadCatalog'
            }
        }
    ],

    refreshCatalog: function () {
        this.catalogEditor.updateNonRemovable(this.resultsetStore);
    }
});
