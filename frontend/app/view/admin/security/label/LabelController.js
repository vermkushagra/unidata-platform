Ext.define('Unidata.view.admin.security.label.LabelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.security.label',

    onAddSecurityLabelClick: function () {
        var securityLabel = Ext.create('Unidata.model.user.SecurityLabelRole'),
            labelGrid = this.lookupReference('securityLabelsGrid'),
            addedRecord;

        addedRecord = labelGrid.getStore().add(securityLabel);
        labelGrid.getSelectionModel().doSelect(addedRecord[0]);
    },

    onDeleteClick: function (button) {
        var title = Unidata.i18n.t('admin.security>removeSecurityLabel'),
            text = Unidata.i18n.t('admin.security>confirmRemoveSecurityLabel');

        this.showPrompt(title, text, this.deleteCurrentSecurityLabel, this, button);
    },

    deleteCurrentSecurityLabel: function () {
        var me = this,
            viewModel = this.getViewModel(),
            securityLabelsStore = viewModel.get('securityLabels'),
            currentSecurityLabel;

        currentSecurityLabel = viewModel.get('currentSecurityLabel');

        currentSecurityLabel.setId(currentSecurityLabel.get('name'));

        if (currentSecurityLabel.isModified('name')) {
            currentSecurityLabel.setId(currentSecurityLabel.getModified('name'));
        }

        currentSecurityLabel.erase({
            success: function () {
                me.showMessage(Unidata.i18n.t('admin.security>removeSecurityLabelSuccess'));
                securityLabelsStore.reload();
            }
        });
    },

    onSaveClick: function () {
        var me                   = this,
            view                 = this.getView(),
            viewModel            = this.getViewModel(),
            container            = view.lookupReference('attributeContainer'),
            securityLabelsGrid   = view.lookupReference('securityLabelsGrid'),
            toEntityCombobox     = this.lookupReference('toEntity'),
            currentSecurityLabel = viewModel.get('currentSecurityLabel'),
            nestedData,
            i;

        if (!this.checkSave()) {
            return;
        }

        currentSecurityLabel.attributes().removeAll();

        container.items.each(function (item) {
            currentSecurityLabel.attributes().add({
                id: item.attributeId,
                name: item.nameField.getValue(),
                path: [toEntityCombobox.getValue(), item.attributeField.getValue()].join('.'),
                value: [toEntityCombobox.getValue(), item.attributeField.getValue()].join('.')
            });
        });

        currentSecurityLabel.setId(currentSecurityLabel.get('name'));

        if (currentSecurityLabel.isModified('name')) {
            currentSecurityLabel.setId(currentSecurityLabel.getModified('name'));
        }

        nestedData = currentSecurityLabel.getAssociatedData(null, {serialize: true, persist: true});

        for (i in nestedData) {
            if (nestedData.hasOwnProperty(i)) {
                currentSecurityLabel.set(i, nestedData[i]);
            }
        }

        currentSecurityLabel.save({
            success: function () {
                var list                   = viewModel.getStore('securityLabels'),
                    currentSecurityLabelId = currentSecurityLabel.getId();

                me.onDeselectSecurityLabel();

                list.load();
                list.on('load', function () {
                    var index;

                    index = list.findExact('name', currentSecurityLabelId);

                    if (index !== -1) {
                        securityLabelsGrid.getSelectionModel().select(index);
                    }
                }, this, {single: true});

                me.showMessage(Unidata.i18n.t('admin.common>dataSaveSuccess'));
            }
        });
    },

    checkSave: function () {
        var labelName = this.lookupReference('labelName'),
            labelForm = this.lookupReference('labelForm'),
            name = labelName.getValue(),
            viewModel = this.getViewModel(),
            securityLabels = viewModel.get('securityLabels'),
            currentSecurityLabel = viewModel.get('currentSecurityLabel'),
            canSave = true;

        securityLabels.each(function (securityLabel) {
            if (currentSecurityLabel !== securityLabel && name === securityLabel.get('name')) {
                Unidata.showError(Unidata.i18n.t('admin.security>labelExists'));

                canSave = false;
            }
        });

        if (!labelForm.isValid()) {
            Unidata.showError(Unidata.i18n.t('admin.security>invalidForm'));

            canSave = false;
        }

        if (!this.getAttributeCount()) {
            Unidata.showError(Unidata.i18n.t('admin.security>attributesNotSet'));

            canSave = false;
        }

        try {
            this.checkCorrectionOfAttributeNames();
            this.checkCorrectionOfAttributeValues();
        } catch (e) {
            canSave = false;
            Unidata.showError(e.message);
        }

        return canSave;
    },

    onSelectSecurityLabel: function (grid, currentSecurityLabel) {
        var panel = this.lookupReference('securityLabelPanel'),
            toEntityCombobox = this.lookupReference('toEntity'),
            entityName,
            attributeRec,
            selectedRecord,
            canCreate = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'create'),
            canWrite  = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'update'),
            viewModel;

        viewModel = this.getViewModel();

        if (canCreate && currentSecurityLabel.phantom) {
            canWrite = true;
        }

        viewModel.set('readOnly', !canWrite);

        if (!currentSecurityLabel.phantom || !canWrite) {
            viewModel.set('nameReadOnly', true);
        } else {
            viewModel.set('nameReadOnly', !canWrite);
        }

        viewModel.set('currentSecurityLabel', currentSecurityLabel);

        attributeRec = currentSecurityLabel.attributes().getAt(0);

        if (attributeRec) {
            entityName = attributeRec.get('path').split('.')[0];

            if (entityName) {
                toEntityCombobox.suspendEvent('select');
                toEntityCombobox.setValue(entityName);
                toEntityCombobox.resumeEvent('select');

                selectedRecord = toEntityCombobox.getRecord();
                this.loadEntityInfo(selectedRecord);
            }
        }

        this.createAllAttributeContainer(currentSecurityLabel);

        if (!this.getAttributeCount()) {
            this.createAttributeContainer();
        }

        this.updateDeleteButtonAttributeContainer();

        panel.setHidden(false);
    },

    onDeselectSecurityLabel: function () {
        var panel = this.lookupReference('securityLabelPanel'),
            toEntityCombobox = this.lookupReference('toEntity');

        panel.setHidden(true);
        toEntityCombobox.setValue(null);
        this.removeAllAttributeContainer();
    },

    onSelectRecord: function (combobox, record) {
        var view = this.getView(),
            container = view.lookupReference('attributeContainer'),
            item = this.createAttributeContainer();

        this.removeAllAttributeContainer();

        container.add(item);

        this.updateDeleteButtonAttributeContainer();

        this.loadEntityInfo(record);
    },

    loadEntityInfo: function (record) {
        var viewModel  = this.getViewModel(),
            entityName = record.get('name'),
            entityType = record.get('type', 'value'),
            recordEntity;

        recordEntity = Ext.create('Unidata.model.entity.' +  entityType, {});

        recordEntity.setId(entityName);
        recordEntity.load({
            scope: this
        });
        //TODO: add failure handling

        viewModel.set('recordEntity', recordEntity);
    },

    onAttributeAddClick: function () {
        var view = this.getView(),
            container = view.lookupReference('attributeContainer'),
            item = this.createAttributeContainer();

        container.add(item);

        this.updateDeleteButtonAttributeContainer();
    },

    createAttributeContainer: function (id, name, attribute) {
        var me = this,
            cfg,
            component;

        cfg = {
            xtype: 'container',
            nameField: null,
            attributeField: null,
            attributeId: id,
            deleteButton: null,
            referenceHolder: true,
            margin: '10 0',
            layout: {
                type: 'hbox',
                align: 'top'
            },
            items: [
                {
                    xtype: 'textfield',
                    reference: 'name',
                    fieldLabel: Unidata.i18n.t('glossary:name'),
                    value: name,
                    validator: function (name) {
                        var regexp = /^[а-яёa-z0-9_., -]{1,}$/i;

                        if (!name) {
                            return Unidata.i18n.t('validation:field.required');
                        }

                        if (!regexp.test(name)) {
                            return Unidata.i18n.t('admin.security>invalidSymolsInField');
                        }

                        return true;
                    },
                    bind: {
                        editable: '{!readOnly}'
                    },
                    listeners: {
                        blur: function (field) {
                            var value = field.getValue();

                            value = Ext.String.trim(value);
                            field.setValue(value);
                        }
                    },
                    margin: '0 20 0 0',
                    msgTarget: 'under',
                    flex: 1
                },
                {
                    xtype: 'combobox',
                    reference: 'attribute',
                    fieldLabel: Unidata.i18n.t('glossary:attribute'),
                    value: attribute,
                    bind: {
                        store: '{attributeStore}',
                        readOnly: '{readOnly}'
                    },
                    editable: false,
                    displayField: 'displayName',
                    valueField: 'name',
                    margin: '0 20 0 0',
                    flex: 1
                },
                {
                    xtype: 'button',
                    text: Unidata.i18n.t('common:delete'),
                    reference: 'deleteButton',
                    margin: '0 5 0 0',
                    handler: function () {
                        this.up('container').destroy();
                        me.updateDeleteButtonAttributeContainer();
                    },
                    bind: {
                        hidden: '{readOnly}'
                    }
                }

            ]
        };

        component = Ext.create(cfg);
        component.nameField = component.lookupReference('name');
        component.attributeField = component.lookupReference('attribute');
        component.deleteButton = component.lookupReference('deleteButton');

        return component;
    },

    removeAllAttributeContainer: function () {
        var view = this.getView(),
            container = view.lookupReference('attributeContainer');

        container.items.each(function (item) {
            item.destroy();
        });
    },

    createAllAttributeContainer: function (securityLabel) {
        var me = this,
            view = this.getView(),
            container = view.lookupReference('attributeContainer'),
            item,
            tmpArray;

        securityLabel.attributes().each(function (attribute) {
            tmpArray = attribute.get('path').split('.');

            item = me.createAttributeContainer(attribute.get('id'), attribute.get('name'), tmpArray[1]);

            container.add(item);
        });
    },

    getAttributeCount: function () {
        var container = this.lookupReference('attributeContainer');

        return container.items.length;
    },

    checkCorrectionOfAttributeNames: function () {
        var container = this.lookupReference('attributeContainer'),
            arrNames = [],
            uniqueNames = [],
            name;

        container.items.each(function (item) {
            name = item.nameField.getValue();

            arrNames.push(name);

            if (name === '') {
                throw new Error(Unidata.i18n.t('admin.security>filledNotAllAttributeNames'));
            }
        });

        uniqueNames = Ext.Array.unique(arrNames);

        if (arrNames.length !== uniqueNames.length) {
            throw new Error(Unidata.i18n.t('admin.security>attributeNamesShouldBeUnique'));
        }
    },

    checkCorrectionOfAttributeValues: function () {
        var container = this.lookupReference('attributeContainer'),
            arrAttrs = [],
            uniqueAttributes = [],
            attr;

        container.items.each(function (item) {
            attr = item.attributeField.getValue();

            arrAttrs.push(attr);

            if (attr === null) {
                throw new Error(Unidata.i18n.t('admin.security>filledNotAllAttributeValues'));
            }
        });

        uniqueAttributes = Ext.Array.unique(arrAttrs);

        if (arrAttrs.length !== uniqueAttributes.length) {
            throw new Error(Unidata.i18n.t('admin.security>attributeValuesShouldBeUnique'));
        }
    },

    updateDeleteButtonAttributeContainer: function () {
        var container = this.lookupReference('attributeContainer'),
            disabled = false;

        if (this.getAttributeCount() === 1) {
            disabled = true;
        }

        container.items.each(function (item) {
            item.deleteButton.setDisabled(disabled);
        });
    }
});
