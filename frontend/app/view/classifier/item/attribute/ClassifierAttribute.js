/**
 * Карточка для атрибута ноды
 *
 * @author Ivan Marshalkin
 * @date 2016-08-11
 */

Ext.define('Unidata.view.classifier.item.attribute.ClassifierAttribute', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifier.item.attribute.ClassifierAttributeController',
        'Unidata.view.classifier.item.attribute.ClassifierAttributeModel'
    ],

    alias: 'widget.classifier.item.attribute',

    viewModel: {
        type: 'classifier.item.attribute'
    },

    controller: 'classifier.item.attribute',

    referenceHolder: true,

    nameField: null,                   // ссылка на поле с именем атрибута
    displayNameField: null,            // ссылка на поле с отображаемым именем атрибута
    descriptionField: null,            // ссылка на поле с описанием
    requiredField: null,               // ссылка на поле "обязательно"
    readOnlyField: null,               // ссылка на поле "только для чтения"
    typeCategoryField: null,           // ссылка на поле тип типа :)
    simpleDataTypeField: null,         // ссылка на поле значение типа
    lookupEntityTypeField: null,       // ссылка на поле справочники
    valueFieldContainer: null,         // ссылка на контейнер содержащий поле со значением

    collapsible: true,
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
        readOnly: null
    },

    bind: {
        title: '{classifierNodeAttributePanelTitle:htmlEncode}'
    },

    items: [
        {
            xtype: 'container',
            reference: 'valueFieldContainer',
            minHeight: 60,
            layout: {
                type: 'vbox',
                align: 'stretch'
            }
        },
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
                readOnly: '{attributeFieldNotUseHasDataReadOnly}'
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
            xtype: 'combobox',
            reference: 'typeCategoryField',
            ui: 'un-field-default' ,
            hidden: true,
            fieldLabel: Unidata.i18n.t('glossary:attributeType'),
            labelAlign: 'top',
            msgTarget: 'under',
            store: {
                fields: ['name', 'value'],
                data: Unidata.model.attribute.SimpleAttribute.getTypesList(),
                filters: [
                    function (record) {
                        var allowType = [
                            'simpleDataType'
                            //'lookupEntityType', // ссылка на справочник в версси 4.2 не поддерживается
                            // 'enumDataType'
                        ];

                        return Ext.Array.contains(allowType, record.get('value'));
                    }
                ]
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
            reference: 'enumDataTypeField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:enum'),
            labelAlign: 'top',
            msgTarget: 'under',
            emptyText: Unidata.i18n.t('common:defaultSelect', {entity: Unidata.i18n.t('classifier>enums')}),

            store: Unidata.util.api.Enumeration.getStore(),
            displayField: 'displayName',
            valueField: 'name',

            editable: false,
            allowBlank: true,
            hidden: true,
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
            reference: 'simpleDataTypeField',
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('glossary:valueType'),
            labelAlign: 'top',
            msgTarget: 'under',
            hidden: true,
            store: {
                fields: ['name', 'displayName'],
                data: Unidata.Constants.getSimpleDataTypes(),
                filters: [
                    function (record) {
                        var disallowType = ['Blob', 'Clob'];

                        return !Ext.Array.contains(disallowType, record.get('name'));
                    }
                ]
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
        }
    ],

    tools: [
        {
            xtype: 'un.roundbtn.delete',
            handler: 'onRemoveClassifierNodeAttribute',
            buttonSize: 'extrasmall',
            shadow: false,
            tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:attribute')}),
            hidden: true,
            bind: {
                hidden: '{!deleteAttributeButtonVisible}'
            }
        },
        {
            xtype: 'un.roundbtn.restore',
            handler: 'onRestoreClassifierNodeAttribute',
            buttonSize: 'extrasmall',
            shadow: false,
            tooltip: Unidata.i18n.t('classifier>restoreAttribute'),
            hidden: true,
            bind: {
                hidden: '{!restoreAttributeButtonVisible}'
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

        me.nameField             = me.lookupReference('nameField');
        me.displayNameField      = me.lookupReference('displayNameField');
        me.descriptionField      = me.lookupReference('descriptionField');
        me.requiredField         = me.lookupReference('requiredField');
        me.readOnlyField         = me.lookupReference('readOnlyField');
        me.typeCategoryField     = me.lookupReference('typeCategoryField');
        me.simpleDataTypeField   = me.lookupReference('simpleDataTypeField');
        me.enumDataTypeField     = me.lookupReference('enumDataTypeField');
        me.lookupEntityTypeField = me.lookupReference('lookupEntityTypeField');
        me.valueFieldContainer   = me.lookupReference('valueFieldContainer');
    },

    onDestroy: function () {
        var me = this;

        me.nameField             = null;
        me.displayNameField      = null;
        me.descriptionField      = null;
        me.requiredField         = null;
        me.readOnlyField         = null;
        me.typeCategoryField     = null;
        me.simpleDataTypeField   = null;
        me.enumDataTypeField     = null;
        me.lookupEntityTypeField = null;
        me.valueFieldContainer   = null;

        me.callParent(arguments);
    }
});
