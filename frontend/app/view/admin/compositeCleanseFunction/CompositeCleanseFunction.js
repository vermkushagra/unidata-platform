Ext.define('Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionController',
        'Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionModel'
    ],

    alias: 'widget.admin.compositeCleanseFunction',

    controller: 'admin.compositeCleanseFunction',

    viewModel: {
        type: 'admin.compositeCleanseFunction'
    },

    itemId: 'refCompositeCleanseFunction',

    config: {
        draftMode: null
    },

    layout: {
        type: 'border',
        align: 'stretch'
    },

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.callParent(arguments);

        DraftModeNotifier.subscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
    },

    onDestroy: function () {
        var me = this,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);

        me.callParent(arguments);
    },

    onGlobalDraftModeChange: function (draftMode) {
        var controller = this.getController();

        if (this.isDestroyed || this.destroying) {
            return;
        }

        controller.onReturnBackClick();
    },

    listeners: {
        afterrender: 'initInstance'
    },

    items: [
        {
            width: 270,
            collapsible: true,
            split: true,
            region: 'west',
            title: Unidata.i18n.t('glossary:functions'),
            overflowY: 'auto',
            items: [
                {
                    xtype: 'component.attributeTree',
                    bind: {
                        data: '{cleanseGroups}'
                    },
                    ddGroup: 'ccfDragDrop',
                    viewConfig: {
                        copy: true,
                        plugins: {
                            ptype: 'treeviewdragdrop',
                            ddGroup: 'ccfDragDrop',
                            appendOnly: true,
                            sortOnDrop: false,
                            containerScroll: true
                        },
                        listeners: {
                            nodedragover: function () {
                                return false;
                            }
                        }
                    },
                    reference: 'functionsGrid'
                }
            ]
        },
        {
            flex: 5,
            rbar: [
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-floppy-disk',
                    text: '',
                    handler: 'onSaveClick',
                    tooltip: Unidata.i18n.t('common:save')
                },
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-reply',
                    text: '',
                    handler: 'onReturnBackClick',
                    tooltip: Unidata.i18n.t('admin.cleanseFunction>back')
                }
            ],
            region: 'center',

            layout: {
                type: 'border',
                align: 'stretch'
            },
            defaults: {
                collapsible: true,
                split: true
            },
            items: [
                {
                    flex: 2,
                    collapsible: false,
                    region: 'center',
                    overflowY: 'auto',
                    xtype: 'container',
                    layout: {
                        type: 'fit',
                        align: 'stretch'
                    },
                    items: [{
                        minHeight: 500,
                        reference: 'cfcontainer',
                        ddGroup: 'ccfDragDrop',
                        bodyCls: 'cf-body-container',
                        items: [
                            {
                                xtype: 'button',
                                cls: 'cf-button-port left',
                                glyph: 'xf067@FontAwesome',
                                handler: 'onEditInputPortClick'
                            },
                            {
                                xtype: 'button',
                                cls: 'cf-button-port right',
                                glyph: 'xf067@FontAwesome',
                                handler: 'onEditOutputPortClick'
                            }
                        ],
                        listeners: {
                            afterrender: 'onContainerRender',
                            resize: 'onResize'
                        }
                    }]
                },
                {
                    title: Unidata.i18n.t('common:execute'),
                    region: 'south',
                    flex: 1,
                    overflowY: 'auto',
                    items: [
                        {
                            flex: 5,
                            layout: {
                                type: 'hbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    flex: 1,
                                    items: [
                                        {
                                            xtype: 'form',
                                            title: Unidata.i18n.t('admin.common>inputPorts'),
                                            reference: 'inputPorts',
                                            bodyPadding: 10,
                                            labelWidth: 100,
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
                                            text: Unidata.i18n.t('common:execute'),
                                            handler: 'executeFunction',
                                            margin: 10
                                        }
                                    ]

                                },
                                {
                                    title: Unidata.i18n.t('admin.cleanseFunction>executionResult'),
                                    flex: 2,
                                    items: [
                                        {
                                            xtype: 'form',
                                            flex: 1,
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
                }]
        }
    ]
});
