Ext.define('Unidata.view.admin.entity.metarecord.dataquality.DataQuality', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.admin.entity.metarecord.dataquality.DataQualityController',
        'Unidata.view.admin.entity.metarecord.dataquality.DataQualityModel'
    ],

    alias: 'widget.admin.entity.metarecord.dataquality',

    controller: 'admin.entity.metarecord.dataquality',

    viewModel: {
        type: 'admin.entity.metarecord.dataquality'
    },

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    draftMode: null,                                  // режим работы с черновиком

    cls: 'unidata-admin-data-quality',
    scrollable: 'vertical',
    methodMapper: [
        {
            method: 'getConstantPortComponentByName'
        },
        {
            method: 'getPortComponentByName'
        },
        {
            method: 'isCurrentDqRule'
        },
        {
            method: 'checkDataQualityValid'
        }
    ],

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

    listeners: {
        activate: 'onDataQualityViewActivate',
        beforerender: 'onDataQualityBeforeRender'
    },

    items: [
        {
            xtype: 'container',
            cls: 'data-quality-container',
            height: 300,
            layout: {
                type: 'fit'
            },
            items: [
                {
                    xtype: 'grid',
                    reference: 'dataQualityGrid',
                    autoScroll: true,
                    bind: {
                        store: '{dqRules}'
                    },
                    cls: 'unidata-admin-data-quality-dqgrid',
                    listeners: {
                        selectionchange: 'onDataQualityGridSelectionChange',
                        beforedeselect: 'onDataQualityGridBeforeDeselect'
                    },
                    tbar: [
                        '->',
                        {
                            xtype: 'button',
                            text: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:rule')}),
                            reference: 'addDqRuleButton',
                            listeners: {
                                click: 'onAddDqRuleButtonClick'
                            },
                            bind: {
                                disabled: '{metaRecordViewReadOnly}'
                            }
                        },
                        {
                            xtype: 'button',
                            text: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:rule')}),
                            reference: 'deleteDqRuleButton',
                            listeners: {
                                click: 'onDeleteDqRuleButtonClick'
                            },
                            bind: {
                                disabled: '{!removeButtonEnabled}'
                            }
                        }
                    ],
                    viewConfig: {
                        plugins: {
                            ptype: 'gridviewdragdrop',
                            dragText: Unidata.i18n.t('admin.metamodel>selectNewPosition'),
                            containerScroll: true
                        },
                        listeners: {
                            drop: 'onDataQualityGridDrop'
                        }
                    },
                    columns: [
                        /* Поле временно деактивировано
                        {
                            text: 'ID правила',
                            dataIndex: 'id',
                            flex: 3,
                            sortable: false
                        },
                        */
                        {
                            text: Unidata.i18n.t('admin.metamodel>order'),
                            dataIndex: 'order',
                            sortable: true,
                            resizable: false,
                            menuDisabled: true,
                            flex: 1
                        },
                        {
                            text: Unidata.i18n.t('glossary:name'),
                            dataIndex: 'name',
                            sortable: false,
                            resizable: true,
                            menuDisabled: true,
                            flex: 3
                        },
                        {
                            text: Unidata.i18n.t('glossary:description'),
                            dataIndex: 'description',
                            sortable: false,
                            resizable: true,
                            menuDisabled: true,
                            flex: 3
                        },
                        {
                            text: Unidata.i18n.t('admin.metamodel>functionName'),
                            dataIndex: 'cleanseFunctionName',
                            sortable: false,
                            resizable: true,
                            menuDisabled: true,
                            flex: 3
                        }
                        //{
                        //    text: 'Имя вложенной сущности',
                        //    dataIndex: 'complexAttributeName',
                        //    flex: 3,
                        //    sortable: false
                        //}
                    ]
                }
            ]
        },
        {
            xtype: 'container',
            cls: 'data-quality-container',
            title: Unidata.i18n.t('admin.metamodel>common'),
            scrollable: 'vertical',
            layout: 'hbox',
            height: 250,
            bind: {
                hidden: '{!currentDqRule}'
            },
            defaults: {
                height: 200
            },
            items: [
                {
                    xtype: 'fieldset',
                    title: Unidata.i18n.t('admin.metamodel>ruleProperty'),
                    layout: 'vbox',
                    flex: 2,
                    margin: '5 5 5 5',
                    items: [
                        {
                            xtype: 'textfield',
                            reference: 'dqFieldName',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>ruleName'),
                            width: '100%',
                            bind: {
                                value: '{currentDqRule.name}',
                                disabled: '{!currentDqRule}',
                                readOnly: '{metaRecordViewReadOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under'
                        },
                        {
                            xtype: 'textarea',
                            fieldLabel: Unidata.i18n.t('glossary:description'),
                            reference: 'dqFieldDescription',
                            width: '100%',
                            bind: {
                                value: '{currentDqRule.description}',
                                disabled: '{!currentDqRule}',
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        }
                        //{
                        //    xtype: 'checkbox',
                        //    fieldLabel: 'Special',
                        //    width: '100%',
                        //    bind: {
                        //        value: '{currentDqRule.special}',
                        //        disabled: '{!currentDqRule}'
                        //    }
                        //},

                    ]
                },
                {
                    xtype: 'fieldset',
                    title: Unidata.i18n.t('admin.metamodel>ruleSettings'),
                    fieldDefaults: {
                        labelWidth: 150,
                        anchor: '100%',
                        height: 30
                    },
                    layout: 'anchor',
                    flex: 2,
                    margin: '5 5 5 0',
                    items: [
                        {
                            xtype: 'pickerfield',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>function'),
                            editable: false,
                            reference: 'cleanseFunctionPickerField',
                            modelValidation: true,
                            msgTarget: 'under',
                            bind: {
                                value: '{currentDqRule.cleanseFunctionName}',
                                disabled: '{!currentDqRule}',
                                readOnly: '{metaRecordViewReadOnly}'
                            },
                            createPicker: function () {
                                var me = this,
                                    picker;

                                picker = new Ext.panel.Panel({
                                        pickerField: me,
                                        floating: true,
                                        hidden: true,
                                        ownerCt: this.ownerCt,
                                        renderTo: document.body,
                                        anchor: '100%',
                                        height: 300,

                                        overflowY: 'auto',
                                        items: [
                                            {
                                                xtype: 'component.attributeTree',
                                                bind: {
                                                    data: '{cleanseGroups}'
                                                },
                                                listeners: {
                                                    itemclick: 'onSelectCleanseFunction'
                                                }
                                            }
                                        ]
                                    });

                                return picker;
                            }
                        },
                        // вложенная сущность временно скрыта, т.к. не используется
                        //{
                        //    xtype: 'pickerfield',
                        //    fieldLabel: 'Вложенная сущность',
                        //    reference: 'attributePickerField',
                        //    width: '100%',
                        //    bind: {
                        //        value: '{currentDqRule.complexAttributeName}',
                        //        disabled: '{!currentDqRule}'
                        //    },
                        //    createPicker: function () {
                        //        var me = this,
                        //            picker = new Ext.panel.Panel({
                        //                pickerField: me,
                        //                floating: true,
                        //                hidden: true,
                        //                ownerCt: this.ownerCt,
                        //                renderTo: document.body,
                        //                height: 200,
                        //                anchor: '100% 100%',
                        //                overflowY: 'auto',
                        //                items: [
                        //                    {
                        //                        xtype: 'component.attributeTree',
                        //                        isSimpleAttributesHidden: true,
                        //                        //rootVisible: false,
                        //                        bind: {
                        //                            data: '{currentRecord}'
                        //                        },
                        //                        listeners: {
                        //                            itemclick: 'onAttributePickerFieldClick'
                        //                        }
                        //                    }
                        //                ]
                        //            });
                        //
                        //        return picker;
                        //    }
                        //},
                        {
                            xtype: 'checkboxgroup',
                            reference: 'dqTypeCBGroup',
                            msgTarget: 'under',
                            validateOnChange: false,
                            items: [
                                {
                                    boxLabel: Unidata.i18n.t('common:validation'),
                                    reference: 'cbGroupValidation',
                                    bind: {
                                        disabled: '{!currentDqRule}',
                                        value: '{currentDqRule.isValidation}',
                                        readOnly: '{metaRecordViewReadOnly}'
                                    }
                                },
                                {
                                    boxLabel: Unidata.i18n.t('admin.metamodel>enrich'),
                                    reference: 'cbGroupEnrich',
                                    bind: {
                                        disabled: '{!currentDqRule}',
                                        value: '{currentDqRule.isEnrichment}',
                                        readOnly: '{metaRecordViewReadOnly}'
                                    }
                                }
                            ],
                            listeners: {
                                change: 'onDqTypeCBGroupChange'
                            }
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: Unidata.i18n.t('admin.metamodel>uses'),
                    flex: 1,
                    layout: 'vbox',
                    margin: '5 5 5 0',
                    scrollable: true,
                    items: [
                        {
                            xtype: 'checkbox',
                            boxLabel: Unidata.i18n.t('admin.metamodel>masterData'),
                            reference: 'masterDataCheckbox',
                            name: 'topping',
                            inputValue: 'MASTER_DATA',
                            margin: '0 0 5 0',
                            listeners: {
                                change: 'onChangeMaterDataCheckbox'
                            },
                            bind: {
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        },
                        {
                            xtype: 'checkboxgroup',
                            reference: 'originsCBGroup',
                            columns: 1,
                            vertical: true,
                            bind: {
                                hidden: '{!currentDqRule}'
                            },
                            listeners: {
                                change: 'onSourceSystemsCBGroupChange'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'container',
            cls: 'data-quality-container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            height: 400,
            scrollable: 'vertical',
            reference: 'attributePanel',
            bind: {
                hidden: '{!currentDqRule}'
            },
            items: [
                {
                    xtype: 'panel',
                    title: Unidata.i18n.t('glossary:attributes'),
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    flex: 2,
                    items: [
                        {
                            xtype: 'attributecombobox',
                            reference: 'attributeComboBox',
                            padding: '0 0 10 20',
                            height: 30
                        },
                        {
                            xtype: 'component.attributeTree',
                            reference: 'attributeTreePanel',
                            isArrayAttributesHidden: true,
                            ddGroup: 'dqDragDrop',
                            flex: 1,
                            overflowY: 'auto',
                            bind: {
                                data: {
                                    bindTo: '{currentRecord}',
                                    deep: true
                                },
                                disabled: '{!currentDqRule}'
                            },
                            listeners: {
                                beforerender: 'onAttributeTreeBeforeRender'
                            },
                            viewConfig: {
                                copy: true,
                                plugins: {
                                    ptype: 'treeviewdragdrop',
                                    ddGroup: 'dqDragDrop',
                                    appendOnly: true,
                                    sortOnDrop: false,
                                    containerScroll: true
                                }
                            }
                        }
                    ]
                },
                {
                    xtype: 'form',
                    flex: 1,
                    autoScroll: true,
                    title: Unidata.i18n.t('admin.common>inputPorts'),
                    reference: 'inputPorts',
                    referenceHolder: true,
                    bodyPadding: 10,
                    labelWidth: 100,
                    fieldDefaults: {
                        labelAlign: 'top'
                    },
                    items: [],
                    bind: {
                        disabled: '{!currentDqRule}'
                    }
                },
                {
                    xtype: 'form',
                    flex: 1,
                    autoScroll: true,
                    title: Unidata.i18n.t('admin.metamodel>outputPorts'),
                    reference: 'outputPorts',
                    referenceHolder: true,
                    bodyPadding: 10,
                    labelWidth: 100,
                    fieldDefaults: {
                        labelAlign: 'top'
                    },
                    items: [],
                    bind: {
                        disabled: '{!currentDqRule}'
                    }
                }]
        },
        {
            xtype: 'panel',
            cls: 'data-quality-container',
            title: Unidata.i18n.t('common:validation'),
            layout: 'hbox',
            height: 400,
            defaults: {
                height: 320
            },
            hidden: true,
            disabled: true,
            scrollable: 'vertical',
            bind: {
                hidden: '{!currentDqRule.isValidation}',
                disabled: '{!currentDqRule.isValidation}'
            },
            items: [
                {
                    xtype: 'fieldset',
                    title: Unidata.i18n.t('common:error'),
                    layout: 'vbox',
                    flex: 2,
                    margin: '5 5 5 5',
                    defaults: {
                        modelValidation: true
                    },
                    items: [
                        {
                            xtype: 'combo',
                            width: '100%',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>makeErrorOnBase'),
                            allowBlank: true,
                            autoSelect: true,
                            forceSelection: true,
                            editable: false,
                            labelAlign: 'top',
                            displayField: 'name',
                            valueField: 'value',
                            reference: 'functionRaiseErrorPortsCombo',
                            bind: {
                                store: '{functionRaiseErrorPorts}',
                                value: '{currentDqRaise.functionRaiseErrorPort}',
                                readOnly: '{metaRecordViewReadOnly}'
                            },
                            emptyText: Unidata.i18n.t(
                                'admin.common>defaultSelect',
                                {entity: Unidata.i18n.t('admin.cleanseFunction>port').toLowerCase()}
                            ),
                            modelValidation: true,
                            msgTarget: 'under'
                        },
                        {
                            xtype: 'combo',
                            reference: 'phaseRaise',
                            width: '100%',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>handleDataPhase'),
                            allowBlank: true,
                            forceSelection: true,
                            editable: false,
                            valueField: 'value',
                            autoSelect: true,
                            labelAlign: 'top',
                            store: {
                                fields: ['text', 'value'],
                                autoLoad: true,
                                data: Unidata.model.dataquality.DqRaise.getPhasesList()
                            },
                            emptyText: Unidata.i18n.t(
                                'admin.common>defaultSelect',
                                {entity: Unidata.i18n.t('admin.metamodel>phase')}
                            ),
                            bind: {
                                readOnly: '{metaRecordViewReadOnly}'
                            },
                            hidden: true,
                            modelValidation: true,
                            msgTarget: 'under'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: Unidata.i18n.t('admin.metamodel>main'),
                    fieldDefaults: {
                        labelWidth: 150,
                        anchor: '100%',
                        height: 30
                    },
                    layout: 'anchor',
                    flex: 2,
                    margin: '5 5 5 0',
                    //TODO: add validation for dqvalidation config fields
                    items: [
                        {
                            xtype: 'combo',
                            width: '100%',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>messageText'),
                            allowBlank: true,
                            autoSelect: true,
                            forceSelection: true,
                            editable: false,
                            labelAlign: 'top',
                            displayField: 'name',
                            valueField: 'value',
                            reference: 'messagePortsCombo',
                            bind: {
                                store: '{messagePorts}',
                                value: '{currentDqRaise.messagePort}',
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        },
                        {
                            xtype: 'textfield',
                            width: '100%',
                            labelAlign: 'top',
                            reference: 'messageText',
                            allowBlank: false,
                            msgTarget: 'under',
                            bind: {
                                value: '{currentDqRaise.messageText}',
                                hidden: '{isMessagePortSelected}',
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        },
                        {
                            xtype: 'combo',
                            reference: 'severityValue',
                            fieldLabel: Unidata.i18n.t('glossary:criticalness'),
                            width: '100%',
                            allowBlank: true,
                            forceSelection: true,
                            editable: false,
                            valueField: 'value',
                            autoSelect: true,
                            labelAlign: 'top',
                            emptyText: Unidata.i18n.t('admin.metamodel>userCriticalness'),
                            store: {
                                fields: ['text', 'value'],
                                autoLoad: true,
                                data: Unidata.model.dataquality.DqRaise.getSeverityList()
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            bind: {
                                value: '{currentDqRaise.severityValue}',
                                hidden: '{isSeverityPortSelected}',
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        },
                        {
                            xtype: 'combo',
                            width: '100%',
                            fieldLabel: Unidata.i18n.t('glossary:category'),
                            allowBlank: true,
                            autoSelect: true,
                            forceSelection: true,
                            editable: false,
                            labelAlign: 'top',
                            displayField: 'name',
                            valueField: 'value',
                            reference: 'categoryPortsCombo',
                            bind: {
                                store: '{categoryPorts}',
                                value: '{currentDqRaise.categoryPort}',
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        },
                        {
                            xtype: 'textfield',
                            width: '100%',
                            labelAlign: 'top',
                            reference: 'categoryText',
                            bind: {
                                value: '{currentDqRaise.categoryText}',
                                hidden: '{isCategoryPortSelected}',
                                readOnly: '{metaRecordViewReadOnly}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            reference: 'masterDataPanel',
            cls: 'data-quality-container',
            layout: 'hbox',
            title: Unidata.i18n.t('admin.metamodel>enrichMasterData'),
            scrollable: 'vertical',
            hidden: true,
            bind: {
                //hidden: '{!currentDqRule.isEnrichment}'
            },
            items: [
                {
                    width: '100%',
                    xtype: 'fieldset',
                    layout: 'anchor',
                    margin: '5 5 5 5',
                    defaults: {
                        modelValidation: true
                    },
                    items: [
                        {
                            xtype: 'radiogroup',
                            reference: 'originsRBGroup',
                            flex: 1,
                            columns: 1,
                            vertical: true,
                            validateOnChange: false,
                            //hidden: true,
                            bind: {
                                //hidden: '{!currentDqRule.isEnrichment}'
                            },
                            listeners: {
                                change: 'onOriginsRBGroupChange'
                            },
                            msgTarget: 'under'
                        },
                        {
                            xtype: 'radiogroup',
                            reference: 'actionRBGroup',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>fixErichChanges'),
                            labelAlign: 'top',
                            validateOnChange: false,
                            flex: 1,
                            columns: 1,
                            vertical: true,
                            hidden: true,
                            items: [
                                {
                                    boxLabel: Unidata.i18n.t('admin.metamodel>changeEntity'),
                                    name: 'dqAction',
                                    inputValue: 'UPDATE_CURRENT'
                                },
                                {
                                    boxLabel: Unidata.i18n.t('admin.metamodel>createNewEntity'),
                                    name: 'dqAction',
                                    inputValue: 'CREATE_NEW'
                                }
                            ],
                            emptyText: Unidata.i18n.t(
                                'admin.common>defaultSelect',
                                {entity: Unidata.i18n.t('admin.metamodel>action')}
                            ),
                            listeners: {
                                change: 'onDqActionRadioGroupChange'
                            },
                            msgTarget: 'under'
                        },
                        {
                            xtype: 'combo',
                            reference: 'phaseEnrich',
                            fieldLabel: Unidata.i18n.t('admin.metamodel>handleDataPhase'),
                            width: '100%',
                            allowBlank: true,
                            forceSelection: true,
                            editable: false,
                            valueField: 'value',
                            validateOnChange: false,
                            validateOnBlur: false,
                            store: {
                                fields: ['text', 'value'],
                                autoLoad: true,
                                data: Unidata.model.dataquality.DqEnrich.getPhasesList()
                            },
                            bind: {
                                value: '{currentDqEnrich.phase}'
                            },
                            autoSelect: true,
                            hidden: true,
                            labelAlign: 'top',
                            emptyText: Unidata.i18n.t(
                                'admin.common>defaultSelect',
                                {entity: Unidata.i18n.t('admin.metamodel>phase')}
                            ),
                            modelValidation: true,
                            msgTarget: 'under'
                        }
                    ]
                }
            ]
        }
    ]
});
