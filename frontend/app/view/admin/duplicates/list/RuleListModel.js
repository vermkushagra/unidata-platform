/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.list.RuleListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.duplicates.rulelist',

    data: {},

    stores: {
        ruleListStore: {
            model: 'Unidata.model.matching.Rule',
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/matching/rules',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        }
    },

    formulas: {}
});
