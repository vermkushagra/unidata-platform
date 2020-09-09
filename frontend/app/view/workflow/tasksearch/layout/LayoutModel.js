/**
 * @author Aleksandr Bavin
 * @date 06.06.2016
 */
Ext.define('Unidata.view.workflow.tasksearch.layout.LayoutModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.layout',

    data: {
        isPagingHidden: true // видимость пагинации для taskSearchHitStore
    },

    stores: {
        userStore: {
            autoLoad: true,
            model: 'Unidata.model.user.User',
            sorters: [
                {
                    property: 'fullName',
                    direction: 'ASC'
                }
            ]
        },
        taskSearchHitStore: {
            model: 'Unidata.model.workflow.Task',
            proxy: 'workflow.task.search',
            pageSize: 10
        }
    }

});
