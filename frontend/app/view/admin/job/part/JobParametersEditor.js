/**
 * Компонент для редактирования парметров операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-14
 */

Ext.define('Unidata.view.admin.job.part.JobParametersEditor', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.job.part.JobParametersEditorController',
        'Unidata.view.admin.job.part.JobParametersEditorModel',
        'Unidata.view.admin.job.part.parameter.Parameter'
    ],

    alias: 'widget.admin.job.editor.parameters',

    controller: 'admin.job.editor.parameters',

    viewModel: {
        type: 'admin.job.editor.parameters'
    },

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'form',
            overflowY: 'auto',
            reference: 'paramsForm',
            layout: {
                type: 'vbox',
                align: 'stretch'
            }
        }
    ],

    showValidationError: function (param, message) {
        this.getController().showValidationError(param, message);
    }

});
