/**
 * Карточка array атрибута узла классификатора
 *
 * @author Ivan Marshalkin
 * @date 2018-05-11
 */

Ext.define('Unidata.view.classifier.item.attribute.ClassifierArrayAttribute', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifier.item.attribute.ClassifierArrayAttributeController',
        'Unidata.view.classifier.item.attribute.ClassifierArrayAttributeModel'
    ],

    alias: 'widget.classifier.item.arrayattribute',

    viewModel: {
        type: 'classifier.item.arrayattribute'
    },

    controller: 'classifier.item.arrayattribute',

    componentCls: 'un-classifier-array-attr',

    referenceHolder: true,

    nameField: null,                   // ссылка на поле с именем атрибута
    displayNameField: null,            // ссылка на поле с отображаемым именем атрибута
    descriptionField: null,            // ссылка на поле с описанием
    requiredField: null,               // ссылка на поле "обязательно"
    readOnlyField: null,               // ссылка на поле "только для чтения"
    typeCategoryField: null,           // ссылка на поле тип типа :)
    arrayDataTypeField: null,          // ссылка на поле значение типа
    lookupEntityTypeField: null,       // ссылка на поле справочники
    valueFieldContainer: null,         // ссылка на контейнер содержащий поле со значением

    collapsible: true,
    animCollapse: false,
    titleCollapse: true,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        titlePosition: 1
    },

    methodMapper: [
        {
            method: 'updateDeletable'
        },
        {
            method: 'updateNodeAttribute'
        },
        {
            method: 'updateOwnAttribute'
        },
        {
            method: 'updateInheritedAttribute'
        },
        {
            method: 'updateClassifierNode'
        },
        {
            method: 'highlightErrors'
        },
        {
            method: 'resetErrors'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'setAttributeOrder'
        }
    ],

    ui: 'un-card',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        classifierNode: null,
        nodeAttribute: null,
        ownAttribute: null,
        inheritedAttribute: null,
        deletable: null,
        readOnly: null,
        allowLiftUp: true,
        allowLiftDown: true
    },

    viewModelAccessors: [
        'allowLiftUp',
        'allowLiftDown'
    ],

    bind: {
        title: '{classifierNodeAttributePanelTitle}'
    },

    items: [
        {
            xtype: 'textfield',
            reference: 'nameField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:name'),
            labelAlign: 'top',
            msgTarget: 'under',
            listeners: {
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'textfield',
            reference: 'displayNameField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:displayName'),
            labelAlign: 'top',
            msgTarget: 'under',
            listeners: {
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{displayNameFieldReadOnly}'
            }
        },
        {
            xtype: 'combobox',
            reference: 'typeCategoryField',
            ui: 'un-field-default' ,
            hidden: true,
            fieldLabel: Unidata.i18n.t('glossary:attributeType'),
            labelAlign: 'top',
            msgTarget: 'under',
            store: {
                fields: ['name', 'value'],
                data: Unidata.model.attribute.SimpleAttribute.getArrayTypesList()
            },
            displayField: 'name',
            valueField: 'value',
            forceSelection: true,
            editable: false,
            allowBlank: true,
            emptyText: Unidata.i18n.t('common:defaultSelect', {entity: Unidata.i18n.t('classifier>attributeType')}),
            listeners: {
                render: function (combobox) {
                    combobox.inputEl.addCls('x-unselectable');
                },
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'combobox',
            reference: 'arrayDataTypeField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:valueType'),
            labelAlign: 'top',
            msgTarget: 'under',
            hidden: true,
            store: {
                fields: ['name', 'displayName'],
                data: Unidata.Constants.getArrayDataTypes()
            },
            valueField: 'name',
            displayField: 'displayName',
            emptyText: Unidata.i18n.t('common:defaultSelect', {entity: Unidata.i18n.t('classifier>type')}),
            editable: false,
            listeners: {
                render: function (combobox) {
                    combobox.inputEl.addCls('x-unselectable');
                },
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'un.entitycombo',
            reference: 'lookupEntityTypeField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:lookupEntityLink'),
            labelAlign: 'top',
            msgTarget: 'under',
            emptyText: Unidata.i18n.t('common:defaultSelect', {entity: Unidata.i18n.t('classifier>lookupEntity')}),
            hidden: true,
            forceSelection: true,
            allowBlank: true,
            showEntities: false,
            listeners: {
                render: function (combobox) {
                    combobox.inputEl.addCls('x-unselectable');
                },
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'container',
            reference: 'valueFieldContainer',
            minHeight: 60,
            hidden: true, // в рамках r4.8.2 функционал не реализуется
            layout: {
                type: 'vbox',
                align: 'stretch'
            }
        },
        {
            xtype: 'textfield',
            reference: 'descriptionField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:description'),
            labelAlign: 'top',
            msgTarget: 'under',
            listeners: {
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'yesnocombobox',
            reference: 'requiredField',
            inverted: true,
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:required'),
            labelAlign: 'top',
            msgTarget: 'under',
            listeners: {
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'yesnocombobox',
            reference: 'readOnlyField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('classifier>readOnly'),
            labelAlign: 'top',
            msgTarget: 'under',
            listeners: {
                change: 'onAttributePropertyChange'
            },
            bind: {
                readOnly: '{attributeFieldReadOnly}'
            }
        },
        {
            xtype: 'keyvalue.input',
            labelAlign: 'top',
            msgTarget: 'under',
            fieldLabel: Unidata.i18n.t('admin.metamodel>customProperties'),
            bind: {
                gridStore: '{nodeAttribute.customProperties}',
                readOnly: '{readOnly}'
            }
        }
    ],

    tools: [
        {
            xtype: 'un.dottedmenubtn',
            scale: 'small',
            menu: {
                xtype: 'un.dottedmenu',
                plain: true,
                items: [
                    {
                        text: Unidata.i18n.t('classifier>restoreAttribute'),
                        qaId: 'restore-attribute',
                        hidden: true,
                        bind: {
                            hidden: '{!restoreAttributeButtonVisible}'
                        },
                        listeners: {
                            click: 'onClassifierNodeAttributeRestoreButtonClick'
                        }
                    },
                    {
                        text: Unidata.i18n.t('common:up'),
                        qaId: 'attribute-up',
                        hidden: true,
                        bind: {
                            hidden: '{readOnly}',
                            disabled: '{!allowLiftUp}'
                        },
                        listeners: {
                            click: 'onClassifierNodeAttributeUpButtonClick'
                        }
                    },
                    {
                        text: Unidata.i18n.t('common:down'),
                        qaId: 'attribute-down',
                        hidden: true,
                        bind: {
                            hidden: '{readOnly}',
                            disabled: '{!allowLiftDown}'
                        },
                        listeners: {
                            click: 'onClassifierNodeAttributeDownButtonClick'
                        }
                    },
                    {
                        text: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:attribute')}),
                        qaId: 'delete-attribute',
                        hidden: true,
                        bind: {
                            hidden: '{!deleteAttributeButtonVisible}'
                        },
                        listeners: {
                            click: 'onClassifierNodeAttributeRemoveButtonClick'
                        }
                    }
                ]
            }
        }
    ],

    listeners: {
        afterrender: 'onViewAfterRender'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.nameField = me.lookupReference('nameField');
        me.displayNameField = me.lookupReference('displayNameField');
        me.descriptionField = me.lookupReference('descriptionField');
        me.requiredField = me.lookupReference('requiredField');
        me.readOnlyField = me.lookupReference('readOnlyField');
        me.typeCategoryField = me.lookupReference('typeCategoryField');
        me.arrayDataTypeField = me.lookupReference('arrayDataTypeField');
        me.lookupEntityTypeField = me.lookupReference('lookupEntityTypeField');
        me.valueFieldContainer = me.lookupReference('valueFieldContainer');
    },

    onDestroy: function () {
        var me = this;

        me.nameField = null;
        me.displayNameField = null;
        me.descriptionField = null;
        me.requiredField = null;
        me.readOnlyField = null;
        me.typeCategoryField = null;
        me.arrayDataTypeField = null;
        me.lookupEntityTypeField = null;
        me.valueFieldContainer = null;

        me.callParent(arguments);
    }
});
