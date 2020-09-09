/**
 * @author Aleksandr Bavin
 * @date 2016-07-08
 */
Ext.define('Unidata.view.admin.security.AdditionalPropertySettingsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.additionalpropertysettings',

    init: function () {
        this.callParent(arguments);

        this.getGridStore().on('beforesync', this.onBeforesync, this);
    },

    onBeforeedit: function (editor, context) {
        var viewModel = this.getViewModel(),
            record = context.record;

        if (!record.phantom && viewModel.get('canUpdate')) {
            return true;
        }

        if (record.phantom && viewModel.get('canCreate')) {
            return true;
        }

        return false;
    },

    getToolbar: function () {
        return this.lookupReference('toolbar');
    },

    getGrid: function () {
        return this.lookupReference('settingsGrid');
    },

    getGridStore: function () {
        return this.getViewModel().get('additionalProperties');
    },

    onBeforesync: function () {
        this.getGrid().setLoading(Unidata.i18n.t('common:save', {context: 'process'}));
        this.getToolbar().setDisabled(true);
    },

    onAddClick: function () {
        this.getGridStore().add({
            name: '',
            displayName: ''
        });
    },

    reloadData: function () {
        this.getGridStore().reload({
            scope: this,
            callback: function (records, operation, success) {
                if (!success) {
                    Unidata.showError(Unidata.i18n.t('admin.security>loadDataError'));
                }
                this.stopLoadingIndicator();
            }
        });
    },

    onSaveClick: function () {
        this.getGridStore().sync({
            scope: this,
            success: function () {
                Unidata.showMessage(Unidata.i18n.t('admin.security>saveDataSuccess'));
                this.reloadData();
            },
            callback: function () {
                this.stopLoadingIndicator();
            }
        });
    },

    stopLoadingIndicator: function () {
        this.getGrid().setLoading(false);
        this.getToolbar().setDisabled(false);
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);
    }

});
