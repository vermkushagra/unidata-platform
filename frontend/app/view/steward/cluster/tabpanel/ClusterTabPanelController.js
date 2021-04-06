/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.tabpanel.ClusterTabPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.cluster.tabpanelview',

    openClusterTab: function (entityName, entityType, clusterRecord, matchingGroup, matchingRule) {
        var view = this.getView(),
            cmp,
            tab;

        if (this.isClusterOpened(clusterRecord)) {
            this.setActiveClusterTab(clusterRecord);

            return;
        }

        cmp = Ext.create('Unidata.view.steward.cluster.merge.Merge', {
            closable: true,
            title: Unidata.i18n.t('cluster>viewCluster'),
            metaRecordKey: {
                entityName: entityName,
                entityType: entityType
            },
            dataRecordKeys: clusterRecord.records().getRange(),
            clusterRecord: clusterRecord,
            matchingRule: matchingRule,
            matchingGroup: matchingGroup
        });

        cmp.on('datarecordopen', this.onDataRecordOpenFromMerge, this);
        cmp.on('clusterchanged', this.oClusterChangeFromMerge, this);

        tab = view.add(cmp);
        view.setActiveTab(tab);
    },

    getOpenedClusterTab: function (clusterRecord) {
        var me        = this,
            view      = this.getView(),
            tabs      = view.items.getRange(),
            resultTab = null;

        Ext.Array.each(tabs, function (tab) {
            var tabClusterRecord;

            if (me.isClusterTab(tab)) {
                tabClusterRecord = tab.getClusterRecord();

                if (clusterRecord && tabClusterRecord && clusterRecord.get('clusterId') === tabClusterRecord.get('clusterId')) {
                    resultTab = tab;

                    return false; // остановка итерации Ext.Array.each
                }
            }
        });

        return resultTab;
    },

    isClusterTab: function (tab) {
        return Ext.getClassName(tab) === 'Unidata.view.steward.cluster.merge.Merge';
    },

    isDataViewerTab: function (tab) {
        return Ext.getClassName(tab) === 'Unidata.view.steward.dataviewerpanel.DataViewerPanel';
    },

    isClusterOpened: function (clusterRecord) {
        var tab    = this.getOpenedClusterTab(clusterRecord),
            opened = tab ? true : false;

        return opened;
    },

    setActiveClusterTab: function (clusterRecord) {
        var view = this.getView(),
            tab  = this.getOpenedClusterTab(clusterRecord);

        if (tab) {
            view.setActiveTab(tab);
        }
    },

    onDataRecordOpenFromMerge: function (openParams) {
        var metaRecord = openParams.metaRecord,
            dataRecord = openParams.dataRecordBundle.dataRecord,
            dataRecordKey = openParams.dataRecordBundle.dataRecordKey,
            etalonId;

        if (dataRecordKey) {
            etalonId = dataRecordKey.get('etalonId');
        }

        this.openDataViewerTab(metaRecord, dataRecord, etalonId);
    },

    oClusterChangeFromMerge: function () {
        var view = this.getView();

        view.fireComponentEvent('clusterchanged');
    },

    openDataViewerTab: function (metaRecord, dataRecord, etalonId) {
        var view = this.getView(),
            viewerPanelCfg,
            viewerPanel,
            cfg,
            tab;

        if (this.isDataViewerOpened(dataRecord, etalonId)) {
            this.setActiveDataViewerTab(dataRecord);

            return;
        }

        etalonId = etalonId || dataRecord.get('etalonId');

        viewerPanelCfg = {
            closable: true,
            listeners: {
                savesuccess: this.onDataViewerSaveSuccess.bind(this),
                deletesuccess: this.onDataViewerDeleteSuccess.bind(this)
            },
            alwaysHideMergeButtons: true
        };

        cfg = {
            metaRecord: metaRecord,
            etalonId: etalonId
        };

        viewerPanel = Unidata.util.DataViewerPanelFactory.buildDataViewerPanel(cfg, viewerPanelCfg);
        tab = view.add(viewerPanel);
        viewerPanel.loadAndShowDataViewer(cfg);

        view.setActiveTab(tab);
    },

    isDataViewerOpened: function (dataRecord, etalonId) {
        var tab    = this.getOpenedDataViewerTab(dataRecord, etalonId),
            opened = tab ? true : false;

        return opened;
    },

    setActiveDataViewerTab: function (dataRecord) {
        var view = this.getView(),
            tab  = this.getOpenedDataViewerTab(dataRecord);

        if (tab) {
            view.setActiveTab(tab);
        }
    },

    getOpenedDataViewerTab: function (dataRecord, etalonId) {
        var me        = this,
            view      = this.getView(),
            tabs      = view.items.getRange(),
            resultTab = null;

        etalonId = etalonId || dataRecord.get('etalonId');

        Ext.Array.each(tabs, function (tab) {
            var tabViewerDataRecord;

            if (me.isDataViewerTab(tab)) {
                tabViewerDataRecord = tab.getDataRecord();

                if (tabViewerDataRecord && etalonId === tabViewerDataRecord.get('etalonId')) {
                    resultTab = tab;

                    return false; // остановка итерации Ext.Array.each
                }
            }
        });

        return resultTab;
    },

    onDataViewerSaveSuccess: function (dataViewerPanel, dataRecord) {
        var view = this.getView(),
            panels = view.items.getRange(),
            DataRecordUtil = Unidata.util.DataRecord,
            dataRecordKey;

        dataRecordKey = DataRecordUtil.buildDataRecordKey({
            dataRecord: dataRecord
        });

        panels.forEach(function (panel) {
            if (!this.isClusterTab(panel)) {
                return;
            }

            view.setStatus(Unidata.StatusConstant.LOADING);
            panel.refreshDataRecord(dataRecordKey).
                then(function () {
                    view.setStatus(Unidata.StatusConstant.READY);
                }, function () {
                    view.setStatus(Unidata.StatusConstant.NONE);
                })
                .done();
        }.bind(this));

        view.fireEvent('datarecordsavesuccess', dataViewerPanel, dataRecord);
    },

    onDataViewerDeleteSuccess: function (dataViewerPanel, dataRecord) {
        var view = this.getView(),
            panels = view.items.getRange(),
            DataRecordUtil = Unidata.util.DataRecord,
            dataRecordKey;

        dataRecordKey = DataRecordUtil.buildDataRecordKey({
            dataRecord: dataRecord
        });

        dataViewerPanel.close();

        panels.forEach(function (panel) {
            if (!this.isClusterTab(panel)) {
                return;
            }

            view.setStatus(Unidata.StatusConstant.LOADING);
            panel.deleteMergeDataRecordByKey(dataRecordKey).
                then(function () {
                    view.setStatus(Unidata.StatusConstant.READY);
                }, function () {
                    view.setStatus(Unidata.StatusConstant.NONE);
                })
                .done();
        }.bind(this));

        view.fireEvent('datarecorddeletesuccess', dataViewerPanel, dataRecord);

    }
});
