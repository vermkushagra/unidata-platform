/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithmModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.duplicates.ruleedit.matchingalgorithm',

    data: {
        rule: null,
        matchingAlgorithm: null,
        readOnly: null
    },

    stores: {
        matchingAlgorithmListStore: {
            model: 'Unidata.model.matching.MatchingAlgorithm',
            autoLoad: false
        }
    },

    formulas: {
        withPreprocessing: {
            bind: {
                bindTo: '{rule.withPreprocessing}',
                deep: true
            },
            get: function (withPreprocessing) {
                return withPreprocessing;
            }
        }
    }
});
