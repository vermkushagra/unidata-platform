/**
 * @author Aleksandr Bavin
 * @date 2017-07-03
 */
Ext.define('Unidata.view.steward.dataimport.wizard.DataImportStep', {

    extend: 'Unidata.view.component.wizard.step.Step',

    config: {
        entityName: null,
        sourceSystem: null,
        mergeWithPreviousVersion: true
    },

    updateEntityName: function (entityName) {
        var nextStep = this.getNextStep();

        if (nextStep) {
            nextStep.setEntityName(entityName);
        }
    },

    updateSourceSystem: function (sourceSystem) {
        var nextStep = this.getNextStep();

        if (nextStep) {
            nextStep.setSourceSystem(sourceSystem);
        }
    },

    updateMergeWithPreviousVersion: function (mergeWithPreviousVersion) {
        var nextStep = this.getNextStep();

        if (nextStep) {
            nextStep.setMergeWithPreviousVersion(mergeWithPreviousVersion);
        }
    }

});
