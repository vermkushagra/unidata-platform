/**
 * @author Aleksandr Bavin
 * @date 21.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.step.ConfirmStepController', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStepController',
    alias: 'controller.confirm',

    /**
     * Перед активацией шага, ставим нужный текст, что бы пользователь понимал, что он подтверждает
     */
    onBeforeactivate: function () {
        var wizard = this.getWizard(),
            content = this.lookupReference('content'),
            confirmText = wizard.getConfirmText();

        // Если какой-то из шагов не разрешил подтверждение, то не открываем
        if (!wizard.isConfirmStepAllowed()) {
            return false;
        }

        if (!confirmText) {
            confirmText = '<p>' + Unidata.i18n.t('search>wizard.operationWillStart', {name: wizard.getOperationName()}) + '</p>';
        }

        content.update(confirmText);
    },

    /**
     * При подтверждении, собираем все данные и запускаем операцию
     */
    onConfirmClick: function () {
        var wizard = this.getWizard(),
            params = {},
            selectedIds = wizard.getSelectedIds(),
            jsonData;

        // основные настройки
        params['@type'] = wizard.getOperationType();

        params['selectedByRequest'] = wizard.getQueryParams();

        if (selectedIds.length) {
            params['selectedByIds'] = selectedIds;
        } else {
            params['selectedByIds'] = null;
            // Костыль для бэкенда UN-2513
            params['selectedByRequest']['count'] = this.getViewModel().get('selectedCount');
        }

        // настройки из SettingsStep
        Ext.Object.merge(params, wizard.getOperationSettings());

        jsonData = Ext.util.JSON.encode(params);

        Ext.Ajax.request({
            url: Unidata.Config.getMainUrl() + 'internal/data/bulk/run',
            method: 'POST',
            jsonData: jsonData,
            scope: this,
            success: function (response) {
                var jsonResp = Ext.util.JSON.decode(response.responseText);

                if (!jsonResp) {
                    this.showErrorMessage();
                }
            },
            failure: function () {
                this.showErrorMessage();
            }
        });

        Unidata.showMessage(
          Unidata.i18n.t('search>wizard.operationStart', {name: wizard.getOperationName()})
        );

        wizard.activateStep(0);
        wizard.fireComponentEvent('resetsettings');
    },

    showErrorMessage: function () {
        Unidata.showError(Unidata.i18n.t('search>wizard.operationError'));
    }

});
