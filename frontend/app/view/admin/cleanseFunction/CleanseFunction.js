Ext.define('Unidata.view.admin.cleanseFunction.CleanseFunction', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.cleanseFunction.CleanseFunctionController',
        'Unidata.view.admin.cleanseFunction.CleanseFunctionModel',

        'Unidata.view.admin.cleanseFunction.window.FileUploadWindow'
    ],

    alias: 'widget.admin.cleanseFunction',

    controller: 'admin.cleanseFunction',
    viewModel: {
        type: 'admin.cleanseFunction'
    },

    config: {
        draftMode: null
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        }
    ],

    cls: 'animated fadeIn',

    referenceHolder: true,

    layout: {
        type: 'border',
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
    },

    onDestroy: function () {
        var me = this,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);

        me.callParent(arguments);
    },

    onGlobalDraftModeChange: function (draftMode) {
        if (this.isDestroyed || this.destroying) {
            return;
        }

        this.setDraftMode(draftMode);
    },

    items: [
        {
            xtype: 'panel',
            width: 270,
            collapsible: true,
            collapseDirection: 'left',
            animCollapse: false,
            titleCollapse: true,
            split: true,
            region: 'west',
            title: Unidata.i18n.t('glossary:functions'),
            overflowY: 'auto',
            ui: 'un-search',
            padding: 0,
            bodyPadding: 0,
            tools: [
                {
                    type: 'plus',
                    handler: 'onAddCleanseFunctionButtonClick',
                    tooltip: Unidata.i18n.t('admin.cleanseFunction>addNewFunction'),
                    bind: {
                        hidden: '{!createCleanseFunctionVisible}'
                    }
                }
            ],
            items: [
                {
                    xtype: 'component.attributeTree',
                    bind: {
                        data: '{cleanseGroups}'
                    },
                    listeners: {
                        itemclick: 'onSelectCleanseFunction'
                    },
                    reference: 'functionsGrid',
                    rootVisible: false,
                    ui: 'dark',
                    cls: 'un-cleanse-function-tree'
                }
            ]
        },
        {
            flex: 5,
            region: 'center',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            bind: {
                title: '<b>{currentRecordIcon} {currentRecordPath:htmlEncode}</b>',
                disabled: '{!functionsGrid.selection}'
            },
            disabled: true,
            cls: 'unidata-admin-cleansefunction',
            scrollable: true,

            dockedItems: [
                {
                    xtype: 'toolbar',
                    cls: 'right-toolbar',
                    dock: 'right',
                    width: 45,
                    defaults: {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'medium'
                    },
                    items: [
                        {
                            iconCls: 'icon-trash2',
                            handler: 'onDeleteConfirmClick',
                            bind: {
                                hidden: '{!canDelete}'
                            },
                            tooltip: Unidata.i18n.t('glossary:removeFunction')
                        },
                        {
                            handler: 'onEditCleanseFunctionButtonClick',
                            tooltip: Unidata.i18n.t('admin.cleanseFunction>editFunction'),
                            bind: {
                                hidden: '{!canEdit}'
                            },
                            iconCls: 'icon-pencil'
                        }
                    ]
                }
            ],
            items: [
                {
                    xtype: 'container',
                    width: '100%',
                    cls: 'cl-section cl-section-properties',
                    defaults: {
                        readOnly: true,
                        readOnlyCls: 'readonly-textfield'
                    },
                    bind: {
                        hidden: '{!currentRecord}'
                    },
                    hidden: true,
                    items: [
                        {
                            xtype: 'container',
                            cls: 'description',
                            bind: {
                                html: '{currentRecord.description}'
                            }
                        },
                        {
                            xtype: 'textfield',
                            bind: {
                                value: '{currentRecord.javaClass}',
                                hidden: '{!currentRecord.javaClass}'
                            },
                            fieldLabel: 'Java class'
                        },
                        {
                            xtype: 'textfield',
                            bind: {
                                value: '{currentRecord.createdInfo}',
                                hidden: '{!currentRecord.createdAt}'
                            },
                            fieldLabel: Unidata.i18n.t('common:created')
                        },
                        {
                            xtype: 'textfield',
                            bind: {
                                value: '{currentRecord.updatedInfo}',
                                hidden: '{!currentRecord.updatedAt}'
                            },
                            fieldLabel: Unidata.i18n.t('common:updated')
                        }
                    ],
                    html: '&nbsp;'
                },
                {
                    xtype: 'container',
                    layout: 'hbox',
                    cls: 'cl-section',
                    bind: {
                        hidden: '{!currentRecord.type}'
                    },
                    items: [
                        {
                            flex: 1,
                            items: [
                                {
                                    xtype: 'form',
                                    autoScroll: true,
                                    title: Unidata.i18n.t('admin.common>inputPorts'),
                                    reference: 'inputPorts',
                                    bodyPadding: 10,
                                    labelWidth: 100,
                                    ui: 'un-card',
                                    defaultType: 'textfield',
                                    fieldDefaults: {
                                        labelAlign: 'top'
                                    },
                                    defaults: {
                                        anchor: '100%',
                                        triggerWrapCls: 'input-port'
                                    },
                                    items: []
                                },
                                {
                                    xtype: 'button',
                                    cls: 'execute-button',
                                    text: Unidata.i18n.t('common:execute'),
                                    handler: 'executeFunction'
                                }
                            ]

                        },
                        {
                            title: Unidata.i18n.t('admin.cleanseFunction>executionResult'),
                            ui: 'un-card',
                            flex: 2,
                            margin: '0 0 0 10',
                            items: [
                                {
                                    xtype: 'form',
                                    flex: 1,
                                    autoScroll: true,
                                    reference: 'outputPorts',
                                    bodyPadding: 10,
                                    labelWidth: 100,
                                    defaultType: 'textfield',
                                    fieldDefaults: {
                                        labelAlign: 'top'
                                    },
                                    defaults: {
                                        anchor: '100%',
                                        readOnly: true,
                                        triggerWrapCls: 'output-port'
                                    },
                                    items: []
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
