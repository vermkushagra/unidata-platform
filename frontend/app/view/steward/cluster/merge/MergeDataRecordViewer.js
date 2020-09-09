/**
 * Компонент "Просмотр записи на консолидацю"
 *
 * @author Sergey Shishigin
 * @date 2016-10-26
 */
Ext.define('Unidata.view.steward.cluster.merge.MergeDataRecordViewer', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.cluster.merge.MergeDataRecord',
        'Unidata.view.steward.cluster.merge.DataRecordBundle',
        'Unidata.util.DataRecordBundle',

        'Unidata.util.DataRecordKey',

        'Unidata.view.steward.cluster.merge.MergeStatusConstant'
    ],

    alias: 'widget.steward.cluster.merge.mergedatarecordviewer',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-card',
    cls: 'un-merge-data-record-viewer',

    excludeTitleText: Unidata.i18n.t('cluster>excludeRecord'),
    excludeMsgText: Unidata.i18n.t('cluster>confirmExcludeRecord'),
    deleteTitleText: Unidata.i18n.t('glossary:removeRecord'),
    deleteMsgText: Unidata.i18n.t('cluster>confirmRemoveRecordFromDisplay'),
    fetchDataRecordFailureText: Unidata.i18n.t('cluster>cantLoadRecord'),
    blockClusterRecordFailureText: Unidata.i18n.t('cluster>cantExcludeRecord'),
    loadFailureText: Unidata.i18n.t('cluster>loadDataError'),

    bind: {
        title: '{viewerTitle}'
    },

    config: {
        dataRecord: null,
        classifierNodes: null,
        dataRecordKeys: null,
        dataRecordBundles: null,
        metaRecord: null,
        clusterRecord: null,
        mergeStatus: null,
        attributeWinnersMap: null,
        initPage: 1,
        readOnly: false,
        hideAttributeTitle: null,
        noWrapTitle: true
    },

    referenceHolder: true,
    store: null,

    dottedMenuButton: null,

    viewModelAccessors: ['readOnly', 'metaRecord', 'clusterRecord'],

    items: [
        //{
        //    //buttons for datarecord
        //},
        {
            xtype: 'pagingtoolbar',
            reference: 'dataRecordSwitcher',
            hideRefreshButton: true,
            hideSeparator3: true,
            hideBeforePageText: false,
            beforePageText: Unidata.i18n.t('glossary:record'),
            firstText: Unidata.i18n.t('cluster>firstRecord'),
            lastText: Unidata.i18n.t('cluster>lastRecord'),
            nextText: Unidata.i18n.t('cluster>nextRecord'),
            prevText: Unidata.i18n.t('cluster>prevRecord')
            //TODO: тултипы с глав.отобр.атр
        }
    ],

    viewModel: {
        data: {
            dataRecord: null,
            clusterRecord: null,
            metaRecord: null,
            mergeStatus: null,
            readOnly: null
        },
        formulas: {
            viewerTitle: {
                bind: {
                    dataRecord: '{dataRecord}',
                    metaRecord: '{metaRecord}'
                },
                get: function (getter) {
                    var title      = '',
                        metaRecord = getter.metaRecord,
                        dataRecord = getter.dataRecord,
                        DataAttributeFormatterUtil = Unidata.util.DataAttributeFormatter;

                    if (dataRecord && metaRecord) {
                        title = DataAttributeFormatterUtil.buildEntityTitleFromDataRecord(metaRecord, dataRecord);
                    }

                    title = Ext.coalesceDefined(title, '');

                    return title;
                }
            },
            excludeActionVisible: {
                bind: {
                    clusterRecord: '{clusterRecord}',
                    metaRecord: '{metaRecord}',
                    mergeStatus: '{mergeStatus}',
                    readOnly: '{readOnly}'
                },
                get: function (getter) {
                    var metaRecord       = getter.metaRecord,
                        clusterRecord       = getter.clusterRecord,
                        mergeStatus         = getter.mergeStatus,
                        readOnly            = getter.readOnly,
                        entityName,
                        userHasUpdateRight,
                        MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
                        visible;

                    // исключение из кластера скрыто по задаче UN-6631 т.к. в r4.7 backend не поддерживает исключение записи из кластера
                    return false;

                    if (!metaRecord || !clusterRecord) {
                        return false;
                    }

                    entityName = metaRecord.get('name');

                    userHasUpdateRight = Unidata.Config.userHasRight(entityName, 'update');

                    mergeStatus = mergeStatus || MergeStatusConstant.NONE;

                    visible = !readOnly && userHasUpdateRight && mergeStatus != MergeStatusConstant.MERGED;

                    visible = Ext.coalesceDefined(visible, false);

                    return visible;
                }
            },
            deleteActionVisible: {
                bind: {
                    metaRecord: '{metaRecord}',
                    mergeStatus: '{mergeStatus}',
                    readOnly: '{readOnly}'
                },
                get: function (getter) {
                    var metaRecord       = getter.metaRecord,
                        mergeStatus         = getter.mergeStatus,
                        readOnly            = getter.readOnly,
                        entityName,
                        userHasUpdateRight,
                        MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
                        visible;

                    if (!metaRecord) {
                        return false;
                    }

                    entityName = metaRecord.get('name');

                    userHasUpdateRight = Unidata.Config.userHasRight(entityName, 'update');

                    mergeStatus = mergeStatus || MergeStatusConstant.NONE;

                    visible = !readOnly && userHasUpdateRight && mergeStatus != MergeStatusConstant.MERGED;

                    visible = Ext.coalesceDefined(visible, false);

                    return visible;
                }
            },
            openActionVisible: {
                bind: {
                    mergeStatus: '{mergeStatus}'
                },
                get: function (getter) {
                    var mergeStatus         = getter.mergeStatus,
                        MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
                        visible;

                    mergeStatus = mergeStatus || MergeStatusConstant.NONE;

                    visible = mergeStatus != MergeStatusConstant.MERGED;

                    visible = Ext.coalesceDefined(visible, false);

                    return visible;
                }
            },
            dottedMenuButtonVisible: {
                bind: {
                    excludeActionVisible: '{excludeActionVisible}',
                    openActionVisible: '{openActionVisible}',
                    deleteActionVisible: '{deleteActionVisible}'
                },
                get: function (getter) {
                    var excludeActionVisible = getter.excludeActionVisible,
                        deleteActionVisible = getter.deleteActionVisible,
                        openActionVisible = getter.openActionVisible,
                        dottedMenuButtonVisible;

                    dottedMenuButtonVisible = excludeActionVisible ||
                                              deleteActionVisible ||
                                              openActionVisible;

                    return dottedMenuButtonVisible;
                }
            }
        }
    },

    buildTools: function () {
        var tools;

        this.dottedMenuButton = Ext.create(
            {
                xtype: 'steward.cluster.merge.dottedmenubtn',
                menuAlign: 'tr-bl?',
                handlers: {
                    exclude: this.onExcludeAction.bind(this),
                    delete: this.onDeleteAction.bind(this),
                    open: this.onOpenAction.bind(this)
                },
                bind: {
                    hidden: '{!dottedMenuButtonVisible}'
                }
            }
        );

        tools = [this.dottedMenuButton];

        return tools;
    },

    initComponent: function () {
        var dataRecordKeys,
            initPage;

        this.tools = this.buildTools();

        this.callParent(arguments);
        this.initReferences();

        this.bindFormulas();
        dataRecordKeys = this.getDataRecordKeys();
        initPage       = this.getInitPage();

        if (!this.store) {
            this.store = this.createStore(dataRecordKeys);
        }
        this.store.on('load', this.onStoreLoad, this);
        this.dataRecordSwitcher.setStore(this.store);
        this.dataRecordSwitcher.on('afterrender', function () {
            this.store.loadPage(initPage);
        }, this);
    },

    createStore: function (dataRecordKeys) {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.data.DataRecordKey',
            proxy: {
                type: 'memory'
            },
            data: dataRecordKeys,
            pageSize: 1
        });

        return store;
    },

    initItems: function () {
        var metaRecord            = this.getMetaRecord(),
            hideAttributeTitle    = this.getHideAttributeTitle(),
            noWrapTitle         = this.getNoWrapTitle(),
            MergeDataRecordViewer = Unidata.view.steward.cluster.merge.MergeDataRecordViewer,
            cfg;

        this.callParent(arguments);

        cfg = {
            metaRecord: metaRecord
        };

        if (hideAttributeTitle !== null) {
            cfg = Ext.apply(cfg, {
                hideAttributeTitle: hideAttributeTitle,
                noWrapTitle: noWrapTitle
            });
        }

        this.mergeDataRecord = MergeDataRecordViewer.buildMergeDataRecord(cfg);

        this.add(this.mergeDataRecord);
    },

    initReferences: function () {
        this.dataRecordSwitcher = this.lookupReference('dataRecordSwitcher');
    },

    bindFormulas: function () {
        var viewModel = this.getViewModel(),
            MergeRecordDottedMenuItem = Unidata.view.steward.dataviewer.card.MergeRecordDottedMenuButton;

        viewModel.bind('{!excludeActionVisible}', function (hidden) {
            this.setHiddenMenuItem(MergeRecordDottedMenuItem.MENU_ITEM_EXCLUDE, hidden);
        }, this, {deep: true});

        viewModel.bind('{!deleteActionVisible}', function (hidden) {
            this.setHiddenMenuItem(MergeRecordDottedMenuItem.MENU_ITEM_DELETE, hidden);
        }, this, {deep: true});

        viewModel.bind('{!openActionVisible}', function (hidden) {
            this.setHiddenMenuItem(MergeRecordDottedMenuItem.MENU_ITEM_OPEN, hidden);
        }, this, {deep: true});
    },

    setHiddenMenuItem: function (menuItemName, hidden) {
        var dottedMenuButton = this.dottedMenuButton;

        dottedMenuButton.getMenuItem(menuItemName).setHidden(hidden);
    },

    onStoreLoad: function (store, records, success, operation) {
        var mergeDataRecord = this.mergeDataRecord,
            dataRecordKey   = null,
            page;

        if (!success) {
            return;
        }

        if (records.length === 0) {
            mergeDataRecord.setDataRecord(null);

            return;
        }

        page = operation.getPage();

        dataRecordKey = records[page - 1];

        if (dataRecordKey) {
            // проверям существует ли период актуальности для текущей даты
            this.checkTimeIntervalForTodayExists(dataRecordKey).then(function () {
                // если существует, то запрашиваем данные записи и рисуем карточку записи на консолидацию
                this.fetchData(dataRecordKey)
                    .then(function (cfg) {
                        var dataRecord = cfg.dataRecord,
                            classifierNodes = cfg.classifierNodes,
                            attributeWinnersMap = this.getAttributeWinnersMap();

                        this.setDataRecord(dataRecord);
                        this.setClassifierNodes(classifierNodes);
                        mergeDataRecord.setDataRecord(dataRecord);
                        mergeDataRecord.setClassifierNodes(classifierNodes);
                        mergeDataRecord.displayDataEntity();
                        mergeDataRecord.highlightBVTAttributes(attributeWinnersMap);
                    }.bind(this), function () {
                        Unidata.showError(this.fetchDataRecordFailureText);
                        //TODO: implement me
                    });
            }.bind(this),
                function () {
                    // если не существует, то выводим сообщение об этом, но запись остается на консолидацию
                    var mergeDataRecord = this.getMergeDataRecord(),
                        attributeWinnersMap = this.getAttributeWinnersMap(),
                        etalonId = dataRecordKey.get('etalonId');

                    mergeDataRecord.dataEntity.removeAll();
                    mergeDataRecord.systemAttributeEntity.removeAll();

                    mergeDataRecord.showNoData(mergeDataRecord.noTimeIntervalForTodayText + '<br>' + Unidata.i18n.t('cluster>idRecord') + ': ' + etalonId);
                    mergeDataRecord.highlightBVTAttributes(attributeWinnersMap);
                    mergeDataRecord.updateLayout();
                }.bind(this)).done();
        }
    },

    /**
     * Проверяем существует ли период актуальности для сегодняшней даты
     * @param dataRecordKey
     * @return {*}
     */
    checkTimeIntervalForTodayExists: function (dataRecordKey) {
        var TimeIntervalApi = Unidata.util.api.TimeInterval,
            etalonId = dataRecordKey.get('etalonId'),
            timeIntervalStore = null,
            deferred,
            promise,
            cfg;

        deferred = new Ext.Deferred();
        promise = deferred.promise;

        cfg = {
            store: timeIntervalStore,
            etalonId: etalonId
        };

        TimeIntervalApi.getTimeline(cfg).then(function (store) {
            var TimeIntervalUtil = Unidata.util.TimeInterval,
                timeIntervals = store.getRange(),
                fromNowDate = new Date(),
                toNowDate = new Date(),
                intersectedTimeIntervals;

            fromNowDate.setHours(0, 0, 0, 0);
            toNowDate.setHours(23, 59, 59, 999);
            intersectedTimeIntervals = TimeIntervalUtil.findIntersectedTimeIntervals(fromNowDate, toNowDate, timeIntervals);

            if (intersectedTimeIntervals.length > 0) {
                deferred.resolve();
            } else {
                deferred.reject();
            }
        },
        function () {
            deferred.reject();
        }).done();

        return promise;
    },

    fetchData: function (dataRecordKey) {
        var deferred,
            dataRecordBundles    = this.getDataRecordBundles(),
            dataRecord,
            classifierNodes,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle,
            dataRecordBundle,
            promise,
            mergeStatus          = this.getMergeStatus(),
            MergeStatusConstant  = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            DataViewerLoader     = Unidata.view.steward.dataviewer.DataViewerLoader,
            etalonId             = dataRecordKey.get('etalonId'),
            etalonDate           = dataRecordKey.get('etalonDate'),
            loadCfg,
            metaRecord           = this.getMetaRecord(),
            options;

        deferred = new Ext.Deferred();

        dataRecordBundle = DataRecordBundleUtil.findDataRecordBundle(dataRecordBundles, dataRecordKey);

        if (dataRecordBundle) {
            dataRecord      = dataRecordBundle.dataRecord;
            classifierNodes = dataRecordBundle.classifierNodes;
        }

        // если замержено, то не грузим записи
        if (mergeStatus === MergeStatusConstant.MERGED) {
            deferred.resolve(dataRecordBundle.getValues());
            promise = deferred.promise;
        } else {
            loadCfg = {
                etalonId: etalonId,
                timeIntervalDate: etalonDate,
                dataRecord: dataRecord,
                metaRecord: metaRecord
            };

            options = {
                isReloadTimeline: false,
                isLoadClusterCount: false,
                isLoadReferenceRelations: false
            };

            promise = DataViewerLoader.load(loadCfg, options);
            promise.then(
                    function (cfg) {
                        cfg = Ext.apply(cfg, {
                            dataRecordKey: dataRecordKey
                        });

                        if (dataRecordBundle) {
                            dataRecordBundle.applyValues(cfg);
                        } else {
                            dataRecordBundle = DataRecordBundleUtil.buildDataRecordBundle(cfg);
                            this.fireComponentEvent('datarecordbundleadd', dataRecordBundle);
                        }
                    }.bind(this),
                    function () {
                        Unidata.showError(this.loadFailureText);
                    }.bind(this))
                .done();
        }

        return promise;
    },

    statics: {
        buildMergeDataRecord: function (customCfg) {
            var cfg,
                cmp;

            customCfg = customCfg || {};

            cfg = {};

            Ext.apply(cfg, customCfg);

            cmp = Ext.create('Unidata.view.steward.cluster.merge.MergeDataRecord', cfg);

            return cmp;
        }
    },

    onExcludeAction: function (btn) {
        var metaRecord    = this.getMetaRecord(),
            clusterRecord = this.getClusterRecord(),
            dataRecordKey,
            params;

        dataRecordKey = this.getCurrentDataRecordKey();

        params = {
            metaRecord: metaRecord,
            clusterRecord: clusterRecord,
            dataRecordKey: dataRecordKey
        };

        Unidata.showPrompt(this.excludeTitleText, this.excludeMsgText, this.excludeMergeDataRecord, this, btn, [params]);
    },

    onDeleteAction: function (btn) {
        var dataRecordKey = this.getCurrentDataRecordKey();

        Unidata.showPrompt(this.deleteTitleText, this.deleteMsgText, this.deleteMergeDataRecord, this, btn, [dataRecordKey]);
    },

    excludeMergeDataRecord: function (params) {
        var dataRecordKey = params.dataRecordKey,
            metaRecord    = params.metaRecord,
            clusterRecord = params.clusterRecord,
            ClusterApi    = Unidata.util.api.Cluster,
            entityName,
            clusterId,
            etalonId,
            cfg;

        dataRecordKey = this.getCurrentDataRecordKey();

        if (!metaRecord || !dataRecordKey || !clusterRecord) {
            return;
        }

        entityName = metaRecord.get('name');
        etalonId   = dataRecordKey.get('etalonId');
        clusterId  = clusterRecord.get('clusterId');

        cfg = {
            entityName: entityName,
            clusterId: clusterId,
            etalonId: etalonId
        };

        ClusterApi.blockClusterRecord(cfg)
            .then(function (result) {
                if (result.success === true) {
                    this.fireEvent('clusterchanged', clusterRecord);
                    this.fireComponentEvent('mergedatarecordbundledelete', this, dataRecordKey);
                } else {
                    Unidata.showError(this.blockClusterRecordFailureText);
                }
            }.bind(this), function () {
                Unidata.showError(this.blockClusterRecordFailureText);
            })
            .done();
    },

    deleteMergeDataRecord: function (dataRecordKey) {
        this.fireComponentEvent('mergedatarecordbundledelete', this, dataRecordKey);
    },

    onOpenAction: function () {
        var cfg,
            metaRecord           = this.getMetaRecord(),
            dataRecordKey,
            dataRecordBundle,
            dataRecordBundles    = this.getDataRecordBundles(),
            DataRecordBundleUtil = Unidata.util.DataRecordBundle;

        dataRecordKey    = this.getCurrentDataRecordKey();
        dataRecordBundle = DataRecordBundleUtil.findDataRecordBundle(dataRecordBundles, dataRecordKey);

        cfg = {
            metaRecord: metaRecord,
            dataRecordBundle: dataRecordBundle
        };

        // datarecordopen event cfg:
        //
        // dataRecordBundle {Unidata.util.DataRecordBundle} Набор структур по отображению записей
        // searchHit {Unidata.model.search.SearchHit} Результат поиска записи
        // metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель (optional)
        // saveCallback {function} Функция, вызываемая после сохранения открытой записи
        this.enableBubble('datarecordopen');
        this.fireEvent('datarecordopen', cfg);
    },

    getCurrentDataRecordKey: function () {
        var store    = this.store,
            paging   = this.dataRecordSwitcher,
            pageData = paging.getPageData(),
            page     = pageData.currentPage;

        if (page < 1) {
            return null;
        }

        return store.getRange()[page - 1];
    },

    setDataRecord: function (dataRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('dataRecord', dataRecord);

        viewModel.notify();
    },

    getDataRecord: function () {
        var viewModel = this.getViewModel(),
            dataRecord;

        if (viewModel) {
            dataRecord = viewModel.get('dataRecord');
        }

        return dataRecord;
    },

    setMetaRecord: function (metaRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('metaRecord', metaRecord);
        viewModel.notify();
    },

    getMetaRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('metaRecord');
    },

    setClusterRecord: function (clusterRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('clusterRecord', clusterRecord);
        viewModel.notify();
    },

    getClusterRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('clusterRecord');
    },

    setMergeStatus: function (mergeStatus) {
        var viewModel = this.getViewModel();

        viewModel.set('mergeStatus', mergeStatus);
        viewModel.notify();
    },

    getMergeStatus: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('mergeStatus');
    },

    addDataRecordKeyToStore: function (dataRecordKey) {
        this.store.add(dataRecordKey);
    },

    deleteDataRecordKeyFromStore: function (dataRecordKey) {
        this.store.remove(dataRecordKey);
    },

    getMergeDataRecord: function () {
        return this.mergeDataRecord;
    }
});
