/**
 * Первоначальный шаг (задание настроек) визарда тестирования DQ
 *
 * @author Ivan Marshalkin
 * @date 2018-02-20
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.step.SettingsStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewStep'
    ],

    alias: 'widget.dqtest.wizard.settingsstep',

    statics: {
        operationTypes: {
            DATA: 'DATA',
            SANDBOX: 'SANDBOX'
        }
    },

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'middle'
    },

    scrollable: 'vertical',

    config: {
        operationType: null,
        metaRecord: null,
        dqRules: null
    },

    title: Unidata.i18n.t('admin.dqtest>launchStepTitle'),

    radioExistedRecord: null,
    radioSandboxRecord: null,

    items: [],

    nextStep: {
        xtype: 'dqtest.wizard.previewstep',
        title: Unidata.i18n.t('admin.dqtest>previewStepTitle')
    },

    listeners: {
        activate: function () {
            var wizard = this.getWizard();

            // событие activate бросается дважды одно самим шагом, другое tab panel
            // TODO: сделать нормально
            if (arguments.length === 2) {
                wizard.wizardWindow.setTitle(Unidata.i18n.t('admin.dqtest>wizardWindowTitle'));
            }
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initStepItems();

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.radioExistedRecord = this.lookupReference('radioExistedRecord');
        this.radioSandboxRecord = this.lookupReference('radioSandboxRecord');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.radioExistedRecord = null;
        this.radioSandboxRecord = null;

        this.callParent(arguments);
    },

    initStepItems: function () {
        this.add([
            {
                xtype: 'fieldset',
                ui: 'un-fieldset-gray',
                title: Unidata.i18n.t('admin.dqtest>sourceSystems'),
                layout: {
                    type: 'hbox',
                    align: 'stretch',
                    pack: 'center'
                },
                width: '80%',
                items: [
                    {
                        xtype: 'radiofield',
                        reference: 'radioExistedRecord',
                        name: 'operationType',
                        inputValue: this.self.operationTypes.DATA,
                        boxLabel: Unidata.i18n.t('admin.dqtest>existedRecord'),
                        qtip: Unidata.i18n.t('admin.dqtest>existedRecord'),
                        margin: '10 15 10 0',
                        listeners: {
                            change: this.onOperationTypeChange,
                            scope: this
                        }
                    },
                    {
                        xtype: 'radiofield',
                        reference: 'radioSandboxRecord',
                        name: 'operationType',
                        inputValue: this.self.operationTypes.SANDBOX,
                        boxLabel: Unidata.i18n.t('admin.dqtest>sandboxRecord'),
                        qtip: Unidata.i18n.t('admin.dqtest>sandboxRecord'),
                        margin: '10 0 10 15',
                        listeners: {
                            change: this.onOperationTypeChange,
                            scope: this
                        }
                    }
                ]
            }
        ]);
    },

    /**
     * Функция вызывается перед активацией следующего шага
     */
    beforeNextStepActivate: function () {
        var step = this.getNextStep(),
            wizard = this.getWizard(),
            metaRecord = this.getMetaRecord(),
            operationType = this.getSelectedOperationType(),
            dqRules = this.getDqRules();

        step.setOperationType(operationType);
        step.setDqRules(dqRules);
        step.setMetaRecord(metaRecord);

        if (operationType === 'DATA') {
            wizard.wizardWindow.setTitle(Unidata.i18n.t('admin.dqtest>wizardWindowExistTitle'));
        } else if (operationType === 'SANDBOX') {
            wizard.wizardWindow.setTitle(Unidata.i18n.t('admin.dqtest>wizardWindowSandboxTitle'));
        }

        wizard.wizardWindow.fullSizeMargin = 20;
        wizard.wizardWindow.resizeFullSizeMarginWindow();
    },

    onOperationTypeChange: function () {
        this.updateNextStepAllowed();
    },

    onSourceSystemChange: function () {
        this.updateNextStepAllowed();
    },

    updateNextStepAllowed: function () {
        var step = this.getNextStep(),
            allowed = true,
            operationType = this.getSelectedOperationType();

        if (!operationType) {
            allowed = false;
        }

        step.setStepAllowed(allowed);
    },

    getSelectedOperationType: function () {
        var operationType;

        if (this.radioExistedRecord.getValue()) {
            operationType = this.radioExistedRecord.inputValue;
        } else if (this.radioSandboxRecord.getValue()) {
            operationType = this.radioSandboxRecord.inputValue;
        }

        return operationType;
    }
});
