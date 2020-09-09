/**
 * @author Aleksandr Bavin
 * @date 2016-08-18
 */
Ext.define('Unidata.view.workflow.tasksearch.resultview.ResultViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.resultview',

    data: {
        /** @type {Unidata.model.workflow.Task} */
        task: null
    },

    formulas: {

        /**
         * Заголовок вкладки
         */
        taskTabTitle: {
            bind: {
                task: '{task}'
            },
            get:  function (getter) {
                var view = this.getView(),
                    task = getter.task,
                    title = '',
                    variables;

                if (task) {
                    variables = task.getVariables();

                    title = [];

                    if (view.getIsTask()) {
                        title.push(task.get('taskId'));
                    } else if (view.getIsProcess()) {
                        title.push(task.get('processId'));
                    }

                    title.push(variables.get('recordTitle'));

                    title = title.join(' | ');
                    // 26 длина текста + 3 точки
                    title = Ext.String.ellipsis(title, 26 + 3);

                    if (view.getIsProcess()) {
                        title =  Unidata.util.Icon.getLinearIcon('site-map') + ' ' + title;
                    }
                }

                return title;
            }
        }
    }

});
