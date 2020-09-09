/**
 * Панель сводной информации о задачах (controller)
 *
 * @author Sergey Shishigin
 * @date 2017-08-04
 */
Ext.define('Unidata.view.component.dashboard.task.TaskController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.task',

    onMyTaskCountChange: function (values) {
        var view = this.getView();

        view.myTaskCounter.setValue(values.total_user_count);
    },

    onAvailableTaskCountChange: function (values) {
        var view = this.getView();

        view.availableTaskCounter.setValue(values.available_count);
    },

    onMyTaskCountClick: function () {
        this.redirectToTasks(0);
    },

    onAvailableTaskCountClick: function () {
        this.redirectToTasks(1);
    },

    /**
     * @param {number} searchType
     */
    redirectToTasks: function (searchType) {
        var hash;

        hash = Unidata.util.Router.buildHash([
            {
                name: 'main',
                values: {
                    section: 'tasks'
                }
            },
            {
                name: 'tasksSearch',
                values: {
                    searchType: searchType
                }
            }
        ]);

        Unidata.util.Router.redirectTo(hash);
    }
});
