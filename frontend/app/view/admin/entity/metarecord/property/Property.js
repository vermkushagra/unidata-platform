Ext.define('Unidata.view.admin.entity.metarecord.property.Property', {
    extend: 'Ext.container.Container',

    controller: 'admin.entity.metarecord.property',
    viewModel: {
        type: 'admin.entity.metarecord.property'
    },

    requires: [
        'Unidata.view.admin.entity.metarecord.property.PropertyController',
        'Unidata.view.admin.entity.metarecord.property.PropertyModel',

        'Unidata.view.admin.entity.metarecord.property.component.ValidityPeriodPanel',
        'Unidata.view.component.EntityComboBox',
        'Unidata.view.admin.entity.metarecord.component.generationstrategy.GenerationStrategy'
    ],

    alias: 'widget.admin.entity.metarecord.property',

    layout: 'border',
    bodyBorder: false,

    draftMode: null,                                  // режим работы с черновиком

    defaults: {
        collapsible: true,
        split: true
    },

    plugins: [
        {
            ptype: 'managedstoreloader',
            pluginId: 'managedstoreloader',
            storeNames: ['classifierStore'],
            callback: function () {
                this.getController().onNecessaryStoresLoad();
            }
        }
    ],

    listeners: {
        activate: 'onActivate'
    },

    generationStrategyForm: null,

    cls: 'un-admin-metarecord-property',

    referenceHolder: true,

    classifierTagField: null,

    methodMapper: [{
        method: 'buildGenerationStrategyAttributeFilters'
    }],

    initComponent: function () {
        var me = this,
            viewModel,
            attributeFilters;

        this.callParent(arguments);
        this.generationStrategyForm = this.lookupReference('generationStrategyForm');

        this.initComponentReference();

        attributeFilters = this.buildGenerationStrategyAttributeFilters();
        viewModel = this.getViewModel();

        viewModel.bind('{currentRecord}', function (metaRecord) {
            var generationStrategyForm = me.generationStrategyForm;

            if (!metaRecord) {
                return;
            }

            generationStrategyForm.initGenerationStrategy(metaRecord, metaRecord, attributeFilters);
        });

        viewModel.bind('{currentRecord}', function (metaRecord) {
            var classifiers;

            if (!metaRecord) {
                return;
            }

            classifiers = metaRecord.get('classifiers') || [];

            me.classifierTagField.setValue(Ext.Array.clone(classifiers));
        });
    },

    initComponentReference: function () {
        var me = this;

        me.classifierTagField = me.lookupReference('classifierTagField');
    },

    onDestroy: function () {
        var me = this;

        me.classifierTagField = null;

        this.callParent(arguments);
    },

    initItems: function () {
        var viewModel = this.getViewModel(),
            items,
            draftMode = this.draftMode;

        this.callParent(arguments);

        items = [
            {
                height: 180,
                title: Unidata.i18n.t('admin.metamodel>main'),
                region: 'center',
                collapsible: false,
                cls: 'un-admin-metarecord-panel-inner',
                scrollable: 'vertical',
                items: [
                    {
                        xtype: 'form',
                        reference: 'form',
                        border: false,
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        defaults: {
                            labelWidth: 160
                        },
                        items: [
                            {
                                xtype: 'fieldcontainer',
                                layout: 'hbox',
                                fieldLabel: Unidata.i18n.t('glossary:name'),
                                defaultType: 'textfield',
                                defaults: {
                                    listeners: {
                                        blur: 'propertyChange'
                                    }
                                },
                                items: [
                                    {
                                        name: 'name',
                                        reference: 'name',
                                        flex: 2,
                                        emptyText: Unidata.i18n.t('glossary:boolean'),
                                        bind: {
                                            value: '{currentRecord.name}',
                                            readOnly: '{metaRecordNameReadOnly}'
                                        },
                                        modelValidation: true,
                                        msgTarget: 'under'
                                    },
                                    {
                                        name: 'displayName',
                                        reference: 'displayName',
                                        flex: 3,
                                        margin: '0 0 0 6',
                                        emptyText: Unidata.i18n.t('admin.common>displayed'),
                                        bind: {
                                            value: '{currentRecord.displayName}',
                                            readOnly: '{viewReadOnly}'
                                        },
                                        modelValidation: true,
                                        msgTarget: 'under'
                                    }
                                ]
                            },
                            {
                                xtype: 'textareafield',
                                name: 'description',
                                fieldLabel: Unidata.i18n.t('admin.metamodel>entityDescription'),
                                bind: {
                                    value: '{currentRecord.description}',
                                    readOnly: '{viewReadOnly}'
                                },
                                listeners: {
                                    blur: 'propertyChange'
                                }
                            },
                            {
                                xtype: 'displayfield',
                                name: 'entity_type',
                                fieldLabel: Unidata.i18n.t('admin.metamodel>entityType'),
                                bind: {
                                    value: '{currentRecord}'
                                },
                                renderer: function (v) {
                                    if (v instanceof Unidata.model.entity.LookupEntity) {
                                        return Unidata.i18n.t('glossary:lookupEntities');
                                    }

                                    return Unidata.i18n.t('glossary:entities');
                                }
                            },
                            {
                                xtype: 'tagfield',
                                reference: 'classifierTagField',
                                fieldLabel: Unidata.i18n.t('glossary:classifiers'),
                                displayField: 'displayName',
                                valueField: 'name',
                                bind: {
                                    store: '{classifierStore}',
                                    readOnly: '{viewReadOnly}'
                                },
                                listeners: {
                                    change: function (field, value) {
                                        var metaRecord = viewModel.get('currentRecord'),
                                            classifiers = metaRecord.get('classifiers') || [];

                                        // tagfield в setValue делает копию массива физически они разные, но эквивалентны по содержимому
                                        // поэтому нам здесь делать нечего т.к. иначе запись будет помечена как измененная
                                        if (Ext.Array.equals(value, classifiers)) {
                                            return;
                                        }

                                        metaRecord.set('classifiers', Ext.Array.clone(value));
                                    }
                                }
                            },
                            {
                                xtype: 'fieldcontainer',
                                layout: 'hbox',
                                fieldLabel: Unidata.i18n.t('glossary:group'),
                                defaults: {
                                    listeners: {
                                        blur: 'propertyChange'
                                    }
                                },
                                items: [
                                    {
                                        xtype: 'un.entitycombo',
                                        modelValidation: true,
                                        msgTarget: 'under',
                                        name: 'groupName',
                                        reference: 'groupName',
                                        fieldLabel: '',
                                        width: 300,
                                        draftMode: draftMode,
                                        showRoot: true,
                                        catalogMode: true,
                                        hideEmptyGroups: false,
                                        bind: {
                                            value: '{currentRecord.groupName}',
                                            readOnly: '{viewReadOnly}'
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'keyvalue.input',
                                fieldLabel: Unidata.i18n.t('admin.metamodel>customProperties'),
                                name: 'customProperties',
                                listeners: {
                                    change: 'propertyChange'
                                },
                                bind: {
                                    gridStore: '{currentRecord.customProperties}',
                                    readOnly: '{viewReadOnly}'
                                }
                            }
                        ]
                    }
                ]
            },
            {
                flex: 1,
                region: 'south',
                header: false,
                layout: {
                    type: 'hbox',
                    align: 'stretchmax'
                },
                scrollable: 'vertical',
                items: [
                    {
                        xtype: 'panel',
                        title: Unidata.i18n.t('admin.metamodel>advanced'),
                        cls: 'un-admin-metarecord-panel-inner',
                        flex: 1,
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        items: [
                            {
                                xtype: 'form',
                                layout: 'form',
                                items: [
                                    {
                                        xtype: 'checkboxfield',
                                        reference: 'statsForDataOperatorCheckBox',
                                        name: 'checkbox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>displayStatsForDataOperator'),
                                        bind: {
                                            value: '{currentRecord.dashboardVisible}',
                                            readOnly: '{viewReadOnly}'
                                        }
                                    },
                                    {
                                        xtype: 'checkboxfield',
                                        name: 'checkbox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>watchAttributeChanges'),
                                        hidden: true,
                                        disabled: true
                                    },
                                    {
                                        xtype: 'checkboxfield',
                                        name: 'checkbox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>watchRecords'),
                                        hidden: true,
                                        disabled: true
                                    },
                                    {
                                        xtype: 'checkboxfield',
                                        name: 'checkbox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>rangeSupport'),
                                        hidden: true,
                                        disabled: true
                                    },
                                    {
                                        xtype: 'combobox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>rangeCompareGranularity'),
                                        displayField: 'data',
                                        value: Unidata.i18n.t('admin.metamodel>second'),
                                        editable: false,
                                        store: {
                                            type: 'array',
                                            fields: ['data'],
                                            data: [
                                                [Unidata.i18n.t('admin.metamodel>second')],
                                                [Unidata.i18n.t('admin.metamodel>minute')],
                                                [Unidata.i18n.t('admin.metamodel>hour')],
                                                [Unidata.i18n.t('admin.metamodel>day')]
                                            ]
                                        },
                                        hidden: true,
                                        disabled: true
                                    },
                                    {
                                        xtype: 'checkbox',
                                        reference: 'timeIntervalBoundariesCheckBox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>timeIntervalBoundaries'),
                                        displayField: 'validityPeriod',
                                        bind: {
                                            value: '{isValidityPeriod}',
                                            disabled: '{currentRecord.hasData}',
                                            readOnly: '{fieldReadOnlyIfMetaRecordHasData}'
                                        },
                                        listeners: {
                                            render: 'onValidityPeriodCheckboxRender'
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'validityperiodpanel',
                                labelWidths: {
                                    start: 150,
                                    end: 150
                                },
                                fieldLabels: {
                                    start: Unidata.i18n.t('admin.metamodel>periodStart'),
                                    end: Unidata.i18n.t('admin.metamodel>periodEnd')
                                },
                                fieldNames: {
                                    start: 'validityPeriodStart',
                                    end: 'validityPeriodEnd'
                                },
                                binds: {
                                    start: {
                                        value: '{validityPeriodStart}',
                                        disabled: '{!isValidityPeriod}',
                                        readOnly: '{fieldReadOnlyIfMetaRecordHasData}'
                                    },
                                    end: {
                                        value: '{validityPeriodEnd}',
                                        disabled: '{!isValidityPeriod}',
                                        readOnly: '{fieldReadOnlyIfMetaRecordHasData}'
                                    }
                                },
                                globalDateLimits: {
                                    MIN: Unidata.Config.getMinDate(),
                                    MAX: Unidata.Config.getMaxDate()
                                },
                                customConfigs: {
                                    start: {
                                        minValue: Unidata.Config.getMinDate(),
                                        maxValue: Unidata.Config.getMaxDate(),
                                        emptyText: Unidata.Config.getMinDateSymbol()
                                    },
                                    end: {
                                        minValue: Unidata.Config.getMinDate(),
                                        maxValue: Unidata.Config.getMaxDate(),
                                        emptyText: Unidata.Config.getMaxDateSymbol()
                                    }
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'admin.entity.metarecord.component.generationstrategy',
                        flex: 1,
                        title: Unidata.i18n.t('admin.metamodel>generationStrategy.externalIdGeneration'),
                        reference: 'generationStrategyForm',
                        cls: 'un-admin-metarecord-panel-inner',
                        bind: {
                            readOnly: '{viewReadOnly}'
                        }
                    }
                ]
            }
        ];

        this.add(items);
    }
});
