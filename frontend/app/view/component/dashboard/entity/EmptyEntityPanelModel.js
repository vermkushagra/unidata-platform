/**
 * @author Aleksandr Bavin
 * @date 2017-10-18
 */
Ext.define('Unidata.view.component.dashboard.entity.EmptyEntityPanelModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard.entity.empty',

    stores: {
        searchHits: {
            model: 'Unidata.model.search.SearchHit',
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
