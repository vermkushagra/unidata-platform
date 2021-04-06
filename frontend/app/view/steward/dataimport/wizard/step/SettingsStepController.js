/**
 * @author Aleksandr Bavin
 * @date 2017-07-03
 */
Ext.define('Unidata.view.steward.dataimport.wizard.step.SettingsStepController', {

    extend: 'Unidata.view.component.wizard.step.StepController',

    alias: 'controller.dataimport.wizard.settings',

    onValidityChange: function (form, valid) {
        var view = this.getView(),
            nextStep = view.getNextStep();

        nextStep.setStepAllowed(valid);
    },

    onMergeWithPreviousVersionChange: function (combo, value) {
        this.getView().setMergeWithPreviousVersion(value);
    },

    onEntityComboChange: function (combo, value) {
        this.getView().setEntityName(value);
    },

    onSourceSystemChange: function (combo, value) {
        this.getView().setSourceSystem(value);
    },

    onSourceSystemSelect: function (combobox, record) {
        var view = this.getView(),
            mergeCheckBox = this.lookupReference('mergeWithPreviousVersion'),
            isAdminSystemName = view.getAdminSystemName() === record.get('name');

        mergeCheckBox.setReadOnly(isAdminSystemName);
        mergeCheckBox.setDisabled(isAdminSystemName);

        if (isAdminSystemName) {
            mergeCheckBox.setValue(true);
        }
    },

    onSourceSystemsStoreLoad: function (store, records, success, eOpts) {
        var view = this.getView(),
            response,
            responseText,
            adminSystemName;

        if (!success) {
            return;
        }

        response = eOpts.getResponse();

        if (!response) {
            return;
        }

        responseText = Ext.decode(response.responseText, true);

        if (responseText) {
            adminSystemName = responseText.adminSystemName;

            view.setAdminSystemName(adminSystemName);
        }
    }

});
