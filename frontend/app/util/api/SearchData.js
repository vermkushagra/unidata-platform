/**
 * API поиска данных
 * @author Sergey Shishigin
 * @date 21-02-2017
 */
Ext.define('Unidata.util.api.SearchData', {
    singleton: true,

    /**
     * Поиск данных
     *
     * @param extraParams {Array} Массив extra params
     * @returns {Ext.promise}
     */
    search: function (extraParams, store) {
        var deferred,
            currentPage,
            page;

        deferred = new Ext.Deferred();

        if (!store) {
            store = this.createSearchDataStore();
        }
        this.configSearchDataStore(store, extraParams);
        currentPage = store.currentPage;
        page = currentPage > (Math.ceil(store.totalCount / store.pageSize)) ? 1 : currentPage;
        store.loadPage(page, {
            scope: store,
            callback: function (searchHits, operation, success) {
                var response;

                if (success) {
                    deferred.resolve({searchHits: searchHits, extraParams: extraParams});
                } else {
                    response = operation.getError().response;

                    deferred.reject(response);
                }
            }
        });

        return deferred.promise;
    },

    /**
     * Создать store для выполнения поискового запроса
     *
     * @private
     * @returns {Ext.data.Store}
     */
    createSearchDataStore: function () {
        var store,
            cfg;

        cfg = {
            model: 'Unidata.model.search.SearchHit'
        };

        store = Ext.create('Ext.data.Store', cfg);

        return store;
    },

    /**
     * Конфигурировать store для выполнения поискового запроса
     * @param store
     * @param extraParams
     * @returns {Ext.data.Store}
     */
    configSearchDataStore: function (store, extraParams) {
        var proxy,
            proxyExtraParams,
            proxyAlias;

        proxyAlias = this.getProxyAliasBySearchType(extraParams['@type']);
        proxy = Ext.createByAlias(proxyAlias);
        proxyExtraParams = proxy.getExtraParams();

        extraParams = Ext.apply(proxyExtraParams, extraParams);

        proxy.setExtraParams(extraParams);

        if (extraParams && extraParams['facets'] && Ext.Array.contains(extraParams['facets'], 'duplicates_only')) {
            proxy.setUrl(Unidata.Config.getMainUrl() + 'internal/search/duplicates');
        }

        store.setProxy(proxy);

        return store;
    },

    /**
     * Сформировать proxyalias в соответствии с типом поиска
     * @param searchType
     * @returns {String}
     */
    getProxyAliasBySearchType: function (searchType) {
        var alias;

        switch (searchType) {
            case 'FORM':
                alias = 'proxy.data.searchproxyform';
                break;
            case 'COMBO':
                alias = 'proxy.data.searchproxycombo';
                break;
            case 'COMPLEX':
                alias = 'proxy.data.searchproxycomplex';
                break;
            default:
                alias = 'proxy.data.searchproxysimple';
        }

        return alias;
    }
});
