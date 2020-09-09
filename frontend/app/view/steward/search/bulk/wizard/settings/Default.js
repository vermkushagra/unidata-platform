/**
 * Виджет, реализующий настройку пакетной операции, все остальные должны наследоваться от него
 * @author Aleksandr Bavin
 * @date 22.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.Default', {
    extend: 'Ext.container.Container',

    alias: 'widget.steward.search.bulk.wizard.settings.default',

    items: {
        html: Unidata.i18n.t('search>wizard.noOperationSettings')
    },

    allowNextStepOnAdded: true, // сразу разрешает следующий шаг

    /**
     * шаг, к которому привязаны настройки
     * @type {Unidata.view.steward.search.bulk.wizard.step.SettingsStep}
     */
    wizardStep: null,

    config: {
        operationSettings: {} // настройки для визарда
    },

    requiredExternalData: [
        'entityName'
    ],

    listeners: {
        added: function () {
            if (this.allowNextStepOnAdded) {
                this.getStep().allowNextStep();
            }
        }
    },

    onDestroy: function () {
        delete this.wizardStep;

        this.callParent(arguments);
    },

    /**
     * Можно запретить переход на шаг подтверждения
     * @returns {boolean}
     */
    isConfirmStepAllowed: function () {
        return true;
    },

    /**
     * Обновляет operationSettings из внешних данных
     */
    updateExternalData: function (operationSettings) {
        var wizard = this.getWizard();

        Ext.Array.each(this.requiredExternalData, function (externalDataName) {
            operationSettings[externalDataName] = wizard.getExternalData(externalDataName);
        });
    },

    /**
     * Обновляет настройки operationSettings, выполняется во время getOperationSettings
     * @param operationSettings
     */
    beforeGetOperationSettings: function () {
        // дополняем operationSettings
    },

    /**
     * Возвращает настройки для пакетной операции
     * @see Unidata.view.steward.search.bulk.wizard.step.SettingsStepController.onBeforedeactivate
     * @returns {Unidata.view.steward.search.bulk.wizard.settings.Default.operationSettings|{}}
     */
    getOperationSettings: function () {
        this.updateExternalData(this.operationSettings);
        this.beforeGetOperationSettings(this.operationSettings);

        return this.operationSettings;
    },

    getStep: function () {
        return this.wizardStep;
    },

    getWizard: function () {
        return this.getStep().getWizard();
    }

});
