/**
 * @author Aleksandr Bavin
 * @date 20.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.WizardStepController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.default',

    setDisabled: function (flag) {
        return this.getView().setDisabled(flag);
    },

    onActivate: function () {
        // отключаем все последующие шаги, т.к. могут быть важные промежуточные
        // которые, если захотят, сами пустят пользователя дальше
        this.disallowNextStep(true);
        this.setDisabled(false);
    },

    /**
     * Доступен ли данный шаг
     */
    isStepAllowed: function () {
        return this.getView().getStepAllowed();
    },

    /**
     * Делает данный шаг доступным
     */
    allowStep: function () {
        if (this.isStepAllowed()) {
            return;
        }

        this.getView().setStepAllowed(true);
    },

    disallowStep: function () {
        this.setDisabled(true);

        if (!this.isStepAllowed()) {
            return;
        }

        this.getView().setStepAllowed(false);
    },

    beforeAllowNextStep: function () {
        return true;
    },

    /**
     * Делает доступным следующий шаг, обычно, если выполнены все необходимые условия на данном шаге
     */
    allowNextStep: function () {
        var nextStep;

        if (!this.beforeAllowNextStep()) {
            return false;
        }

        nextStep = this.getWizard().getNextStep(this.getView());

        if (nextStep) {
            nextStep.allowStep();
        }
    },

    /**
     * @param cascade - рекурсивно отключает все шаги
     */
    disallowNextStep: function (cascade) {
        var nextStep = this.getWizard().getNextStep(this.getView());

        if (nextStep) {
            nextStep.disallowStep();

            if (cascade) {
                nextStep.disallowNextStep(true);
            }
        }
    },

    /**
     * @returns {Unidata.view.steward.search.bulk.wizard.Wizard}
     */
    getWizard: function () {
        return this.getView().wizardTabPanel;
    },

    /**
     * При клике на кнопку "Далее"
     */
    onNextClick: function () {
        this.getWizard().activateNextStep();
    },

    /**
     * При клике на кнопку "Назад"
     */
    onPrevClick: function () {
        this.getWizard().activatePrevStep();
    }

});
