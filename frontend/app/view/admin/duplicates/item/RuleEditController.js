/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.item.RuleEditController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.ruleedit',

    init: function () {
        var viewModel = this.getViewModel();

        this.callParent(arguments);

        viewModel.bind('{!ruleEditable}', function (value) {
            this.updateMatchingAlgorithmsReadOnly(value);
        }, this, {deep: true});
    },

    updateRule: function (rule) {
        var viewModel = this.getViewModel();

        viewModel.set('rule', rule);

        this.clearMatchingAlgorithmContainers();
        this.restoreMatchingAlgorithmContainers();
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);

        this.updateMatchingAlgorithmsReadOnly(readOnly);
    },

    updateMatchingAlgorithmsReadOnly: function (readOnly) {
        var view = this.getView(),
            containers;

        containers = view.matchingAlgorithmsContainer.items;

        if (containers) {
            containers.each(function (container) {
                container.setReadOnly(readOnly);
            });
        }
    },

    clearMatchingAlgorithmContainers: function () {
        var view = this.getView();

        view.matchingAlgorithmsContainer.removeAll();
    },

    restoreMatchingAlgorithmContainers: function () {
        var me         = this,
            view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            rule       = view.getRule();

        if (!rule) {
            return;
        }

        rule.matchingAlgorithms().each(function (matchingAlgorithm) {
            var container;

            container = me.createMatchingAlgorithmContainer(metaRecord, matchingAlgorithm);

            view.matchingAlgorithmsContainer.add(container);
        });
    },

    createMatchingAlgorithmContainer: function (metaRecord, matchingAlgorithm) {
        var viewModel = this.getViewModel(),
            readOnly  = viewModel.get('readOnly'),
            container;

        container = Ext.create('Unidata.view.admin.duplicates.item.fieldset.MatchingAlgorithm', {
            metaRecord: metaRecord,
            matchingAlgorithm: matchingAlgorithm,
            matchingAlgorithmStore: viewModel.getStore('matchAlgorithmsStore'),
            readOnly: readOnly,
            listeners: {
                'deletealgorithm': {
                    fn: this.onDeleteMatchingAlgorithm,
                    scope: this
                }
            },
            viewModel: {
                data: {
                    rule: viewModel.get('rule')
                }
            }
        });

        return container;
    },

    onDeleteMatchingAlgorithm: function (container, matchingAlgorithm) {
        var view = this.getView(),
            rule = view.getRule();

        view.matchingAlgorithmsContainer.remove(container);
        rule.matchingAlgorithms().remove(matchingAlgorithm);
    },

    onAddMatchAlgorithmButtonClick: function () {
        var view       = this.getView(),
            rule       = view.getRule(),
            metaRecord = view.getMetaRecord(),
            matchingAlgorithm,
            container;

        matchingAlgorithm = Ext.create('Unidata.model.matching.MatchingAlgorithm');

        rule.matchingAlgorithms().add(matchingAlgorithm);

        container = this.createMatchingAlgorithmContainer(metaRecord, matchingAlgorithm);

        view.matchingAlgorithmsContainer.add(container);
    },

    onRuleSaveButtonClick: function () {
        var view = this.getView(),
            rule = view.getRule();

        if (!this.isRuleHasAlgorithm()) {
            Unidata.showError(Unidata.i18n.t('admin.duplicates>ruleHasntAlgorithms'));

            return;
        }

        if (!this.isValidRule() || !this.isValidMatchingAlgorithms()) {
            Unidata.showError(Unidata.i18n.t('admin.duplicates>ruleIncorrect'));

            return;
        }

        if (view.fireEvent('beforesaverule', rule) !== false) {
            this.saveRule(rule);
        } else {
            Unidata.showError(Unidata.i18n.t('admin.duplicates>ruleWithNameExists'));
        }
    },

    onRuleActiveToggle: function (self) {
        var view = this.getView(),
            rule = view.getRule();

        rule.set('active', self.pressed);
    },

    /**
     * Сохраняет правило
     * @param rule
     */
    saveRule: function (rule) {
        var me = this;

        rule.save({
            success: function () {
                me.commitChanges();

                Unidata.showMessage(Unidata.i18n.t('admin.duplicates>ruleSaveSuccess'));
            }
        });
    },

    disableEditor: function () {
        var view = this.getView();

        view.setDisabled(true);
    },

    enableEditor: function () {
        var view = this.getView();

        view.setDisabled(false);
    },

    /**
     * Возвращает истину если правило заполнено валидно
     */
    isValidRule: function () {
        var view    = this.getView(),
            rule    = view.getRule(),
            isValid = true;

        if (!rule.get('name')) {
            isValid = false;
        }

        this.clearIncorrectField();
        this.markIncorrectField();

        return isValid;
    },

    markIncorrectField: function () {
        var view = this.getView(),
            rule = view.getRule();

        if (!rule.get('name')) {
            view.ruleName.markInvalid('');
        }
    },

    clearIncorrectField: function () {
        var view = this.getView();

        view.ruleName.clearInvalid();
    },

    /**
     * Возвращает истину если добавленые алгоритмы сопоставления валидны
     */
    isValidMatchingAlgorithms: function () {
        var view       = this.getView(),
            containers = view.matchingAlgorithmsContainer,
            isValid    = true;

        containers.items.each(function (container) {
            if (!container.isValidMatchingAlgorithm()) {
                isValid = false;
            }
        });

        return isValid;
    },

    isRuleHasAlgorithm: function () {
        var view = this.getView(),
            rule = view.getRule();

        return rule.matchingAlgorithms().getCount() > 0;
    },

    commitChanges: function () {
        var view = this.getView(),
            rule = view.getRule(),
            matchingAlgorithms = rule.matchingAlgorithms();

        rule.commit();
        matchingAlgorithms.commitChanges();

        matchingAlgorithms.each(function (matchingAlgorithm) {
            var matchingFields = matchingAlgorithm.matchingFields();

            matchingAlgorithm.commit();

            matchingFields.commitChanges();

            matchingFields.each(function (matchingField) {
                matchingField.commit();
            });
        });
    }
});
