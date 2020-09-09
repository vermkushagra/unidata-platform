/**
 * @author Aleksandr Bavin
 * @date 2016-08-18
 */
Ext.define('Unidata.view.workflow.tasksearch.resultview.ResultViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.resultview',

    init: function () {
        var view = this.getView();

        this.getViewModel().bind('{task}', this.initTask, this);

        view.addComponentListener('workflowtask_close', this.onWorkflowTaskClose, this);
        view.addComponentListener('workflowtask_reload', this.onWorkflowTaskReload, this);
    },

    onWorkflowTaskClose: function () {
        Ext.defer(function () {
            this.getView().destroy();
        }, 1, this);
    },

    onWorkflowTaskReload: function (task) {
        if (this.taskItemContainerInited) {
            this.initSubjectItem(task.getVariables());
        }
    },

    initTaskItemContainer: function () {
        this.taskItemContainerInited = true;
        this.initSubjectItem(this.getViewModel().get('task').getVariables());
    },

    /**
     * Инициализация задачи и всего, что с ней связано
     * @param {Unidata.model.workflow.Task} task
     */
    initTask: function (task) {
        var view = this.getView();

        if (!task) {
            return;
        }

        view.setTaskId(task.get('taskId'));

        view.add({
            xtype: 'tabpanel',
            ui: 'un-underlined',
            reference: 'itemsContainer',
            cls: 'un-panel-body-transparent',
            layout: 'fit',
            items: [
                {
                    xtype: 'workflow.task.workflowtask',
                    reference: 'workflowtask',
                    expandTask: true, // если необходимо раскрывать только для задач view.getIsTask()
                    expandProcess: view.getIsProcess(),
                    viewModel: {
                        data: {
                            task: task
                        }
                    }
                },
                {
                    xtype: 'panel',
                    title: Unidata.i18n.t('glossary:record'),
                    layout: 'fit',
                    reference: 'taskItemContainer',
                    listeners: {
                        show: {
                            fn: 'initTaskItemContainer',
                            single: true
                        }
                    }
                }
            ]
        });
    },

    /**
     * Инициализация отображения того, над чем мы производим манипуляции
     * @param {Unidata.model.workflow.TaskVariables} taskVariables
     */
    initSubjectItem: function (taskVariables) {
        var taskItemContainer = this.lookupReference('taskItemContainer'),
            viewer,
            promiseMetaRecord;

        taskItemContainer.removeAll();
        taskItemContainer.setLoading(true);

        promiseMetaRecord = Unidata.util.api.MetaRecord.getMetaRecord({
            entityName: taskVariables.get('entityName'),
            entityType: taskVariables.get('entityType')
        });

        Ext.Deferred.all([promiseMetaRecord])
            .then(function (results) {
                var loadCfg;

                loadCfg = {
                    drafts: true,
                    diffToDraft: true,
                    etalonId: taskVariables.get('etalonId'),
                    metaRecord: results[0],
                    timeIntervalStore: Unidata.util.api.TimeInterval.createStore(),
                    allowDetachOriginOperation: false
                };

                Unidata.util.MetaRecord.hasHiddenAttribute(results[0]);

                Unidata.view.steward.dataviewer.DataViewerLoader.load(loadCfg, {})
                    .then(function (cfg) {
                        viewer = Ext.create('Unidata.view.steward.dataviewer.DataViewer', cfg);

                        viewer.on('render', function () {
                            viewer.showDataViewer();
                        }, this, {single: true});

                        taskItemContainer.add(viewer);

                    }, function () {
                        Unidata.showError(Unidata.i18n.t('workflow>tasksearch.loadError'));
                    })
                    .always(function () {
                        taskItemContainer.add({
                            xtype: 'container',
                            margin: 16,
                            html: Unidata.i18n.t('workflow>tasksearch.noAccessToRecord')
                        });
                        taskItemContainer.setLoading(false);
                    })
                    .done();

            })
            .otherwise(function () {
                Unidata.showError(Unidata.i18n.t('workflow>tasksearch.loadError'));
                taskItemContainer.setLoading(false);
            })
            .done();
    },

    onToggleView: function (segmentedbutton, button) {
        var index = segmentedbutton.items.indexOf(button);

        this.lookupReference('itemsContainer').items.each(function (item, itemIndex) {
            if (index != itemIndex) {
                item.hide();
            } else {
                item.show();
            }
        });
    }

});
