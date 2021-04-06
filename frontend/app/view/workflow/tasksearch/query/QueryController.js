/**
 * @author Aleksandr Bavin
 * @date 2016-06-29
 */
Ext.define('Unidata.view.workflow.tasksearch.query.QueryController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.query',

    initViewModel: function () {
        var view = this.getView();

        this.callParent(arguments);

        Unidata.module.MainViewManager.addViewChangeListener('tasks', this.onTaskViewShow);
        view.addComponentListener('workflowtask_complete', this.search, this);
        view.addComponentListener('workflowtask_reload', this.search, this);
        view.addComponentListener('searchswitcherchange', this.onSearchSwitcherChange, this);
    },

    onTaskViewShow: function (component) {
        component.query.search();
    },

    onSearchSwitcherChange: function (newValue) {
        var searchConst = Unidata.view.workflow.tasksearch.query.Query,
            view = this.getView(),
            viewModel = this.getViewModel(),
            searchMy = (newValue == searchConst.SEARCH_MY),
            searchAvailable = (newValue == searchConst.SEARCH_AVAILABLE),
            searchHistorical = (newValue == searchConst.SEARCH_HISTORICAL),
            searchComplex = (newValue == searchConst.SEARCH_COMPLEX);

        viewModel.set('searchMy', searchMy);
        viewModel.set('searchAvailable', searchAvailable);
        viewModel.set('searchHistorical', searchHistorical);
        viewModel.set('searchComplex', searchComplex || searchHistorical);

        viewModel.notify();

        if (searchComplex || searchHistorical) {
            viewModel.get('taskSearchHitStore').removeAll();
            view.expand();
        } else {
            this.search();
            view.collapse();
        }
    },

    /**
     * При клике на поиск
     */
    onSearchClick: function () {
        this.search();
    },

    search: function () {
        var me = this,
            userStore = this.getViewModel().get('userStore');

        if (!userStore.isLoaded()) {
            userStore.on({
                load: function () {
                    me.searchForced();
                }
            });
        } else {
            me.searchForced();
        }
    },

    /**
     * собираем данные из формы и загружаем стор
     */
    searchForced: function () {
        var TaskApi = Unidata.util.api.Task,
            view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.get('taskSearchHitStore'),
            cfg,
            poller;

        cfg = {
            variablesList: view.variablesList,
            values: view.getValues(),
            store: store
        };

        // при открытии экрана "Задачи" принудительно отправляем запрос на task counts
        poller = Unidata.module.poller.TaskCountPoller.getInstance();
        poller.pollRequest();

        TaskApi.getTasks(cfg);
    }

});
