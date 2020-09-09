Ext.define('Unidata.view.component.search.query.relation.item.RelationSearchItemController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query.relation.item.relationsearchitem',

    init: function () {
        var view;

        this.callParent(arguments);

        view = this.getView();

        if (view.doNotShowWindow) {
            this.initRecordGridSilently();
        } else {
            this.showSearchWindow();
        }
    },

    initRecordGridSilently: function () {
        var MetaRecordApi = Unidata.util.api.MetaRecord,
            MetaRecordUtil = Unidata.util.MetaRecord,
            view = this.getView(),
            recordGrid = view.recordGrid,
            entityTo = view.getEntityTo(),
            selectedRelation = view.getRelation(),
            metaRecordKey;

        metaRecordKey = {
            entityName: entityTo,
            entityType: MetaRecordUtil.TYPE_ENTITY
        };

        MetaRecordApi.getMetaRecord(metaRecordKey)
            .then(function (metaRecord) {
                recordGrid.setRelation(selectedRelation);
                recordGrid.setMetaRecord(metaRecord);
                view.setStatus(Unidata.StatusConstant.READY);
            }, function () {
                Unidata.showError(view.relMetaRecordLoadErrorText);
                view.setStatus(Unidata.StatusConstant.READY);
            })
            .done();
    },

    showSearchWindow: function () {
        var MetaRecordApi = Unidata.util.api.MetaRecord,
            MetaRecordUtil = Unidata.util.MetaRecord,
            view = this.getView(),
            recordGrid = view.recordGrid,
            metaRecord = recordGrid.getMetaRecord(),
            entityTo = view.getEntityTo(),
            selectedRelation = view.getRelation(),
            metaRecordKey;

        // ничего не изменилось
        if (metaRecord &&
            metaRecord.get('name') === entityTo &&
            MetaRecordUtil.isEntity(metaRecord)) {
            return;
        }

        recordGrid.clearList();

        if (!entityTo) {
            return;
        }

        metaRecordKey = {
            entityName: entityTo,
            entityType: MetaRecordUtil.TYPE_ENTITY
        };

        view.setStatus(Unidata.StatusConstant.LOADING);

        MetaRecordApi.getMetaRecord(metaRecordKey)
            .then(function (metaRecord) {
                recordGrid.setRelation(selectedRelation);
                recordGrid.setMetaRecord(metaRecord);
                recordGrid.showSearchHitsSelectionWindow();
                view.setStatus(Unidata.StatusConstant.READY);
            }, function () {
                Unidata.showError(view.relMetaRecordLoadErrorText);
                view.setStatus(Unidata.StatusConstant.READY);
            })
            .done();
    },

    onDeleteButtonClick: function () {
        var view = this.getView();

        view.fireEvent('relationsearchitemdelete', view);
    },

    updateRelation: function (relation) {
        var entityTo = null,
            view = this.getView();

        if (relation) {
            entityTo = relation.get('toEntity');
        }

        this.updatePanelTitle();

        view.setEntityTo(entityTo);
    },

    updatePanelTitle: function (count) {
        var view = this.getView(),
            relation = view.getRelation(),
            title;

        if (!relation) {
            return;
        }

        title = relation.get('displayName');

        if (view.recordGrid && count) {
            title += ' (' + count + ')';
        }

        view.setTitle(title);
    },

    /**
     *
     * @returns {String}
     */
    getRelationName: function () {
        var view = this.getView(),
            relation = view.getRelation(),
            relationName = null;

        if (relation) {
            relationName = relation.get('name');
        }

        return relationName;
    },

    /**
     *
     * @returns {String[]}
     */
    getEtalonIds: function () {
        var view = this.getView(),
            recordGrid = view.recordGrid,
            etalonIds = [],
            store,
            searchHits;

        if (recordGrid) {
            store    = recordGrid.getStore();
            searchHits = store.getDataSource().getRange();
            etalonIds = Ext.Array.map(searchHits, function (searchHit) {
                return searchHit.get('etalonId');
            });
        }

        return etalonIds;
    },

    updateSelectionMode: function (selectionMode) {
        var view = this.getView();

        if (view.isEditModeDisabled()) {
            return;
        }

        if (selectionMode) {
            view.setEditMode(view.editModeType.SELECTION);
        } else {
            view.setEditMode(view.editModeType.NONE);
        }
    },

    onRecordGridSelectionChange: function () {
        var view = this.getView(),
            recordGrid = view.recordGrid,
            selectionModel = recordGrid.getSelectionModel(),
            selectionMode = selectionModel.getCount() > 0;

        view.setSelectionMode(selectionMode);
    }
});
