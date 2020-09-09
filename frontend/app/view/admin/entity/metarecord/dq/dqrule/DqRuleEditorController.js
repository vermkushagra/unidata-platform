/**
 * Редактор правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEditorController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.dq.dqruleeditor',

    VALIDATE: 'VALIDATE',
    ENRICH: 'ENRICH',

    onCleanseFunctionItemClick: function (tree, node) {
        var cleanseFunction,
            cleanseFunctionPath;

        cleanseFunction = node.get('record');

        if (!(cleanseFunction instanceof Unidata.model.cleansefunction.CleanseFunction)) {
            return;
        }
        cleanseFunctionPath = node.getPath('name', '.').substring(2);
        this.loadAndUpdateCleanseFunction(cleanseFunctionPath, cleanseFunctionPath);
    },

    onIsValidationChange: function (self, checked) {
        var view = this.getView(),
            dqRuleValidation = view.dqRuleValidation,
            dqRule = view.getDqRule(),
            dqRaise = dqRule.getRaise(),
            validationDisabled = checked && !this.isValidationAvailable(view.getCleanseFunction()),
            hideDqRuleValidation = !checked || !this.isValidationAvailable(view.getCleanseFunction());

        if (validationDisabled) {
            this.showWarningForRule(this.VALIDATE);
        } else if (dqRule.data.isEnrichment === true && !this.isEnrichAvailable(view.getCleanseFunction())) {
            this.showWarningForRule(this.ENRICH);
        } else {
            this.hideWarning();
        }

        dqRuleValidation.setHidden(hideDqRuleValidation);

        if (checked) {
            if (!dqRaise) {
                dqRaise = Ext.create('Unidata.model.dataquality.DqRaise', {});
                dqRule.setRaise(dqRaise);
                dqRuleValidation.setDqRaise(dqRaise);
            }
        } else {
            dqRule.getRaise().drop();
            dqRuleValidation.setDqRaise(null);
        }
    },

    onIsEnrichChange: function (self, checked) {
        var view = this.getView(),
            dqRule = view.getDqRule(),
            dqRuleEnrichment = view.dqRuleEnrichment,
            dqEnrich = dqRule.getEnrich(),
            enrichDisabled = checked && !this.isEnrichAvailable(view.getCleanseFunction()),
            hideDqRuleEnrichment = !checked || !this.isEnrichAvailable(view.getCleanseFunction());

        if (enrichDisabled) {
            this.showWarningForRule(this.ENRICH);
        } else if (dqRule.data.isValidation === true && !this.isValidationAvailable(view.getCleanseFunction())) {
            this.showWarningForRule(this.VALIDATE);
        } else {
            this.hideWarning();
        }

        dqRuleEnrichment.setHidden(hideDqRuleEnrichment);

        if (checked) {
            if (!dqEnrich) {
                dqEnrich = Ext.create('Unidata.model.dataquality.DqEnrich', {});
                dqRule.setEnrich(dqEnrich);
                dqRuleEnrichment.setDqEnrich(dqEnrich);
            }
        } else {
            dqRule.getEnrich().drop();
            dqRuleEnrichment.setDqEnrich(null);
        }
    },

    loadAndUpdateCleanseFunction: function (cleanseFunctionName, cleanseFunctionPath) {
        var view = this.getView(),
            CleanseFunctionApi = Unidata.util.api.CleanseFunction;

        view.setLoading(true);
        CleanseFunctionApi.loadCleanseFunction(cleanseFunctionName, view.draftMode)
            .then(function (cleanseFunction) {
                view.setCleanseFunction(cleanseFunction);
                view.setCleanseFunctionPath(cleanseFunctionPath);
                view.setLoading(false);
            }, function () {
                view.setLoading(false);
            }).done();
    },

    showWarningForRule: function (ruleType) {
        var view = this.getView(),
            warningText,
            dqRuleSelectWarning = view.dqRuleFunction.dqRuleSelectWarning;

        if (ruleType === this.VALIDATE) {
            warningText = view.dqRuleFunction.getValidationWarningTemplate();
        } else {
            warningText = view.dqRuleFunction.getEnrichWarningTemplate();
        }

        if (dqRuleSelectWarning.hidden === true) {
            dqRuleSelectWarning.setHidden(false);
        }
        dqRuleSelectWarning.setText(warningText);
    },

    hideWarning: function () {
        var view = this.getView(),
            dqRuleSelectWarning = view.dqRuleFunction.dqRuleSelectWarning;

        dqRuleSelectWarning.setHidden(true);
    },

    /**
     * Обработчик смены readOnly
     */
    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('dqRuleEditorReadOnly', Boolean(readOnly));
    },

    updateDqRule: function (dqRule) {
        var dqRuleValidation = view.dqRuleValidation,
            dqRuleEnrichment = view.dqRuleEnrichment;

        if (!dqRule) {
            return;
        }

        dqRuleValidation.setDqRaise(dqRule.getRaise());
        dqRuleEnrichment.setDqEnrich(dqRule.getEnrich());
    },

    isValidationAvailable: function (clearanceFunction) {
        var ports;

        if (!clearanceFunction) {
            return false;
        }

        ports = clearanceFunction.outputPorts();

        if (ports.count() === 0) {
            return false;
        }

        return Ext.Array.some(ports.getRange(), function (port) {
            return port.data.dataType === Unidata.model.dataquality.DqRule.DQ_RULE_PORT_DATA_TYPES.BOOLEAN;
        });

    },

    isEnrichAvailable: function (clearanceFunction) {
        var ports;

        if (!clearanceFunction) {
            return false;
        }

        ports = clearanceFunction.outputPorts();

        if (ports.count() === 0) {
            return false;
        }

        return true;
    },

    onExecutionContextPathChanged: function () {
        var view = this.getView(),
            dqRuleFunction = view.dqRuleFunction,
            dqRulePortPanel = dqRuleFunction.dqRulePortPanel;

        // при изменении контекста выполнения перерисовываем все порты
        dqRulePortPanel.rebuildPorts();
    }

});
