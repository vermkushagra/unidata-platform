/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.list.RuleListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.rulelist',

    /**
     * Обработчик изменения metaRecord
     * @param metaRecord
     */
    updateMetaRecord: function () {
        this.reloadRuleList();
    },

    /**
     * Возвращает список правил в гриде
     */
    getRules: function () {
        var view = this.getView();

        return view.ruleList.getStore().getRange();
    },

    /**
     * Заменить правило по индексу
     *
     * @param index
     * @param rule
     */
    replaceRuleAtIndex: function (index, rule) {
        var view = this.getView(),
            store;

        store = view.ruleList.getStore();

        store.removeAt(index);
        store.insert(index, rule);
    },

    /**
     * Обновление списка правил
     */
    reloadRuleList: function () {
        var view       = this.getView(),
            metaRecord = view.getMetaRecord();

        view.ruleList.getStore().reload({
            params: {
                entityName: metaRecord.get('name')
            }
        });
    },

    /**
     * Обработчик клика по кнопке добавления правила
     */
    onAddRuleButtonClick: function () {
        var view = this.getView();

        if (view.fireEvent('beforeruleadd') === false) {
            return;
        }

        this.addNewRule();
    },

    addNewRule: function () {
        var view       = this.getView(),
            viewModel  = this.getViewModel(),
            metaRecord = view.getMetaRecord(),
            selectionModel,
            rule;

        rule = Ext.create('Unidata.model.matching.Rule', {
            active: true,
            name: '',
            description: '',
            entityName: metaRecord.get('name')
        });

        viewModel.getStore('ruleListStore').add(rule);

        selectionModel = view.ruleList.getSelectionModel();
        selectionModel.select(rule);
    },

    /**
     * Удаление правила
     */
    onDeleteRuleButtonClick: function (tree, row, column, e, eOpts, record) {
        var title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmRemoveRule');

        Unidata.showPrompt(title, msg, this.deleteRule, this, null, [record]);
    },

    /**
     * Удаляет правило
     * @param record
     */
    deleteRule: function (record) {
        var me = this,
            view = this.getView();

        record.erase({
            success: function () {
                view.fireEvent('ruledelete');

                me.reloadRuleList();
            }
        });
    },

    onRuleSelect: function (selectionModel, record, index) {
        var me = this,
            view = this.getView(),
            sm   = view.ruleList.getSelectionModel();

        //TODO: delete
        if (record.phantom) {
            view.setCurrentRule(record);

            view.fireEvent('ruleload', record);

            return;
        }

        sm.setLocked(true);
        Unidata.model.matching.Rule.load(record.getId(), {
            success: function (rule) {
                view.setCurrentRule(rule);
                me.replaceRuleAtIndex(index, rule);

                view.ruleList.suspendEvent('select');

                sm.setLocked(false);
                me.setSelectedRule(rule);
                sm.setLocked(true);

                view.ruleList.resumeEvent('select');

                view.fireEvent('ruleload', rule);
            },
            callback: function () {
                sm.setLocked(false);
            }
        });
    },

    onRuleDeselect: function () {
        var view = this.getView();

        view.fireEvent('ruledeselect');
    },

    onRuleGroupButtonClick: function () {
        var view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            ruleStore,
            records,
            wnd;

        ruleStore = view.ruleList.getStore();

        records = ruleStore.getRange();

        wnd = Ext.create('Unidata.view.admin.duplicates.group.GroupListEditor', {
            rules: records,
            metaRecord: metaRecord,
            modal: true,
            width: 800,
            height: 600
        });
        wnd.show();
    },

    /**
     * Определяет доступность колонки удаления
     *
     * @returns {*|boolean}
     */
    isDeleteRuleButtonDisabled: function () {
        // у пользователя должны быть права на удаление
        return !Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'delete');
    },

    onRuleBeforeSelect: function (grid, record) {
        var view    = this.getView(),
            oldRule = view.getCurrentRule(),
            newRule = record;

        return view.fireEvent('beforeruleselect', oldRule, newRule);
    },

    /**
     * Выбирает правило для редактирования
     *
     * @param rule
     */
    setSelectedRule: function (rule) {
        var view = this.getView(),
            sm   = view.ruleList.getSelectionModel();

        view.ruleList.suspendEvent('beforeselect');

        sm.select(rule);

        view.ruleList.resumeEvent('beforeselect');
    },

    removeRuleFromList: function (rule) {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('ruleListStore');

        store.remove(rule);
    },

    /**
     * Обработка клика по кнопке экспорта
     */
    onRuleExportButtonClick: function () {
        var view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            entityName = metaRecord.get('name'),
            url        = Unidata.Config.getMainUrl() + 'internal/matching/xml/',
            downloadConfig;

        downloadConfig = {
            method: 'GET',
            url: url,
            params: {
                entityName: entityName,
                token: Unidata.Config.getToken()
            }
        };

        Unidata.util.DownloadFile.downloadFile(downloadConfig);
    },

    /**
     * Обработка клика по кнопке импорта
     */
    onRuleImportButtonClick: function (button) {
        var me   = this,
            view = this.getView();

        Ext.widget({
            xtype: 'form.window',
            title: Unidata.i18n.t('admin.duplicates>duplicateSearchRulesImport'),
            animateTarget: button,
            formParams: {
                method: 'POST',
                url: Unidata.Config.getMainUrl() + 'internal/matching/xml/',
                items: [
                    {
                        xtype: 'fileuploadfield',
                        msgTarget: 'under',
                        allowBlank: false
                    }
                ],
                baseParams: {
                }
            },
            listeners: {
                submitstart: function () {
                    view.setLoading(true);
                },
                submitend: function (cmp, success) {
                    view.setLoading(false);

                    if (success) {
                        Unidata.showMessage(Unidata.i18n.t('admin.duplicates>rulesSuccessImported'));
                    }

                    me.reloadRuleList();
                }
            }
        }).show();
    }
});
