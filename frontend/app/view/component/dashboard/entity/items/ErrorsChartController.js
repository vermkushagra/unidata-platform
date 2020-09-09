/**
 * @author Aleksandr Bavin
 * @date 2017-06-27
 */
Ext.define('Unidata.view.component.dashboard.entity.items.ErrorsChartController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.entity.dqerrors.chart',

    /**
     * Загрузка данных
     */
    loadStats: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            statsStore = viewModel.getStore('stats'),
            statsStoreProxy = statsStore.getProxy(),
            entityName = view.getEntityName();

        if (!entityName) {
            return;
        }

        statsStoreProxy.setExtraParam('entityName', entityName);

        statsStore.load();

        view.fireEvent('loadingstart', view);
    },

    onStatsStoreLoad: function (store, records, success) {
        var view = this.getView();

        if (success && records.length) {
            this.updateChartData(records[0]);
        }

        view.fireEvent('loadingend', view);
    },

    updateChartData: function (record) {
        var view = this.getView();

        Ext.Object.each(Unidata.EntityPanelConstants.ERROR_SEVERITY, function (severity) {
            view.setSeverityCount(severity, record.get(severity) || 0);
        }, this);
    }

});
