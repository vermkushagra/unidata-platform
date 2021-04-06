/**
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.view.workflow.task.WorkflowTaskController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.workflowtask',

    reloadTask: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            task = viewModel.get('task'),
            taskId = task.get('taskId');

        view.setLoading(true);

        task.load().always(function () {
            view.setLoading(false);
            view.lookupReference('loadableItems').items.each(function (item) {
                if (item instanceof Unidata.view.workflow.task.panels.AbstractPanel) {
                    item.updateProcessId(item.getProcessId());
                }
            });
            view.initTaskActions(task.actions());
            view.fireComponentEvent('workflowtask_reload', task);
        }).done();
    },

    onAssignButtonClick: function (button) {
        var me = this,
            view = this.getView(),
            task = this.getViewModel().get('task');

        Ext.MessageBox.show({
            title: Unidata.i18n.t('workflow>takeTask'),
            msg: Unidata.i18n.t('workflow>confirmTakeTask'),
            buttons: Ext.MessageBox.YESNO,
            animateTarget: button,
            scope: this,
            fn: function (btn) {
                if (btn == 'yes') {
                    view.setLoading(true);
                    task.assign().then(
                        function () {
                            me.reloadTask();
                        },
                        function () {
                            view.setLoading(false);
                        }
                    );
                }
            }
        });
    },

    onUnassignButtonClick: function (button) {
        var me = this,
            view = this.getView(),
            task = this.getViewModel().get('task');

        Ext.MessageBox.show({
            title: Unidata.i18n.t('workflow>toCommonTasks'),
            msg: Unidata.i18n.t('workflow>confirmToCommonTasks'),
            buttons: Ext.MessageBox.YESNO,
            animateTarget: button,
            scope: this,
            fn: function (btn) {
                if (btn == 'yes') {
                    view.setLoading(true);
                    task.unassign().then(
                        function () {
                            me.reloadTask();
                        },
                        function () {
                            view.setLoading(false);
                        }
                    );
                }
            }
        });
    },

    onTaskActionClick: function (menuItem) {
        /** @type {Unidata.model.workflow.TaskAction} */
        var action = menuItem.action;

        Ext.MessageBox.show({
            title: action.get('name'),
            msg: action.get('description'),
            buttons: Ext.MessageBox.YESNO,
            animateTarget: menuItem,
            scope: this,
            fn: function (btn) {
                var task, view;

                if (btn == 'yes') {
                    view = this.getView();
                    task = this.getViewModel().get('task');

                    task.complete(action.get('code')).then(function () {
                        view.fireComponentEvent('workflowtask_complete', task);
                        view.fireComponentEvent('workflowtask_close', task);
                    }).done();
                }
            }
        });
    }

});
