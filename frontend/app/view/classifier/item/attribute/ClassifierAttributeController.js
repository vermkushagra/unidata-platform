/**
 * @author Ivan Marshalkin
 * @date 2016-08-11
 */

Ext.define('Unidata.view.classifier.item.attribute.ClassifierAttributeController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifier.item.attribute',

    updateClassifierNode: function (classifierNode) {
        var viewModel = this.getViewModel();

        viewModel.set('classifierNode', classifierNode);
    },

    updateDeletable: function (deletable) {
        var viewModel = this.getViewModel();

        viewModel.set('deletable', deletable);
    },

    updateNodeAttribute: function (nodeAttribute) {
        var view      = this.getView(),
            viewModel = this.getViewModel();

        if (view.rendered) {
            this.initFieldValues();
        }

        viewModel.set('nodeAttribute', nodeAttribute);
    },

    updateOwnAttribute: function (ownAttribute) {
        var viewModel = this.getViewModel();

        viewModel.set('ownAttribute', ownAttribute);

        this.refreshNodeAttribute();
    },

    updateInheritedAttribute: function (inheritedAttribute) {
        var view      = this.getView(),
            viewModel = this.getViewModel();

        viewModel.set('inheritedAttribute', inheritedAttribute);

        this.refreshNodeAttribute();
    },

    refreshNodeAttribute: function () {
        var view               = this.getView(),
            ownAttribute       = view.getOwnAttribute(),
            inheritedAttribute = view.getInheritedAttribute(),
            nodeAttribute      = null;

        if (ownAttribute) {
            nodeAttribute = ownAttribute;
        } else if (inheritedAttribute) {
            nodeAttribute = inheritedAttribute;
        }

        view.setNodeAttribute(nodeAttribute);
    },

    onViewAfterRender: function () {
        this.initFieldValues();
    },

    onRemoveClassifierNodeAttribute: function () {
        var view          = this.getView(),
            nodeAttribute = view.getNodeAttribute(),
            msg = Unidata.i18n.t('classifier>confirmRemoveAttribute'),
            classifierNode = view.getClassifierNode(),
            hasData = classifierNode.get('hasData');

        if (hasData) {
            msg = Unidata.i18n.t('classifier>haveRecordsClassifiedByNode') + '<br>' + msg;
        }

        this.showPrompt(Unidata.i18n.t('glossary:removeAttribute'), msg, function () {
            view.fireEvent('removeattribute', nodeAttribute);
        });
    },

    onAttributePropertyChange: function () {
        var properties;

        this.handleAttributePropertyChangeBeforeUpdateNode.apply(this, arguments);

        properties = this.getAttributeProperties();

        this.updateNodeAttributeProperty(properties);

        this.handleAttributePropertyChangeAfterUpdateNode.apply(this, arguments);

        // некоторые поля могут быть неактивны из-за значений логически связанных полей
        this.deactivateFieldsByValues();
    },

    handleAttributePropertyChangeBeforeUpdateNode: function (field) {
        var me = this;

        switch (field.getReference()) {
            case 'nameField':
                break;
            case 'displayNameField':
                break;
            case 'descriptionField':
                break;
            case 'requiredField':
                me.handleAttributePropertyRequiredChangeBeforeUpdateNode.apply(me, arguments);
                break;
            case 'readOnlyField':
                break;
            case 'typeCategoryField':
                me.handleAttributePropertyTypeCategoryChangeBeforeUpdateNode.apply(me, arguments);
                break;
            case 'enumDataTypeField':
            case 'simpleDataTypeField':
                me.handleAttributePropertySimpleDataTypeChangeBeforeUpdateNode.apply(me, arguments);
                break;
            case 'lookupEntityTypeField':
                break;
        }
    },

    handleAttributePropertyChangeAfterUpdateNode: function (field) {
        var me = this;

        switch (field.getReference()) {
            case 'nameField':
                break;
            case 'displayNameField':
                break;
            case 'descriptionField':
                break;
            case 'requiredField':
                break;
            case 'readOnlyField':
                break;
            case 'typeCategoryField':
                me.handleAttributePropertyTypeCategoryChangeAfterUpdateNode.apply(me, arguments);
                break;
            case 'enumDataTypeField':
            case 'simpleDataTypeField':
                me.handleAttributePropertySimpleDataTypeChangeAfterUpdateNode.apply(me, arguments);
                break;
            case 'lookupEntityTypeField':
                break;
        }
    },

    handleAttributePropertyTypeCategoryChangeBeforeUpdateNode: function (field, newValue) {
        this.displayFieldByTypeCategoryProperty(newValue);
    },

    handleAttributePropertySimpleDataTypeChangeBeforeUpdateNode: function () {
    },

    handleAttributePropertyRequiredChangeBeforeUpdateNode: function (field, newValue) {
        var view = this.getView(),
            field = view.readOnlyField;

        // если поля обязательно для заполнения, то недопустимо устанавливать значение readonly = true
        if (newValue === false) {
            field.setValue(false);
        }
    },

    handleAttributePropertyTypeCategoryChangeAfterUpdateNode: function () {
    },

    handleAttributePropertySimpleDataTypeChangeAfterUpdateNode: function () {
        var view          = this.getView(),
            nodeAttribute = view.getNodeAttribute();

        nodeAttribute.set('value', null);

        this.createValueField();
    },

    displayFieldByTypeCategoryProperty: function (typeCategory) {
        var view = this.getView(),
            fields = [
                'lookupEntityTypeField',
                'simpleDataTypeField',
                'enumDataTypeField'
            ],
            i = 0,
            ln = fields.length,
            fieldName,
            field;

        view.typeCategoryField.show();

        for (; i < ln; i++) {
            fieldName = fields[i];
            view[fieldName].hide();

            // поле, которе выбрано - не сбрасываем
            if (typeCategory + 'Field' != fieldName) {
                view[fieldName].setValue(null);
            }
        }

        if (field = view[typeCategory + 'Field']) {
            field.show();
        }
    },

    updateNodeAttributeProperty: function (properties) {
        var view          = this.getView(),
            nodeAttribute = view.getNodeAttribute();

        Ext.Object.each(properties, function (key, value) {
            nodeAttribute.set(key, value);
        });
    },

    getAttributeProperties: function () {
        var view = this.getView(),
            result;

        result = {
            name: view.nameField.getValue(),
            displayName: view.displayNameField.getValue(),
            description: view.descriptionField.getValue(),
            nullable: view.requiredField.getValue(),
            readOnly: view.readOnlyField.getValue(),
            typeCategory: view.typeCategoryField.getValue(),
            simpleDataType: view.simpleDataTypeField.getValue(),
            enumDataType: view.enumDataTypeField.getValue(),
            lookupEntityType: view.lookupEntityTypeField.getValue(),
            value: view.valueFieldContainer.field.getValue()
        };

        // для строки пустое значение должно быть интерпретировано как null
        if (result['simpleDataType'] === 'String' && Ext.isEmpty(result['value'])) {
            result['value'] = null;
        }

        return result;
    },

    initFieldValues: function () {
        var view          = this.getView(),
            nodeAttribute = view.getNodeAttribute(),
            fields;

        this.createValueField();
        fields = this.getPropertyFieldsList();

        Ext.Object.each(fields, function (propertyName, field) {
            var value = nodeAttribute.get(propertyName);

            if (nodeAttribute.get('simpleDataType') === 'Date' && propertyName === 'value') {
                value = Ext.Date.parse(value, 'c');
            }

            field.suspendEvent('change');
            field.setValue(value);
            field.resumeEvent('change');
        });

        this.displayFieldByTypeCategoryProperty(nodeAttribute.get('typeCategory'));

        // некоторые поля могут быть неактивны из-за значений логически связанных полей
        this.deactivateFieldsByValues();
    },

    getValueFieldDefaultCfg: function () {
        return {
            ui: 'un-field-default' ,
            fieldLabel: Unidata.i18n.t('classifier>value'),
            msgTarget: 'under',
            labelAlign: 'top',
            allowBlank: true,
            listeners: {
                change: {
                    fn: this.onAttributePropertyChange,
                    scope: this
                },
                focus: {
                    fn: this.onValueAttributePropertyFocus,
                    scope: this
                }
            },
            bind: {
                readOnly: '{attributeFieldNotUseHasDataReadOnly}'
            }
        };
    },

    valueFieldFactorySimpleDataType: function () {
        var view           = this.getView(),
            nodeAttribute  = view.getNodeAttribute(),
            simpleDataType = nodeAttribute.get('simpleDataType'),
            field,
            fieldCfg,
            defaultFieldCfg;

        defaultFieldCfg = this.getValueFieldDefaultCfg();

        switch (simpleDataType) {
            case 'String':
                fieldCfg = {
                    xtype: 'textfield'
                };
                break;
            case 'Integer':
                fieldCfg = {
                    xtype: 'numberfield',
                    allowDecimals: false
                };
                break;
            case 'Number':
                fieldCfg = {
                    xtype: 'numberfield'
                };
                break;
            case 'Boolean':
                fieldCfg = {
                    xtype: 'combobox',
                    valueField: 'value',
                    displayField: 'name',
                    editable: false,
                    store: {
                        fields: [
                            'name',
                            'value'
                        ],
                        data: [
                            {
                                name: Unidata.i18n.t('common:yes'),
                                value: true
                            },
                            {
                                name: Unidata.i18n.t('common:no'),
                                value: false
                            },
                            {
                                name: Unidata.i18n.t('classifier>notSet'),
                                value: null
                            }
                        ]
                    }
                };
                break;
            case 'Date':
                fieldCfg = {
                    xtype: 'datefield'
                };
                break;
            case 'Timestamp':
                fieldCfg = {
                    xtype: 'datetimefield'
                };
                break;
            case 'Time':
                fieldCfg = {
                    xtype: 'timetextfield'
                };
                break;
            default:
                fieldCfg = {
                    xtype: 'textfield',
                    readOnly: true,
                    disabled: true
                };
                break;
        }

        field = Ext.widget(Ext.applyIf(fieldCfg, defaultFieldCfg));

        return field;
    },

    valueFieldFactoryEnumDataType: function () {
        var view = this.getView(),
            enumerationStore = Unidata.util.api.Enumeration.getStore(),
            enumerationName = view.enumDataTypeField.getValue(),
            cfg = this.getValueFieldDefaultCfg(),
            combobox;

        cfg.editable = false;

        combobox = Unidata.util.Enumeration.createEnumerationComboBox(enumerationStore, enumerationName, cfg);

        return combobox;
    },

    valueFieldFactory: function () {
        var view = this.getView(),
            typeCategory = view.typeCategoryField.getValue();

        switch (typeCategory) {
            case 'enumDataType':
                return this.valueFieldFactoryEnumDataType();
            case 'simpleDataType':
            default:
                return this.valueFieldFactorySimpleDataType();
        }
    },

    createValueField: function () {
        var view                = this.getView(),
            nodeAttribute       = view.getNodeAttribute(),
            simpleDataType      = nodeAttribute.get('simpleDataType'),
            valueFieldContainer = view.valueFieldContainer,
            field;

        field = this.valueFieldFactory();

        valueFieldContainer.removeAll();
        valueFieldContainer.add(field);

        valueFieldContainer.field = field;

        return valueFieldContainer.field;
    },

    onValueAttributePropertyFocus: function () {
        var view               = this.getView(),
            classifierNode     = view.getClassifierNode(),
            ownAttribute       = view.getOwnAttribute(),
            inheritedAttribute = view.getInheritedAttribute(),
            nodeAttribute      = view.getNodeAttribute();

        if (nodeAttribute === inheritedAttribute &&  inheritedAttribute.get('value') === null) {
            // выполняем на следующем тике иначе ExtJs падает с ошибкой на установке классов элементу уже не существующему
            setTimeout(function () {
                ownAttribute = Ext.create('Unidata.model.attribute.ClassifierNodeAttribute', inheritedAttribute.getData());

                classifierNode.nodeAttrs().add(ownAttribute);

                view.setOwnAttribute(ownAttribute);

                view.valueFieldContainer.field.focus();
            }, 0);
        }
    },

    onRestoreClassifierNodeAttribute: function () {
        var view               = this.getView(),
            classifierNode     = view.getClassifierNode(),
            ownAttribute       = view.getOwnAttribute(),
            nodeAttribute      = view.getNodeAttribute();

        if (nodeAttribute === ownAttribute) {
            classifierNode.nodeAttrs().remove(ownAttribute);

            view.setOwnAttribute(null);
        }
    },

    /**
     * Подсвечивает ошибки по полям
     */
    highlightErrors: function () {
        var view          = this.getView(),
            nodeAttribute = view.getNodeAttribute(),
            hasError      = false,
            fieldErrors;

        view.nameField.clearInvalid();
        view.displayNameField.clearInvalid();
        view.typeCategoryField.clearInvalid();
        view.simpleDataTypeField.clearInvalid();

        // поле имя
        fieldErrors = this.getNameFieldErrors();

        if (Ext.isArray(fieldErrors) && fieldErrors.length) {
            view.nameField.markInvalid(fieldErrors);
            hasError = true;
        }

        // поле отображаемое имя
        fieldErrors = this.getFieldErrors('displayName');

        if (Ext.isArray(fieldErrors) && fieldErrors.length) {
            view.displayNameField.markInvalid(fieldErrors);
            hasError = true;
        }

        // поле тип значения
        if (!nodeAttribute.isValidSimpleDataTypeField()) {
            if (Ext.isEmpty(view.typeCategoryField.getValue())) {
                view.typeCategoryField.markInvalid(Unidata.i18n.t('classifier>notSelectType'));
            }

            view.simpleDataTypeField.markInvalid(Unidata.i18n.t('classifier>notSelectType'));
            hasError = true;
        }

        // разворачиваем панель если были ошибки
        if (hasError) {
            view.expand();
        }
    },

    /**
     * Сбрасывает показ ошибок
     */
    resetErrors: function () {
        var view = this.getView();

        view.nameField.clearInvalid();
        view.displayNameField.clearInvalid();
        view.simpleDataTypeField.clearInvalid();
    },

    getFieldErrors: function (fieldName) {
        var view          = this.getView(),
            nodeAttribute = view.getNodeAttribute(),
            fieldErrors   = [],
            modelField,
            fieldValidate;

        modelField    = nodeAttribute.getField(fieldName);
        fieldValidate = modelField.validate(nodeAttribute.get(fieldName));

        if (fieldValidate !== true) {
            fieldErrors.push(fieldValidate);
        }

        fieldErrors = Ext.Array.merge(fieldErrors);

        return fieldErrors;
    },

    getNameFieldErrors: function () {
        var view           = this.getView(),
            nodeAttribute  = view.getNodeAttribute(),
            classifierNode = view.getClassifierNode(),
            fieldErrors;

        fieldErrors = this.getFieldErrors('name');

        if (!classifierNode.isAttributeNameUnique(nodeAttribute.get('name'))) {
            fieldErrors = Ext.Array.merge(fieldErrors, Unidata.i18n.t('classifier>nameNotUnique'));
        }

        return fieldErrors;
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);
    },

    deactivateFieldsByValues: function () {
        var fields = this.getPropertyFieldsList(),
            readOnlyFields = this.getReadOnlyFieldsByValues(),
            disabledFields = this.getDisabledFieldsByValues();

        // дизаблим поля
        Ext.Object.each(disabledFields, function (propertyName, propertyValue) {
            var field = fields[propertyName];

            field.setDisabled(propertyValue);
        });

        // делаем поля только для чтения
        Ext.Object.each(readOnlyFields, function (propertyName, propertyValue) {
            var field = fields[propertyName];

            field.setReadOnly(propertyValue);
        });
    },

    getReadOnlyFieldsByValues: function () {
        var readOnlyFields = {};

        return readOnlyFields;
    },

    getDisabledFieldsByValues: function () {
        var fieldValues = this.getAttributeProperties(),
            disabledFields;

        disabledFields = {
            readOnly: false
        };

        if (fieldValues['nullable'] === false) {
            disabledFields['readOnly'] = true;
        }

        return disabledFields;
    },

    getPropertyFieldsList: function () {
        var view = this.getView(),
            fields;

        fields = {
            name: view.nameField,
            displayName: view.displayNameField,
            description: view.descriptionField,
            nullable: view.requiredField,
            readOnly: view.readOnlyField,
            typeCategory: view.typeCategoryField,
            simpleDataType: view.simpleDataTypeField,
            enumDataType: view.enumDataTypeField,
            lookupEntityType: view.lookupEntityTypeField,
            value: view.valueFieldContainer.field
        };

        return fields;
    }
});
