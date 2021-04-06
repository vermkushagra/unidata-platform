/**
 * @author Aleksandr Bavin
 * @date 2017-07-03
 */
Ext.define('Unidata.view.steward.dataimport.wizard.DataImportWizard', {

    extend: 'Unidata.view.component.wizard.Wizard',

    requires: [
        'Unidata.view.steward.dataimport.wizard.DataImportStep',
        'Unidata.view.steward.dataimport.wizard.step.*'
    ],

    alias: 'widget.dataimport.wizard',

    firstStep: {
        xtype: 'dataimport.wizard.settings'
    },

    config: {
        entityName: null
    },

    initComponent: function () {
        this.callParent(arguments);

        this.setFirstStep(
            {
                xtype: 'dataimport.wizard.settings',
                entityName: this.getEntityName()
            }
        );
    },

    updateEntityName: function (entityName) {
        var firstStep = this.getFirstStep();

        if (firstStep) {
            firstStep.setEntityName(entityName);
        }
    }

});
