/**
 * @author Aleksandr Bavin
 * @date 2017-02-13
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelimport.ConfirmStepController', {
    extend: 'Unidata.view.component.wizard.step.StepController',

    alias: 'controller.admin.entity.wizard.step.modelimport.confirm',

    onActivate: function () {
        this.callParent(arguments);

        this.lookupReference('confirmCheckbox').setValue(false);
    },

    onComponentAfterRender: function () {
        var view = this.getView();

        view.confirmCheckbox.setActiveError(Unidata.i18n.t('admin.metamodel>importWillDropDraft'));
    }

});
