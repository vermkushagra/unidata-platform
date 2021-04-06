Ext.define('Unidata.view.admin.sourcesystems.resultset.ResultsetController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.sourcesystems.resultset',

    renderColumn: function (value, metadata, record) {
        return Unidata.model.sourcesystem.SourceSystem.createTypeIcon() + ' ' + record.get('name');
    },

    onAddRecordButtonClick: function () {
        var record = Ext.create('Unidata.model.sourcesystem.SourceSystem', {});

        this.fireViewEvent('addrecord', this.getView(), record);
    },

    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel();

        if (view.isDestroyed || view.destroying) {
            return;
        }

        viewModel.set('readOnly', readOnly);
    }
});
