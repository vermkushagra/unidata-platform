Ext.define('Unidata.view.admin.sourcesystems.layout.Layout', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.sourcesystems.layout.LayoutController',
        'Unidata.view.admin.sourcesystems.layout.LayoutModel',

        'Unidata.view.admin.sourcesystems.resultset.Resultset',
        'Unidata.view.admin.sourcesystems.recordshow.Recordshow'
    ],

    alias: 'widget.admin.sourcesystems.layout',

    viewModel: {
        type: 'admin.sourcesystems.layout'
    },
    controller: 'admin.sourcesystems.layout',

    cls: 'animated fadeIn',

    config: {
        draftMode: null
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        }
    ],

    referenceHolder: true,

    resultsetPanel: null,
    recordshowTabPanel: null,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier,
            globalDraftMode;

        this.callParent(arguments);

        this.initComponentReference();

        globalDraftMode = DraftModeNotifier.getDraftMode();
        this.setDraftMode(globalDraftMode);

        DraftModeNotifier.subscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
    },

    initComponentReference: function () {
        var me = this;

        me.resultsetPanel = me.lookupReference('resultsetPanel');
        me.recordshowTabPanel = me.lookupReference('recordshowTabPanel');
    },

    onDestroy: function () {
        var me = this,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        me.resultsetPanel = null;
        me.recordshowTabPanel = null;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);

        me.callParent(arguments);
    },

    onGlobalDraftModeChange: function (draftMode) {
        var items;

        if (this.isDestroyed || this.destroying) {
            return;
        }

        this.setDraftMode(draftMode);

        items = this.recordshowTabPanel.items;

        items.each(function (tab) {
            tab.close();
        });
    },

    items: [
        {
            xtype: 'admin.sourcesystems.resultset',
            reference: 'resultsetPanel',
            listeners: {
                addrecord: 'onAddRecord'
            }
        },
        {
            xtype: 'admin.sourcesystems.recordshow',
            reference: 'recordshowTabPanel',
            flex: 1
        }
    ]
});
