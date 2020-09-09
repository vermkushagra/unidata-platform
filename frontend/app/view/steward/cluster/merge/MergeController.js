/**
 * Экран "Консолидация записей кластера" (controller)
 *
 * @author Sergey Shishigin
 * @date 2016-10-25
 */
Ext.define('Unidata.view.steward.cluster.merge.MergeController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.cluster.merge',

    onRenderInit: function () {
        var errors,
            MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            view                = this.getView();

        errors = this.checkConfig();

        if (errors.length > 0) {
            errors.forEach(function (error) {
                Unidata.showError(error);
            });

            return;
        }

        view.setStatus(MergeStatusConstant.LOADING);
        this.loadData()
            .then(this.loadDataFulfilled.bind(this), this.loadDataRejected.bind(this))
            .done();
    },

    /**
     * Грузим данные для экрана консолидации
     *
     * @returns {*|boolean|Ext.promise.Promise|Ext.Promise}
     */
    loadData: function () {
        var view                = this.getView(),
            dataRecordKeys      = view.getDataRecordKeys(),
            etalonIds,
            mergePreview        = view.getMergePreview(),
            metaRecord          = view.getMetaRecord(),
            metaRecordKey       = view.getMetaRecordKey(),
            MetaRecordApi       = Unidata.util.api.MetaRecord,
            DataRecordUtil      = Unidata.util.DataRecord,
            promises            = [],
            metaPromise         = null,
            mergePreviewPromise = null;

        if (!metaRecord && metaRecordKey) {
            metaPromise = MetaRecordApi.getMetaRecord(metaRecordKey);
        }
        promises.push(metaPromise);

        if (!mergePreview) {
            etalonIds = DataRecordUtil.pluckEtalonIds(dataRecordKeys);

            if (etalonIds.length > 1) {
                mergePreviewPromise = this.loadClassifierNodesAndMergePreview(etalonIds);
            }
        }
        promises.push(mergePreviewPromise);

        return Ext.Deferred.all(promises);
    },

    /**
     * Инициализируем массив dataRecordBundles
     *
     * @returns {*}
     */
    initDataRecordBundles: function () {
        var view              = this.getView(),
            dataRecordBundles = view.getDataRecordBundles();

        if (!Ext.isArray(dataRecordBundles)) {
            dataRecordBundles = [];
            view.setDataRecordBundles(dataRecordBundles);
        }

        return dataRecordBundles;
    },

    /**
     * Добавить панель отображения записей для мержа на экран
     *
     * @param mergeDataRecordViewer
     */
    addMergeDataRecordViewer: function (mergeDataRecordViewer) {
        var view = this.getView(),
            contentContainer = view.contentContainer;

        mergeDataRecordViewer = contentContainer.add(mergeDataRecordViewer);
        view.relayEvents(mergeDataRecordViewer, ['clusterchanged']);
    },

    /**
     * @private
     */
    loadDataFulfilled: function (result) {
        var view                    = this.getView(),
            metaRecord              = view.getMetaRecord(),
            metaRecordNew           = result[0],
            classifierNodes          = result[1],
            MergeStatusConstant     = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            contentContainer        = view.contentContainer,
            mergeDataRecordViewer,
            i,
            mergeRecordDisplayCountReal;

        // устанавливаем загруженные объекты
        if (!metaRecord && metaRecordNew) {
            view.setMetaRecord(metaRecordNew);
        }

        view.setPreviewClassifierNodes(classifierNodes);

        this.initDataRecordBundles();

        view.mergePreviewPanel = this.buildMergePreviewPanel();

        view.mergeDataRecordViewers = [];
        mergeRecordDisplayCountReal = view.calcMergeDataRecordDisplayCountReal();

        for (i = 0; i < mergeRecordDisplayCountReal; i++) {
            mergeDataRecordViewer = this.buildMergeDataRecordViewer({initPage: i + 1});
            view.mergeDataRecordViewers.push(mergeDataRecordViewer);
        }

        contentContainer.add(view.mergePreviewPanel);

        view.mergeDataRecordViewers.forEach(this.addMergeDataRecordViewer.bind(this));

        view.setStatus(MergeStatusConstant.NOTMERGED);
    },

    onAfterLayout: function () {
        var view = this.getView();

        view.updatePanelWidths();
    },

    /**
     * @private
     */
    loadDataRejected: function () {
        var view                = this.getView(),
            MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant;

        view.setStatus(MergeStatusConstant.NONE);
    },

    /**
     * Проверка входного конфига
     *
     * @returns {Array}
     */
    checkConfig: function () {
        var view           = this.getView(),
            dataRecordKeys = view.getDataRecordKeys(),
            metaRecord     = view.getMetaRecord(),
            metaRecordKey  = view.getMetaRecordKey(),
            errors         = [],
            errorsTxtList  = view.errorsTxtList;

        if (!dataRecordKeys) {
            errors.push(errorsTxtList['data']);
        }

        if (!metaRecord && !metaRecordKey) {
            errors.push(errorsTxtList['meta']);
        }

        return errors;
    },

    updateMetaRecord: function (metaRecord) {
        var view = this.getView(),
            metaRecordKey;

        if (!metaRecord) {
            return;
        }

        metaRecordKey = {
            entityName: metaRecord.get('name'),
            entityType: metaRecord.getType()
        };

        view.setMetaRecordKey(metaRecordKey);
    },

    onMergeButtonClick: function () {
        this.mergeDataRecord();
    },

    onCancelButtonClick: function () {
        var view = this.getView();

        view.close();
    },

    mergeDataRecord: function () {
        var view                = this.getView(),
            MergeApi            = Unidata.util.api.Merge,
            dataRecordKeys      = view.getDataRecordKeys(),
            MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            clusterRecord       = view.getClusterRecord(),
            winnerEtalonId = view.getWinnerEtalonId(),
            cfg;

        cfg = {
            dataRecordKeys: dataRecordKeys,
            winnerEtalonId: winnerEtalonId
        };

        view.setStatus(MergeStatusConstant.LOADING);

        MergeApi.doMerge(cfg)
            .then(function (result) {
                    view.setWinnerEtalonId(result.winnerEtalonId);
                    view.setStatus(MergeStatusConstant.MERGED);
                    view.fireEvent('clusterchanged', clusterRecord);
                    Unidata.showMessage(view.mergeSuccessText);
                },
                function () {
                    view.setStatus(MergeStatusConstant.NONE);
                })
            .done();
    },

    buildMergePreviewPanel: function (customCfg) {
        var view                  = this.getView(),
            MergeDataRecordViewer = Unidata.view.steward.cluster.merge.MergeDataRecordViewer,
            toolsCfg,
            metaRecord            = view.getMetaRecord(),
            mergePreview          = view.getMergePreview(),
            previewClassifierNodes = view.getPreviewClassifierNodes(),
            mergePreviewDataRecord,
            panelWidth,
            cfg;

        panelWidth = view.calcPanelWidth();

        cfg = {
            width: panelWidth,
            dataRecord: mergePreview ? mergePreview.previewRecord : null,
            metaRecord: metaRecord,
            classifierNodes: previewClassifierNodes,
            header: {},
            title: Unidata.i18n.t('cluster>consolidationResult'),
            ui: 'un-card',
            noWrapTitle: true,
            noDataTpl: '<div class="un-no-data">' + Unidata.i18n.t('cluster>addSomeOneRecord').toLowerCase() + '</div>'
        };

        toolsCfg = this.buildMergePreviewToolsCfg();

        Ext.apply(cfg, customCfg);
        Ext.apply(cfg, toolsCfg);

        mergePreviewDataRecord = MergeDataRecordViewer.buildMergeDataRecord(cfg);
        mergePreviewDataRecord.addCls('un-merge-preview');

        return mergePreviewDataRecord;
    },

    buildMergeDataRecordViewer: function (customCfg) {
        var view              = this.getView(),
            metaRecord        = view.getMetaRecord(),
            dataRecordKeys    = view.getDataRecordKeys(),
            clusterRecord     = view.getClusterRecord(),
            readOnly          = view.getReadOnly(),
            mergePreview      = view.getMergePreview(),
            dataRecordBundles = view.getDataRecordBundles(),
            attributeWinnersMap,
            panel,
            panelWidth,
            cfg;

        attributeWinnersMap = mergePreview ? mergePreview.attributeWinnersMap : null;
        panelWidth = view.calcPanelWidth();

        cfg = {
            width: panelWidth,
            dataRecordKeys: dataRecordKeys,
            metaRecord: metaRecord,
            dataRecordBundles: dataRecordBundles,
            clusterRecord: clusterRecord,
            attributeWinnersMap: attributeWinnersMap,
            readOnly: readOnly,
            hideAttributeTitle: true
        };

        Ext.apply(cfg, customCfg);

        panel = Ext.create('Unidata.view.steward.cluster.merge.MergeDataRecordViewer', cfg);

        return panel;
    },

    buildMergePreviewToolsCfg: function () {
        return {
            tools: [
                {
                    xtype: 'un.fontbutton.open',
                    tooltip: Unidata.i18n.t('cluster>openWinnerRecord'),
                    handler: this.onOpenWinnerRecordButtonClick.bind(this),
                    bind: {
                        hidden: '{!openWinnerRecordButtonVisible}'
                    }
                }
            ]
        };
    },

    onOpenWinnerRecordButtonClick: function () {
        var cfg,
            view           = this.getView(),
            metaRecord     = view.getMetaRecord(),
            winnerEtalonId = view.getWinnerEtalonId(),
            dataRecordBundle,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle;

        dataRecordBundle = DataRecordBundleUtil.buildDataRecordBundle({
            etalonId: winnerEtalonId
        });

        cfg = {
            dataRecordBundle: dataRecordBundle,
            metaRecord: metaRecord
        };

        // datarecordopen event cfg:
        //
        // dataRecordBundle {Unidata.util.DataRecordBundle} Набор структур по отображению записей
        // searchHit {Unidata.model.search.SearchHit} Результат поиска записи
        // metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель (optional)
        // saveCallback {function} Функция, вызываемая после сохранения открытой записи
        view.fireEvent('datarecordopen', cfg);
    },

    /**
     * @public
     * @param dataRecordBundle
     */
    addMergeDataRecordBundle: function (dataRecordBundle) {
        var view                    = this.getView(),
            mergeDataRecordViewers  = view.mergeDataRecordViewers,
            mergeDataRecordCount    = view.getMergeDataRecordCount(),
            mergeRecordDisplayCount = view.getMergeRecordDisplayCount(),
            dataRecordViewer;

        if (this.addDataRecordBundle(dataRecordBundle)) {
            mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
                mergeDataRecordViewer.addDataRecordKeyToStore(dataRecordBundle.dataRecordKey);
                mergeDataRecordViewer.dataRecordSwitcher.doRefresh();
            });

            if (mergeDataRecordCount < mergeRecordDisplayCount) {
                dataRecordViewer = this.buildMergeDataRecordViewer({initPage: mergeDataRecordCount + 1});
                mergeDataRecordViewers.push(dataRecordViewer);
                this.addMergeDataRecordViewer(dataRecordViewer);
            }

            this.onMergeDataChanged();
        }
    },

    processMergePreview: function (mergePreview) {
        var view = this.getView();

        view.setMergePreview(mergePreview);

        return mergePreview.previewRecord;
    },

    /**
     * Перегрузить и обновить mergePreview
     *
     * @returns {*}
     */
    refreshMergePreview: function () {
        var view                = this.getView(),
            dataRecordKeys      = view.getDataRecordKeys(),
            DataRecordUtil      = Unidata.util.DataRecord,
            MergeStatusConstant = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            etalonIds,
            promise;

        etalonIds = DataRecordUtil.pluckEtalonIds(dataRecordKeys);

        view.setStatus(MergeStatusConstant.LOADING);

        promise = this.loadClassifierNodesAndMergePreview(etalonIds);

        promise.then(function (classifierNodes) {
                view.mergePreviewPanel.setClassifierNodes(classifierNodes);
                view.mergePreviewPanel.displayDataEntity();
                view.setStatus(MergeStatusConstant.NOTMERGED);
            }, function () {
                view.setStatus(MergeStatusConstant.NONE);
                Unidata.showError(view.mergePreviewLoadFailedText);
            })
            .done();

        return promise;
    },

    loadClassifierNodesAndMergePreview: function (etalonIds) {
        var MergeApi = Unidata.util.api.Merge,
            DataViewerLoader = Unidata.view.steward.dataviewer.DataViewerLoader,
            funcs,
            promise;

        funcs = [
            MergeApi.getMergePreview.bind(MergeApi),
            this.processMergePreview.bind(this),
            DataViewerLoader.loadClassifierNodes.bind(DataViewerLoader)
        ];
        promise = Ext.Deferred.pipeline(funcs, {etalonIds: etalonIds});

        return promise;
    },

    /**
     *
     * @returns {Ext.Deferred.promise}
     */
    onMergeDataChanged: function () {
        var view                = this.getView(),
            dataRecordKeys      = view.getDataRecordKeys(),
            promise,
            deferred;

        deferred = new Ext.Deferred();

        if (dataRecordKeys.length === 0) {
            view.close();
            deferred.resolve(true);
            promise = deferred.promise;
        } else if (dataRecordKeys.length > 1) {
            view.updateTdAttrWidth();
            promise = this.refreshMergePreview();
        } else if (dataRecordKeys.length === 1) {
            view.updateTdAttrWidth();
            view.setMergePreview(null);
            view.mergePreviewPanel.setDataRecord(null);
            view.mergePreviewPanel.displayDataEntity();
            deferred.resolve(true);
            promise = deferred.promise;
        }

        return promise;
    },

    onDataRecordBundleAdd: function (dataRecordBundle) {
        this.addDataRecordBundle(dataRecordBundle);
    },

    /**
     * @public
     * @param dataRecordKey
     * @param mergeDataRecordViewer
     */
    deleteMergeDataRecordByKey: function (dataRecordKey, mergeDataRecordViewer) {
        var view                    = this.getView(),
            contentContainer        = view.contentContainer,
            mergeDataRecordViewers  = view.mergeDataRecordViewers,
            mergeRecordDisplayCount = view.getMergeRecordDisplayCount(),
            mergeDataRecordCount,
            promise,
            deferred;

        deferred = new Ext.Deferred();

        if (this.deleteDataRecordBundleByKey(dataRecordKey)) {
            mergeDataRecordCount = view.getMergeDataRecordCount();

            if (mergeDataRecordCount < mergeRecordDisplayCount) {
                // если не задан, то удаляем последний
                if (!mergeDataRecordViewer) {
                    mergeDataRecordViewer = mergeDataRecordViewers[mergeDataRecordViewers.length - 1];
                }
                contentContainer.remove(mergeDataRecordViewer);
                Ext.Array.remove(mergeDataRecordViewers, mergeDataRecordViewer);
                view.updateTdAttrWidth();
                view.updatePanelWidths();
            }

            mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
                var dataRecordKeys;

                dataRecordKeys = mergeDataRecordViewer.getDataRecordKeys();

                if (Ext.isArray(dataRecordKeys) && dataRecordKeys.length > 0) {
                    mergeDataRecordViewer.deleteDataRecordKeyFromStore(dataRecordKey);
                    mergeDataRecordViewer.dataRecordSwitcher.doRefresh();
                }
            });

            promise = this.onMergeDataChanged();
        } else {
            deferred.resolve(true);
            promise = deferred.promise;
        }

        return promise;
    },

    onMergeDataRecordBundleDelete: function (mergeDataRecordViewer, dataRecordKey) {
        this.deleteMergeDataRecordByKey(dataRecordKey, mergeDataRecordViewer);
    },

    /**
     * @private
     * @param dataRecordBundle
     * @returns {*}
     */
    addDataRecordBundle: function (dataRecordBundle) {
        var view                 = this.getView(),
            dataRecordBundles    = view.getDataRecordBundles(),
            dataRecordKeys       = view.getDataRecordKeys(),
            dataRecordKey,
            found,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle,
            DataRecordKeyUtil    = Unidata.util.DataRecordKey;

        if (!dataRecordBundle) {
            return null;
        }

        dataRecordKey = dataRecordBundle.dataRecordKey;

        if (!dataRecordKey) {
            return null;
        }

        found = DataRecordBundleUtil.findDataRecordBundle(dataRecordBundles, dataRecordKey);

        if (!found) {
            dataRecordBundles.push(dataRecordBundle);
        } else {
            return null;
        }

        if (!DataRecordKeyUtil.findDataRecordKey(dataRecordKeys, dataRecordKey)) {
            dataRecordKeys.push(dataRecordKey);
            view.setMergeDataRecordCount(dataRecordKeys.length);
        }

        return dataRecordBundle;
    },

    deleteDataRecordBundleByKey: function (dataRecordKey) {
        var view                 = this.getView(),
            dataRecordBundles    = view.getDataRecordBundles(),
            dataRecordKeys       = view.getDataRecordKeys(),
            dataRecordBundle,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle,
            DataRecordKeyUtil    = Unidata.util.DataRecordKey,
            found;

        found = DataRecordKeyUtil.findDataRecordKey(dataRecordKeys, dataRecordKey);

        if (!found) {
            return;
        }

        dataRecordBundle = DataRecordBundleUtil.findDataRecordBundle(dataRecordBundles, dataRecordKey);
        Ext.Array.remove(dataRecordBundles, dataRecordBundle);
        Ext.Array.remove(dataRecordKeys, found);

        view.setMergeDataRecordCount(dataRecordKeys.length);

        return dataRecordBundle;
    },

    updateMergePreview: function (mergePreview) {
        var view = this.getView(),
            mergeDataRecordViewers = view.mergeDataRecordViewers,
            previewRecord;

        if (!mergePreview || !Ext.isArray(mergeDataRecordViewers) || mergeDataRecordViewers.length === 0) {
            return;
        }

        previewRecord = mergePreview.previewRecord;
        view.mergePreviewPanel.setDataRecord(previewRecord);
        view.setWinnerEtalonId(mergePreview.winnerEtalonId);

        mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
            var mergeDataRecord = mergeDataRecordViewer.getMergeDataRecord();

            mergeDataRecordViewer.setAttributeWinnersMap(mergePreview.attributeWinnersMap);
            mergeDataRecord.highlightBVTAttributes(mergePreview.attributeWinnersMap);
        });
    },

    updateReadOnly: function (value) {
        var view = this.getView();

        view.mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
            mergeDataRecordViewer.setReadOnly(value);
        });
    },

    /**
     * Обновить информацию в экране на основании новых сведений о dataRecord
     *
     * @param dataRecordKey
     */
    refreshDataRecord: function (dataRecordKey) {
        var MergeStatusConstant  = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            view                 = this.getView(),
            dataRecordBundles    = view.getDataRecordBundles(),
            DataRecordKeyUtil    = Unidata.util.DataRecordKey,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle,
            dataRecordKeys       = view.getDataRecordKeys(),
            status               = view.getStatus(),
            isDataRecordKeyExists,
            dataRecordBundle,
            deferred,
            promise;

        deferred = new Ext.Deferred();
        isDataRecordKeyExists = Boolean(DataRecordKeyUtil.findDataRecordKey(dataRecordKeys, dataRecordKey));

        // если кластер не замерженный или такого ключа нет в составе кластера или ключей меньше или равно 1, то обновлять ничего не надо
        if (status === MergeStatusConstant.MERGED || dataRecordKeys.length <= 1 || !isDataRecordKeyExists) {
            deferred.resolve(true);
            promise = deferred.promise;

            return promise;
        }

        // удаляем dataRecordBundle для dataRecordKey
        dataRecordBundle = DataRecordBundleUtil.findDataRecordBundle(dataRecordBundles, dataRecordKey);

        if (dataRecordBundle) {
            Ext.Array.remove(dataRecordBundles, dataRecordBundle);
        }

        // обновляем mergePreview
        if (dataRecordKeys.length > 1) {
            promise = this.refreshMergePreview();
        }

        // если такая запись, то рефрешим dataMergePreviewers
        view.mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
            var currentDataRecordKey = mergeDataRecordViewer.getCurrentDataRecordKey(),
                isEqual;

            isEqual = DataRecordKeyUtil.isEqual(currentDataRecordKey, dataRecordKey);

            if (isEqual) {
                mergeDataRecordViewer.dataRecordSwitcher.doRefresh();
            }
        });

        return promise;
    }
});
