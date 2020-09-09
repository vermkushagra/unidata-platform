/**
 * @author Aleksandr Bavin
 * @date 2017-01-31
 */
Ext.define('Unidata.view.admin.entity.wizard.step.EntityOperationTypeStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.entity.wizard.step.oldmodelimport.UploadFileStep',
        'Unidata.view.admin.entity.wizard.step.modelimport.UploadFileStep',
        'Unidata.view.admin.entity.wizard.step.modelexport.ConfirmStep'
    ],

    alias: 'widget.admin.entity.wizard.step.operationtype',

    items: [],

    statics: {
        operationTypes: {
            IMPORT: 'import',
            EXPORT: 'export',
            IMPORT_OLD: 'import_old'
        },
        operationTypeNames: {
            IMPORT: Unidata.i18n.t('admin.metamodel>advancedImport'),
            EXPORT: Unidata.i18n.t('common:export'),
            IMPORT_OLD: Unidata.i18n.t('admin.metamodel>baseImport')
        }
    },

    config: {
        operationType: null
    },

    initItems: function () {
        this.callParent(arguments);

        this.add({
            xtype: 'radiogroup',
            reference: 'radiogroup',
            columns: 1,
            defaults: {
                listeners: {
                    render: function (cmp) {
                        if (!cmp.qtip) {
                            return;
                        }

                        Ext.QuickTips.register({
                            target: cmp.getEl(),
                            text: cmp.qtip
                        });
                    }
                }
            },
            items: [
                {
                    boxLabel: this.self.operationTypeNames.IMPORT,
                    name: 'operationType',
                    inputValue: this.self.operationTypes.IMPORT,
                    qtip: Unidata.i18n.t('admin.metamodel>entitiesWithAllRelations'),
                    disabled: !Unidata.Config.userHasRights(Unidata.Config.RIGHT.ADMIN_DATA_MANAGEMENT, ['read', 'create', 'update'])
                },
                {
                    boxLabel: this.self.operationTypeNames.IMPORT_OLD,
                    name: 'operationType',
                    inputValue: this.self.operationTypes.IMPORT_OLD,
                    qtip: Unidata.i18n.t('admin.metamodel>entitiesOnly'),
                    disabled: !Unidata.Config.userHasRights(Unidata.Config.RIGHT.ADMIN_DATA_MANAGEMENT, ['read', 'create', 'update'])
                },
                {
                    boxLabel: this.self.operationTypeNames.EXPORT,
                    name: 'operationType',
                    inputValue: this.self.operationTypes.EXPORT,
                    disabled: !Unidata.Config.userHasRight(Unidata.Config.RIGHT.ADMIN_DATA_MANAGEMENT, 'read')
                }
            ],
            listeners: {
                change: this.onOperationTypeChange,
                scope: this
            }
        });

        // отображаем потенциальные шаги
        this.setNextStep({
            xtype: 'component.wizard.step',
            title: Unidata.i18n.t('glossary:settings'),
            nextStep: {
                xtype: 'component.wizard.step',
                title: Unidata.i18n.t('common:confirmation')
            }
        });
    },

    onOperationTypeChange: function (radiogroup, newValue) {
        var nextStepOld = this.getNextStep(),
            nextStep;

        this.setOperationType(newValue.operationType);

        switch (newValue.operationType) {
            case this.self.operationTypes.IMPORT:
                this.setTitle(Unidata.i18n.t('common:import'));
                nextStep = {
                    xtype: 'admin.entity.wizard.step.modelimport.uploadfile'
                };
                break;
            case this.self.operationTypes.EXPORT:
                this.setTitle(Unidata.i18n.t('common:export'));
                nextStep = {
                    xtype: 'admin.entity.wizard.step.modelexport.confirm'
                };
                break;
            case this.self.operationTypes.IMPORT_OLD:
                this.setTitle(Unidata.i18n.t('common:import'));
                nextStep = {
                    xtype: 'admin.entity.wizard.step.oldmodelimport.uploadfile'
                };
                break;
        }

        nextStep.stepAllowed = true;

        this.setNextStep(nextStep);

        if (nextStepOld) {
            nextStepOld.destroy();
        }
        //Следующий шаг доступен только по кнопке "Далее"
        this.getNextStep().setDisabled(true);
    }

});
