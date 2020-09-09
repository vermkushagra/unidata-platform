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
            task = viewModel.get('task');

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
        var action = menuItem.action,
            msg;

        msg = Ext.String.format(
            '{0}<br/><br/>{1}',
            action.get('description'),
            Unidata.i18n.t('workflow>addComment') + ':'
        );

        Ext.MessageBox.show({
            prompt: true,
            multiline: true,
            width: 300,
            title: action.get('name'),
            msg: msg,
            buttons: Ext.MessageBox.YESNO,
            animateTarget: menuItem,
            scope: this,
            fn: function (btn, text) {
                var task, view, comment;

                if (btn === 'yes') {
                    view = this.getView();
                    task = this.getViewModel().get('task');

                    if (!Ext.isEmpty(Ext.String.trim(text))) {
                        comment = Ext.create('Unidata.model.workflow.Comment', {
                            processInstanceId: task.get('processId'),
                            message: text
                        });

                        comment.save();
                    }

                    task.complete(action.get('code')).then(function () {
                        view.fireComponentEvent('workflowtask_complete', task);
                        view.fireComponentEvent('workflowtask_close', task);
                    }).done();
                }
            }
        });
    },

    onHistoryPanelReload: function (records) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            activeTasks = [],
            multiple;

        Ext.Array.each(records, function (record) {
            if (Ext.isEmpty(record.get('endTime'))) {
                activeTasks.push(record);
            }
        });

        activeTasks = activeTasks.reverse();
        multiple = activeTasks.length > 1;

        view.tasksDetailPanel.removeAll();

        Ext.Array.each(activeTasks, function (task) {
            var panel;

            panel = Ext.create('Unidata.view.workflow.task.details.TaskDetails', {
                task: null,
                taskHistory: task,
                taskId: task.get('id'),
                collapsible: multiple ? true : false,
                collapsed: multiple ? true : false,
                animCollapse: false,
                titleCollapse: true
            });

            view.tasksDetailPanel.add(panel);
            panel.loadTask();
        });

        viewModel.set('activeTaskCount', activeTasks.length);
    },

    showProcessMap: function (button) {
        var viewModel = this.getViewModel(),
            task = viewModel.get('task'),
            src;

        src = Ext.String.format(
            '{0}internal/data/workflow/diagram/{1}?token={2}&finished={3}',
            Unidata.Config.getMainUrl(),
            task.get('processId'),
            Unidata.Config.getToken(),
            task.get('processFinished')
        );

        Ext.create('Ext.window.Window', {
            title: Unidata.i18n.t('workflow>processState'),
            qaId: 'process-map-window', // для QA отдела. Используются в автотестах
            draggable: false,
            resizable: false,
            modal: true,
            minWidth: 300,
            minHeight: 200,
            maxHeight: window.innerHeight - 50,
            maxWidth: window.innerWidth - 50,
            loading: true,
            layout: 'fit',
            animateTarget: button,
            alwaysCentered: true,
            listeners: {
                show: function () {
                    var wnd = this,
                        imageContainer;

                    imageContainer = wnd.add({
                        xtype: 'container',
                        flex: 1,
                        scrollable: true,
                        layout: {
                            type: 'hbox',
                            align: 'stretchmax'
                        },
                        items: {
                            xtype: 'image',
                            src: src,
                            listeners: {
                                load: {
                                    element: 'el',
                                    fn: function () {
                                        wnd.updateLayout();

                                        wnd.center();
                                        imageContainer.setLoading(false);
                                    }
                                }
                            }
                        }
                    });

                    imageContainer.setLoading(true);
                }
            }
        }).show();

    }

});
