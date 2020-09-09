/**
 *
 * @author Ivan Marshalkin
 * @date 2018-01-29
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.DataQualityController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.dq',

    init: function () {
        var view = this.getView();

        this.callParent(arguments);

        this.setReadOnlyComponentState(view.getReadOnly());
    },

    showDqRule: function (dqRule) {
        var me = this,
            dqRuleName,
            CleanseFunctionApi = Unidata.util.api.CleanseFunction,
            view = this.getView(),
            dqRuleEditorContainer = view.dqRuleEditorContainer,
            cleanseFunctionName,
            title;

        dqRuleEditorContainer.removeAll();

        if (!dqRule) {
            return;
        }

        dqRuleName = dqRule.get('name');
        title = this.buildDqRuleEditorContainerTitle(dqRuleName);
        dqRuleEditorContainer.setTitle(title);

        cleanseFunctionName = dqRule.get('cleanseFunctionName');

        if (dqRule.phantom || !cleanseFunctionName) {
            me.initAndCreateDqRuleEditor(dqRule, null);
        } else {
            view.setLoading(true);

            CleanseFunctionApi.loadCleanseFunction(cleanseFunctionName, view.draftMode)
                .then(function (cleanseFunction) {
                    me.initAndCreateDqRuleEditor(dqRule, cleanseFunction);
                    view.setLoading(false);
                }, function () {
                    view.setLoading(false);
                }).done();
        }
    },

    initAndCreateDqRuleEditor: function (dqRule, cleanseFunction) {
        var view = this.getView(),
            me = this,
            dqRuleEditorContainer = view.dqRuleEditorContainer,
            dqRuleEditor,
            readOnly = view.getReadOnly();

        // специальное правило качества всегда должно быть только для чтения
        if (dqRule.get('special')) {
            readOnly = true;
        }

        dqRuleEditor = me.createDqRuleEditor(dqRule, cleanseFunction, {
            flex: 1,
            readOnly: readOnly
        });

        dqRuleEditorContainer.add(dqRuleEditor);

        if (view.dqRuleEditor) {
            view.dqRuleEditor.destroy();
        }

        view.dqRuleEditor = dqRuleEditor;

        dqRuleEditor.on('portupathchanged', me.onPortUPathChanged.bind(me));
        dqRuleEditor.on('dqrulenamechange', me.onDqRuleNameChange.bind(me));

        if (dqRuleEditorContainer.getCollapsed() !== false) {
            dqRuleEditorContainer.expand();
        }
    },

    createDqRuleEditor: function (dqRule, cleanseFunction, customCfg) {
        var dqRuleEditor,
            view = this.getView(),
            sourceSystems = view.getSourceSystems(),
            cleanseFunctions = view.getCleanseFunctions(),
            metaRecord = view.getMetaRecord(),
            draftMode = view.draftMode,
            cfg;

        cfg = {
            dqRule: dqRule,
            sourceSystems: sourceSystems,
            cleanseFunctions: cleanseFunctions,
            cleanseFunction: cleanseFunction,
            metaRecord: metaRecord,
            draftMode: draftMode
        };

        cfg = Ext.apply(cfg, customCfg);
        dqRuleEditor = Ext.create('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEditor', cfg);

        return dqRuleEditor;
    },

    onDqRuleGridSelectionChange: function (self, selected) {
        var view = this.getView(),
            dqRule = null;

        if (selected.length) {
            dqRule = selected[0];
        }

        if (dqRule) {
            view.dqNavigation.setFlex(1);
            view.dqRuleEditorContainer.setFlex(1);

            view.updateLayout();
        } else {
            view.dqRuleEditorContainer.setHeight(100);
            view.dqRuleEditorContainer.setFlex(0);

            view.updateLayout();
        }

        this.showDqRule(dqRule);
    },

    /**
     * Обработчик события изменения порта
     *
     * @param self {Unidata.view.admin.entity.metarecord.dq.dqrule.DqRulePort}
     * @param dqRule {Unidata.model.dataquality.DqRule}
     */
    onPortUPathChanged: function (self, dqRule) { // jscs:ignore disallowUnusedParams
        var view = this.getView();

        view.dqNavigation.refreshViewByDqRule(dqRule);
    },

    onDqRuleNameChange: function (self, name) {
        var view = this.getView(),
            title;

        title = this.buildDqRuleEditorContainerTitle(name);

        view.dqRuleEditorContainer.setTitle(title);
    },

    buildDqRuleEditorContainerTitle: function (name) {
        var view = this.getView(),
            title,
            clsPrefix = view.cls;

        title = Ext.String.format(Unidata.i18n.t('admin.dq>dqRuleConfigTitle') + ': <span class="{0}-title-info">{1}</span>', clsPrefix, name);

        return title;
    },

    /**
     * Обработчик смены readOnly
     */
    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);

        this.setReadOnlyComponentState(readOnly);
    },

    setReadOnlyComponentState: function (readOnly) {
        var view = this.getView(),
            items = view.items;

        if (!items.isMixedCollection) {
            return;
        }

        view.dqNavigation.setReadOnly(readOnly);

        // редактор создается динамически, поэтому его может не быть
        if (view.dqRuleEditor) {
            view.dqRuleEditor.setReadOnly(readOnly);
        }
    },

    commitChanges: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord();

        if (!metaRecord) {
            return;
        }

        metaRecord.dataQualityRules().each(function (dqRule) {
            dqRule.commitDeepDirty();
        });
    },

    onDataQualityActivate: function () {
        var view = this.getView();

        if (view.dqNavigation) {
            view.dqNavigation.refreshAttributeTree();
        }
    }
});
