/**
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.view.workflow.task.WorkflowTaskModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.workflowtask',

    data: {
        /** @type {Unidata.model.workflow.Task} */
        task: null,
        activeTaskCount: null
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
        currentUserIsAssignee: {
            bind: {
                bindTo: '{task.taskAssignee}'
            },
            get: function (taskAssignee) {
                return taskAssignee == Unidata.Config.getUser().get('login');
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
