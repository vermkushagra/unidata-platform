/**
 * Настройка модификации записей
 * @author Aleksandr Bavin
 * @date 22.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.ModifyRecords', {
    extend: 'Unidata.view.steward.search.bulk.wizard.settings.Default',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierPanel',
        'Unidata.view.steward.search.bulk.wizard.settings.ModifyRecordsController'
    ],

    alias: 'widget.steward.search.bulk.wizard.settings.modifyrecords',

    controller: 'modifyrecords',

    requiredExternalData: [
        'entityName'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    referenceHolder: true,

    methodMapper: [
        {
            method: 'hideAttribute'
        },
        {
            method: 'filterDataRecord'
        }
    ],

    items: [
        {
            xtype: 'combobox',
            ui: 'un-field-default',
            reference: 'comboboxAttributeList',
            fieldLabel: '',
            emptyText: Unidata.i18n.t('search>wizard.attributeEmptyText'),
            editable: false,
            multiSelect: true,
            displayField: 'displayName',
            valueField: 'name',
            queryMode: 'local',
            store: {
                fields: [
                    'name',
                    'displayName'
                ],
                data: []
            },
            listeners: {
                change: 'onAttributeComboboxChange'
            }
        },
        {
            xtype: 'combobox',
            ui: 'un-field-default',
            reference: 'comboboxClassifierList',
            fieldLabel: '',
            emptyText: Unidata.i18n.t('search>wizard.classifierEmptyText'),
            editable: false,
            multiSelect: true,
            displayField: 'displayName',
            valueField: 'name',
            queryMode: 'local',
            listeners: {
                change: 'onClassifierComboboxChange'
            }
        },
        {
            xtype: 'combobox',
            ui: 'un-field-default',
            reference: 'comboboxRelationsList',
            fieldLabel: '',
            emptyText: Unidata.i18n.t('search>wizard.relationEmptyText'),
            editable: false,
            multiSelect: true,
            displayField: 'displayName',
            valueField: 'name',
            queryMode: 'local',
            listeners: {
                change: 'onRelationsComboboxChange'
            }
        },
        {
            xtype: 'container',
            layout: 'fit',
            flex: 1,
            items: {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                scrollable: true,
                items: [
                    {
                        xtype: 'dataentity',
                        reference: 'dataEntity',
                        hiddenAttribute: false,
                        useCarousel: true,      // выводим в карусельном виде, т.к. плоский вид классификаторов RO
                        useAttributeGroup: true,
                        readOnly: false
                    },
                    {
                        xtype: 'relation.referencepanel',
                        reference: 'referencePanel',
                        drafts: false,
                        operationId: null,
                        readOnly: false
                    }
                ]
            }
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.on('added', this.initOnAdded, this);
    },

    onDestroy: function () {
        delete this.currentMetaRecord;

        this.callParent(arguments);
    },

    isNewMetaRecord: function () {
        var metaRecord = this.getMetaRecord();

        if (metaRecord == this.currentMetaRecord) {
            return false;
        } else {
            this.currentMetaRecord = metaRecord;

            return true;
        }
    },

    /**
     * Инициализация при активации
     */
    initOnAdded: function () {
        if (this.isNewMetaRecord()) {
            this.initDataEntity();
            this.initAttributesCombobox();
            this.initClassifierCombobox();
            this.initRelationsCombobox();
        }
    },

    initDataEntity: function () {
        var dataEntity = this.lookupReference('dataEntity'),
            metaRecord = this.getMetaRecord(),
            dataRecord;

        dataRecord = Ext.create('Unidata.model.data.Record', {
            entityName: metaRecord.get('name'),
            validFrom: null,
            validTo: null,
            status: 'NEW'
        });

        dataEntity.removeAll();
        dataEntity.setEntityData(metaRecord, dataRecord, []);
    },

    getMetaRecord: function () {
        return this.getWizard().getMetarecord();
    },

    initRelationsCombobox: function () {
        var metaRecord = this.getMetaRecord(),
            comboboxRelationsList = this.lookupReference('comboboxRelationsList'),
            relationsStore;

        if (!metaRecord.relations) {
            comboboxRelationsList.hide();

            return;
        }

        comboboxRelationsList.show();

        relationsStore = metaRecord.relations();

        comboboxRelationsList.setStore(
            Ext.create('Ext.data.ChainedStore', {
                source: relationsStore,
                filters: [
                    {
                        property: 'relType',
                        value: 'References'
                    }
                ]
            })
        );
    },

    initClassifierCombobox: function () {
        var metaRecord = this.getMetaRecord(),
            classifierStore = Unidata.util.api.Classifier.getClassifiersStore(),
            comboboxClassifierList = this.lookupReference('comboboxClassifierList');

        comboboxClassifierList.setStore(
            Ext.create('Ext.data.ChainedStore', {
                source: classifierStore,
                filters: [
                    {
                        property: 'name',
                        operator: 'in',
                        value: metaRecord.get('classifiers')
                    }
                ]
            })
        );
    },

    initAttributesCombobox: function () {
        var attributeList = this.getAttributeList(),
            comboboxAttributeList = this.lookupReference('comboboxAttributeList'),
            comboboxAttributeListStore = comboboxAttributeList.getStore();

        comboboxAttributeListStore.removeAll();
        comboboxAttributeList.reset();
        comboboxAttributeListStore.add(attributeList);
        // фильтруем после инициализации - для корректного отображения, при выборе классификаторов
        this.getController().onAttributeComboboxChange();
    },

    /**
     * Собирает список атрибутов для comboboxAttributeList
     * @returns {Array}
     */
    getAttributeList: function () {
        var metaRecord = this.getMetaRecord(),
            valueNames = ['name', 'displayName'],
            attributeStores = metaRecord.getHasManyStores([
                'complexAttributes',
                'simpleAttributes',
                'arrayAttributes'
            ]),
            list = [];

        Ext.Array.each(attributeStores, function (store) {
            list = Ext.Array.push(
                list,
                this.getStoreValues(store, valueNames)
            );
        }, this);

        return list;
    },

    /**
     * Собирает из стора массив значений по нужным полям
     * @param {Ext.data.Store} store
     * @param {String[]} valueNames
     */
    getStoreValues: function (store, valueNames) {
        var me = this,
            result = [];

        store.each(function (storeItem) {
            var resultItem = {};

            // доп фильтр атрибутов
            if (me.hideAttribute(storeItem)) {
                return true; //пропускаем те, которые нужно спрятать
            }

            Ext.Array.each(valueNames, function (valueName) {
                resultItem[valueName] = storeItem.get(valueName);
            });

            result.push(resultItem);
        });

        return result;
    },

    beforeGetOperationSettings: function (operationSettings) {
        var comboboxAttributeList = this.lookupReference('comboboxAttributeList'),
            comboboxClassifierList = this.lookupReference('comboboxClassifierList'),
            referencePanel = this.lookupReference('referencePanel'),
            dataEntity = this.lookupReference('dataEntity'),
            dataRecord = dataEntity.getDataRecord(),
            relations = [],
            etalonRecordRO;

        // фильтруем атрибуты dataRecord
        this.filterDataRecord(dataRecord);

        etalonRecordRO = dataRecord.getData({
            associated: true,
            serialize: true,
            persist: true
        });

        referencePanel.items.each(function (refItem) {
            var refItemViewModel,
                referenceData,
                relationTo;

            if (refItem.isVisible()) {
                refItemViewModel = refItem.getViewModel();
                relationTo = refItemViewModel.get('referenceData');
                referenceData = relationTo.getData({associated: true, serialize: true});

                referenceData.etalonIdTo = refItem.controller.input.getEtalonId();

                Ext.Object.each(referenceData, function (key, value, obj) {
                    if (value === '') {
                        obj[key] = null;
                    }
                });

                relations.push(referenceData);
            }
        });

        if (!comboboxAttributeList.getValue().length && !comboboxClassifierList.getValue().length) {
            operationSettings.etalonRecordRO = null;
        } else {
            operationSettings.etalonRecordRO = etalonRecordRO;
        }

        operationSettings.relations = relations;
    },

    isConfirmStepAllowed: function () {
        var comboboxAttributeList = this.lookupReference('comboboxAttributeList'),
            comboboxClassifierList = this.lookupReference('comboboxClassifierList'),
            comboboxRelationsList = this.lookupReference('comboboxRelationsList'),
            dataEntity = this.lookupReference('dataEntity');

        if (!comboboxAttributeList.getValue().length &&
            !comboboxClassifierList.getValue().length &&
            !comboboxRelationsList.getValue().length
        ) {
            Unidata.showError(Unidata.i18n.t('search>wizard.requiredError'));

            return false;
        }

        if (!dataEntity.isFieldsValid()) {
            Unidata.showError(Unidata.i18n.t('search>wizard.dataFillError'));

            return false;
        }

        if (!dataEntity.isSecurityLabelValid()) {
            Unidata.showError(Unidata.i18n.t('search>wizard.securityLabelError'));

            return false;
        }

        return true;
    }

});
