/**
 * @author Aleksandr Bavin
 * @date 2017-01-26
 */
Ext.define('Unidata.view.component.wizard.Wizard', {
    extend: 'Unidata.view.component.wizard.WizardTabPanel',

    requires: [
        'Unidata.view.component.wizard.WizardController',
        'Unidata.view.component.wizard.WizardModel',
        'Unidata.view.component.wizard.step.Step'
    ],

    alias: 'widget.component.wizard',

    viewModel: {
        type: 'component.wizard'
    },

    controller: 'component.wizard',

    cls: 'un-wizard',

    referenceHolder: true,

    methodMapper: [
        {
            method: 'getAllSteps'
        },
        {
            method: 'isBlocked'
        }
    ],

    config: {
        /**
         * Первый шаг визарда, который определяет все последующие
         * @type {Unidata.view.component.wizard.step.Step}
         */
        firstStep: null,

        /**
         * Признак того что визард заблокирован
         */
        blocked: false
    },

    listeners: {
        add: 'onStepAdd',
        remove: 'onStepRemove',
        tabchange: 'onTabChange'
    },

    items: [],

    initItems: function () {
        this.callParent(arguments);
        this.getController().addFirstStep();
    },

    updateBlocked: function (blocked) {
        this.fireEvent('blockchange', this, blocked);
    },

    applyFirstStep: function (firstStep) {
        if (firstStep instanceof Unidata.view.component.wizard.step.Step) {
            return firstStep;
        }

        return Ext.widget(firstStep);
    },

    /**
     * @param {Unidata.view.component.wizard.step.Step} firstStep
     */
    updateFirstStep: function () {
        if (this.rendered) {
            this.getController().addFirstStep();
        }
    }

});
