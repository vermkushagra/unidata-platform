/**
 * @author Ivan Marshalkin
 * @date 2018-05-26
 */

Ext.define('Unidata.view.workflow.task.details.TaskDetailsModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.taskdetails',

    data: {
        task: null,
        taskHistory: null
    },

    formulas: {
        recordStateText: {
            bind: {
                bindTo: '{task.recordState}'
            },
            get: function (recordState) {
                if (recordState) {
                    return Ext.String.format(
                        '<span class="un-workflow-task-record-state">{0}</span>',
                        Unidata.i18n.t('workflow>tasksearch.task.' + recordState.toLowerCase())
                    );
                } else {
                    return '';
                }
            }
        },
        taskFinishedText: {
            bind: {
                bindTo: '{task.finished}'
            },
            get: function (finished) {
                return finished ?
                    Unidata.i18n.t('glossary:completed', {context: 'female'}) :
                    Unidata.i18n.t('glossary:notCompleted', {context: 'female'});
            }
        },
        processFinishedText: {
            bind: {
                bindTo: '{task.processFinished}'
            },
            get: function (finished) {
                return finished ?
                    Unidata.i18n.t('glossary:completed', {context: 'male'}) :
                    Unidata.i18n.t('glossary:notCompleted', {context: 'male'});
            }
        }
    }

});
