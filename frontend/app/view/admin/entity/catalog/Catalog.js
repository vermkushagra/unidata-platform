/**
 * Компонент для управления каталогом реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.view.admin.entity.catalog.Catalog', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.catalog.CatalogController',
        'Unidata.view.admin.entity.catalog.CatalogModel',
        'Ext.tree.Panel',
        'Unidata.view.component.grid.column.FontAwesomeAction',
        'Unidata.view.component.grid.column.TreeColumn'
    ],

    alias: 'widget.admin.entity.catalog',

    controller: 'admin.entity.catalog',

    viewModel: {
        type: 'admin.entity.catalog'
    },

    config: {
        title: Unidata.i18n.t('admin.metamodel>catalogTitle'),
        draftMode: false
    },

    nonRemovable: null,
    referenceHolder: true,
    constraintContainer: null,

    cls: 'un-catalog',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    methodMapper: [
        {
            method: 'reloadCatalogStore'
        },
        {
            method: 'updateDraftMode'
        },
        {
            method: 'onButtonDeleteClick'
        },
        {
            method: 'deleteActionIsDisabled'
        }
    ],

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.setDraftMode(DraftModeNotifier.getDraftMode());

        this.callParent(arguments);

        this.initReferences();

        this.initContainerItems();

        DraftModeNotifier.subscribe(DraftModeNotifier.types.APPLYDRAFT, this.onDraftApply, this);
        DraftModeNotifier.subscribe(DraftModeNotifier.types.REMOVEDRAFT, this.onDraftRemove, this);
    },

    initReferences: function () {
        this.constraintContainer = this.lookupReference('constraintContainer');
    },

    onDestroy: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.constraintContainer = null;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.APPLYDRAFT, this.onDraftApply, this);
        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.REMOVEDRAFT, this.onDraftRemove, this);

        this.callParent(arguments);
    },

    /**
     * Обработчик события опубликования черновика
     */
    onDraftApply: function () {
        var controller = this.getController();

        controller.refreshCatalogData();
    },

    /**
     * Обработчик события удаление черновика
     */
    onDraftRemove: function () {
        var controller = this.getController();

        controller.refreshCatalogData();
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'right',
            cls: 'un-catalog-toolbar',
            border: 1,
            items: [
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'medium',
                    handler: 'onButtonSaveClick',
                    tooltip: Unidata.i18n.t('common:save'),
                    bind: {
                        hidden: '{!savingAllowed}',
                        disabled: '{savingBlocked}'
                    },
                    iconCls: 'icon-floppy-disk'
                }
            ]
        }
    ],

    items: [
        {
            xtype: 'container',
            reference: 'constraintContainer',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            margin: 10,
            maxWidth: 800
        }
    ],

    initContainerItems: function () {
        var me = this,
            store;

        store = this.getViewModel().get('catalogStore');

        this.constraintContainer.add([
            {
                xtype: 'container',
                reference: 'metaModelData',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                padding: '10 0 0 0',
                items: [
                    {
                        xtype: 'textfield',
                        reference: 'metaModelName',
                        margin: '0 0 0 10',
                        fieldLabel: Unidata.i18n.t('admin.metamodel>metaModelName'),
                        labelWidth: 200,
                        listeners: {
                            change: 'onMetaModelNameChange'
                        },
                        bind: {
                            readOnly: '{!metaModelEditAllowed}'
                        }
                    },
                    {
                        xtype: 'displayfield',
                        reference: 'metaModelVersion',
                        margin: '0 0 0 10',
                        fieldLabel: Unidata.i18n.t('admin.metamodel>metaModelVersion'),
                        labelWidth: 200,
                        renderer: function (v) {
                            return v;
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                flex: 1,
                ui: 'un-cardcontainer',
                layout: 'fit',
                items: [
                    {
                        xtype: 'container',
                        layout: 'fit',
                        cls: 'un-overlay',
                        hidden: true,
                        reference: 'needRefreshOverlay',
                        items: [{
                            xtype: 'container',
                            layout: {
                                type: 'hbox',
                                align: 'middle',
                                pack: 'center'
                            },
                            items: [{
                                xtype: 'button',
                                scale: 'small',

                                text: Unidata.i18n.t('common:refresh'),
                                handler: function () {
                                    this.reloadCatalogStore();
                                    this.showRefreshCatalogOverlay(false);
                                },
                                scope: me
                            }]

                        }]
                    },
                    {
                        xtype: 'treepanel',
                        reference: 'catalogTree',
                        rootVisible: false,
                        cls: 'un-catalog-tree',
                        dockedItems: [
                            {
                                xtype: 'toolbar',
                                dock: 'left',
                                items: [
                                    {
                                        xtype: 'button',
                                        ui: 'un-toolbar-admin',
                                        scale: 'medium',
                                        handler: 'onButtonAddClick',
                                        tooltip: Unidata.i18n.t('admin.metamodel>addCatalogItem'),
                                        bind: {
                                            hidden: '{!createAllowed}',
                                            disabled: '{!creationAllowedInSelection}'
                                        },
                                        iconCls: 'icon-plus-circle'
                                    }
                                ]
                            }
                        ],
                        store: store, // биндинг стора глючит почему-то
                        columns: [
                            {
                                xtype: 'un.treecolumn',
                                dataIndex: 'name',
                                width: 200,
                                text: Unidata.i18n.t('glossary:name'),
                                hideable: false,
                                sortable: false,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: false,
                                    modelValidation: true
                                }
                            },
                            {
                                xtype: 'gridcolumn',
                                dataIndex: 'displayName',
                                flex: 1,
                                text: Unidata.i18n.t('glossary:displayName'),
                                hideable: false,
                                sortable: false,
                                editor: {
                                    xtype: 'textfield',
                                    allowBlank: false,
                                    modelValidation: true
                                }
                            },
                            {
                                xtype: 'un.actioncolumn',
                                width: 50,
                                hideable: false,
                                items: [
                                    {
                                        faIcon: 'trash-o',
                                        handler: 'onButtonDeleteClick',
                                        isDisabled: 'deleteActionIsDisabled'
                                    }
                                ],
                                listeners: {
                                    mouseover: function () {
                                        var currentEl = arguments[1],
                                            record = arguments[5],
                                            relatedRecords = this.nonRemovable[record.get('groupName')],
                                            tip = '';

                                        if (relatedRecords && relatedRecords.length > 0) {
                                            tip = Unidata.i18n.t('admin.metamodel>catalogCantBeRemoved');
                                        } else if (record.get('groupName') === 'ROOT') {
                                            tip = Unidata.i18n.t('admin.metamodel>rootCatalogCantBeRemoved');
                                        }

                                        currentEl.title = tip;

                                    },
                                    scope: me
                                }
                            }
                        ],
                        plugins: [
                            {
                                ptype: 'cellediting',
                                pluginId: 'cellediting',
                                clicksToEdit: 1,
                                listeners: {
                                    beforeedit: 'onCellBeforeEdit',
                                    validateedit: 'onCellValidate',
                                    edit: 'onCellEdit'
                                }
                            }
                        ],
                        listeners: {
                            itemcontextmenu: 'onItemContextMenu',
                            select: 'onItemSelect',
                            deselect: 'onItemDeselect'
                        }
                    }

                ]
            }
        ]);
    },

    listeners: {
        show: 'onShowCatalog'
    },

    getDirty: function () {
        var viewModel = this.getViewModel(),
            dirty = false;

        if (viewModel) {
            dirty = viewModel.get('dirtyMetaModelName') || viewModel.get('dirtyCatalog');
        }

        return dirty;
    },

    showRefreshCatalogOverlay: function (flag) {
        var refreshOverlay = this.lookupReference('needRefreshOverlay');

        if (flag) {
            refreshOverlay.show();
        } else {
            refreshOverlay.hide();
        }
    },

    updateNonRemovable: function (resultsetStore) {
        var perentGroup;

        this.nonRemovable = {};

        resultsetStore.each(function (item) {
            if (!item.get('groupName') && item.parentNode && item.parentNode.get('groupName')) {
                perentGroup = item.parentNode.get('groupName');

                if (!this.nonRemovable[perentGroup]) {
                    this.nonRemovable[perentGroup] = [];
                }

                this.nonRemovable[perentGroup].push(item.get('displayName'));
            }
        }, this);

        this.lookupReference('catalogTree').getView().refresh();
    }

});
