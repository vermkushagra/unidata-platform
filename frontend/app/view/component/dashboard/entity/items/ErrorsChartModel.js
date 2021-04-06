/**
 * @author Aleksandr Bavin
 * @date 2017-06-27
 */
Ext.define('Unidata.view.component.dashboard.entity.items.ErrorsChartModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard.entity.dqerrors.chart',

    stores: {
        stats: {
            fields: [
                Unidata.EntityPanelConstants.ERROR_SEVERITY.CRITICAL,
                Unidata.EntityPanelConstants.ERROR_SEVERITY.HIGH,
                Unidata.EntityPanelConstants.ERROR_SEVERITY.LOW,
                Unidata.EntityPanelConstants.ERROR_SEVERITY.NORMAL
            ],
            proxy: 'stat.stats.error',
            listeners: {
                load: 'onStatsStoreLoad'
            }
        }
    }

});
