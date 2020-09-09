Ext.define('Unidata.view.component.search.query.relation.list.RelationSearchListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query.relation.list.relationsearchlist',

    /**
     * Создать панельку поиска по связям
     */
    createRelationSearchItem: function (relation, supplementaryRequestTerm) {
        var view = this.getView(),
            metaRecord =  view.getMetaRecord(),
            item;

        item = Ext.create('Unidata.view.component.search.query.relation.item.RelationSearchItem', {
            metaRecord: metaRecord,
            searchQuery: view.getSearchQuery(),
            supplementaryRequestTerm: supplementaryRequestTerm,
            relation: relation,
            relationName: relation.get('name'), // для нужд QA отдела
            relationType: relation.get('relType'), // для нужд QA отдела
            relationFromEntity: relation.get('fromEntity'), // для нужд QA отдела
            relationToEntity: relation.get('toEntity') // для нужд QA отдела
        });

        item.on('relationsearchitemdelete', this.onRelationSearchItemDelete, this);

        view.add(item);

        view.fireEvent('change');
    },

    getRelationSearchItems: function () {
        return this.getView().items.getRange();
    },

    /**
     * Удалить панельку поиска по связям
     * @param item {Unidata.view.component.search.query.relation.item.RelationSearchItem}
     */
    removeRelationSearchItem: function (item) {
        var view = this.getView();

        if (!item) {
            return;
        }

        view.remove(item);
    },

    updateMetaRecord: function (metaRecord) {
        var view = this.getView(),
            MetaRecordUtil = Unidata.util.MetaRecord;

        if (MetaRecordUtil.isEntity(metaRecord)) {
            view.relationPicker.setMetaRecord(metaRecord);

            view.setDisabled(false);
        } else {
            // для справочников поиск для связей не доступен, поэтому удаляем все плитки
            view.removeAll();

            view.setDisabled(true);
        }

        this.updateaddRelationSearchItemButtonEnabled(metaRecord);
    },

    updateaddRelationSearchItemButtonEnabled: function (metaRecord) {
        var MetaRecordUtil = Unidata.util.MetaRecord,
            viewModel = this.getViewModel(),
            relations;

        if (!Boolean(metaRecord) || !MetaRecordUtil.isEntity(metaRecord)) {
            viewModel.set('addRelationSearchItemButtonEnabled', false);

            return;
        }

        relations = metaRecord.getRelationsFilteredByPermission();

        viewModel.set('addRelationSearchItemButtonEnabled', relations.length > 0);
    },

    /**
     * Обработчик события addrelationsearchitembuttonclick
     */
    onAddRelationSearchItemButtonClick: function (button) {
        var view = this.getView();

        view.expand();
        view.relationPicker.showBy(button, 'r-r', [5, 0]);
    },

    /**
     * Прячет выбиралаку связи
     */
    hidePicker: function () {
        this.getView().relationPicker.hide();
    },

    /**
     * При выборе связи
     */
    onRelationSelect: function (relation) {
        this.hidePicker();
        this.createRelationSearchItem(relation);
    },

    /**
     * Прячем выбиралку, если кликнули не по гриду
     * @param picker
     * @param event
     */
    onPickerBlur: function (picker, event) {
        if (!event.within(picker.picker.el, true)) {
            picker.hide();
        }
    },

    /**
     * Обработчик события relationsearchitemdelete
     * @param item {Unidata.view.component.search.query.relation.item.RelationSearchItem}
     */
    onRelationSearchItemDelete: function (item) {
        var view = this.getView();

        this.removeRelationSearchItem(item);

        view.fireEvent('change');
    },

    onShowEtalonClusterWindowButtonClick: function () {
        this.showEtalonClusterWindow();
    },

    /**
     * Отображает окно списков записей
     *
     */
    showEtalonClusterWindow: function () {
        var wnd;

        wnd = Ext.create('Unidata.component.search.query.etaloncluster.EtalonClusterWindow');
        wnd.show();
    }
});
