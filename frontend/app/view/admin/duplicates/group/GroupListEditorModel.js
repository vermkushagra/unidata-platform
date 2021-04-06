/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.group.GroupListEditorModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.duplicates.grouplisteditor',

    data: {},

    stores: {
        ruleListStore: {
            model: 'Unidata.model.matching.Rule',
            autoLoad: false
        }
    },

    formulas: {}
});
