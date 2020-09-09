/**
 * @author Aleksandr Bavin
 * @date 2017-01-26
 */
Ext.define('Unidata.view.component.wizard.WizardController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.wizard',

    init: function () {
    },

    /**
     * При переключении таба
     */
    onTabChange: function () {
    },

    /**
     * При добавлении элемента в tabpanel
     *
     * @param wizard
     * @param step
     */
    onStepAdd: function (wizard, step) {
        var wizard = this.getView(),
            nextStep = step.getNextStep();

        if (nextStep) {
            this.insertStep(step, nextStep);
        }

        step.setWizard(wizard);

        step.on('blockwizard', this.blockWizard, this);
        step.on('unblockwizard', this.unblockWizard, this);

        step.on('updatenextstep', this.insertStep, this);
        step.on('beforeactivate', this.beforeActivateStep, this);
        step.on('activate', this.activateStep, this);
        step.on('finish', this.finishWizard, this);
    },

    onStepRemove: function (wizard, step) {
        step.setWizard(null);

        step.un('blockwizard', this.blockWizard, this);
        step.un('unblockwizard', this.unblockWizard, this);

        step.un('updatenextstep', this.insertStep, this);
        step.un('beforeactivate', this.beforeActivateStep, this);
        step.un('activate', this.activateStep, this);
        step.un('finish', this.finishWizard, this);
    },

    beforeActivateStep: function () {
        if (this.isBlocked()) {
            return false;
        }
    },

    activateStep: function (step) {
        this.getView().setActiveTab(step);
    },

    finishWizard: function () {
        var view = this.getView();

        view.fireEvent('finish', this);
        view.destroy();
    },

    /**
     * Добавляет newStep после afterStep, все старые элементы убираются
     *
     * @param afterStep
     * @param newStep
     */
    insertStep: function (afterStep, newStep) {
        var view = this.getView(),
            index = view.items.indexOf(afterStep);

        Ext.Array.each(view.items.getRange(index + 1), function (item) {
            view.remove(item, false);
        }, this, true);

        view.add(newStep);
    },

    /**
     * Добавляет первый шаг, все старые айтемы удаляются
     */
    addFirstStep: function () {
        var view = this.getView();

        view.removeAll(false);
        view.add(view.getFirstStep());
    },

    /**
     * Возвращает все шаги, которые достумны в данный момент
     *
     * @returns {Array}
     */
    getAllSteps: function () {
        var view = this.getView(),
            firstStep = view.getFirstStep();

        if (!firstStep) {
            return [];
        }

        return firstStep.getAllNextSteps(true);
    },

    blockWizard: function () {
        var view = this.getView();

        view.setBlocked(true);
    },

    unblockWizard: function () {
        var view = this.getView();

        view.setBlocked(false);
    },

    isBlocked: function () {
        var view = this.getView();

        return Boolean(view.getBlocked());
    }
});
