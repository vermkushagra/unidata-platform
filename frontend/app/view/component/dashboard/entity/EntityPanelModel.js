/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.entity.EntityPanelModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard.entity',

    stores: {
        searchHits: {
            model: 'Unidata.model.search.SearchHit',
            pageSize: 10000,
            proxy: {
                type: 'data.searchproxy',
                url: Unidata.Config.getMainUrl() + 'internal/search/meta',
                extraParams: {
                    fields: 'dashboardVisible',
                    text: 'true'
                }
            },
            listeners: {
                load: 'onSearchHitsStoreLoad'
            }
        }
    }

});
