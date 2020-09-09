/**
 * Компонент для поиска, оперирует коллекцией термов, которые предоставляют данные для запроса
 *
 * @author Aleksandr Bavin
 * @date 2018-02-09
 */
Ext.define('Unidata.module.search.SearchQuery', {

    mixins: [
        'Unidata.mixin.ConfigBind',
        'Unidata.mixin.search.TermsCollection'
    ],

    requires: [
        'Unidata.module.search.SearchQueryTerm',
        'Unidata.module.search.term.*',
        'Unidata.module.search.term.attribute.*',
        'Unidata.module.search.term.classifier.*',
        'Unidata.module.search.term.relation.*',
        'Unidata.module.search.term.dq.*',
        'Unidata.module.search.term.facet.*'
    ],

    config: {
        loadTerms: null
    },

    constructor: function (config) {
        var loadTerms;

        this.callParent(arguments);

        this.initConfig(config);

        if (loadTerms = this.getLoadTerms()) {
            Ext.Array.each(loadTerms, function (termConfig) {
                this.addTerm(Ext.create(termConfig));
            }, this);
        } else {
            this.initTerms();
        }

        this.onTermsReady();
    },

    initTerms: Ext.emptyFn,

    onTermsReady: Ext.emptyFn,

    /**
     * @returns {Ext.promise.Promise}
     */
    search: function () {
        var deferred = new Ext.Deferred(),
            store = this.getSearchResultStore(),
            currentPage = store.currentPage,
            page = currentPage > (Math.ceil(store.totalCount / store.pageSize)) ? 1 : currentPage;

        this.getTermsData()
            .then(
                function (termsData) {
                    store.getProxy().setExtraParams(termsData);

                    store.loadPage(page, {
                        scope: store,
                        callback: function (searchHits, operation, success) {
                            // console.log('searchQuery', searchHits);
                            if (!success) {
                                deferred.reject();
                            } else {
                                deferred.resolve(searchHits);
                            }
                        }
                    });
                }
            )
            .done();

        return deferred.promise;
    },

    onTermsMapUpdate: function () {
        this.invalidateStub();
    },

    /**
     * Находит {@link Unidata.module.search.term.SortField}
     *
     * @param {string} field
     * @returns {Unidata.module.search.term.SortField | null}
     */
    findSortFieldTerm: function (field) {
        var term;

        term = this.getTermsCollection().findBy(function (term) {
            if (term instanceof Unidata.module.search.term.SortField) {
                return term.getField() === field;
            }

            return false;
        });

        return term;
    },

    getSearchResultStore: function () {
        if (!this.searchResultStore) {
            this.searchResultStore = new Ext.data.Store({
                model: 'Unidata.model.search.SearchHit',
                pageSize: Unidata.Config.getCustomerCfg()['SEARCH_ROWS'],
                proxy: 'data.searchquery'
            });
        }

        return this.searchResultStore;
    }

});
