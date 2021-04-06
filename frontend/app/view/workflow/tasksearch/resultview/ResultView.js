/**
 * @author Aleksandr Bavin
 * @date 2016-08-18
 */
Ext.define('Unidata.view.workflow.tasksearch.resultview.ResultView', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.workflow.task.WorkflowTask',
        'Unidata.view.workflow.tasksearch.resultview.ResultViewController',
        'Unidata.view.workflow.tasksearch.resultview.ResultViewModel',

        'Unidata.view.steward.dataviewer.DataViewerLoader'
    ],

    alias: 'widget.workflow.task.tasksearch.resultview',

    viewModel: {
        type: 'resultview'
    },

    controller: 'resultview',

    layout: 'fit',

    referenceHolder: true,

    eventBusHolder: true,
    bubbleBusEvents: [
        'workflowtask_complete',
        'workflowtask_reload'
    ],

    cls: 'un-tasksearch-resultview',

    closable: true,

    title: Unidata.i18n.t('workflow>task'),

    bind: {
        title: '{task.taskId}'
    },

    config: {
        taskId: null
    },

    updateTaskId: function (taskId) {
        var viewModel, currentTask;

        if (taskId) {
            viewModel = this.getViewModel();
            currentTask = viewModel.get('task');

            if (currentTask && currentTask.get('taskId') == taskId) {
                return;
            }

            Unidata.model.workflow.Task.load(taskId).then(function (task) {
                viewModel.set('task', task);
            });
        }
    }
});
