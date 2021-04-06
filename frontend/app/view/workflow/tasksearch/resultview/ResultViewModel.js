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
    }

});
