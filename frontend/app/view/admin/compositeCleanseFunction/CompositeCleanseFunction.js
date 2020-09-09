Ext.define('Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionController',
        'Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionModel',

        'Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor'
    ],

    alias: 'widget.admin.compositeCleanseFunction',

    controller: 'admin.compositeCleanseFunction',

    viewModel: {
        type: 'admin.compositeCleanseFunction'
    },

    itemId: 'refCompositeCleanseFunction',

    config: {
        draftMode: null,
        ccfNode: null
    },

    layout: {
        type: 'border',
        align: 'stretch'
    },

    cls: 'un-composite-cleanse-function',

    methodMapper: [
        {
            method: 'createSlider'
        }
    ],

    cfcontainer: null,
    cfFullContainer: null,
    supportedExecutionContextsCheckboxGroup: null,

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.callParent(arguments);

        DraftModeNotifier.subscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
    },

    onDestroy: function () {
        var me                = this,
            DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);

        me.callParent(arguments);
    },

    onGlobalDraftModeChange: function () {
        var controller = this.getController();

        if (this.isDestroyed || this.destroying) {
            return;
        }

        controller.onReturnBackClick();
    },

    listeners: {
        afterrender: 'initInstance'
    },

    /**
     * Рендеринг колонки "Тип системного блока"
     * @param {String} type
     * @return {String}
     */
    typeColumnRenderer: function (type) {
        var CCF_BLOCK_TYPE = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction.CCF_BLOCK_TYPE,
            div,
            clsPrefix = this.cls;

        if (type !== CCF_BLOCK_TYPE.CONSTANT && type !== CCF_BLOCK_TYPE.IFTHENELSE) {
            return null;
        }

        if (type === CCF_BLOCK_TYPE.CONSTANT) {
            div = Ext.String.format('<div class="{0}-icon-{1}" />', clsPrefix, type.toLowerCase());
        } else if (type === CCF_BLOCK_TYPE.IFTHENELSE) {
            div = Ext.String.format('<div class="{0}-icon-{1} un-icon-9" />', clsPrefix, type.toLowerCase());
        }

        return div;
    },

    initItems: function () {
        var ExecutionContextEnumList = Unidata.util.DataQuality.executionContextEnumList,
            CompositeCleanseFunction = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction,
            items,
            cls = this.cls,
            gridCls;

        this.callParent(arguments);
        gridCls = Ext.String.format('{0}-grid-system-block', cls);

        items = [
            {
                width: 270,
                collapsible: true,
                split: true,
                region: 'west',
                title: Unidata.i18n.t('glossary:functions'),
                overflowY: 'auto',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'component.attributeTree',
                        flex: 1,
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
                                containerScroll: true,
                                dragText: Unidata.i18n.t('admin.cleanseFunction>function')
                            },
                            listeners: {
                                nodedragover: function () {
                                    return false;
                                }
                            }
                        },
                        reference: 'functionsGrid'
                    },
                    {
                        xtype: 'grid',
                        height: 200,
                        title: Unidata.i18n.t('admin.cleanseFunction>systemBlocks'),
                        hideHeaders: true,
                        cls: gridCls,
                        viewConfig: {
                            plugins: {
                                ddGroup: 'ccfDragDrop',
                                ptype: 'gridviewdragdrop',
                                enableDrag: true,
                                enableDrop: false
                            }
                        },
                        columns: [
                            {
                                dataIndex: 'type',
                                menuDisabled: true,
                                width: 30,
                                renderer: this.typeColumnRenderer.bind(this)
                            },
                            {
                                dataIndex: 'displayName',
                                menuDisabled: true,
                                flex: 1
                            }
                            ],
                        store: {
                            fields: ['displayName', 'type'],
                            data: [
                                {
                                    type: CompositeCleanseFunction.CCF_BLOCK_TYPE.CONSTANT,
                                    displayName: Unidata.i18n.t('admin.cleanseFunction>constant')
                                },
                                {
                                    type: CompositeCleanseFunction.CCF_BLOCK_TYPE.IFTHENELSE,
                                    displayName: Unidata.i18n.t('admin.cleanseFunction>ifthenelse')
                                }
                            ]
                        }
                    },
                    {
                        xtype: 'panel',
                        title: Unidata.i18n.t('admin.dq>executionContextMode'),
                        height: 130,
                        items: [
                            {
                                xtype: 'checkboxgroup',
                                reference: 'supportedExecutionContextsCheckboxGroup',
                                msgTarget: 'under',
                                allowBlank: false,
                                columns: 1,
                                items: [
                                    {
                                        boxLabel: Unidata.i18n.t('admin.dq>executionContextGlobal'),
                                        name: 'supported_execution_contexts',
                                        inputValue: ExecutionContextEnumList.GLOBAL
                                    },
                                    {
                                        boxLabel: Unidata.i18n.t('admin.dq>executionContextLocal'),
                                        name: 'supported_execution_contexts',
                                        inputValue: ExecutionContextEnumList.LOCAL
                                    }
                                ]
                            }
                        ]
                    }
                ]
            },
            {
                flex: 5,
                cls: 'un-cf-full-container',
                reference: 'cfFullContainer',
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
                        overflowX: 'auto',
                        xtype: 'container',
                        layout: {
                            type: 'hbox',
                            align: 'stretch'
                        },
                        items: [{
                            minHeight: 500,
                            minWidth: 1500,
                            reference: 'cfcontainer',
                            ddGroup: 'ccfDragDrop',
                            cls: 'cf-container',
                            bodyCls: 'cf-body-container',
                            items: [
                                {
                                    xtype: 'button',
                                    cls: 'cf-button-port left icon-plus',
                                    handler: 'onEditInputPortClick'
                                },
                                {
                                    xtype: 'button',
                                    cls: 'cf-button-port right icon-plus',
                                    handler: 'onEditOutputPortClick'
                                }
                            ],
                            listeners: {
                                afterrender: 'onContainerRender',
                                resize: 'onResize'
                            }
                        }]
                    }
                    ]
            }
        ];

        this.add(items);
        this.initReferences();

        this.cfFullContainer.on('render', function (container) {
            var el = document.createElement('div');

            el.setAttribute('class', 'un-composite-cleanse-function-slider-container');
            el = container.getEl().appendChild(new Ext.dom.Element(el));
            this.createSlider(el);
        }, this);
    },

    initReferences: function () {
        this.cfcontainer                             = this.lookupReference('cfcontainer');
        this.cfFullContainer                         = this.lookupReference('cfFullContainer');
        this.supportedExecutionContextsCheckboxGroup = this.lookupReference('supportedExecutionContextsCheckboxGroup');
    },

    statics: {
        CCF_BLOCK_TYPE: {
            INPUT_PORTS: 'INPUT_PORTS',
            OUTPUT_PORTS: 'OUTPUT_PORTS',
            FUNCTION: 'FUNCTION',
            CONSTANT: 'CONSTANT',
            IFTHENELSE: 'IFTHENELSE'
        }
    }
}
);
