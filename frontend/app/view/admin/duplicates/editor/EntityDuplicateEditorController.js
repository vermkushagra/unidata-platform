/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.editor.EntityDuplicateEditorController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.entityduplicateeditor',

    /**
     * Обработка обновления метамодели
     *
     * @param metaRecord
     */
    updateMetaRecord: function (metaRecord) {
        var view = this.getView();

        view.attributeTree.setData(metaRecord);

        view.ruleList.setMetaRecord(metaRecord);
        view.ruleEditor.setMetaRecord(metaRecord);

        view.ruleEditor.disableEditor();
    },

    /**
     * Обработчик успешной загрузки списка правил
     *
     * @param rule
     */
    onRuleLoad: function (rule) {
        var view      = this.getView(),
            viewModel = this.getViewModel();

        viewModel.set('currentRule', rule);

        view.ruleEditor.setHidden(false);

        view.ruleEditor.setRule(rule);

        view.ruleEditor.enableEditor();
    },

    onBeforeSaveRule: function (rule4Save) {
        var view = this.getView(),
            rules,
            nameExist;

        rules = view.ruleList.getRules();

        nameExist = Ext.Array.some(rules, function (rule) {
            return rule !== rule4Save && rule.get('name') === rule4Save.get('name');
        });

        return !nameExist;
    },

    onBeforeRuleSelect: function (oldRule, newRule) {
        var title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmLeaveUnsavedRule'),
            view  = this.getView(),
            oldRulePhantom;

        // если предыдущего выделения нет то делать здесь нечего
        if (!oldRule) {
            return;
        }

        oldRulePhantom = oldRule.phantom;

        function acceptRejectChanges () {
            if (oldRulePhantom) {
                view.ruleList.removeRuleFromList(oldRule);
            } else {
                oldRule.rejectRuleChanges();
            }

            view.ruleList.setSelectedRule(newRule);
        }

        // если текущее правило с изменениями запрашиваем разрешение на смену редактируемого правила
        if (view.ruleEditor.isRuleEditorDirty() || oldRulePhantom) {
            Unidata.showPrompt(title, msg, acceptRejectChanges);

            return false;
        }
    },

    onBeforeRuleAdd: function () {
        var me    = this,
            view  = this.getView(),
            title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmAddNewRule'),
            rule  = view.ruleEditor.getRule();

        function acceptRejectChanges () {
            rule.rejectRuleChanges();
            me.resetEntityDuplicateEditor();

            view.ruleList.addNewRule();
        }

        // если текущее правило требует сохранения запрещено добавлять
        if (view.ruleEditor.isRuleEditorDirty()) {
            Unidata.showPrompt(title, msg, acceptRejectChanges);

            return false;
        }
    },

    isEntityDuplicateEditorDirty: function () {
        var view = this.getView();

        return view.ruleEditor.isRuleEditorDirty();
    },

    resetEntityDuplicateEditor: function () {
        var view = this.getView();

        view.ruleEditor.setRule(null);
        view.ruleEditor.setHidden(true);

        view.ruleList.setCurrentRule(null);
    },

    onRuleDeselect: function () {
        var view = this.getView();

        view.ruleEditor.setHidden(true);
    },

    onRuleDelete: function () {
        this.resetEntityDuplicateEditor();
    }
});
