/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.DashboardController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard',

    save: function () {
        var view = this.getView(),
            saveData = view.getSaveData(),
            currentUserValueOld = Unidata.BackendStorage.getCurrentUserValue(Unidata.BackendStorageKeys.DASHBOARD),
            currentUserValueNew = Ext.JSON.encode(saveData);

        if (currentUserValueOld === currentUserValueNew) {
            return;
        }

        Unidata.BackendStorage.setCurrentUserValue(Unidata.BackendStorageKeys.DASHBOARD, currentUserValueNew);
        Unidata.BackendStorage.save().then(function () {
            Unidata.showMessage(Unidata.i18n.t('dashboard>saveMessage.saved'));
        });
    },

    reset: function () {
        var dashboardData = Unidata.BackendStorage.getCurrentUserValue(Unidata.BackendStorageKeys.DASHBOARD),
            dashboardDecodedData = Ext.JSON.decode(dashboardData),
            view = this.getView();

        view.setGrid(dashboardDecodedData.grid);
        this.toggleGridView();
    },

    toggleGridView: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            grid = view.getGrid();

        if (grid.getEditMode()) {
            this.save();
        }

        grid.setEditMode(!grid.getEditMode());

        viewModel.set('gridEditMode', grid.getEditMode());
    }

});
