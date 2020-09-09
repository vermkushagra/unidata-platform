/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.DuplicatesController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates',

    oneEntitySelectionChange: function (tree, selectedRecord) {
        this.hideEntityDuplicateEditor();

        if (selectedRecord.length) {
            this.selectNodeHandler(selectedRecord[0]);
        }
    },

    hideEntityDuplicateEditor: function () {
        var view = this.getView();

        view.entityDuplicateEditor.hide();
    },

    showEntityDuplicateEditor: function () {
        var view = this.getView();

        view.entityDuplicateEditor.show();
    },

    selectNodeHandler: function (record) {
        var me = this,
            view = this.getView(),
            entityName = record.get('entityName'),
            entityType = record.get('type'),
            cfg,
            promise;

        cfg = {
            entityName: entityName,
            entityType: entityType
        };

        promise = Unidata.util.api.MetaRecord.getMetaRecord(cfg);
        promise.then(function (metaRecord) {
            view.entityDuplicateEditor.setMetaRecord(metaRecord);

            me.showEntityDuplicateEditor();
        }).done();
    },

    /**
     *
     * @param selectionModel
     * @param record
     * @param index
     * @param eOpts
     * @returns {boolean}
     */
    onBeforeSelectEntity: function (selectionModel, record) {
        var view  = this.getView(),
            title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmLeaveUnsavedEntity');

        function acceptRejectChanges () {
            view.entityTree.suspendEvent('beforeselect');

            selectionModel.select([record]);
            view.entityDuplicateEditor.resetEntityDuplicateEditor();

            view.entityTree.resumeEvent('beforeselect');
        }

        // если изменения требуют сохранения то спрашиваем покинуть раздел или нет
        if (this.isDuplicateEditorDirty()) {
            Unidata.showPrompt(title, msg, acceptRejectChanges);

            return false;
        } else {
            view.entityDuplicateEditor.resetEntityDuplicateEditor();
        }
    },

    /**
     * Возвращает истину если текущее состояние требует сохранения
     *
     * @returns {*}
     */
    isDuplicateEditorDirty: function () {
        var view = this.getView();

        return view.entityDuplicateEditor.isEntityDuplicateEditorDirty();
    }
});
