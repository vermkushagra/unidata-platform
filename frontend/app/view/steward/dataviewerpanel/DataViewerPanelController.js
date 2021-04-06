Ext.define('Unidata.view.steward.dataviewerpanel.DataViewerPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewerpanel',

    init: function () {
        var me        = this,
            view      = me.getView();

        view.on('render', me.onComponentRender, me, {single: true});
    },

    loadDataViewerFulfilled: function (cfg) {
        var dataRecord = cfg.dataRecord,
            status = dataRecord.get('status');

        cfg.alwaysHideMergeButtons = true;

        this.showDataViewer(cfg);
    },

    loadDataViewerRejected: function () {
        var view = this.getView(),
            etalonId = view.getEtalonId(),
            eventCfg,
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        // TODO: использовать статусы более правильно
        eventCfg = {
            etalonId: etalonId,
            status: viewerConst.VIEWER_STATUS_FAILED
        };

        view.fireEvent('dataviewerstatuschanged', view, eventCfg);
    },

    loadAndShowDataViewer: function (cfg) {
        var view = this.getView(),
            etalonId = cfg.etalonId,
            operationId = cfg.operationId,
            dataRecord = cfg.dataRecord,
            metaRecord = cfg.metaRecord,
            options = {},
            referenceRelations,
            timeIntervalStore,
            viewerCfg,
            loadCfg,
            readOnly = view.getReadOnly(),
            DataViewerLoader = Unidata.view.steward.dataviewer.DataViewerLoader;

        timeIntervalStore = Unidata.util.api.TimeInterval.createStore();

        loadCfg = {
            etalonId: etalonId,
            operationId: operationId,
            metaRecord: metaRecord,
            timeIntervalStore: timeIntervalStore
        };

        if (etalonId) {
            DataViewerLoader.load(loadCfg, options)
                .then(this.loadDataViewerFulfilled.bind(this), this.loadDataViewerRejected.bind(this))
                .done();
        } else {
            dataRecord = Unidata.util.DataRecord.buildDataRecord(metaRecord);

            // TODO: Ivan Marshalkin необходимо решение без "подпиливания"
            // для справочников не создаем референсы ибо их там нет
            if (Unidata.util.MetaRecord.isEntity(metaRecord)) {
                referenceRelations = Unidata.view.steward.relation.ReferencePanel.createEmptyReferenceData(metaRecord);
            }

            // new data record
            viewerCfg = {
                dataRecord: dataRecord,
                metaRecord: metaRecord,
                timeIntervalStore: timeIntervalStore,
                referenceRelations: referenceRelations
            };

            this.showDataViewer(viewerCfg);
        }
    },

    showDataViewer: function (viewerCfg) {
        var view = this.getView(),
            viewer,
            dataRecord = viewerCfg.dataRecord,
            etalonId = viewerCfg.etalonId,
            operationId = viewerCfg.operationId,
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            status = dataRecord.get('status'),
            readOnly = view.getReadOnly(),
            alwaysHideMergeButtons = view.getAlwaysHideMergeButtons(),
            eventCfg;

        viewerCfg.readOnly = readOnly;
        viewerCfg.alwaysHideMergeButtons = alwaysHideMergeButtons;

        if (operationId) {
            viewerCfg.readOnly = true;
        }

        viewer = Ext.create('Unidata.view.steward.dataviewer.DataViewer', viewerCfg);

        viewer.on('render', function () {
            // TODO: использовать статусы более правильно
            eventCfg = {
                etalonId: etalonId,
                dataRecordStatus: status,
                status: viewerConst.VIEWER_STATUS_DONE
            };

            viewer.showDataViewer();
            view.fireEvent('dataviewerstatuschanged', view, eventCfg);
        }, this);

        view.setDataViewer(viewer);
    },

    onComponentRender: function () {
        // this.onRefreshStart();
    },

    onDataRecordSaveSuccess: function () {
        var view = this.getView();

        this.appendViewToListener('savesuccess', view, arguments);
    },

    onDataRecordDeleteSuccess: function () {
        var view = this.getView();

        this.appendViewToListener('deletesuccess', view, arguments);
    },

    onDataRecordDatacardloadfail: function () {
        var view = this.getView();

        this.appendViewToListener('datacardloadfail', view, arguments);
    },

    onDataRecordDeclineSuccess: function () {
        var view = this.getView();

        this.appendViewToListener('declinesuccess', view, arguments);
    },

    onDataRecordMerge: function () {
        var view = this.getView();

        this.appendViewToListener('merge', view, arguments);
    },

    onDataRecordManualMerge: function () {
        var view = this.getView();

        this.appendViewToListener('datarecordmanualmerge', view, arguments);
    },

    onDataCardLoad: function (cfg) {
        var view = this.getView();

        view.setDataRecord(cfg.dataRecord);
    },

    onRefreshStart: function () {
        var view = this.getView();

        if (!view.isMasked()) {
            view.setLoading(view.loadingText);
        }
    },

    onRefreshEnd: function () {
        var view = this.getView();

        view.setLoading(false);
    },

    onRelationReferenceDirtyChange: function (dirty) {
        var viewModel = this.getViewModel();

        viewModel.set('relationReferenceDirty', dirty);
    }
});
