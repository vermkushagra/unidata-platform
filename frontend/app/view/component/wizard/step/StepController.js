/**
 * @author Aleksandr Bavin
 * @date 2017-01-26
 */
Ext.define('Unidata.view.component.wizard.step.StepController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.wizard.step',

    onActivate: function () {
        var view = this.getView(),
            nextStep = view.getNextStep();

        view.setDisabled(false);

        if (nextStep) {
            nextStep.setDisabled(true);
        }

        this.updateButtons();
    },

    /**
     * При клике "Далее"
     */
    onNextClick: function () {
        var view = this.getView(),
            nextStep = view.getNextStep();

        if (nextStep) {
            nextStep.activate();
        }
    },

    /**
     * При клике "Далее"
     */
    onPrevClick: function () {
        var view = this.getView(),
            prevStep = view.getPrevStep();

        if (prevStep) {
            prevStep.activate();
        }
    },

    /**
     * Обновляет состояние кнопкок на основе step
     */
    updateButtons: function () {
        var step = this.getView(),
            nextStep = step.getNextStep(),
            prevStep = step.getPrevStep();

        this.updateNextButton(step, nextStep, prevStep);
        this.updatePrevButton(step, nextStep, prevStep);
    },

    updateNextButton: function (step, nextStep, prevStep) {
        var nextButton = this.lookupReference('nextButton'),
            disabled;

        if (!nextButton) {
            return;
        }

        if (!nextStep) {
            return nextButton.setHidden(true);
        } else {
            nextButton.setHidden(false);
        }

        disabled = (step.isDisabled() || !nextStep.getStepAllowed());

        nextButton.setDisabled(disabled);
    },

    updatePrevButton: function (step, nextStep, prevStep) {
        var prevButton = this.lookupReference('prevButton');

        if (!prevButton) {
            return;
        }

        if (!prevStep) {
            return prevButton.setHidden(true);
        } else {
            prevButton.setHidden(false);
        }
    },

    /**
     * Возвращает все следующие шаги
     *
     * @param {boolean} [includeSelf]
     */
    getAllNextSteps: function (includeSelf) {
        var view = this.getView(),
            nextSteps = [],
            nextStep = view.getNextStep();

        if (includeSelf) {
            nextSteps.push(view);
        }

        if (nextStep) {
            nextSteps = nextSteps.concat(nextStep.getAllNextSteps(true));
        }

        return nextSteps;
    },

    /**
     * Возвращает все предыдущие шаги
     *
     * @param {boolean} [includeSelf]
     */
    getAllPrevSteps: function (includeSelf) {
        var view = this.getView(),
            prevSteps = [],
            prevStep = view.getPrevStep();

        if (prevStep) {
            prevSteps = prevSteps.concat(prevStep.getAllPrevSteps(true));
        }

        if (includeSelf) {
            prevSteps.push(view);
        }

        return prevSteps;
    },

    /**
     * Активирует шаг
     */
    activate: function () {
        var view = this.getView();

        if (view.fireEvent('beforeactivate', view) === false) {
            return;
        }

        view.fireEvent('activate', view);
    },

    /**
     * Завершает работу визарда
     */
    finish: function () {
        var view = this.getView();

        view.fireEvent('finish', view);
    }

});
