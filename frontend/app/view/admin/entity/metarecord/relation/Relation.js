Ext.define('Unidata.view.admin.entity.metarecord.relation.Relation', {
    extend: 'Ext.panel.Panel',

    controller: 'admin.entity.metarecord.relation',
    viewModel: {
        type: 'admin.entity.metarecord.relation'
    },

    requires: [
        'Unidata.view.admin.entity.metarecord.relation.RelationController',
        'Unidata.view.admin.entity.metarecord.relation.RelationModel',
        'Unidata.view.component.EntityComboBox'
    ],

    alias: 'widget.admin.entity.metarecord.relation',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    draftMode: null,                                  // режим работы с черновиком

    cls: 'unidata-admin-entity-metarecord-relation',

    changeToContainsTitle: Unidata.i18n.t('admin.metamodel>changeRelationType'),
    changeToContainsText: Unidata.i18n.t('admin.metamodel>confirmChangeRelationTypeToOn'),

    relationTypeComboBox: null,
    attributeTreePanel: null,
    displayAttributeList: null,
    searchAttributeList: null,

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'relationComboBoxPickerOnBeforeSelect'
        }
    ],

    mixins: ['Unidata.mixin.StatusManageable'],

    initItems: function () {
        var draftMode = this.draftMode;

        this.callParent(arguments);

        this.add([
            {
                flex: 2,
                xtype: 'grid',
                hideHeaders: true,
                bind: '{relations}',
                reference: 'relationGrid',
                cls: 'unidata-admin-data-relation-relgrid',
                columns: [
                    {
                        flex: 1,
                        xtype: 'templatecolumn',
                        reference: 'relationItemColumn',
                        sortable: true,
                        resizable: true,
                        menuDisabled: true,
                        tpl: '' //tpl is generated in a controller init() method
                    }
                ],
                tbar: [
                    {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'small',
                        iconCls: 'icon-plus',
                        handler: 'onAddRelation',
                        tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:relation')}),
                        bind: {
                            disabled: '{metaRecordViewReadOnly}'
                        }
                    },
                    {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'small',
                        iconCls: 'icon-trash2',
                        handler: 'onDeleteRelation',
                        tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:relation')}),
                        bind: {
                            disabled: '{!removeButtonEnabled}'
                        }
                    }
                ],
                listeners: {
                    beforedeselect: 'onRelationGridBeforeDeselect',
                    select: 'onRelationGridSelect'
                }
            },
            {
                flex: 6,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                disabled: true,
                bind: {
                    disabled: '{!relationGrid.selection}'
                },
                defaults: {
                    bodyPadding: 10
                },
                items: [
                    {
                        defaults: {
                            cls: 'un-form-items-height-auto',
                            border: false,
                            xtype: 'panel',
                            flex: 1,
                            layout: 'anchor',
                            margin: 10
                        },
                        xtype: 'form',

                        fieldDefaults: {
                            labelWidth: 150,
                            anchor: '100%',
                            height: 30
                        },

                        layout: 'hbox',

                        items: [
                            {
                                items: [
                                    {
                                        xtype: 'textfield',
                                        reference: 'relName',
                                        fieldLabel: Unidata.i18n.t('glossary:relationName'),
                                        emptyText: Unidata.i18n.t('admin.metamodel>selectRelationName'),
                                        name: 'relName',
                                        bind: {
                                            value: '{currentRelation.name}',
                                            readOnly: '{!relationNameEditable}'
                                        },
                                        modelValidation: true,
                                        msgTarget: 'under'
                                    },
                                    {
                                        xtype: 'textfield',
                                        reference: 'displayName',
                                        fieldLabel: Unidata.i18n.t('glossary:displayName'),
                                        emptyText: Unidata.i18n.t('admin.metamodel>selectDisplayedRelationName'),
                                        name: 'displayName',
                                        bind: {
                                            value: '{currentRelation.displayName}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        },
                                        modelValidation: true,
                                        msgTarget: 'under'
                                    },
                                    {
                                        xtype: 'combobox',
                                        fieldLabel: Unidata.i18n.t('glossary:relationType'),
                                        reference: 'relationTypeComboBox',
                                        editable: false,
                                        //data is generated in a controller init() method
                                        bind: {
                                            value: '{currentRelation.relType}',
                                            readOnly: '{!relationTypeEditable}'
                                        },
                                        valueField: 'key',
                                        displayField: 'value',
                                        name: 'relType',
                                        modelValidation: true,
                                        msgTarget: 'under'
                                    },
                                    {
                                        xtype: 'keyvalue.input',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>customProperties'),
                                        name: 'customProperties',
                                        bind: {
                                            gridStore: '{currentRelation.customProperties}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        }
                                    },
                                    {
                                        xtype: 'checkboxfield',
                                        name: 'required',
                                        fieldLabel: Unidata.i18n.t('glossary:required_f'),
                                        bind: {
                                            hidden: '{isDisabledAttribute}',
                                            value: '{currentRelation.required}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        }
                                    }
                                ]
                            },
                            {
                                items: [
                                    {
                                        xtype: 'un.entitycombo',
                                        reference: 'entityRelation',
                                        fieldLabel: Unidata.i18n.t('glossary:linkedEntity'),
                                        draftMode: draftMode,
                                        name: 'toEntity',
                                        bind: {
                                            value: '{currentRelation.toEntity}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        },
                                        listeners: {
                                            select: 'onSelectEntityRelation',
                                            endprocessresponse: 'onEndProcessResponse'
                                        },
                                        emptyText: Unidata.i18n.t('admin.metamodel>selectRelatedEntity'),
                                        modelValidation: true,
                                        showLookupEntities: false
                                    },
                                    {
                                        xtype: 'un.attributetagfield',
                                        reference: 'displayAttributeList',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>displayedAttributes'),
                                        bind: {
                                            value: '{currentRelation.toEntityDefaultDisplayAttributes}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        },
                                        store: {
                                            model: 'Unidata.model.attribute.AbstractAttribute',
                                            sorters: [{
                                                property: 'displayName',
                                                direction: 'ASC'
                                            }]
                                        },
                                        triggers: {
                                            clear: {
                                                cls: 'x-form-clear-trigger',
                                                handler: function () {
                                                    this.clearValue();
                                                }
                                            }
                                        },
                                        listeners: {
                                            render: function (tagfield) {
                                                var sorters = [
                                                    {
                                                        property: 'order',
                                                        direction: 'ASC'
                                                    }
                                                ];

                                                tagfield.valueCollection.setSorters(sorters);
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'un.attributetagfield',
                                        reference: 'searchAttributeList',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>searchAttributes'),
                                        bind: {
                                            hidden: '{isRelTypeContains}',
                                            value: '{currentRelation.toEntitySearchAttributes}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        },
                                        store: {
                                            model: 'Unidata.model.attribute.AbstractAttribute',
                                            sorters: [{
                                                property: 'displayName',
                                                direction: 'ASC'
                                            }]
                                        },
                                        triggers: {
                                            clear: {
                                                cls: 'x-form-clear-trigger',
                                                handler: function () {
                                                    this.clearValue();
                                                }
                                            }
                                        },
                                        listeners: {
                                            render: function (tagfield) {
                                                var sorters = [
                                                    {
                                                        property: 'order',
                                                        direction: 'ASC'
                                                    }
                                                ];

                                                tagfield.valueCollection.setSorters(sorters);
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'checkbox',
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>showAttributes'),
                                        bind: {
                                            value: '{currentRelation.useAttributeNameForDisplay}',
                                            readOnly: '{metaRecordViewReadOnly}'
                                        }
                                    },
                                    {
                                        xtype: 'pickerfield',
                                        hidden: true,
                                        fieldLabel: Unidata.i18n.t('admin.metamodel>nestedEntity'),
                                        reference: 'displayFieldPicker',
                                        bind: '{currentRelation.toEntityDefaultDisplayAttribute}',
                                        modelValidation: true,
                                        msgTarget: 'under',
                                        createPicker: function () {
                                            var me = this,
                                                picker = new Ext.panel.Panel({
                                                    pickerField: me,
                                                    floating: true,
                                                    hidden: true,
                                                    ownerCt: this.ownerCt,
                                                    renderTo: document.body,
                                                    height: 200,
                                                    anchor: '100% 100%',
                                                    overflowY: 'auto',
                                                    items: [
                                                        {
                                                            xtype: 'component.attributeTree',
                                                            bind: {
                                                                data: '{selectedRelationEntity}'
                                                            },
                                                            listeners: {
                                                                itemclick: 'onDisplayFieldSelect'
                                                            }
                                                        }
                                                    ]
                                                });

                                            return picker;
                                        }
                                    }
                                ]
                            }]
                    },
                    {
                        flex: 2,
                        xtype: 'admin.entity.metarecord.attribute',
                        draftMode: draftMode,
                        reference: 'attributeTreePanel',
                        isComplexAttributesHidden: true,
                        isArrayAttributesHidden: true,
                        bind: {
                            disabled: '{isDisabledAttribute}',
                            readOnly: '{metaRecordViewReadOnly}'
                        },
                        hiddenAttributeFields: [
                            'readOnly',
                            'hidden',
                            'searchable',
                            'displayable',
                            'mainDisplayable',
                            'unique'
                        ],
                        viewModel: {
                            links: {
                                currentRecord: '{currentRelation}'
                            },
                            data: {
                                allowOnlySimpleType: true
                            }
                        }
                    }
                ]
            }
        ]);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.relationTypeComboBox = this.lookupReference('relationTypeComboBox');
        this.attributeTreePanel = this.lookupReference('attributeTreePanel');
        this.displayAttributeList = this.lookupReference('displayAttributeList');
        this.searchAttributeList = this.lookupReference('searchAttributeList');
    },

    initListeners: function () {
        var relationTypeComboBox = this.relationTypeComboBox,
            picker = relationTypeComboBox.getPicker();

        picker.on('beforeselect', this.relationComboBoxPickerOnBeforeSelect, this);
    }
});
