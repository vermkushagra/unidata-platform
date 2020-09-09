Ext.define('Unidata.view.admin.entity.resultset.Resultset', {
    extend: 'Ext.panel.Panel',

    controller: 'admin.entity.resultset',
    viewModel: {
        type: 'admin.entity.resultset'
    },

    requires: [
        'Unidata.view.admin.entity.wizard.step.EntityOperationTypeStep',
        'Unidata.view.admin.entity.resultset.ResultsetController',
        'Unidata.view.admin.entity.resultset.ResultsetModel',
        'Unidata.view.admin.entity.metasearch.MetasearchWindow',
        'Unidata.view.component.wizard.Wizard',
        'Unidata.view.component.EntityTree'
    ],

    alias: 'widget.admin.entity.resultset',

    width: 300,
    ui: 'un-search',

    referenceHolder: true,
    reference: 'resultsetPanel',

    entityTree: null,                    // ссылка на компонент дерева

    config: {
        draftMode: false
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        }
    ],

    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    padding: 0,
    bodyPadding: 0,
    collapsible: true,
    collapseDirection: 'left',
    collapseMode: 'header',
    animCollapse: false,
    titleCollapse: true,
    floatable: false,

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.setDraftMode(DraftModeNotifier.getDraftMode());

        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.entityTree = this.lookupReference('resultsetGrid');
    },

    onDestroy: function () {
        var me = this;

        me.entityTree = null;

        me.callParent(arguments);
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: 0,
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            defaults: {
                margin: 10,
                buttonSize: 'medium'
            },
            items: [
                {
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-arrow-up-circle',
                    tooltip: Unidata.i18n.t('admin.metamodel>draftModeAdminPanelTooltip'),
                    listeners: {
                        click: 'onDraftModeAdminPanelButtonClick'
                    },
                    bind: {
                        hidden: '{!draftAdminPanelButtonVisible}'
                    }
                },
                {
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-puzzle',
                    tooltip: Unidata.i18n.t('common:importOrExport'),
                    handler: function () {
                        var wizard,
                            wnd;

                        wizard = Ext.widget({
                            xtype: 'component.wizard',
                            firstStep: {
                                xtype: 'admin.entity.wizard.step.operationtype',
                                title: Unidata.i18n.t('admin.metamodel>selectOperation')
                            }
                        });

                        wnd = Ext.widget({
                            xtype: 'window',
                            title: Unidata.i18n.t('admin.metamodel>importOrExportMetaModel'),
                            width: 500,
                            monitorResize: true,
                            alwaysCentered: true,
                            resizable: false,
                            draggable: false,
                            layout: 'fit',
                            modal: true,
                            items: wizard
                        });

                        wnd.on('beforeclose', function () {
                            if (!wizard || wizard.destroying || wizard.destroyed) {
                                return;
                            }

                            if (wizard.isBlocked()) {
                                return false;
                            }
                        }, this);

                        // ресайзим окно при переключании на шаг(с шага) с настройками
                        wizard.on('tabchange', function (tabPanel, newStep, oldStep) {
                            if (newStep instanceof Unidata.view.admin.entity.wizard.step.modelimport.SettingsStep) {
                                wnd.fullSizeMargin = 100;
                                wnd.onWindowResize();
                            }

                            if (oldStep instanceof Unidata.view.admin.entity.wizard.step.modelimport.SettingsStep) {
                                wnd.fullSizeMargin = null;
                                wnd.setWidth(500);
                                wnd.setHeight(null);
                                wnd.center();
                            }
                        });

                        wizard.on('destroy', function () {
                            if (!wnd.destroyed && !wnd.destroying) {
                                wnd.close();
                            }
                        }, this);

                        wnd.show();
                    },
                    bind: {
                        hidden: '{!importExportButtonVisible}'
                    },
                    securedResource: 'ADMIN_DATA_MANAGEMENT',
                    securedEvent: 'read'
                },
                {
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-register',
                    tooltip: Unidata.i18n.t('admin.metamodel>metaModelProperty'),
                    text: '',
                    listeners: {
                        click: 'onEditCatalogButtonClick'
                    },
                    securedResource: 'ADMIN_DATA_MANAGEMENT',
                    securedEvent: 'read'
                },
                {
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-plus-circle',
                    tooltip: Unidata.i18n.t('admin.metamodel>addEntityOrLookupEntity'),
                    text: '',
                    listeners: {
                        click: 'onAddRecordButtonClick'
                    },
                    bind: {
                        hidden: '{!createButtonVisible}'
                    }
                }
            ]
        }
    ],

    tools: [
        {
            type: 'search',
            reference: 'searchTool',
            handler: 'onSearchButtonClick',
            tooltip: Unidata.i18n.t('common:search')
        }
    ],

    tbar: {
        xtype: 'pagingtoolbar',
        reference: 'pagingToolbar',
        bind: {
            store: '{resultsetStore}',
            hidden: '{!isPagingEnable}'
        },
        cls: 'paging-toolbar',
        displayInfo: false,
        emptyMsg: Unidata.i18n.t('admin.common>noRecords'),
        hideRefreshButton: true
    },
    items: [
        {
            xtype: 'un.entitytree',
            reference: 'resultsetGrid',
            hideHeaders: true,
            focusable: false,
            deferEmptyText: false,
            emptyText: Unidata.i18n.t('admin.metamodel>emptyResultSet'),
            flex: 1,
            catalogMode: false,
            bind: {
                store: '{resultsetStore}'
            },
            ui: 'dark'
        }
    ],

    bind: {
        title: Unidata.i18n.t('glossary:entitiesOrLookupEntities')
    },

    reloadTree: function () {
        var store = this.getViewModel().get('resultsetStore'),
            proxy = store.getProxy();

        proxy.setDraftMode(this.getDraftMode());

        store.reload();
    }
});
