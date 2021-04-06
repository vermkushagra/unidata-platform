/**
 * @author Aleksandr Bavin
 * @date 22.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.ModifyRecordsController', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStepController',
    alias: 'controller.modifyrecords',

    selectedNodesByClassifier: null,

    init: function () {
        var view = this.getView();

        this.selectedNodesByClassifier = {};

        view.addComponentListener('datarecordclassifiernodechange', this.onClassifiernodeschange, this);
    },

    /**
     * Фильтует связи для отображения
     */
    onRelationsComboboxChange: function (combobox, newValue) {
        var dataEntity = this.lookupReference('dataEntity'),
            dataRecord = dataEntity.getDataRecord(),
            metaRecord = dataEntity.getMetaRecord();

        this.displayReferences(newValue, metaRecord, dataRecord);
    },

    displayReferences: function (relNames, metaRecord, dataRecord) {
        var referencePanel = this.lookupReference('referencePanel'),
            referenceRelations;

        if (!referencePanel.inited) {
            referenceRelations = Unidata.view.steward.relation.ReferencePanel.createEmptyReferenceData(metaRecord);

            referencePanel
                .setMetaRecord(metaRecord)
                .setDataRecord(dataRecord)
                .displayReferenceRelations(referenceRelations);

            referencePanel.inited = true;
        }

        // оставляем только те, которые выбрали
        referencePanel.items.each(function (refPanelItem) {
            var relName = refPanelItem.getRelationName();

            if (relNames.indexOf(relName) !== -1) {
                refPanelItem.show();
            } else {
                refPanelItem.hide();
            }
        });
    },

    /**
     * При выборе узла классификатора
     * @param classifier
     * @param selectedClassifierNode
     */
    onClassifiernodeschange: function (classifier, selectedClassifierNode) {
        var me = this,
            dataEntity = this.lookupReference('dataEntity'),
            dataRecord = dataEntity.getDataRecord(),
            dataRecordClassifiers = dataRecord.classifiers(),
            classifierName = classifier.get('name'),
            nodes,
            promise;

        if (selectedClassifierNode) {
            this.selectedNodesByClassifier[classifierName] = selectedClassifierNode;
        } else {
            // Нужно добавить классификатор с classifierNodeId, что бы деклассифицаировать
            dataRecordClassifiers.add({
                classifierName: classifierName,
                classifierNodeId: null
            });
            delete this.selectedNodesByClassifier[classifierName];
        }

        nodes = Ext.Object.getValues(this.selectedNodesByClassifier);

        promise = Unidata.util.api.Classifier.getClassifierNodes(nodes, 'DATA');

        promise
            .then(function (nodes) {
                Ext.Array.each(nodes, function (node) {
                    me.classierNodeFulfilled(node);

                    // скрываем readOnly или атрибуты с значением UN-3086
                    node.nodeAttrs().each(function (nodeAttr) {
                        if (nodeAttr.get('readOnly') || nodeAttr.get('value')) {
                            nodeAttr.set('hidden', true);
                        }
                    });
                });
                dataEntity.setClassifierNodes(nodes);
                dataEntity.displayDataEntity();
            })
            .done();
    },

    classierNodeFulfilled: function (classifierNode) {
        var nodeAttrs = classifierNode.nodeAttrs(),
            inheritedNodeAttrs = classifierNode.inheritedNodeAttrs();

        nodeAttrs.removeAll();
        inheritedNodeAttrs.removeAll();
        nodeAttrs.add(classifierNode.raw.nodeAttrs);
        inheritedNodeAttrs.add(classifierNode.raw.inheritedNodeAttrs);
    },

    /**
     * Фильтует классификаторы для отображения
     */
    onClassifierComboboxChange: function (combobox, newValue, oldValue) {
        var view = this.getView(),
            dataEntity = this.lookupReference('dataEntity'),
            dataRecord = dataEntity.getDataRecord(),
            dataRecordClassifiers = dataRecord.classifiers(),
            metaRecord = dataEntity.getMetaRecord(),
            entityMetaRecord = Ext.create('Unidata.model.entity.Entity'),
            addedValues = Ext.Array.difference(newValue, oldValue),
            removedValues = Ext.Array.difference(oldValue, newValue);

        entityMetaRecord = this.copyAttributes2NewEntity(metaRecord, entityMetaRecord);

        // удаляем классификаторы, которые не выбраны
        Ext.Array.each(removedValues, function (classifierName) {
            var recordIndex = dataRecordClassifiers.findExact('classifierName', classifierName);

            if (recordIndex != -1) {
                dataRecordClassifiers.removeAt(recordIndex);
            }
        });

        // добавляем новые классификаторы
        Ext.Array.each(addedValues, function (classifierName) {
            var recordIndex = dataRecordClassifiers.findExact('classifierName', classifierName);

            if (recordIndex == -1) {
                dataRecordClassifiers.add({
                    classifierName: classifierName,
                    classifierNodeId: null
                });
            }
        });

        dataEntity.setMetaRecord(entityMetaRecord);
        dataEntity.displayDataEntity();
    },

    /**
     * Фильтруем атрибуты metaRecord
     */
    onAttributeComboboxChange: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            attributeStores = metaRecord.getHasManyStores([
                'complexAttributes',
                'simpleAttributes',
                'arrayAttributes'
            ]),
            dataEntity = this.lookupReference('dataEntity'),
            entityMetaRecord = Ext.create('Unidata.model.entity.Entity');

        Ext.Array.each(attributeStores, function (store) {
            store.filterBy(this.filterAttributes, this);
        }, this);

        entityMetaRecord = this.copyAttributes2NewEntity(metaRecord, entityMetaRecord);

        dataEntity.setMetaRecord(entityMetaRecord);
        dataEntity.displayDataEntity();

        Ext.Array.each(attributeStores, function (store) {
            store.clearFilter();
        }, this);
    },

    /**
     * Копирует атрибуты из одной метамодели в другую
     *
     * @param sourceMetaRecord
     * @param destMetaRecord
     * @returns {*}
     */
    copyAttributes2NewEntity: function (sourceMetaRecord, destMetaRecord) {
        var hasComplex = Unidata.util.MetaRecord.hasComplexAttribute(sourceMetaRecord),
            comboboxClassifierList = this.lookupReference('comboboxClassifierList'),
            storeSource,
            storeDest;

        storeSource = sourceMetaRecord.simpleAttributes();
        storeDest = destMetaRecord.simpleAttributes();

        // копируем простые атрибуты
        storeDest.removeAll();
        storeDest.add(storeSource.getRange());

        // копируем комлексные
        if (hasComplex) {
            storeSource = sourceMetaRecord.complexAttributes();
            storeDest = destMetaRecord.complexAttributes();

            storeDest.removeAll();
            storeDest.add(storeSource.getRange());
        }

        // копируем массивы
        if (sourceMetaRecord.arrayAttributes) {
            storeSource = sourceMetaRecord.arrayAttributes();
            storeDest = destMetaRecord.arrayAttributes();

            storeDest.removeAll();
            storeDest.add(storeSource.getRange());
        }

        // копируем имя
        destMetaRecord.set('name', sourceMetaRecord.get('name'));

        // копируем классификаторы
        // destMetaRecord.set('classifiers', sourceMetaRecord.get('classifiers'));
        destMetaRecord.set('classifiers', comboboxClassifierList.getValue());

        // копируем связи
        if (sourceMetaRecord.relations && destMetaRecord.relations) {
            destMetaRecord.relations().setData(sourceMetaRecord.relations().getData());
        }

        return destMetaRecord;
    },

    /**
     * Фильтрует атрибуты dataRecord
     * @param dataRecord
     */
    filterDataRecord: function (dataRecord) {
        var attributeStores = dataRecord.getHasManyStores([
            'complexAttributes',
            'simpleAttributes',
            'arrayAttributes'
        ]);

        Ext.Array.each(attributeStores, function (store) {
            store.setRemoteFilter(false);
            store.clearFilter(true);
            store.filterBy(this.filterAttributes, this);
        }, this);
    },

    /**
     * Фильтр атрибутов
     */
    filterAttributes: function (record) {
        var comboboxAttributeList = this.lookupReference('comboboxAttributeList'),
            values = comboboxAttributeList.getValue();

        // доп фильтр атрибутов
        if (this.hideAttribute(record)) {
            return false;
        }

        return Ext.Array.contains(values, record.get('name'));
    },

    /**
     * Дополнительная проверка для фильтрации атрибутов
     * Тут проверяются всякие кастомные моменты для покза/скрытия атрибута
     * @param record
     * @returns {boolean}
     */
    hideAttribute: function (record) {
        // ридонли не показываем
        if (record.get('readOnly')) {
            return true;
        }

        // ссылки на веб ресурс не показываем
        if (record.get('typeCategory') == 'linkDataType') {
            return true;
        }

        // уникальные атрибуты не отображаем смотри UN-7256
        if (record.get('unique')) {
            return true;
        }
    }

});
