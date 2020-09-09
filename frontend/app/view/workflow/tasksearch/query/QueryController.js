/**
 * @author Aleksandr Bavin
 * @date 2016-06-29
 */
Ext.define('Unidata.view.workflow.tasksearch.query.QueryController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.query',

    prevTaskSearchHistorical: true,
    prevTaskSearchComplex: true,

    init: function () {
        this.callParent(arguments);
        this.initRouter();
    },

    initRouter: function () {
        Unidata.util.Router.on('tasksSearch', this.updateFormValuesByTokenOnRender, this);
        this.updateFormValuesByTokenOnRender();
    },

    updateFormValuesByTokenOnRender: function () {
        var view = this.getView();

        if (view.rendered) {
            this.updateFormValuesByToken();
        } else {
            view.on('render', this.updateFormValuesByToken, this);
        }
    },

    updateFormValuesByToken: function () {
        var view = this.getView(),
            form = view.lookupReference('taskForm').getForm(),
            formFields = form.getFields(),
            tasksSearch = Unidata.util.Router.getTokenValues('tasksSearch');

        if (!tasksSearch) {
            return;
        }

        Ext.Object.each(tasksSearch, function (key, value) {
            var field = formFields.findBy(function (field) {
                return field.getName() === key;
            });

            if (field && field.setValue) {
                field.setValue(value);
            }
        }, this);

        setTimeout(function () {
            Unidata.util.Router.removeToken('tasksSearch');
        }, 100);
    },

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

    onSearchSwitcherChange: function (newValue, oldValue) {
        var searchConst = Unidata.view.workflow.tasksearch.query.Query,
            view = this.getView(),
            viewModel = this.getViewModel(),
            searchMy = (newValue == searchConst.SEARCH_MY),
            searchAvailable = (newValue == searchConst.SEARCH_AVAILABLE),
            searchHistorical = (newValue == searchConst.SEARCH_HISTORICAL),
            searchComplex = (newValue == searchConst.SEARCH_COMPLEX),
            taskSearchHitStore;

        viewModel.set('searchMy', searchMy);
        viewModel.set('searchAvailable', searchAvailable);
        viewModel.set('searchHistorical', searchHistorical);
        viewModel.set('searchComplex', searchComplex || searchHistorical);

        viewModel.notify();

        if (oldValue === searchConst.SEARCH_HISTORICAL) {
            this.prevTaskSearchHistorical = this.isTaskSearch();
            view.radioTypeSearchTask.setValue(true);
        } else if (oldValue === searchConst.SEARCH_COMPLEX) {
            this.prevTaskSearchComplex = this.isTaskSearch();
            view.radioTypeSearchTask.setValue(true);
        }

        if (newValue === searchConst.SEARCH_HISTORICAL) {
            if (this.prevTaskSearchHistorical) {
                view.radioTypeSearchTask.setValue(true);
            } else {
                view.radioTypeSearchProcess.setValue(true);
            }
        } else if (newValue === searchConst.SEARCH_COMPLEX) {
            if (this.prevTaskSearchComplex) {
                view.radioTypeSearchTask.setValue(true);
            } else {
                view.radioTypeSearchProcess.setValue(true);
            }
        }

        if (searchComplex || searchHistorical) {
            taskSearchHitStore = viewModel.get('taskSearchHitStore');

            if (taskSearchHitStore) {
                taskSearchHitStore.removeAll();
            }

            view.expand();
        } else {
            view.collapse();
        }

        this.search();
    },

    /**
     * При клике на поиск
     */
    onSearchClick: function () {
        this.search();
    },

    search: function () {
        clearTimeout(this.searchTimer);
        this.searchTimer = Ext.defer(this.searchDelayed, 150, this);
    },

    searchDelayed: function () {
        var me = this,
            viewModel = this.getViewModel(),
            binding;

        binding = viewModel.bind('{userStore}', function (userStore) {
            if (!userStore) {
                return;
            }

            binding.destroy();

            if (!userStore.isLoaded()) {
                userStore.on({
                    load: function () {
                        me.searchForced();
                    }
                });
            } else {
                me.searchForced();
            }
        });
    },

    /**
     * собираем данные из формы и загружаем стор
     */
    searchForced: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.get('taskSearchHitStore'),
            cfg,
            poller;

        cfg = {
            variablesList: view.variablesList,
            values: this.getSearchFormValues(),
            store: store
        };

        // при открытии экрана "Задачи" принудительно отправляем запрос на task counts
        poller = Unidata.module.poller.TaskCountPoller.getInstance();
        poller.pollRequest();

        Unidata.util.api.Task.getTasks(cfg);
    },

    /**
     *
     * @returns {*}
     */
    getSearchFormValues: function () {
        var view = this.getView(),
            values = null;

        if (this.isTaskSearch()) {
            values = view.taskForm.getValues();
        } else if (this.isProcessSearch()) {
            values = view.processForm.getValues();
        }

        return values;
    },

    /**
     * Обработчик события смены типа поиска
     */
    onSearchOperationTypeChange: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.get('taskSearchHitStore');

        view.taskForm.hide();
        view.processForm.hide();

        if (this.isTaskSearch()) {
            view.taskForm.show();
            store.setProxy(Ext.create('Unidata.proxy.workflow.TaskSearchProxy'));
        } else if (this.isProcessSearch()) {
            view.processForm.show();
            store.setProxy(Ext.create('Unidata.proxy.workflow.ProcessSearchProxy'));
        }

        view.fireEvent('operationtypechange', view, this.getSelectedSearchOperationType());
    },

    getSelectedSearchOperationType: function () {
        var view = this.getView(),
            searchOperationType = null;

        if (view.radioTypeSearchTask.getValue()) {
            searchOperationType = view.radioTypeSearchTask.inputValue;
        } else if (view.radioTypeSearchProcess.getValue()) {
            searchOperationType = view.radioTypeSearchProcess.inputValue;
        }

        return searchOperationType;
    },

    isTaskSearch: function () {
        var searchOperationType = this.getSelectedSearchOperationType();

        return searchOperationType === Unidata.view.workflow.tasksearch.query.Query.searchOperationTypes.TASK;
    },

    isProcessSearch: function () {
        var searchOperationType = this.getSelectedSearchOperationType();

        return searchOperationType === Unidata.view.workflow.tasksearch.query.Query.searchOperationTypes.PROCESS;
    }
});
