/**
 * @author Aleksandr Bavin
 * @date 2017-01-26
 */
Ext.define('Unidata.view.component.wizard.step.StepModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.wizard.step',

    data: {
        nextStep: null,
        stepDisabled: true
    },

    formulas: {
        nextStepExist: {
            bind: {
                bindTo: '{nextStep}'
            },
            get: function (nextStep) {
                return !Ext.isEmpty(nextStep);
            }
        },
        nextStepDisabled: {
            bind: {
                bindTo: '{nextStep}'
            },
            get: function (nextStep) {
                if (!Ext.isEmpty(nextStep)) {
                    return nextStep.isDisabled();
                } else {
                    return false;
                }
            }
        }
    }

});
