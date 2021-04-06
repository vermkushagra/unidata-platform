Ext.define('Unidata.view.admin.entity.metarecord.consolidation.ConsolidationController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.consolidation',

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            sourceSystemsStore = viewModel.getStore('sourceSystems'),
            draftMode = view.draftMode;

        sourceSystemsStore.load({
            params: {
                draft: draftMode
            }
        });

        viewModel.set('bvtRecords', new Ext.util.Collection());
        viewModel.set('bvrRecords', new Ext.util.Collection());
    },

    onComponentAfterRender: function () {
        var metaRecord    = this.getCurrentRecord(),
            viewModel     = this.getViewModel(),
            bvtGrid       = this.lookupReference('bvtGridWeight'),
            columnManager = bvtGrid.getColumnManager(),
            gridColumns   = columnManager.getColumns(),
            bvtFieldSet   = this.lookupReference('bvtFieldSet'),
            bvrFieldSet   = this.lookupReference('bvrFieldSet'),
            attributeWeightStore,
            sourceSystemWeightStore;

        if (!gridColumns.length) {
            this.reconfigureBvtGrid();
        }

        this.updateMergeSettings(metaRecord);

        attributeWeightStore = this.getBvtAttributeStore(metaRecord);
        sourceSystemWeightStore = this.getBvrSourceSystemsConfigStore(metaRecord);

        bvtFieldSet.setCheckBoxValue(attributeWeightStore.getCount() > 0);
        bvrFieldSet.setCheckBoxValue(sourceSystemWeightStore.getCount() > 0);

        viewModel.setupStore(attributeWeightStore, 'attributeWeight');
        viewModel.setupStore(sourceSystemWeightStore, 'sourceSystemWeight');
    },

    onSourceSystemsStoreLoad: function (store, records, success, eOpts) {
        var bvtGrid   = this.lookupReference('bvtGridWeight'),
            viewModel = this.getViewModel(),
            response,
            responseText,
            adminSystemName;

        if (!success) {
            return;
        }

        response = eOpts.getResponse();

        if (!response) {
            return;
        }

        responseText = Ext.decode(response.responseText, true);

        if (responseText) {
            adminSystemName = responseText.adminSystemName;

            viewModel.set('adminSystemName', adminSystemName);
        }

        if (bvtGrid.rendered) {
            this.reconfigureBvtGrid();
        }
    },

    createNewBvtSourceSystemWeight: function (metaRecord) {
        var me                 = this,
            viewModel          = this.getViewModel(),
            sourceSystemsStore = viewModel.getStore('sourceSystems'),
            attributes         = this.getBvtAttributeStore(metaRecord);

        sourceSystemsStore.each(function (sourceSystemRecord) {
            var sourceSystemName   = sourceSystemRecord.get('name'),
                sourceSystemWeight = -1;

            attributes.each(function (attributeRecord) {
                var attributeName = attributeRecord.get('name');

                me.createDefaultBvtAttributeWeight(metaRecord, attributeName, sourceSystemName, sourceSystemWeight);
            });
        });
    },

    createNewBvrSourceSystemWeight: function (metaRecord) {
        var me                = this,
            viewModel         = this.getViewModel(),
            sourceSystemStore = viewModel.getStore('sourceSystems');

        sourceSystemStore.each(function (sourceSystemRecord) {
            var sourceSystemName   = sourceSystemRecord.get('name'),
                sourceSystemWeight = -1;

            me.createDefaultBvrSourceSystemWeight(metaRecord, sourceSystemName, sourceSystemWeight);
        });
    },

    updateMergeSettings: function (metaRecord) {
        this.updateMergeSettingsModelStructure(metaRecord);

        if (this.getBvrSourceSystemConfigCount(metaRecord)) {
            this.createNewBvrSourceSystemWeight(metaRecord);
            this.dropPhantomBvrSourceSystemWeight(metaRecord);
        }

        if (this.getBvtAttributeCount(metaRecord)) {
            this.createNewBvtSourceSystemWeight(metaRecord);
            this.dropPhantomBvtAttributeWeight(metaRecord);
            this.dropPhantomBvtSourceSystemWeight(metaRecord);
        }
    },

    getCurrentRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('currentRecord');
    },

    updateMergeSettingsModelStructure: function (metaRecord) {
        var mergeSettings;

        if (!metaRecord.getMergeSettings()) {
            metaRecord.setMergeSettings(Ext.create('Unidata.model.mergesettings.MergeSettings'));
        }

        mergeSettings = metaRecord.getMergeSettings();

        if (!mergeSettings.getBvtMergeSettings()) {
            mergeSettings.setBvtMergeSettings(Ext.create('Unidata.model.mergesettings.Bvt'));
        }

        if (!mergeSettings.getBvrMergeSettings()) {
            mergeSettings.setBvrMergeSettings(Ext.create('Unidata.model.mergesettings.Bvr'));
        }
    },

    getBvtAttributeStore: function (metaRecord) {
        var mergeSettings    = metaRecord.getMergeSettings(),
            bvtMergeSettings = mergeSettings.getBvtMergeSettings(),
            attributes       = bvtMergeSettings.attributes();

        return attributes;
    },

    getBvrSourceSystemsConfigStore: function (metaRecord) {
        var mergeSettings       = metaRecord.getMergeSettings(),
            bvrMergeSettings    = mergeSettings.getBvrMergeSettings(),
            sourceSystemsConfig = bvrMergeSettings.sourceSystemsConfig();

        return sourceSystemsConfig;
    },

    getAttributeNameAtFirstLevel: function (metaRecord) {
        var MetaRecordUtil = Unidata.util.MetaRecord,
            attributeNames = [];

        function addAttributeName (attribute) {
            attributeNames.push(attribute.get('name'));
        }

        metaRecord.simpleAttributes().each(addAttributeName);

        metaRecord.arrayAttributes().each(addAttributeName);

        if (Ext.isFunction(metaRecord.aliasCodeAttributes)) {
            metaRecord.aliasCodeAttributes().each(addAttributeName);
        }

        if (MetaRecordUtil.isEntity(metaRecord)) {
            metaRecord.complexAttributes().each(addAttributeName);
        }

        return attributeNames;
    },

    fillBvtAttributeWeight: function (metaRecord) {
        var me                 = this,
            viewModel          = this.getViewModel(),
            sourceSystemsStore = viewModel.getStore('sourceSystems'),
            attributeNames     = this.getAttributeNameAtFirstLevel(metaRecord);

        sourceSystemsStore.each(function (sourceSystemRecord) {
            var sourceSystemName   = sourceSystemRecord.get('name'),
                sourceSystemWeight = sourceSystemRecord.get('weight');

            Ext.Array.each(attributeNames, function (attributeName) {
                me.createDefaultBvtAttributeWeight(metaRecord, attributeName, sourceSystemName, sourceSystemWeight);
            });
        });
    },

    clearBvtAttributreWeight: function (metaRecord) {
        var store = this.getBvtAttributeStore(metaRecord);

        store.removeAll();
    },

    fillBvrSourceSystemWeight: function (metaRecord) {
        var me                = this,
            viewModel         = this.getViewModel(),
            sourceSystemStore = viewModel.getStore('sourceSystems');

        sourceSystemStore.each(function (sourceSystemRecord) {
            var sourceSystemName   = sourceSystemRecord.get('name'),
                sourceSystemWeight = sourceSystemRecord.get('weight');

            me.createDefaultBvrSourceSystemWeight(metaRecord, sourceSystemName, sourceSystemWeight);
        });
    },

    clearBvrSourceSystemWeight: function (metaRecord) {
        var store = this.getBvrSourceSystemsConfigStore(metaRecord);

        store.removeAll();
    },

    createDefaultBvrSourceSystemWeight: function (metaRecord, sourceSystemName, defaultWeight) {
        var sourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord),
            idx;

        idx = sourceSystemsConfig.findExact('name', sourceSystemName);

        if (idx === -1) {
            sourceSystemsConfig.add({
                name: sourceSystemName,
                description: '',
                weight: defaultWeight
            });
        }
    },

    dropPhantomBvtAttributeWeight: function (metaRecord) {
        var attributeNames = this.getAttributeNameAtFirstLevel(metaRecord),
            attributes     = this.getBvtAttributeStore(metaRecord),
            remove         = [];

        attributes.each(function (attribute) {
            if (!Ext.Array.contains(attributeNames, attribute.get('name'))) {
                remove.push(attribute);
            }
        });

        attributes.remove(remove);
    },

    dropPhantomBvtSourceSystemWeight: function (metaRecord) {
        var viewModel          = this.getViewModel(),
            attributes         = this.getBvtAttributeStore(metaRecord),
            sourceSystemsStore = viewModel.getStore('sourceSystems');

        attributes.each(function (attribute) {
            var sourceSystemsConfig = attribute.sourceSystemsConfig(),
                remove              = [];

            sourceSystemsConfig.each(function (sourceSystemsConfigRecord) {
                if (sourceSystemsStore.findExact('name', sourceSystemsConfigRecord.get('name')) === -1) {
                    remove.push(sourceSystemsConfigRecord);
                }
            });

            sourceSystemsConfig.remove(remove);
        });
    },

    dropPhantomBvrSourceSystemWeight: function (metaRecord) {
        var viewModel           = this.getViewModel(),
            sourceSystemsStore  = viewModel.getStore('sourceSystems'),
            sourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord),
            remove              = [];

        sourceSystemsConfig.each(function (config) {
            if (sourceSystemsStore.findExact('name', config.get('name')) === -1) {
                remove.push(config);
            }
        });

        sourceSystemsConfig.remove(remove);
    },

    createDefaultBvtAttributeWeight: function (metaRecord, attributeName, sourceSystemName, weigth) {
        var attributes = this.getBvtAttributeStore(metaRecord),
            attributesByName,
            sourceSystemsConfig,
            idx;

        if (attributes.findExact('name', attributeName) === -1) {
            attributes.add({
                name: attributeName
            });
        }

        idx = attributes.findExact('name', attributeName);
        attributesByName = attributes.getAt(idx);

        sourceSystemsConfig = attributesByName.sourceSystemsConfig();
        idx = sourceSystemsConfig.findExact('name', sourceSystemName);

        if (idx === -1) {
            sourceSystemsConfig.add({
                name: sourceSystemName,
                weight: weigth
            });
        }
    },

    reconfigureBvtGrid: function () {
        var me                = this,
            viewModel         = this.getViewModel(),
            sourceSystemStore = viewModel.getStore('sourceSystems'),
            bvtGrid           = this.lookupReference('bvtGridWeight'),
            columns           = [],
            metaRecord        = this.getCurrentRecord();

        columns.push({
            xtype: 'widgetcolumn',
            width: 40,
            widget: {
                xtype: 'button',
                ui: 'un-toolbar-block-panel',
                scale: 'small',
                tooltip: Unidata.i18n.t('common:delete'),
                iconCls: 'icon-trash2',
                handler: function (button) {
                    var grid   = this.up('grid'),
                        store  = grid.getStore(),
                        record = button.getWidgetRecord();

                    store.remove(record);

                    me.updateUnselectedBvtAttributeStore();
                },
                bind: {
                    disabled: '{metaRecordViewReadOnly}'
                }
            }
        });
        columns.push({
            header: Unidata.i18n.t('glossary:attribute'),
            dataIndex: 'attribute_name',
            sortable: true,
            resizable: true,
            hideable: false,
            menuDisabled: true,
            width: 400,
            renderer: function (value, meta) {
                var record        = meta.record,
                    metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, record.get('name'));

                return metaAttribute.get('displayName');
            }
        });

        sourceSystemStore.each(function (sourceSystemRecord) {
            var sourceSystemName = sourceSystemRecord.get('name'),
                column;

            column = {
                header: sourceSystemName,
                dataIndex: sourceSystemName,
                sortable: false,
                resizable: false,
                hideable: false,
                menuDisabled: true,
                editor: {
                    xtype: 'numberfield',
                    hideTrigger: true,
                    allowDecimals: false,
                    minValue: 0,
                    maxValue: 99,
                    validateOnChange: true,
                    selectOnFocus: true,
                    listeners: {
                        // запрещаем вставлять из буфера
                        paste: {
                            element: 'inputEl',
                            fn: function (event) {
                                event.preventDefault();
                            }
                        },
                        change: function (field, newValue, oldValue) {
                            if (!field.isValid()) {
                                field.setValue(oldValue);
                            }
                        },
                        render: function () {
                            var checkWeightUnique = Unidata.Config.getCheckSourceSystemWeightUnique();

                            if (!checkWeightUnique) {
                                this.setMaxValue(100);
                            }
                        }
                    }
                },
                renderer: function (value, meta) {
                    var record              = meta.record,
                        column              = meta.column,
                        sourceSystemsConfig = record.sourceSystemsConfig(),
                        result              = '';

                    sourceSystemsConfig.each(function (sourceSystem) {
                        if (sourceSystem.get('name') === column.dataIndex) {
                            result = sourceSystem.get('weight');
                        }
                    });

                    if (result === -1) {
                        result = Unidata.i18n.t('glossary:notSet');
                    }

                    return result;
                }
            };

            columns.push(column);
        });

        bvtGrid.reconfigure(columns);
    },

    onAttributeWeightBeforeCellEdit: function (editor, context) {
        var viewModel = this.getViewModel(),
            adminSystemName = viewModel.get('adminSystemName');

        if (viewModel.get('metaRecordViewReadOnly')) {
            return false;
        }

        if (context.column.dataIndex === adminSystemName) {
            return false;
        }
    },

    onAttributeWeightCellValidateEdit: function (editor, context) {
        var record              = context.record,
            column              = context.column,
            sourceSystemsConfig = record.sourceSystemsConfig(),
            isValid             = true,
            checkWeightUnique   = Unidata.Config.getCheckSourceSystemWeightUnique();

        if (!checkWeightUnique) {
            return isValid;
        }

        if (context.value) {
            sourceSystemsConfig.each(function (sourceSystem) {
                if (sourceSystem.get('name') !== column.dataIndex && sourceSystem.get('weight') === context.value) {
                    isValid = false;
                }
            });
        }

        return isValid;
    },

    onAttributeWeightCellEdit: function (editor, context) {
        var record              = context.record,
            column              = context.column,
            sourceSystemsConfig = record.sourceSystemsConfig();

        if (context.value === null) {
            context.value = -1;
        }

        sourceSystemsConfig.each(function (sourceSystem) {
            if (sourceSystem.get('name') === column.dataIndex) {
                sourceSystem.set('weight', context.value);
            }
        });

        if (record) {
            record.commit();
        }
    },

    onSourceSystemWeightBeforeCellEdit: function (editor, context) {
        var record          = context.record,
            viewModel       = this.getViewModel(),
            adminSystemName = viewModel.get('adminSystemName');

        if (record.get('name') === adminSystemName) {
            return false;
        }
    },

    onSourceSystemWeightCellValidateEdit: function (editor, context) {
        var metaRecord          = this.getCurrentRecord(),
            record              = context.record,
            sourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord),
            value               = context.value,
            isValid             = true,
            checkWeightUnique   = Unidata.Config.getCheckSourceSystemWeightUnique();

        if (!checkWeightUnique) {
            return isValid;
        }

        if (value) {
            sourceSystemsConfig.each(function (sourceSystemConfigRecord) {
                var weight = sourceSystemConfigRecord.get('weight');

                if (sourceSystemConfigRecord !== record && weight === value) {
                    isValid = false;
                }
            });
        }

        return isValid;
    },

    onSourceSystemWeightCellEdit: function (editor, context) {
        var record = context.record,
            value  = context.value;

        record.set('weight', value);

        record.commit();
    },

    onSourceSystemWeightToggle: function (fieldSet, checked) {
        var metaRecord = this.getCurrentRecord(),
            bvrGrid    = this.lookupReference('bvrGridWeight');

        if (checked) {
            if (this.getBackupBvrCount()) {
                this.restoreBvrRecord();
            } else {
                this.fillBvrSourceSystemWeight(metaRecord);
            }
        } else {
            this.backupBvrRecord();
            this.clearBvrSourceSystemWeight(metaRecord);
        }

        bvrGrid.getView().refresh();
    },

    onAttributeWeightToggle: function (fieldSet, checked) {
        var metaRecord        = this.getCurrentRecord(),
            bvtGrid           = this.lookupReference('bvtGridWeight'),
            attributeCombobox = this.lookupReference('unselectedBvtAttributeCombo');

        if (checked) {
            this.restoreBvtRecord();
            attributeCombobox.setDisabled(false);
        } else {
            this.backupBvtRecord();
            this.clearBvtAttributreWeight(metaRecord);
            attributeCombobox.setDisabled(true);
        }

        bvtGrid.getView().refresh();
        this.updateUnselectedBvtAttributeStore();
    },

    refreshBvtGridView: function () {
        var bvtGrid = this.lookupReference('bvtGridWeight');

        if (bvtGrid.rendered) {
            bvtGrid.getView().refresh();
        }
    },

    updateUnselectedBvtAttributeStore: function () {
        var MetaRecordUtil = Unidata.util.MetaRecord,
            metaRecord     = this.getCurrentRecord(),
            combobox       = this.lookupReference('unselectedBvtAttributeCombo'),
            comboboxStore  = combobox.getStore(),
            attributeStore = this.getBvtAttributeStore(metaRecord),
            filter;

        comboboxStore.clearFilter();
        comboboxStore.removeAll();

        function addAttributeToStore (attribute) {
            // weblink не участвует в консолидации т.к. атрибут является вычисляемым
            if (attribute.isLinkDataType()) {
                return;
            }

            comboboxStore.add({
                name: attribute.get('name'),
                displayName: attribute.get('displayName')
            });
        }

        if (MetaRecordUtil.isEntity(metaRecord)) {
            metaRecord.complexAttributes().each(addAttributeToStore);
        }

        metaRecord.simpleAttributes().each(addAttributeToStore);

        metaRecord.arrayAttributes().each(addAttributeToStore);

        if (Ext.isFunction(metaRecord.aliasCodeAttributes)) {
            metaRecord.aliasCodeAttributes().each(addAttributeToStore);
        }

        filter = new Ext.util.Filter({
            filterFn: function (record) {
                return attributeStore.findExact('name', record.get('name')) === -1;
            }
        });

        comboboxStore.addFilter(filter);
    },

    onUnselectedAttributeComboSelect: function (combobox, record) {
        var me                 = this,
            viewModel          = this.getViewModel(),
            adminSystemName    = viewModel.get('adminSystemName'),
            metaRecord         = this.getCurrentRecord(),
            sourceSystemsStore = viewModel.getStore('sourceSystems'),
            attributeName;

        if (!record) {
            return;
        }

        attributeName = record.get('name');

        sourceSystemsStore.each(function (sourceSystemRecord) {
            var sourceSystemName   = sourceSystemRecord.get('name'),
                sourceSystemWeight = -1;

            if (adminSystemName === sourceSystemName) {
                sourceSystemWeight = sourceSystemRecord.get('weight');
            }

            me.createDefaultBvtAttributeWeight(metaRecord, attributeName, sourceSystemName, sourceSystemWeight);
        });

        combobox.clearValue();

        this.refreshBvtGridView();
        this.updateUnselectedBvtAttributeStore();
    },

    isValid: function () {
        var metaRecord = this.getCurrentRecord(),
            bvtAttributes,
            bvrSourceSystemsConfig,
            isValid    = true;

        this.updateMergeSettings(metaRecord);

        bvtAttributes          = this.getBvtAttributeStore(metaRecord);
        bvrSourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord);

        bvtAttributes.each(function (attribute) {
            var sourceSystemsConfig = attribute.sourceSystemsConfig();

            sourceSystemsConfig.each(function (record) {
                if (record.get('weight') === -1) {
                    isValid = false;
                }
            });
        });

        bvrSourceSystemsConfig.each(function (record) {
            if (record.get('weight') === -1) {
                isValid = false;
            }
        });

        return isValid;
    },

    getBvtAttributeCount: function (metaRecord) {
        var bvtAttributes = this.getBvtAttributeStore(metaRecord);

        return bvtAttributes.getCount();
    },

    getBvrSourceSystemConfigCount: function (metaRecord) {
        var bvrSourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord);

        return bvrSourceSystemsConfig.getCount();
    },

    getBackupBvtCount: function () {
        var viewModel  = this.getViewModel(),
            collection = viewModel.get('bvtRecords');

        return collection.getCount();
    },

    getBackupBvrCount: function () {
        var viewModel  = this.getViewModel(),
            collection = viewModel.get('bvrRecords');

        return collection.getCount();
    },

    backupBvtRecord: function () {
        var metaRecord    = this.getCurrentRecord(),
            viewModel     = this.getViewModel(),
            collection    = viewModel.get('bvtRecords'),
            bvtAttributes = this.getBvtAttributeStore(metaRecord);

        collection.removeAll();

        bvtAttributes.each(function (record) {
            collection.add(record);
        });
    },

    restoreBvtRecord: function () {
        var metaRecord     = this.getCurrentRecord(),
            viewModel      = this.getViewModel(),
            collection     = viewModel.get('bvtRecords'),
            bvtAttributes  = this.getBvtAttributeStore(metaRecord),
            attributeNames = this.getAttributeNameAtFirstLevel(metaRecord);

        bvtAttributes.removeAll();

        collection.each(function (record) {
            if (Ext.Array.contains(attributeNames, record.get('name'))) {
                bvtAttributes.add(record);
            }
        });
    },

    backupBvrRecord: function () {
        var metaRecord             = this.getCurrentRecord(),
            viewModel              = this.getViewModel(),
            collection             = viewModel.get('bvrRecords'),
            bvrSourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord);

        collection.removeAll();

        bvrSourceSystemsConfig.each(function (record) {
            collection.add(record);
        });
    },

    restoreBvrRecord: function () {
        var metaRecord             = this.getCurrentRecord(),
            viewModel              = this.getViewModel(),
            collection             = viewModel.get('bvrRecords'),
            bvrSourceSystemsConfig = this.getBvrSourceSystemsConfigStore(metaRecord);

        bvrSourceSystemsConfig.removeAll();

        collection.each(function (record) {
            bvrSourceSystemsConfig.add(record);
        });
    },

    onNecessaryStoresLoad: function () {
        var view = this.getView();

        view.fireEvent('loadallstore');
    },

    onRenderBvrGridWeight: function () {
    }
});
