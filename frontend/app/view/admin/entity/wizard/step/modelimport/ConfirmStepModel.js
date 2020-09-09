/**
 * @author Aleksandr Bavin
 * @date 2017-02-13
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelimport.ConfirmStepModel', {
    extend: 'Unidata.view.component.wizard.step.StepModel',

    alias: 'viewmodel.admin.entity.wizard.step.modelimport.confirm',

    data: {
        checkedCount: 0,
        importCount: 0,
        includeUsers: false,
        includeRoles: false
    }

});
