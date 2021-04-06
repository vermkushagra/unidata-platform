/**
 * Модель компонента для редактирования операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.part.JobEditorModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.job.editor',

    data: {
        job: null,
        jobMetaRecord: null,
        readOnly: true,
        nameEditable: false,
        deleteAllowed: false
    },

    formulas: {

        allowChangeActiveFlag: {
            bind: {
                readOnly: '{readOnly}',
                jobWithError: '{job.error}'
            },
            get:  function (getter) {
                return !getter.readOnly &&  !getter.jobWithError;
            }
        }
    }
});
