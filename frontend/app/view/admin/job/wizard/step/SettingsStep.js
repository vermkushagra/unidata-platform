/**
 * Первоначальный шаг (задание настроек). Визард импорта / экспорта списка операций (jobs)
 *
 * @author Ivan Marshalkin
 * @date 2018-03-19
 */

Ext.define('Unidata.view.admin.admin.job.wizard.step.SettingsStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.admin.job.wizard.step.ExportStep',
        'Unidata.view.admin.admin.job.wizard.step.ImportStep'
    ],

    alias: 'widget.jobimportexport.wizard.settingsstep',

    statics: {
        operationTypes: {
            EXPORT: 'EXPORT',
            IMPORT: 'IMPORT'
        }
    },

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'middle',
        pack: 'center'
    },

    bodyPadding: '0 16 8 16',

    scrollable: 'vertical',

    config: {
        operationType: null
    },

    title: Unidata.i18n.t('admin.job>wizard>settingsStepTitle'),

    radioExportOperation: null,
    radioImportOperation: null,

    items: [],

    // фейковй степ для отображения следующего шага
    nextStep: {
        xtype: 'component.wizard.step',
        title: Unidata.i18n.t('admin.job>wizard>fakeExecutionStepTitle')
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initStepItems();

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.radioExportOperation = this.lookupReference('radioExportOperation');
        this.radioImportOperation = this.lookupReference('radioImportOperation');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.radioExportOperation = null;
        this.radioImportOperation = null;

        this.callParent(arguments);
    },

    initStepItems: function () {
        this.add([
            {
                xtype: 'fieldset',
                border: false,
                layout: {
                    type: 'hbox',
                    align: 'stretch',
                    pack: 'center'
                },
                width: '80%',
                items: [
                    {
                        xtype: 'radiofield',
                        reference: 'radioImportOperation',
                        boxLabel: Unidata.i18n.t('admin.job>wizard>importOperation'),
                        name: 'operationType',
                        inputValue: this.self.operationTypes.IMPORT,
                        qtip: Unidata.i18n.t('admin.job>wizard>importOperation'),
                        margin: '10 15 10 0',
                        listeners: {
                            change: this.onOperationTypeChange,
                            scope: this
                        }
                    },
                    {
                        xtype: 'radiofield',
                        reference: 'radioExportOperation',
                        boxLabel: Unidata.i18n.t('admin.job>wizard>exportOperation'),
                        name: 'operationType',
                        inputValue: this.self.operationTypes.EXPORT,
                        qtip: Unidata.i18n.t('admin.job>wizard>exportOperation'),
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

    onOperationTypeChange: function () {
        this.setNextStepWidget();
        this.updateNextStepAllowed();
    },

    updateNextStepAllowed: function () {
        var step = this.getNextStep(),
            allowed = true,
            operationType = this.getSelectedOperationType();

        if (!step) {
            return;
        }

        if (!operationType) {
            allowed = false;
        }
        //Следующий шаг доступен только по кнопке "Далее"
        step.setDisabled(true);

        step.setStepAllowed(allowed);
    },

    setNextStepWidget: function () {
        var operationType = this.getSelectedOperationType(),
            nextStep = null;

        switch (operationType) {
            case 'EXPORT':
                nextStep = {
                    xtype: 'jobimportexport.wizard.exportstep'
                };
                break;

            case 'IMPORT':
                nextStep = {
                    xtype: 'jobimportexport.wizard.importstep'
                };
                break;
        }

        if (nextStep) {
            this.setNextStep(nextStep);
        }
    },

    getSelectedOperationType: function () {
        var operationType;

        if (this.radioExportOperation.getValue()) {
            operationType = this.radioExportOperation.inputValue;
        } else if (this.radioImportOperation.getValue()) {
            operationType = this.radioImportOperation.inputValue;
        }

        return operationType;
    }
});
