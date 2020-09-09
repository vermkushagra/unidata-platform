Ext.define('Unidata.view.steward.search.searchpanel.SearchPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.search.searchpanel',

    relayers: null,

    getSelectedSearchHits: function () {
        var view = this.getView(),
            resultsetPanel = view.resultsetPanel;

        return resultsetPanel.getSelectedSearchHits();
    },

    initRelayers: function () {
        var view = this.getView(),
            resultsetPanel = view.resultsetPanel;

        this.relayers = view.relayEvents(resultsetPanel, ['itemclick', 'selectionchange'], 'resultset');
    },

    onDestroy: function () {
        Ext.destroy(this.relayers);

        if (this.bulkWizard) {
            this.bulkWizard.destroy();
            this.bulkWizardAdded = false;
        }

        // здень не нужно вызывать this.callParent(arguments); т.к. его нет
    },

    onSelectionChange: function (selectionModel, useQueryCount) {
        var view = this.getView(),
            recordshowTabPanel = view.recordshowTabPanel,
            bulkWizard = this.getBulkWizard(),
            etalonIds = [];

        bulkWizard.setUseQueryCount(useQueryCount);

        selectionModel.getSelected().each(function (selected) {
            etalonIds.push(selected.get('etalonId'));
        });

        etalonIds = Ext.Array.unique(etalonIds);

        bulkWizard.setSelectedIds(etalonIds);

        if (selectionModel.getCount() || useQueryCount) {
            if (!this.bulkWizardAdded) {
                recordshowTabPanel.add(this.bulkWizard);
                this.bulkWizardAdded = true;
            }
            recordshowTabPanel.setActiveTab(this.bulkWizard);
        } else {
            recordshowTabPanel.remove(this.bulkWizard, false);
            this.bulkWizardAdded = false;
        }
    },

    onRecordChanged: function () {
        var view = this.getView(),
            queryPanel = view.queryPanel;

        queryPanel.doSearch();
    },

    getBulkWizard: function () {
        if (this.bulkWizard === undefined) {
            this.bulkWizard = Ext.create({
                title: Unidata.i18n.t('search>wizard.operations'),
                tabConfig: {
                    cls: 'datarecord-tab-active'
                },
                xtype: 'steward.search.bulk.wizard'
            });
            this.bulkWizard.on('close', this.onBulkWizardClose, this);
        }

        return this.bulkWizard;
    },

    onBulkWizardClose: function () {
        var view           = this.getView(),
            resultsetPanel = view.resultsetPanel;

        resultsetPanel.deselectAll();
    },

    onSearchSuccess: function (searchHits, extraParams) {
        var view = this.getView(),
            bulkWizard = this.getBulkWizard(),
            resultsetPanel = view.resultsetPanel;

        bulkWizard.setQueryParams(extraParams);

        // перенесено из LayoutController.js
        // если выбраны удалённые или не утверждённые - отключаем режим редактирования
        if (extraParams && extraParams['facets']) {
            if (Ext.Array.contains(extraParams['facets'], 'pending_only') ||
                Ext.Array.contains(extraParams['facets'], 'inactive_only')) {
                resultsetPanel.deselectAll();
                resultsetPanel.setEditMode(resultsetPanel.editModeType.DISABLED);
            } else {
                if (this.allPeriodSearch) {
                    resultsetPanel.setEditMode(resultsetPanel.editModeType.DISABLED);
                }
            }
        } else {
            if (this.allPeriodSearch) {
                resultsetPanel.setEditMode(resultsetPanel.editModeType.DISABLED);
            }
        }
    },

    onEntityChange: function (metaRecord) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            resultsetPanel = view.resultsetPanel,
            bulkWizard = this.getBulkWizard(),
            entityName = metaRecord.get('name'),
            tab;

        tab = this.findTableResultSetTab();

        if (tab) {
            tab.reconfigureGridColumnsByMetaRecord(metaRecord);
        }

        resultsetPanel.setMetaRecord(metaRecord);

        bulkWizard.setMetarecord(metaRecord);
        bulkWizard.setExternalData('entityName', entityName);

        viewModel.set('entity', metaRecord);
    },

    onWantAddEtalonId: function (meta, callback) {
        var tabPanel = this.lookupReference('recordshowTabPanel');

        tabPanel.createRecordTab(null, meta, Unidata.i18n.t('search>query.newLinkedRecord'), null, callback);
    },

    /**
     * В данном методе можно добавить tools кнопки для панельки query
     * @returns {Array}
     */
    buildQueryPanelTools: function () {
        var tools = [];

        return tools;
    },

    buildResultsetPanelTools: function () {
        var tools;

        tools = [
            {
                type: 'table',
                tooltip: Unidata.i18n.t('search>tableresultset.resultsInTableForm'),
                handler: this.onShowTableResultSetButtonClick.bind(this)
            }
        ];

        return tools;
    },

    onShowTableResultSetButtonClick: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            metaRecord = viewModel.get('entity'),
            tab;

        if (!metaRecord) {
            return;
        }

        view.queryPanel.setTableSearch(true);
        view.queryPanel.doSearch();

        tab = this.findTableResultSetTab();

        // уже существующую вкладку показываем
        if (tab) {
            view.recordshowTabPanel.setActiveTab(tab);

            return;
        }

        // если вкладки небыло создаем ее
        tab = Ext.create('Unidata.view.steward.search.tableresultset.TableResultset', {
            title: Unidata.i18n.t('search>tableresultset.resultsType'),
            allPeriodSearch: this.allPeriodSearch
        });

        tab.on('itemclick', this.onTableResultSetItemClick, this);

        tab.on('close', this.onCloseTableResultsetTab, this);

        tab.reconfigureGridColumnsByMetaRecord(metaRecord);

        view.recordshowTabPanel.add(tab);
        view.recordshowTabPanel.setActiveTab(tab);

        view.queryPanel.linkResultsetPanel(tab);
    },

    /**
     * Возвращает вкладку отображающую табличный результат поиска
     *
     * @returns {*}
     */
    findTableResultSetTab: function () {
        var view = this.getView(),
            tab = null;

        view.recordshowTabPanel.items.each(function (item) {
            if (item instanceof Unidata.view.steward.search.tableresultset.TableResultset) {
                tab = item;

                return false; // прекращение итерации
            }
        });

        return tab;
    },

    /**
     * Обработчик клики по записи в плиточном результате поиска
     *
     * @param component
     * @param searchHit
     */
    onResultSetItemClick: function (component, searchHit) {
        var queryPanel,
            metaRecord,
            searchTerm,
            searchQuery,
            recordshowTabPanel,
            pendingOnly;

        recordshowTabPanel = this.lookupReference('recordshowTabPanel');
        queryPanel = this.getView().lookupReference('queryPanel');
        metaRecord = queryPanel.getViewModel().get('metarecord');
        searchQuery = queryPanel.getSearchQuery();
        searchTerm = searchQuery.findTerm('facet.pending_only');
        pendingOnly = searchTerm.getTermIsActive();

        recordshowTabPanel.createRecordTabFromRecord(searchHit, metaRecord, pendingOnly);
    },

    /**
     * Обработчик клики по записи в табличном результате поиска
     *
     * @param component
     * @param searchHit
     */
    onTableResultSetItemClick: function (component, searchHit) {
        var queryPanel,
            metaRecord,
            searchTerm,
            searchQuery,
            recordshowTabPanel,
            pendingOnly;

        recordshowTabPanel = this.lookupReference('recordshowTabPanel');
        queryPanel = this.getView().lookupReference('queryPanel');
        metaRecord = queryPanel.getViewModel().get('metarecord');
        searchQuery = queryPanel.getSearchQuery();
        searchTerm = searchQuery.findTerm('facet.pending_only');
        pendingOnly = searchTerm.getTermIsActive();

        recordshowTabPanel.createRecordTabFromRecord(searchHit, metaRecord, pendingOnly);
    },

    /**
     * Обработка закрытия вкладки табличного результата поиска
     */
    onCloseTableResultsetTab: function () {
        var view = this.getView();

        // срасываем флаг т.к. сейчас табличный результат поиска может быть один
        view.queryPanel.setTableSearch(false);
    },

    onFacetsChange: function (queryPanel, facets) {
        var view = this.getView(),
            allPeriodActual = facets.allPeriodActual,
            resultsetPanel = view.resultsetPanel,
            tab;

        this.allPeriodSearch = allPeriodActual;

        tab = this.findTableResultSetTab();

        if (tab) {
            tab.setAllPeriodSearch(allPeriodActual);
        }

        resultsetPanel.setAllPeriodSearch(allPeriodActual);

        resultsetPanel.clearResultset();
        resultsetPanel.refreshPaging();

        if (facets.allPeriodActual || facets.inactiveOnly || facets.pendingOnly || facets.includeInactive) {
            resultsetPanel.setEditMode(resultsetPanel.editModeType.DISABLED);
        } else {
            resultsetPanel.setEditMode(resultsetPanel.editModeType.NONE);
        }
    }
});
