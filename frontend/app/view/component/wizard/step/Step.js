/**
 * @author Aleksandr Bavin
 * @date 2017-01-26
 */
Ext.define('Unidata.view.component.wizard.step.Step', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.wizard.step.StepController',
        'Unidata.view.component.wizard.step.StepModel'
    ],

    alias: 'widget.component.wizard.step',

    viewModel: {
        type: 'component.wizard.step'
    },

    controller: 'component.wizard.step',

    cls: 'un-wizard-step',

    referenceHolder: true,

    methodMapper: [
        {
            method: 'updateButtons'
        },
        {
            method: 'getAllNextSteps'
        },
        {
            method: 'getAllPrevSteps'
        },
        {
            method: 'activate'
        },
        {
            method: 'finish'
        }
    ],

    /**
     * Предыдущий шаг
     * @type {Unidata.view.component.wizard.step.Step}
     */
    prevStep: null,

    config: {
        title: Unidata.i18n.t('glossary:step'),
        stepAllowed: false,
        stepIndex: 1,
        /**
         * Следующий шаг
         * @type {Unidata.view.component.wizard.step.Step}
         */
        nextStep: null
    },

    listeners: {
        activate: 'onActivate'
    },

    defaults: {
        padding: '0 0 8 0'
    },

    dockedItems: [
        {
            xtype: 'un.toolbar',
            reference: 'buttonItems',
            dock: 'bottom',
            autoHide: true,
            items: []
        }
    ],

    initItems: function () {
        var dockedButtons;

        this.callParent(arguments);

        dockedButtons = this.createDockedButtons();

        if (!Ext.isEmpty(dockedButtons)) {
            this.lookupReference('buttonItems').add(dockedButtons);
        }
    },

    createDockedButtons: function () {
        return [
            {
                xtype: 'button',
                text: Unidata.i18n.t('admin.metamodel>importPrev'),
                reference: 'prevButton',
                color: 'transparent',
                listeners: {
                    click: 'onPrevClick'
                }
            },
            {
                xtype: 'container',
                flex: 1
            },
            {
                xtype: 'button',
                text: Unidata.i18n.t('admin.metamodel>importNext'),
                reference: 'nextButton',
                disabled: true,
                // hidden: true,
                listeners: {
                    click: 'onNextClick'
                }
            }
        ];
    },

    /**
     * @returns {Unidata.view.component.wizard.step.Step}
     */
    getPrevStep: function () {
        return this.prevStep;
    },

    setDisabled: function (disabled) {
        var nextStep = this.getNextStep(),
            changed = (this.isDisabled() != disabled),
            result;

        if (disabled && nextStep) {
            nextStep.setDisabled(disabled);
        }

        result = this.callParent(arguments);

        if (changed) {
            this.fireStateChange();
        }

        return result;
    },

    applyNextStep: function (nextStep) {
        if (nextStep instanceof Unidata.view.component.wizard.step.Step) {
            return nextStep;
        }

        return Ext.widget(nextStep);
    },

    updateStepAllowed: function (stepAllowed) {
        this.fireEvent('allowchange', stepAllowed);
    },

    /**
     * @param {Unidata.view.component.wizard.step.Step} nextStep
     * @param {Unidata.view.component.wizard.step.Step} nextStepOld
     */
    updateNextStep: function (nextStep, nextStepOld) {
        if (nextStepOld) {
            nextStepOld.prevStep = null;

            nextStepOld.un('statechange', this.fireStateChange, this);
            nextStepOld.un('allowchange', this.updateButtons, this);
        }

        if (nextStep) {
            nextStep.prevStep = this;

            nextStep.on('statechange', this.fireStateChange, this);
            nextStep.on('allowchange', this.updateButtons, this);

            if (this.isDisabled()) {
                nextStep.setDisabled(true);
            }

            nextStep.setStepIndex(this.getStepIndex() + 1);
        }

        this.fireEvent('updatenextstep', this, nextStep);

        this.fireStateChange();
    },

    fireStateChange: function () {
        this.fireEvent('statechange', this);
        this.getController().updateButtons();
    },

    onDestroy: function () {
        var nextStep = this.getNextStep();

        if (nextStep) {
            nextStep.destroy();
        }

        this.callParent(arguments);
    }

});
