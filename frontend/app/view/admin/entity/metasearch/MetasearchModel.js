/**
 * @author Aleksandr Bavin
 * @date 30.05.2016
 */
Ext.define('Unidata.view.admin.entity.metasearch.MetasearchModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.metasearch',

    data: {
        totalCount: 0
    },

    stores: {
        searchResultStore: {
            model: 'Unidata.model.search.SearchHit',
            pageSize: 25,
            proxy: {
                type: 'ajax',
                limitParam: 'count',
                startParam: '',
                url: Unidata.Config.getMainUrl() + 'internal/search/meta',
                reader: {
                    type: 'json',
                    rootProperty: 'hits',
                    totalProperty: 'total_count'
                }
            }
        }
    }

});
