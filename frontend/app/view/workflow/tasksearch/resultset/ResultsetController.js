/**
 * @author Aleksandr Bavin
 * @date 06.06.2016
 */
Ext.define('Unidata.view.workflow.tasksearch.resultset.ResultsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.resultset',

    init: function () {
        this.initEvents();
        this.initRouter();
    },

    initRouter: function () {
        Unidata.util.Router.on('tasksSearch', this.updateSearchTypeByToken, this);
        this.updateSearchTypeByToken();
    },

    updateSearchTypeByToken: function () {
        var view = this.getView(),
            searchType = Unidata.util.Router.getTokenValue('tasksSearch', 'searchType');

        view.setSearchType(searchType);
    },

    initEvents: function () {
        var searchResultGrid = this.lookupReference('searchResultGrid'),
            searchStore = this.getTaskSearchHitStore(),
            userStore = this.getUserStore();

        if (!userStore.isLoaded()) {
            // при загрузке стора с пользователями,
            // показываем индикатор загрузки на гриде с результатами
            userStore.on({
                beforeload: function () {
                    searchResultGrid.setLoading(true);
                },
                load: function () {
                    searchResultGrid.setLoading(false);
                }
            });
        }

        searchStore.on('load', this.updatePaging, this);

        Unidata.event.manager.Approve.on({
            approvesuccess: this.reloadResults,
            approvefailure: this.reloadResults,
            declinesuccess: this.reloadResults,
            declinefailure: this.reloadResults,
            scope: this
        });
    },

    onSearchSwitcherChange: function (combobox, newValue, oldValue) {
        var viewModel = this.getViewModel();

        // сбрасываем параметры перед переключением свитчера
        this.changeSearchTypeCount(Unidata.view.workflow.tasksearch.query.Query.SEARCH_HISTORICAL, 0);
        this.changeSearchTypeCount(Unidata.view.workflow.tasksearch.query.Query.SEARCH_COMPLEX, 0);
        viewModel.set('isPagingHidden', true);

        this.getView().fireComponentEvent('searchswitcherchange', newValue, oldValue);
    },

    updatePaging: function (store) {
        this.getViewModel().set('isPagingHidden', store.getTotalCount() <= store.getPageSize());
        this.changeSearchTypeCount(null, store.getTotalCount());
    },

    /**
     * @returns {Ext.data.Store}
     */
    getTaskSearchHitStore: function () {
        return this.getStore('taskSearchHitStore');
    },

    getUserStore: function () {
        return this.getStore('userStore');
    },

    onRefreshButtonClick: function () {
        this.reloadResults();
    },

    reloadResults: function () {
        this.getTaskSearchHitStore().reload();
    },

    /**
     * Шаблон рендера результата поисковой выдачи для задач
     *
     * @returns {Ext.Template}
     */
    getTaskResultItemTemplate: function () {
        var format = Unidata.Config.getDateTimeFormat(),
            tpl;

        tpl = new Ext.XTemplate(
            '<ul>',
            '<tpl if="taskTitle">',
            '<li><span class="un-result-grid-item-header">{taskTitle}</span></li>',
            '</tpl>',
            '<tpl if="taskId">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.taskId') + ': </span><span class="un-result-grid-item-data">{taskId}</span>',
            '<tpl if="recordState">',
            '<span class="un-result-grid-item-tag">{recordState}</span>',
            '</tpl>',
            '</tpl>',
            '</li>',
            '<tpl if="recordTitle">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.recordName') + ': </span><span class="un-result-grid-item-data">{recordTitle}</span></li>',
            '</tpl>',
            '<tpl if="entityTypeTitle">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('glossary:entity') + ': </span><span class="un-result-grid-item-data">{entityTypeTitle}</span></li>',
            '</tpl>',
            '<tpl if="createDate">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.createDate') + ': </span><span class="un-result-grid-item-data">{createDate:date("' + format + '")}</span></li>',
            '</tpl>',
            '<tpl if="finished">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.finishDate') + ': </span><span class="un-result-grid-item-data">{finishedDate:date("' + format + '")}</span></li>',
            '</tpl>',
            '</ul>',
            {
                compiled: true
            }
        );

        return tpl;
    },

    /**
     * Шаблон рендера результата поисковой выдачи для процессов
     *
     * @returns {Ext.Template}
     */
    getProcessResultItemTemplate: function () {
        var format = Unidata.Config.getDateTimeFormat(),
            tpl;

        tpl = new Ext.XTemplate(
            '<ul>',
            '<tpl if="processTitle">',
            '<li><span class="un-result-grid-item-header">{processTitle}</span></li>',
            '</tpl>',
            '<tpl if="processId">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.processId') + ': </span><span class="un-result-grid-item-data">{processId}</span></li>',
            '</tpl>',
            '<tpl if="processTypeName">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>processTypeName') + ': </span><span class="un-result-grid-item-data">{processTypeName}</span></li>',
            '</tpl>',
            '<tpl if="recordTitle">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.recordName') + ': </span><span class="un-result-grid-item-data">{recordTitle}</span></li>',
            '</tpl>',
            '<tpl if="entityTypeTitle">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('glossary:entity') + ': </span><span class="un-result-grid-item-data">{entityTypeTitle}</span></li>',
            '</tpl>',
            '<tpl if="createDate">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.createDate') + ': </span><span class="un-result-grid-item-data">{createDate:date("' + format + '")}</span></li>',
            '</tpl>',
            '<tpl if="finished">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>tasksearch.finishDate') + ': </span><span class="un-result-grid-item-data">{finishedDate:date("' + format + '")}</span></li>',
            '</tpl>',

            '<tpl if="processFinished">',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>processState') + ': </span><span class="un-result-grid-item-data">' + Unidata.i18n.t('glossary:completed', {context: 'male'}) + '</span></li>',

            '<tpl else>',
            '<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('workflow>processState') + ': </span><span class="un-result-grid-item-data">' + Unidata.i18n.t('glossary:notCompleted', {context: 'male'}) + '</span></li>',
            '</tpl>',
            '</ul>',
            {
                compiled: true
            }
        );

        return tpl;
    },

    /**
     * Рендер колонки грида
     * @param value
     * @param metadata
     * @param {Unidata.model.workflow.Task} task
     * @returns {String}
     */
    columnRenderer: function (value, metadata, task) {
        var view = this.getView(),
            variablesData = task.getVariables().getData(),
            data = task.getData(),
            userExit = Unidata.uiuserexit.overridable.workflow.tasksearch.Resultset,
            tpl;

        data = Ext.Object.merge(data, variablesData);

        if (data['recordState']) {
            data['recordState'] = Unidata.i18n.t('workflow>tasksearch.task.' + data['recordState'].toLowerCase());
        }

        if (view.getResultType() === 'TASK') {
            tpl = this.getTaskResultItemTemplate();
        } else {
            tpl = this.getProcessResultItemTemplate();
        }

        data = userExit.modifyTplData(data, view.getResultType());

        return tpl.apply(data);
    },

    getUserFullName: function (login) {
        var userStore = this.getUserStore(),
            userRecord = userStore.findRecord('login', login);

        if (userRecord) {
            return userRecord.get('fullName');
        }

        return '';
    },

    /**
     * При клике на результат
     * @param {Unidata.model.workflow.Task} record
     */
    onRowClick: function (table, record) {
        this.getView().fireEvent('opentask', record);
    },

    onMyTaskCountChange: function (values, oldValues) {
        var view = this.getView(),
            Query = Unidata.view.workflow.tasksearch.query.Query,
            viewEl = view.getEl();

        if (viewEl && viewEl.isVisible(true)) {
            // обновляем список только если экран и список открыты[
            this.changeSearchTypeCount(Query.SEARCH_MY, values.total_user_count, oldValues.total_user_count);
        }
    },

    onAvailableTaskCountChange: function (values, oldValues) {
        var view = this.getView(),
            Query = Unidata.view.workflow.tasksearch.query.Query,
            viewEl = view.getEl();

        if (viewEl && viewEl.isVisible(true)) {
            // обновляем список только если экран и список открытых
            this.changeSearchTypeCount(Query.SEARCH_AVAILABLE, values.available_count, oldValues.available_count);
        }
    },

    /**
     * Обновить счетчик для соответствующего типа фильтра
     * @param searchType Тип счетчика, определенный здесь Unidata.view.workflow.tasksearch.query.Query.statics
     * @param count Значение счетчика
     * @param oldCount Старое значение счетчика
     */
    changeSearchTypeCount: function (searchType, count, oldCount) {
        var Query = Unidata.view.workflow.tasksearch.query.Query,
            comboBox = this.lookupReference('searchTypeComboBox'),
            store = comboBox.getStore(),
            selection,
            index,
            found,
            searchTypeName,
            currentSearchType;

        selection = comboBox.getSelection();

        if (selection) {
            currentSearchType = selection.get('searchType');
        }

        searchType = Ext.isNumber(searchType) ? searchType : currentSearchType;

        searchTypeName = Ext.String.format('{0} ({1})', Query.defaultSearchTypeNames[searchType], count);

        // обновить счетчик в выпадающем списке
        index = store.findExact('searchType', searchType);

        if (index > -1) {
            found = store.getAt(index);
        }

        if (found) {
            found.set('searchTypeName', searchTypeName);
        }

        // обновить экран если выбран соотв. пункт выпадающего списка
        if (currentSearchType === searchType && Ext.isNumber(oldCount)) {
            this.reloadResults();
        }
    },

    initCounter: function () {
        var Query = Unidata.view.workflow.tasksearch.query.Query,
            poller;

        poller = Unidata.module.poller.TaskCountPoller.getInstance();

        // инициализируем счетчики при входе в экран
        if (Ext.isNumber(poller.myTaskCount)) {
            this.changeSearchTypeCount(Query.SEARCH_MY, poller.myTaskCount, null);
        }

        if (Ext.isNumber(poller.availableTaskCount)) {
            this.changeSearchTypeCount(Query.SEARCH_AVAILABLE, poller.availableTaskCount, null);
        }
    },

    onComboBoxRender: function () {
        this.initCounter();
    }
});
