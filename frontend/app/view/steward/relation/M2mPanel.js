/**
 * Панель реализующая отображения связи многие-ко-многим
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.M2mPanel', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.relation.m2mpanel',

    mixins: {
        // Поиск временно отключен
        //search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.m2m.M2m',
        'Unidata.view.steward.relation.RelationFactory'
    ],

    referenceHolder: true,

    cls: 'un-relation-m2m-panel',

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        readOnly: null,
        saveAtomic: true
    },

    unidataLayoutManagerEnabled: true,
    unidataLayoutManagerText: 'm2m updatelayout',
    unidataLayoutManagerDelay: 100,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('relation>manyToMany'),
    header: false,

    initComponent: function () {
        this.callParent(arguments);
    },

    /**
     * Установка флага "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var items = this.items;

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    },

    /**
     * Отображение связей
     */
    displayRelation: function () {
        var me = this;

        me.buildRelation();
    },

    checkPanelsDirty: function () {
        return Ext.Array.reduce(this.items.getRange(), function (previous, m2mItem) {
            return m2mItem.checkPanelsDirty(true) || previous;
        }, false);
    },

    onM2mDirtyChange: function (view, dirty) {
        if (!dirty) {
            dirty = this.checkPanelsDirty();
        }
        this.fireEvent('m2mdirtychange', dirty);
    },

    /**
     * Построение панелей для связей
     */
    displayRelations: function (type) {
        var RelationFactory = Unidata.view.steward.relation.RelationFactory,
            drafts     = this.getDrafts(),
            operationId = this.getOperationId(),
            metaRecord = this.getMetaRecord(),
            dataRecord = this.getDataRecord(),
            readOnly   = this.getReadOnly(),
            saveAtomic    = this.getSaveAtomic(),
            panels,
            cfg;

        cfg = {
            dataRecord: dataRecord,
            operationId: operationId,
            drafts: drafts,
            readOnly: readOnly,
            saveAtomic: saveAtomic,
            listeners: {
                m2mdirtychange: this.onM2mDirtyChange.bind(this)
            }
        };

        if (dataRecord.phantom) {
            cfg = Ext.apply(cfg, {
                relationViewType: Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL
            });
        }

        panels = RelationFactory.buildRelationPanels(type, metaRecord, cfg);
        this.removeAll(true);
        this.add(panels);
    },

    getRelationManyToManyDiff: function () {
        var relationReferenceDiff,
            toUpdate,
            toDelete,
            relationReferenceToUpdate,
            relationReferenceToDelete;

        relationReferenceDiff = Ext.create('Unidata.model.data.RelationReferenceDiff');
        toUpdate = relationReferenceDiff.toUpdate();
        toDelete = relationReferenceDiff.toDelete();

        Ext.Array.each(this.items.getRange(), function (m2mItem) {
            if (m2mItem.checkPanelsDirty()) {
                relationReferenceToDelete = m2mItem.getRelationReferenceToDelete();
                relationReferenceToUpdate = m2mItem.getRelationReferenceToUpdate();
                toDelete.add(relationReferenceToDelete);
                toUpdate.add(relationReferenceToUpdate);
            }
        });

        return relationReferenceDiff;
    },

    isRelationsValid: function () {
        var items = this.items.getRange(),
            valid;

        if (!Ext.isArray(items) || !items.length) {
            return true;
        }

        // отключаем layout т.к.  на большом количестве атрибутов начинает тупить проверка валидации смотри UN-3444
        Ext.suspendLayouts();

        valid = Ext.Array.reduce(items, function (previous, m2mItem) {
            var isValid;

            isValid = m2mItem.checkPanelsValid() && previous;

            return isValid;
        }, true);

        // смотри UN-3444
        Ext.resumeLayouts(true);

        return valid;
    },

    reset: function () {
        Ext.Array.each(this.items.getRange(), function (m2mItem) {
            m2mItem.reset();
        });

        this.fireEvent('m2mdirtychange', false);
    }
});
