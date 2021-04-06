/**
 * @author Aleksandr Bavin
 * @date 2017-06-22
 */
Ext.define('Unidata.view.component.dashboard.entity.items.CountsModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard.entity.counts',

    stores: {
        stats: {
            model: 'Unidata.model.dashboard.Stats',
            proxy: 'stat.stats.last',
            listeners: {
                load: 'onStatsStoreLoad'
            }
        }
    }

});
