/**
 * Шаг предпросмотра записей для визарда тестирования DQ
 *
 * @author Ivan Marshalkin
 * @date 2018-02-20
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.testwizard.step.RunStep',
        'Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewData',
        'Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewSandbox'
    ],

    alias: 'widget.dqtest.wizard.previewstep',

    statics: {
    },

    referenceHolder: true,

    previewComponent: null,

    layout: 'fit',

    config: {
        metaRecord: null,
        operationType: null,
        dqRules: null
    },

    items: [],

    listeners: {
    },

    nextStep: {
        xtype: 'dqtest.wizard.runstep',
        title: Unidata.i18n.t('admin.dqtest>resultStepTitle')
    },

    listeners: {
        activate: function (step) {
            var metaRecord = this.getMetaRecord(),
                operationType = this.getOperationType(),
                nextStep;

            // событие activate бросается дважды одно самим шагом, другое tab panel
            // TODO: сделать нормально
            if (arguments.length === 2) {
                if (this.previewComponent && this.previewComponent.getOperationType() === operationType) {
                    return;
                }

                if (this.previewComponent) {
                    Ext.destroy(this.previewComponent);
                    this.previewComponent = null;
                }

                if (operationType === 'DATA') {
                    this.previewComponent = Ext.create('Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewData', {
                        metaRecord: metaRecord,
                        wizardStep: step,
                        operationType: operationType
                    });
                } else if (operationType === 'SANDBOX') {
                    this.previewComponent = Ext.create('Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewSandbox', {
                        metaRecord: metaRecord,
                        wizardStep: step,
                        operationType: operationType
                    });
                }

                if (this.previewComponent) {
                    this.add(this.previewComponent);
                }

                // после пересоздания следующий шаг недоступен
                nextStep = step.getNextStep();
                nextStep.setStepAllowed(false);
            }
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        if (this.previewComponent) {
            Ext.destroy(this.previewComponent);
            this.previewComponent = null;
        }

        this.callParent(arguments);
    },

    /**
     * Функция вызывается перед активацией предыдущего шага
     */
    beforePrevStepActivate: function () {
        var wizard = this.getWizard();

        wizard.wizardWindow.fullSizeMargin = null;
        wizard.wizardWindow.setSize(600, 300);
        wizard.wizardWindow.center();
    },

    /**
     * Функция вызывается перед активацией следующего шага
     */
    beforeNextStepActivate: function () {
        var step = this.getNextStep(),
            metaRecord = this.getMetaRecord(),
            operationType = this.getOperationType(),
            dqRules = this.getDqRules(),
            searchHits;

        searchHits = this.previewComponent.getSelectedSearchHits();

        step.setOperationType(operationType);
        step.setDqRules(dqRules);
        step.setMetaRecord(metaRecord);
        step.setSearchHits(searchHits);
    }
});
