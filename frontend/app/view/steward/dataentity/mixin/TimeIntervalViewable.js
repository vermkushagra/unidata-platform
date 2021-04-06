/**
 * Mixin для применения TimeInterval
 *
 * author: Sergey Shishigin 2016-03-03
 */

Ext.define('Unidata.view.steward.dataentity.mixin.TimeIntervalViewable', {
    createErrorText: Unidata.i18n.t('dataentity>recordShouldBeSaved'),
    deleteErrorText: Unidata.i18n.t('dataentity>cantDeleteLastTimeInterval'),
    deletePromptText: Unidata.i18n.t('dataentity>confirmDeleteRecord'),
    deletePromptTitle: Unidata.i18n.t('dataentity>removeVersion'),
    undoPromptText: Unidata.i18n.t('dataentity>confirmCancelCreateTimeInterval'),
    undoPromptTitle: Unidata.i18n.t('dataentity>cancelCreateTimeInterval'),
    beforeSelectText: Unidata.i18n.t('dataentity>confirmCancelEditTimeInterval'),
    beforeSelectTitle: Unidata.i18n.t('glossary:unsavedChanges'),

    onUndoTimeIntervalCreate: function (timeInterval, btn) {
        this.showUndoTimeIntervalPrompt(timeInterval, btn);
    },

    showTimeIntervalDeleteDialog: function (dataView, etalonId, yesCallback, btn) {
        var store = dataView.getStore();

        if (!dataView.getIsCopyMode()) {
            if (store.count() > 1) {
                Unidata.showPrompt(this.deletePromptTitle, this.deletePromptText, yesCallback, this, btn, [etalonId]);
            } else {
                Unidata.showError(this.deleteErrorText);
            }
        }
    },

    showUndoTimeIntervalPrompt: function (timeInterval, btn) {
        if (timeInterval.getIsCopyMode()) {
            this.showPrompt(this.undoPromptTitle, this.undoPromptText, this.undoRecordCopy, this, btn, [timeInterval]);
        }
    },

    undoRecordCopy: function (timeInterval) {
        timeInterval.setIsCopyMode(false);
    },

    showBeforeSelectTimeIntervalPrompt: function (dataView, dataRecord) {
        function changeTimeInterval (timeInterval) {
            dataView.suspendEvent('beforeselect');
            dataView.setSelection(timeInterval);
            dataView.resumeEvent('beforeselect');
        }

        if (this.getView().getDirty()) {
            Unidata.showPrompt(this.beforeSelectTitle, this.beforeSelectText, changeTimeInterval, this, null, [dataRecord]);

            return false;
        }

        return true;
    }
});
