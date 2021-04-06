Ext.define('Unidata.view.steward.search.recordshow.RecordshowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.steward.search.recordshow',

    duplicateDataRecordsLoadFailureText: Unidata.i18n.t('search>recordshow.loadDuplicateError'),

    etalonTokenValues: null,

    init: function () {
        this.initRouter();
        this.initListeners();
    },

    initRouter: function () {
        var view = this.getView(),
            tokenValues = Unidata.util.Router.getTokenValues('etalon');

        this.routeToEtalonId(tokenValues);

        Unidata.util.Router.on('etalon', this.routeToEtalonId, this);
        Unidata.util.Router.on('main', this.onMainTokenChange, this);

        view.on('destroy', this.removeRouterTokens, this);
        view.on('tabchange', this.onTabChange, this);
    },

    onMainTokenChange: function (tokenValues, oldTokenValues) {
        if (tokenValues.section === oldTokenValues.section) {
            return;
        }

        if (oldTokenValues.section && oldTokenValues.section === 'data') {
            this.removeRouterTokens();
        } else if (tokenValues.section && tokenValues.section === 'data') {
            if (tokenValues.reset) {
                this.etalonTokenValues = null;
            } else {
                this.restoreRouterTokens();
            }
        }
    },

    removeRouterTokens: function () {
        this.etalonTokenValues = Unidata.util.Router.getTokenValues('etalon');
        Unidata.util.Router.removeToken('etalon');
    },

    restoreRouterTokens: function () {
        if (Ext.isObject(this.etalonTokenValues) && !Ext.Object.isEmpty(this.etalonTokenValues)) {
            Unidata.util.Router.setToken('etalon', this.etalonTokenValues);
        }
    },

    routeToEtalonId: function (tokenValues) {
        if (tokenValues && tokenValues.etalonId) {
            this.createRecordTab(
                tokenValues.etalonId,
                undefined,
                undefined,
                undefined,
                undefined,
                tokenValues.operationId
            );
        }
    },

    initListeners: function () {
        var view = this.getView();
    },

    /**
     * При переключении таба, устанавливаем etalonId в роуте
     */
    onTabChange: function (tabPanel, newCard, oldCard) {
        if (newCard instanceof Unidata.view.steward.dataviewerpanel.DataViewerPanel) {
            newCard.on('close', this.onNewCardClose, this, {args: [tabPanel, newCard]});

            // ставим хеш по тихому смотри подробности UN-6142
            Unidata.util.Router.suspendTokenEvents();
            Unidata.util.Router
                .setTokenValue('etalon', 'etalonId', newCard.getEtalonId())
                .setTokenValue('etalon', 'operationId');
            Unidata.util.Router.resumeTokenEvents();
        } else {
            Unidata.util.Router.removeToken('etalon');
        }

        if (oldCard) {
            oldCard.un('close', this.onNewCardClose, this);
        }
    },

    /**
     * При закрытии таба, проверяем, остались ли еще табы
     * Если табов не осталось, то убираем из роута etalonId
     * @param tabPanel
     * @param newCard
     */
    onNewCardClose: function (tabPanel, newCard) {
        var removeToken = true;

        tabPanel.items.each(function (item) {
            if (item == newCard) {
                return true;
            }

            if (item instanceof Unidata.view.steward.dataviewerpanel.DataViewerPanel) {
                removeToken = false;

                return false;
            }
        });

        if (removeToken) {
            Unidata.util.Router.removeToken('etalon');
        }
    },

    /**
     * Создать record tab (если это возможно)
     *
     * @param etalonId
     * @param metaRecord
     * @param title
     * @param sourceRecord
     * @param saveCallback
     * @param operationId
     */
    createRecordTab: function (etalonId, metaRecord, title, sourceRecord, saveCallback, operationId) {
        var view = this.getView(),
            dataViewerPanel,
            dataViewerPanelCfg;

        if (!view.onBeforeCreateDataRecordTab(etalonId)) {
            return;
        }

        title = title || view.tabLoadingTitleText;

        dataViewerPanelCfg = {
            etalonId: etalonId,
            operationId: operationId,
            metaRecord: metaRecord,
            saveCallback: saveCallback,
            sourceRecord: sourceRecord
        };

        dataViewerPanel = Unidata.util.DataViewerPanelFactory.buildDataViewerPanel(dataViewerPanelCfg);
        this.configDataViewerPanelListeners(dataViewerPanel);
        view.add(dataViewerPanel);
        view.setActiveTab(dataViewerPanel);
        dataViewerPanel.loadAndShowDataViewer(dataViewerPanelCfg);
    },

    /**
     * Обработка saveCallback, вызываемого при сохранении dataRecord
     *
     * @param saveCallback {function}
     * @param dataViewerPanel
     * @param dataRecord
     */
    handleSaveCallback: function (saveCallback, dataViewerPanel, dataRecord) {
        var tabPanel = this.getView(),
            currentTab = this.getView().getActiveTab();

        if (saveCallback && Ext.isFunction(saveCallback.fn) && tabPanel.items.contains(currentTab)) {
            saveCallback.fn.apply(saveCallback.context, [dataViewerPanel, dataRecord]);
            tabPanel.setActiveTab(currentTab);
            dataViewerPanel.close();
        }
    },

    onDataViewerSaveSuccess: function (dataViewerPanel, dataRecord) {
        var view = this.getView(),
            saveCallback   = dataViewerPanel.getSaveCallback(),
            DataRecordUtil = Unidata.util.DataRecord,
            mergeTab,
            dataRecordKey;

        view.fireEvent('recordsave', dataViewerPanel, dataRecord);

        this.handleSaveCallback(saveCallback, dataViewerPanel, dataRecord);

        // Передаем null, т.к. ищем таб для ручной консолидации (единственный)
        mergeTab = view.findMergeTabByClusterId(null);

        if (mergeTab) {
            dataRecordKey = DataRecordUtil.buildDataRecordKey({
                dataRecord: dataRecord
            });
            view.setStatus(Unidata.StatusConstant.LOADING);
            mergeTab.card.refreshDataRecord(dataRecordKey).
                then(function () {
                    view.setStatus(Unidata.StatusConstant.READY);
                }, function () {
                    view.setStatus(Unidata.StatusConstant.NONE);
                })
                .done();
        }
    },

    onDataViewerDeleteSuccess: function (dataViewerPanel, dataRecord) {
        var view = this.getView(),
            DataRecordUtil = Unidata.util.DataRecord,
            mergeTab,
            dataRecordKey;

        dataViewerPanel.close();
        view.fireEvent('recorddelete', dataViewerPanel, dataRecord);

        // Передаем null, т.к. ищем таб для ручной консолидации (единственный)
        mergeTab = view.findMergeTabByClusterId(null);

        if (mergeTab) {
            dataRecordKey = DataRecordUtil.buildDataRecordKey({
                dataRecord: dataRecord
            });
            view.setStatus(Unidata.StatusConstant.LOADING);
            mergeTab.card.deleteMergeDataRecordByKey(dataRecordKey).
                then(function () {
                    view.setStatus(Unidata.StatusConstant.READY);
                }, function () {
                    view.setStatus(Unidata.StatusConstant.NONE);
                })
                .done();
        }
    },

    onDataViewerDataRecordStatusChanged: function (etalonId, status) {
        var view = this.getView();

        view.setTabStyle(etalonId, status);
    },

    onDataViewerStatusChanged: function (panel, cfg) {
        var status = cfg.status,
            dataRecordStatus = cfg.dataRecordStatus,
            etalonId = cfg.etalonId,
            view = this.getView(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        switch (status) {
            case viewerConst.VIEWER_STATUS_DONE:
                view.setTabStyle(etalonId, dataRecordStatus);
                break;

            case viewerConst.VIEWER_STATUS_FAILED:
                view.remove(panel);
                break;
        }
    },

    onDataViewerDataRecordDatacardloadfail: function (dataViewerPanel) {
        dataViewerPanel.close();
    },

    /**
     * Создать таб для ручной консолидации
     *
     * @param dataRecordBundle {Unidata.view.steward.cluster.merge.DataRecordBundle}
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
     */
    createManualMergePanel: function (dataRecordBundle, metaRecord) {
        var view = this.getView(),
            Merge = Unidata.view.steward.cluster.merge.Merge,
            tab,
            cfg;

        cfg = {
            metaRecord: metaRecord,
            dataRecordKeys: [dataRecordBundle.dataRecordKey],
            dataRecordBundles: [dataRecordBundle],
            listeners: {
                datarecordopen: this.onDataRecordOpen.bind(this)
            }
        };

        tab = Merge.buildMergeTab(cfg);
        view.relayEvents(tab, ['clusterchanged']);
        view.insert(0, tab);
        view.setTabStyle(null, 'MERGE');

        return tab;
    },

    addDataRecordToMergeTab: function (mergeTab, dataRecord) {
        mergeTab.addMergeDataRecord(dataRecord);
        //TODO: implement me
        throw new Error('Method is not implemented');
    },

    onDataRecordManualMerge: function (dataRecordTab, dataRecordBundle, metaRecord) {
        var view = this.getView(),
            MetaRecordUtil = Unidata.util.MetaRecord,
            MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            mergeTab = null,
            mergePanel;

        // Передаем null, т.к. ищем таб для ручной консолидации (единственный)
        mergeTab = view.findMergeTabByClusterId(null);

        // если пытаемся добавить dataRecord при замерженном кластере, то открываем новый кластер
        if (mergeTab && mergeTab.card.getStatus() === MergeStatusConstant.MERGED) {
            mergeTab.card.close();
            mergeTab = null;
        }

        if (!mergeTab) {
            mergePanel = this.createManualMergePanel(dataRecordBundle, metaRecord);
            view.setActiveTab(mergePanel);
            dataRecordTab.close();
        } else {
            if (MetaRecordUtil.isEqual(mergeTab.card.getMetaRecord(), metaRecord)) {
                // TODO: переписать с промисами
                mergeTab.card.addMergeDataRecordBundle(dataRecordBundle);
                view.setActiveTab(mergeTab.card);
                dataRecordTab.close();
            } else {
                Unidata.showError(view.invalidMergeMetaRecordText);
            }
        }
    },

    configDataViewerPanelListeners: function (panel) {
        panel.on('savesuccess', this.onDataViewerSaveSuccess, this);
        panel.on('deletesuccess', this.onDataViewerDeleteSuccess, this);
        panel.on('datarecordstatuschanged', this.onDataViewerDataRecordStatusChanged, this);
        panel.on('datacardloadfail', this.onDataViewerDataRecordDatacardloadfail, this);
        panel.on('datarecordmanualmerge', this.onDataRecordManualMerge, this);
        panel.on('datarecordopen', this.onDataRecordOpen, this);
        panel.on('dataviewerstatuschanged', this.onDataViewerStatusChanged, this);
    },

    onDataRecordOpen: function (cfg) {
        var etalonId,
            dataRecordBundle = cfg.dataRecordBundle,
            metaRecord = cfg.metaRecord,
            saveCallback = cfg.saveCallback,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle,
            tabComponent,
            tabPanel = this.getView();

        etalonId = DataRecordBundleUtil.retrieveEtalonId(dataRecordBundle);

        if (!etalonId || (tabComponent = this.findRecordTab(etalonId)) === null) {
            tabPanel.createRecordTab(etalonId, metaRecord, Unidata.i18n.t('dataviewer>loading'), null, saveCallback);
        } else {
            tabPanel.setActiveTab(tabComponent);
        }
    },

    findRecordTab: function (etalonId) {
        var tabPanel     = this.getView(),
            tabComponent = null;

        // TODO: Объединить с Unidata.view.steward.search.recordshow.RecordshowController.findDataRecordTabByEtalonId
        tabPanel.items.each(function (item) {
            var itemEtalonId;

            if (Ext.isFunction(item.getEtalonId)) {
                itemEtalonId = item.getEtalonId();
            } else {
                return false;
            }

            if (itemEtalonId === etalonId) {
                tabComponent = item;

                return false;
            }
        });

        return tabComponent;
    },

    createRecordTabFromRecord: function (searchHit, metaRecord) {
        var etalonId,
            recordshowTabPanel,
            title,
            metaRecord;

        recordshowTabPanel = this.getView();
        etalonId = searchHit.get('etalonId');

        if (etalonId && metaRecord && searchHit) {
            title = Unidata.util.DataAttributeFormatter.buildEntityTitleFromSearchHit(metaRecord, searchHit);
        }

        recordshowTabPanel.createRecordTab(etalonId, metaRecord, title);
    },

    changeTab: function (newCard) {
        this.getView().suspendEvent('beforetabchange');
        this.getView().setActiveTab(newCard);
        this.getView().resumeEvent('beforetabchange');
    },

    /**
     * Добавляем кнопку создания нового датарекорда в панель
     */
    onCreateDataRecordButtonAfterRender: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            tabBar = this.lookupReference('recordshowTabBar'),
            button;

        button = Ext.create('Unidata.view.steward.search.recordshow.CreateDataRecordButton', {
            renderTo: tabBar.prebody,
            disabled: true
        });

        view.createDataRecordButton = button;

        // берем по хитрому метарекорд из search panel
        viewModel.bind('{entity}', function (metaRecord) {
            var entityName = '';

            if (metaRecord) {
                entityName = metaRecord.get('name');
            }

            // отложенная операция чтоб не было зацикленного вызова viewmodel.notify
            Ext.Function.defer(function (button, entityName) {
                if (!Unidata.Config.userHasRight(entityName, 'create') || !Boolean(metaRecord)) {
                    button.setDisabled(true);
                } else {
                    button.setDisabled(false);
                }

            }, 0, this, [button, entityName]);
        }, this, {deep: true});

        button.on('click', this.onCreateDataRecordButtonClick, this);

        button.alignTo(tabBar.prebody, 't-t', [0, 6]);
    },

    /**
     * Обработка клика по кнопке создания нового дата рекорда
     */
    onCreateDataRecordButtonClick: function (button) {
        var viewModel = this.getViewModel(),
            metaRecord = viewModel.get('entity'),
            etalonId = null,
            entityName,
            dataRecord;

        if (!metaRecord) {
            return;
        }

        entityName = metaRecord.get('name');

        dataRecord = Ext.create('Unidata.model.data.Record', {
            entityName: entityName
        });
        Unidata.util.DataRecord.bindManyToOneAssociationListeners(dataRecord);

        this.createRecordTab(etalonId, metaRecord, '', null);
    }
});
