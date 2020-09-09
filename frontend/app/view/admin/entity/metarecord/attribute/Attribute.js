Ext.define('Unidata.view.admin.entity.metarecord.attribute.Attribute', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.metarecord.attribute.AttributeController',
        'Unidata.view.admin.entity.metarecord.attribute.AttributeModel',

        'Unidata.view.component.AttributeTree',
        'Unidata.view.admin.entity.metarecord.attribute.error.ErrPanel',
        'Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategy'
    ],

    alias: 'widget.admin.entity.metarecord.attribute',

    controller: 'admin.entity.metarecord.attribute',
    viewModel: {
        type: 'admin.entity.metarecord.attribute'
    },

    componentCls: 'unidata-admin-metarecord-attribute',

    draftMode: null,                                  // режим работы с черновиком

    isComplexAttributesHidden: false,
    isArrayAttributesHidden: false,
    hiddenAttributeFields: [],
    lookupEntityDisplayAttributesStore: null,
    lookupEntitySearchAttributesStore: null,
    attributePropertyGrid: null,

    config: {
        readOnly: false
    },

    layout: {
        type: 'fit'
    },

    listeners: {
        beforerender: 'onAttributeBeforeRender'
    },

    metaRecordLoadFailedText: null,

    generationStrategyForm: null,
    referenceHolder: true,

    plugins: [
        {
            ptype: 'managedstoreloader',
            pluginId: 'managedstoreloader',
            storeNames: ['lookupEntities', 'entities', 'simpleDataTypes'],
            callback: function () {
                this.getController().onNecessaryStoresLoad();
            }
        }
    ],

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'deleteAttribute'
        },
        {
            method: 'getLookupEntityDisplayAttributesEditor'
        },
        {
            method: 'getLookupEntitySearchAttributesEditor'
        },
        {
            method: 'getAllEditors'
        },
        {
            method: 'buildLookupEntityDefaultValueText'
        },
        {
            method: 'fillLookupEntityDefaultValueText'
        },
        {
            method: 'changeLookupMetaRecord'
        },
        {
            method: 'buildAttributePropertyGridCfg'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'buildGenerationStrategyAttributeFilters'
        }
    ],

    isErrors: false,

    initItems: function () {
        var items;

        this.callParent(arguments);

        items = [
            {
                xtype: 'panel',
                reference: 'attributePanel',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'top',
                    defaults: {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'medium'
                    },
                    items: [
                        '->',
                        {
                            itemId: 'code-attribute-add-button',
                            reference: 'addCodeAttrButton',
                            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:codeAttribute')}),
                            listeners: {
                                click: 'onAddAttributeButtonClick'
                            },
                            bind: {
                                hidden: '{!isLookupEntity}',
                                disabled: '{readOnly}'
                            },
                            iconCls: 'icon-file-charts'
                        },
                        {
                            itemId: 'simple-attribute-add-button',
                            reference: 'addSimpleAttrButton',
                            tooltip: Unidata.i18n.t('admin.metamodel>addSimpleAttribute'),
                            listeners: {
                                click: 'onAddAttributeButtonClick'
                            },
                            bind: {
                                disabled: '{readOnly}'
                            },
                            iconCls: 'icon-file-stats'
                        },
                        {
                            itemId: 'complex-attribute-add-button',
                            reference: 'addComplexAttrButton',
                            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:complexAttribute')}),
                            listeners: {
                                click: 'onAddAttributeButtonClick'
                            },
                            bind: {
                                hidden: '{isComplexAttributesHidden}',
                                disabled: '{readOnly}'
                            },
                            iconCls: 'icon-file-image'
                        },
                        {
                            itemId: 'array-attribute-add-button',
                            reference: 'addArrayAttrButton',
                            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:arrayAttribute')}),
                            listeners: {
                                click: 'onAddAttributeButtonClick'
                            },
                            bind: {
                                hidden: '{isArrayAttributesHidden}',
                                disabled: '{readOnly}'
                            },
                            iconCls: 'icon-file-spreadsheet'
                        },
                        {
                            tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:attribute')}),
                            itemId: 'delete-attribute-button',
                            cls: 'attribute-selection-dependent',
                            disabled: true,
                            bind: {
                                disabled: '{!isCurrentAttributeCanDeleted}'
                            },
                            listeners: {
                                click: 'onDeleteAttributeButtonClick'
                            },
                            iconCls: 'icon-trash2'
                        },
                        {
                            tooltip: Unidata.i18n.t('common:up'),
                            cls: 'attribute-selection-dependent',
                            disabled: true,
                            typeOrderButton: 'up',
                            reference: 'orderAttributeButtonUp',
                            listeners: {
                                click: 'onChangeOrderAttributeButtonClick'
                            },
                            bind: {
                                disabled: '{!isPreviousSiblingExists}'
                            },
                            iconCls: 'icon-exit-up'
                        },
                        {
                            tooltip: Unidata.i18n.t('common:down'),
                            cls: 'attribute-selection-dependent',
                            disabled: true,
                            typeOrderButton: 'down',
                            reference: 'orderAttributeButtonDown',
                            listeners: {
                                click: 'onChangeOrderAttributeButtonClick'
                            },
                            bind: {
                                disabled: '{!isNextSiblingExists}'
                            },
                            iconCls: 'icon-exit-down'
                        }
                    ]
                }],
                items: [
                    {
                        xtype: 'container',
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        flex: 1,
                        items: [
                            {
                                height: 30,
                                padding: '0 0 10 20',
                                xtype: 'un.treesearchcombobox',
                                reference: 'attributeComboBox'
                            },
                            {
                                flex: 1,
                                cls: 'attribute-tree-component',
                                xtype: 'component.attributeTree',
                                reference: 'attributeTreePanel',
                                bind: {
                                    data: {
                                        bindTo: '{currentRecord}',
                                        deep: true
                                    }
                                },
                                listeners: {
                                    beforedeselect: 'onAttributeTreeNodeBeforeDeselect',
                                    select: 'onAttributeTreeNodeSelect',
                                    deselect: 'onAttributeTreeNodeDeselect',
                                    beforerender: 'onAttributeTreeBeforeRender'
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        cls: 'unidata-admin-metarecord-attribute-config-container',
                        flex: 1,
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        margin: '0 8',
                        items: [
                            {
                                xtype: 'container',
                                cls: 'attribute-property-grid-container',
                                reference: 'attributePropertyGridContainer',
                                flex: 1,
                                layout: {
                                    type: 'vbox',
                                    align: 'stretch'
                                },
                                items: [
                                    {
                                        margin: '0 0 10 5',
                                        xtype: 'attribute.errpanel',
                                        reference: 'errorPanel',
                                        errorCollapsed: false,
                                        hidden: true
                                    }
                                ]
                            },
                            {
                                xtype: 'admin.entity.metarecord.component.generationstrategy',
                                title: Unidata.i18n.t('admin.metamodel>generationStrategy.codeAttributeGeneration'),
                                reference: 'generationStrategyForm',
                                cls: 'un-admin-metarecord-panel-inner',
                                bind: {
                                    readOnly: '{readOnly}',
                                    hidden: '{!isCodeAttributeStringSelected}'
                                },
                                hidden: true
                            }
                        ]
                    }
                ]
            }

        ];

        this.add(items);
    },

    initComponent: function () {
        var attributePropertyGridContainer,
            attributePropertyGridCfg,
            viewModel = this.getViewModel(),
            me = this;

        this.callParent(arguments);

        attributePropertyGridContainer = this.lookupReference('attributePropertyGridContainer');

        this.lookupEntityDisplayAttributesStore = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.attribute.AbstractAttribute',
            sorters: [{
                property: 'displayName',
                direction: 'ASC'
            }]
        });
        this.lookupEntitySearchAttributesStore = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.attribute.AbstractAttribute',
            sorters: [{
                property: 'displayName',
                direction: 'ASC'
            }]
        });

        attributePropertyGridCfg = this.buildAttributePropertyGridCfg({
            flex: 1
        });

        this.attributePropertyGrid = attributePropertyGridContainer.add(attributePropertyGridCfg);
        // всегда закрывать выпадающий список displayAttributes при cellclick
        this.attributePropertyGrid.getView().on('beforecellclick', this.onAttributePropertyGridBeforeCellClick, this);
        this.generationStrategyForm = this.lookupReference('generationStrategyForm');

        // выполняем действия после присвоения (загрузки) metaRecord
        viewModel.bind('{currentRecord}', function (metaRecord) {
            var generationStrategyForm = me.generationStrategyForm,
                attributeFilters,
                attribute;

            if (!metaRecord || !Unidata.util.MetaRecord.isLookup(metaRecord)) {
                return;
            }

            attribute = metaRecord.getCodeAttribute();

            if (!attribute) {
                return;
            }

            attributeFilters = me.buildGenerationStrategyAttributeFilters();
            // инициализируем generation strategy
            generationStrategyForm.initGenerationStrategy(metaRecord, attribute, attributeFilters);
        });
    },

    onAttributePropertyGridBeforeCellClick: function () {
        this.completeAllEditors();
    },

    completeAllEditors: function () {
        var editors = this.getAllEditors();

        editors.forEach(function (editor) {
            if (editor.editing) {
                editor.completeEdit();
            }
        });
    }
});
