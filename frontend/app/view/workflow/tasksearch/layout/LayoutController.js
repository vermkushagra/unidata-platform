/**
 * @author Aleksandr Bavin
 * @date 06.06.2016
 */
Ext.define('Unidata.view.workflow.tasksearch.layout.LayoutController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.layout',

    taskTokenValues: null,

    init: function () {
        this.initRouter();
    },

    initRouter: function () {
        var tokenValues = Unidata.util.Router.getTokenValues('taskId');

        this.routeToTask(tokenValues);

        Unidata.util.Router.on('task', this.routeToTask, this);
        Unidata.util.Router.on('main', this.onMainTokenChange, this);
    },

    onMainTokenChange: function (tokenValues, oldTokenValues) {
        if (tokenValues.section === oldTokenValues.section) {
            return;
        }

        if (oldTokenValues.section && oldTokenValues.section === 'tasks') {
            this.removeRouterTokens();
        } else if (tokenValues.section && tokenValues.section === 'tasks') {
            if (tokenValues.reset) {
                this.taskTokenValues = null;
                Unidata.util.Router.removeTokenValue('main', 'reset');
            } else {
                this.restoreRouterTokens();
            }
        }
    },

    removeRouterTokens: function () {
        this.taskTokenValues = Unidata.util.Router.getTokenValues('task');
        Unidata.util.Router.removeToken('task');
    },

    restoreRouterTokens: function () {
        if (Ext.isObject(this.taskTokenValues) && !Ext.Object.isEmpty(this.taskTokenValues)) {
            Unidata.util.Router.setToken('task', this.taskTokenValues);
        }
    },

    routeToTask: function (tokenValues) {
        if (tokenValues && tokenValues.taskId) {
            this.openTaskById(tokenValues.taskId);
        }
    },

    /**
     * При переключении таба
     */
    onTabchange: function (tabPanel, newCard) {
        var task, taskId;

        if (newCard instanceof Unidata.view.workflow.tasksearch.resultview.ResultView) {
            task = newCard.getViewModel().get('task');

            if (task) {
                taskId = task.get('taskId');
            } else {
                taskId = newCard.getTaskId();
            }

            Unidata.util.Router.setTokenValue('task', 'taskId', taskId);
        } else {
            Unidata.util.Router.removeToken('task');
        }
    },

    /**
     * При удалении таба
     */
    onTabremove: function (tabpanel) {
        if (!tabpanel.items.getCount()) {
            Unidata.util.Router.removeToken('task');
        }
    },

    /**
     * Открывает задачу используя модель
     * @param {Unidata.model.workflow.Task} task
     */
    openTask: function (task) {
        var tasks = this.lookupReference('tasks'),
            taskId = task.get('taskId'),
            tab = this.findTabByTaskId(taskId);

        if (!tab) {
            tab = Ext.widget({
                xtype: 'workflow.task.tasksearch.resultview',
                viewModel: {
                    data: {
                        task: task
                    }
                }
            });

            tasks.add(tab);
        }

        tasks.setActiveTab(tab);
    },

    /**
     * Открывает задачу по id
     * @param taskId
     */
    openTaskById: function (taskId) {
        var tasks = this.lookupReference('tasks'),
            tab = this.findTabByTaskId(taskId);

        if (!tab) {
            tab = Ext.widget({
                xtype: 'workflow.task.tasksearch.resultview',
                taskId: taskId
            });

            tasks.add(tab);
        }

        tasks.setActiveTab(tab);
    },

    /**
     * При клике на результат - показываем запись
     * @param {Unidata.model.workflow.Task} task
     */
    onOpenTask: function (task) {
        this.openTask(task);
    },

    findTabByTaskId: function (taskId) {
        var tasks = this.lookupReference('tasks'),
            tab;

        tasks.items.each(function (item) {
            var task = item.getViewModel().get('task');

            if (!task) {
                return true;
            }

            if (task.get('taskId') == taskId) {
                tab = item;

                return false;
            }
        });

        return tab;
    }

});
