/**
 *
 * @author Ivan Marshalkin
 * @date 2016-12-01
 */

Ext.define('Unidata.view.component.search.query.classifier.ClassifierFilterItemModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query.classifieritem',

    data: {
        searchQuery: null,
        supplementarySearchQuery: null
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

    formulas: {}
});
