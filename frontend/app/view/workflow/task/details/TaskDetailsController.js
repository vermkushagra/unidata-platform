/**
 * @author Ivan Marshalkin
 * @date 2018-05-26
 */

Ext.define('Unidata.view.workflow.task.details.TaskDetailsController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.taskdetails',

    onPanelExpand: function () {
        this.loadTask();
    },

    loadTask: function () {
        var view = this.getView();

        Unidata.model.workflow.Task.load(view.getTaskId()).then(function (task) {
            view.setTask(task);
        });
    }
});
