/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.group.GroupListEditorController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.grouplisteditor',

    init: function () {
        var view     = this.getView(),
            viewMode = this.getViewModel(),
            rules    = view.getRules();

        // добавляем только уже сохраненные правила
        rules = Ext.Array.filter(rules, function (rule) {
            return !rule.phantom;
        });

        viewMode.getStore('ruleListStore').add(rules);

        this.setGroupTreeMetaRecord(view.getMetaRecord());
        this.setGroupTreeRules(view.getRules());
    },

    updateMetaRecord: function (metaRecord) {
        this.setGroupTreeMetaRecord(metaRecord);
    },

    updateRules: function (rules) {
        this.setGroupTreeRules(rules);
    },

    setGroupTreeMetaRecord: function (metaRecord) {
        var view = this.getView();

        if (view.groupTree) {
            view.groupTree.setMetaRecord(metaRecord);
        }
    },

    setGroupTreeRules: function (rules) {
        var view = this.getView();

        if (view.groupTree) {
            view.groupTree.setRules(rules);
        }
    }
});
