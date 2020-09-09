/**
 * @author Aleksandr Bavin
 * @date 20.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.step.SettingsStep', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStep',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.settings.Default',
        'Unidata.view.steward.search.bulk.wizard.settings.ModifyRecords',
        'Unidata.view.steward.search.bulk.wizard.settings.RemoveRecords',
        'Unidata.view.steward.search.bulk.wizard.settings.RemoveRelationTo',
        'Unidata.view.steward.search.bulk.wizard.settings.RemoveRelationFrom',

        'Unidata.view.steward.search.bulk.wizard.step.SettingsStepController'
    ],

    alias: 'widget.steward.search.bulk.wizard.step.settings',

    controller: 'settings',

    /**
     * Текущий виджет с настройками
     * @type {Unidata.view.steward.search.bulk.wizard.settings.Default}
     */
    currentSettingsWidget: null,

    listeners: {
        activate: 'onActivate',
        beforedeactivate: 'onBeforedeactivate'
    },

    isConfirmStepAllowed: function () {
        if (!this.currentSettingsWidget) {
            return false;
        }

        return this.currentSettingsWidget.isConfirmStepAllowed();
    },

    onDestroy: function () {
        delete this.currentSettingsWidget;

        this.callParent(arguments);
    }

});
