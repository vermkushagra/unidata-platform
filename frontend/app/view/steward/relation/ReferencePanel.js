/**
 * Контейнер со связями
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.ReferencePanel', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.relation.referencepanel',

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.reference.Reference'
    ],

    referenceHolder: true,

    config: {
        referencesData: null,
        metaRecord: null,
        dataRecord: null,
        readOnly: null,
        dirty: false,
        saveAtomic: true
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('relation>referenceType'),
    header: false,

    cls: 'un-relation-reference-panel',
    referenceEditors: null,

    initComponent: function () {
        this.changesByRecordId = {};
        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);

        if (this.referencesData) {
            this.displayReferenceRelations(this.referencesData);
        }
    },

    updateReadOnly: function (newReadOnly) {
        var items = this.items;

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(newReadOnly);
        });
    },

    displayReferenceRelations: function (referencesData) {
        var panels;

        this.referencesData = referencesData;

        if (!this.rendered) {
            return;
        }

        panels = this.buildReferencePanels(referencesData);
        this.removeAll(true);
        this.referenceEditors = panels;
        this.add(panels);
    },

    // TODO: Объединить с Unidata.view.steward.relation.RelationFactory.buildRelationPanels
    buildReferencePanels: function (referencesData) {
        var me = this,
            relType = 'References',
            metaRecord,
            dataRecord,
            readOnly,
            panels,
            relationGroup,
            metaRelations,
            relationNamesOrdered,
            sorters;

        metaRelations = Ext.create('Ext.util.Collection');
        Ext.Array.each(referencesData, function (referenceData) {
            metaRelations.add(referenceData.meta);
        });

        metaRecord = me.metaRecord;
        dataRecord = me.dataRecord;
        readOnly   = me.getReadOnly();
        panels      = [];

        relationGroup = metaRecord.getRelationGroupByType(relType);

        if (relationGroup) {
            // если определен порядок групп связей, то сортируем связи в соответствии с этим порядком
            relationNamesOrdered = relationGroup.get('relations');
            sorters = {
                sorterFn: Unidata.util.Sorter.byListSorterFn.bind(this, relationNamesOrdered, 'name', 'displayName'),
                direction: 'ASC'
            };
        } else {
            // по умолчанию сортируем связи по алфавиту
            sorters = {
                property: 'displayName',
                direction: 'ASC'
            };
        }
        metaRelations.setSorters(sorters);

        metaRelations.each(function (metaRelation) {
            var metaRelationName = metaRelation.get('name'),
                foundReferenceData;

            foundReferenceData = Ext.Array.findBy(referencesData, function (referenceData) {
                return referenceData.meta.get('name') === metaRelationName;
            });

            if (!foundReferenceData) {
                throw new Error(Unidata.i18n.t('relation>noReferenceDataError'));
            }

            panels.push(
                Ext.widget({
                    xtype: 'relation.reference',
                    metaRecord:    metaRecord,
                    dataRecord:    dataRecord,
                    referenceMeta: metaRelation,
                    relationName: metaRelation.get('name'), // QA использует имя связи для поиска
                    referenceData: foundReferenceData.data,
                    readOnly:      readOnly,
                    listeners: {
                        scope: me,
                        valueremoved: me.onRelationRemoved,
                        valuechange: me.onRelationChange,
                        attributedirtychange: me.onAttributeDirtyChange
                    }
                })
            );
        });

        return panels;
    },

    /**
     * При удалении записи создаём отметку о том, что что-то изменилось
     */
    onRelationRemoved: function () {
        var dirty;

        this.changesByRecordId['relationRemoved'] = true;
        dirty = this.checkPanelsDirty();
        this.setDirty(dirty);
        this.fireEvent('referencedirtychange', dirty);
    },

    /**
     * При изменении референса
     */
    onRelationChange: function (dropdown) {
        var dirty;

        this.changesByRecordId[dropdown.getId()] = !dropdown.isInitialValue();
        dirty = this.checkPanelsDirty();
        this.setDirty(dirty);
        this.fireEvent('referencedirtychange', dirty);
    },

    onAttributeDirtyChange: function (dirty) {
        this.setDirty(dirty);
        this.fireEvent('referencedirtychange', dirty);
    },

    /**
     * Проверяет, изменился ли какой-нибудь из референсов
     * @returns {boolean}
     */
    checkPanelsDirty: function () {
        var changesByRecordId = Ext.Object.getValues(this.changesByRecordId),
            hasChanges = false;

        Ext.Array.each(changesByRecordId, function (item) {
            if (item == true) {
                hasChanges = true;

                return false; //break
            }
        });

        if (hasChanges) {
            return hasChanges;
        }

        // проверяем изменения атрибутов
        if (this.items) {
            this.items.each(function (item) {
                if (item.checkDirty() && item.getCurrentEtalonId() !== null) {
                    hasChanges = true;

                    return false;
                }
            });
        }

        return hasChanges;
    },

    reset: function () {
        this.changesByRecordId = {};
        this.fireEvent('m2mdirtychange', false);
    },

    saveReferenceRelations: function () {
        var me = this,
            promises = [],
            promise,
            referenceEditors;

        referenceEditors = this.referenceEditors ? this.referenceEditors : [];

        Ext.Array.each(referenceEditors, function (referenceEditor) {
            if (referenceEditor.isChanged() || referenceEditor.checkDirty()) {
                promises.push(referenceEditor.saveReferenceRelations());
            }
        });

        promise = Ext.Deferred.all(promises);

        promise.then(function () {
            me.changesByRecordId = {}; //сбрасываем изменения
        });

        return promise;
    },

    /**
     * @param forceGetAll Принудительное получение всех связей в секции toUpdate
     * @return {Unidata.model.data.RelationReferenceDiff}
     */
    getRelationReferenceDiff: function (forceGetAll) {
        var relationReferenceDiff,
            toUpdate,
            toDelete,
            relationReferenceToUpdate,
            relationReferenceToDelete;

        forceGetAll = Ext.isBoolean(forceGetAll) ? forceGetAll : false;

        relationReferenceDiff = Ext.create('Unidata.model.data.RelationReferenceDiff');
        toUpdate = relationReferenceDiff.toUpdate();
        toDelete = relationReferenceDiff.toDelete();

        Ext.Array.each(this.referenceEditors, function (referenceEditor) {
            if (forceGetAll || referenceEditor.isChanged() || referenceEditor.checkDirty()) {
                relationReferenceToDelete = referenceEditor.getRelationReferenceToDelete();
                relationReferenceToUpdate = referenceEditor.getRelationReferenceToUpdate();
                toDelete.add(relationReferenceToDelete);
                toUpdate.add(relationReferenceToUpdate);
            }
        });

        return relationReferenceDiff;
    },

    isRelationsValid: function () {
        var valid;

        if (!Ext.isArray(this.referenceEditors) || !this.referenceEditors.length) {
            return true;
        }

        // отключаем layout т.к.  на большом количестве атрибутов начинает тупить проверка валидации смотри UN-3444
        Ext.suspendLayouts();

        valid = Ext.Array.reduce(this.referenceEditors, function (previous, referenceEditor) {
            var isValid;

            isValid = referenceEditor.checkValid() && previous;

            return isValid;
        }, true);

        // смотри UN-3444
        Ext.resumeLayouts(true);

        return valid;
    },

    statics: {
        /**
         * Создаем незаполненную информацию о данных связях
         * Метод используется при создании новой записи и новых связей типа ссылка
         *
         * @param metaRecord
         * @returns {Array}
         */
        createEmptyReferenceData: function (metaRecord) {
            var referenceRelation,
                referenceData = [];

            referenceRelation = metaRecord.relations().query('relType', 'References', false, false, true);

            referenceRelation.each(function (metaRelation) {
                referenceData.push({
                    meta: metaRelation,
                    data: null
                });
            });

            return referenceData;
        }
    }
});
