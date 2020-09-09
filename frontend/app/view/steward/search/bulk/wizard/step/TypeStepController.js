/**
 * @author Aleksandr Bavin
 * @date 21.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.step.TypeStepController', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStepController',
    alias: 'controller.type',

    /**
     * При некоторых условиях, нельзя активировать следующий шаг
     * @returns {boolean}
     */
    beforeAllowNextStep: function () {
        var wizard = this.getWizard();

        if (!wizard.getOperationType()) {
            return false;
        }

        return true;
    },

    onActivate: function () {
        this.callParent(arguments);

        this.getWizard().setConfirmText(null);

        // пробуем разрешить следующий шаг
        this.allowNextStep();
    },

    /**
     * При выборе типа операции
     */
    onRadiogroupChange: function (radiogroup, newValue) {
        var view = this.getView(),
            wizard = this.getWizard();

        // при reset
        if (!newValue.selectedType) {
            this.disallowNextStep(true);

            return;
        }

        wizard.setOperationType(
            newValue.selectedType
        );

        wizard.setOperationName(
            view.allTypes[newValue.selectedType]
        );

        this.allowNextStep();
    }

});
