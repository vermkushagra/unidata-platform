Ext.define('Unidata.view.component.search.query.QueryModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query',

    data: {
        searchQuery: null,
        defaultDisplayAttributesSwitcherHidden: true,
        defaultSearchAttributesSwitcherHidden: true,
        searchSectionVisible: {},
        metarecord: null,
        entityReadOnly: null,
        attributeTabletsCount: 0
    },

    stores: {
        searchClassifierResultStore: {
            model: 'Unidata.model.search.SearchHit',
            proxy: {
                type: 'ajax',
                limitParam: 'count',
                startParam: '',
                url: Unidata.Config.getMainUrl() + 'internal/search/meta',
                reader: {
                    type: 'json',
                    extraParams: {
                        fields: 'classifiers'
                    },
                    rootProperty: 'hits',
                    totalProperty: 'total_count'
                }
            }
        }
    },

    formulas: {
        operationTypeHidden: {
            get: function () {
                return !Unidata.Config.getCustomerCfg()['SEARCH_OPERATION_TYPE'];
            }
        },
        attributeFilterPanelVisible: {
            bind: {
                attributeTabletsCount: '{filterPanel.searchableAttributesCount}'
            },
            get: function (getter) {
                var attributeTabletsCount = getter.attributeTabletsCount;

                return attributeTabletsCount > 0;
            }
        }
    }
});
