Ext.define('Unidata.view.admin.entity.metarecord.presentation.PresentationController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.presentation',

    init: function () {
        var viewModel = this.getViewModel();

        this.callParent(arguments);

        viewModel.bind('{metaRecordViewReadOnly}', function (readOnly) {
            var view = this.getView();

            view.setReadOnly(readOnly);
        }, this, {deep: true});
    },

    initAttributes: function () {
        var view = this.getView(),
            attributePresentationPanel = view.attributePresentationPanel;

        attributePresentationPanel.initAttributes();
    },

    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);

        if (view.isConfiguring) {
            return;
        }

        this.syncReadOnly();
    },

    syncReadOnly: function () {
        var view = this.getView(),
            readOnly = view.getReadOnly();

        view.attributePresentationPanel.setReadOnly(readOnly);
        view.relationPresentationPanel.setReadOnly(readOnly);
    }
});
