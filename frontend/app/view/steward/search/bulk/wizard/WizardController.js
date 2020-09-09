/**
 * @author Aleksandr Bavin
 * @date 16.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.WizardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.wizard',

    /**
     * Активируем первый шаг при добавлении визарда
     */
    onBeforeactivate: function () {
        // this.activateStep(0);
    },

    onTabchange: function () {
        this.updateNextStepAllowed();
    },

    onStepAdd: function (tabpanel, step) {
        step.on('stepallowchange', this.updateNextStepAllowed, this);
    },

    onStepRemove: function (tabpanel, step) {
        step.un('stepallowchange', this.updateNextStepAllowed, this);
    },

    updateNextStepAllowed: function () {
        var viewModel = this.getViewModel(),
            nextStep = this.getNextStep(),
            nextStepAllowed = false;

        if (nextStep) {
            nextStepAllowed = nextStep.isStepAllowed();
        }

        viewModel.set('nextStepAllowed', nextStepAllowed);
    },

    getWizardTabpanel: function () {
        return this.getView().wizardTabpanel;
    },

    /**
     * Возвращает все шаги (табы)
     */
    getAllSteps: function () {
        return this.getWizardTabpanel().items;
    },

    /**
     * Возвращает шаг, со смещением, относительно fromStep или активного шага, если не передать fromStep
     * @param {number} offset
     * @param {Unidata.view.steward.search.bulk.wizard.WizardStep} [fromStep]
     * @returns {undefined|Unidata.view.steward.search.bulk.wizard.WizardStep}
     */
    getStepWithOffset: function (offset, fromStep) {
        var allSteps = this.getAllSteps(),
            afterThisTabIndex;

        if (!fromStep) {
            fromStep = this.getWizardTabpanel().getActiveTab();
        }

        afterThisTabIndex = allSteps.indexOf(fromStep);

        return allSteps.getAt(afterThisTabIndex + offset);
    },

    /**
     * Возвращает следующий шаг
     * @param [fromStep] относительно какого шага (по дефолту относительно активного)
     * @returns {undefined|Unidata.view.steward.search.bulk.wizard.WizardStep}
     */
    getNextStep: function (fromStep) {
        return this.getStepWithOffset(1, fromStep);
    },

    /**
     * Возвращает предыдущий шаг
     * @param [fromStep] относительно какого шага (по дефолту относительно активного)
     * @returns {undefined|Unidata.view.steward.search.bulk.wizard.WizardStep}
     */
    getPrevStep: function (fromStep) {
        return this.getStepWithOffset(-1, fromStep);
    },

    activateStep: function (index) {
        this.getWizardTabpanel().setActiveTab(index);
    },

    /**
     * Активирует следующий шаг
     */
    activateNextStep: function () {
        this.activateStep(this.getNextStep());
    },

    /**
     * Активирует предыдущий шаг
     */
    activatePrevStep: function () {
        this.activateStep(this.getPrevStep());
    }

});
