/**
 * @author Aleksandr Bavin
 * @date 2017-06-22
 */
Ext.define('Unidata.view.component.dashboard.entity.items.EntityLineChartModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard.entity.linechart',

    stores: {
        stats: {
            model: 'Unidata.model.dashboard.Stats',
            proxy: 'stat.stats',
            listeners: {
                load: 'onStatsStoreLoad'
            }
        }
    }

});
