Ext.define('Unidata.view.component.search.query.QueryModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query',

    data: {
        searchSectionVisible: {},
        metarecord: null,
        entityReadOnly: null,
        attributeTabletsCount: 0,
        queryPresetPanelVisible: false
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
        attributeFilterPanelVisible: {
            bind: {
                attributeTabletsCount: '{attributeTabletsCount}'
            },
            get: function (getter) {
                var attributeTabletsCount = getter.attributeTabletsCount;

                return attributeTabletsCount > 0;
            }
        }
    }
});
