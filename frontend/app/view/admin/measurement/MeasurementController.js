/**
 * @author Ivan Marshalkin
 * @date 2016-11-08
 */

Ext.define('Unidata.view.admin.measurement.MeasurementController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.measurement',

    init: function () {
        this.callParent(arguments);

        this.reloadMeasurementValuesStore();
    },

    reloadMeasurementValuesStore: function () {
        var promise;

        promise = this.getMeasurementPromiseStoreLoad();
        this.handleMeasurementPromiseStoreLoad(promise);
    },

    handleMeasurementPromiseStoreLoad: function (promise) {
        promise.then(
            this.onImportMeasurementValuesSuccessLoad.bind(this),
            this.onImportMeasurementValuesFailureLoad.bind(this)
        ).done();
    },

    getMeasurementPromiseStoreLoad: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = viewModel.getStore('measurementValuesStore'),
            draftMode = view.getDraftMode(),
            storeLoadCfg,
            promise;

        storeLoadCfg = {
            params: {
                draft: draftMode
            }
        };
        promise = Unidata.util.api.MeasurementValues.loadStore(store, true, storeLoadCfg);

        return promise;
    },

    onImportMeasurementValuesSuccessLoad: function (measurementValuesStore) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            measurementTreeStore = viewModel.getStore('measurementTreeStore'),
            rootNode = measurementTreeStore.getRoot(),
            rootChildNodes,
            rootNodesCount;

        rootNode.removeAll();

        measurementValuesStore.each(function (measurementValue) {
            var units = measurementValue.measurementUnits(),
                measurementValueNodeCfg,
                node;

            measurementValueNodeCfg = {
                leaf: false,
                nodeType: 'MEASUREMENTVALUE_NODE',
                record: measurementValue
            };

            Ext.apply(measurementValueNodeCfg, {
                    checked: false
                });

            node = rootNode.appendChild(measurementValueNodeCfg);

            units.each(function (unit) {
                node.appendChild({
                    leaf: true,
                    nodeType: 'MEASUREMENTUNIT_NODE',
                    record: unit
                });
            });
        });

        if (rootNode.hasChildNodes()) {
            rootNode.expand();
        }

        rootChildNodes = this.getRootChildNodes();
        rootNodesCount = rootChildNodes.length;

        viewModel.set('rootNodesCount', rootNodesCount);

        this.onMeasurementNodeCheckChange();
    },

    onImportMeasurementValuesFailureLoad: function () {
    },

    onImportMeasurementButtonChange: function (uploadFilefield) {
        var me = this,
            view = this.getView(),
            draftMode = view.getDraftMode(),
            fileUploadDownload,
            FileUploadDownloadUtil = Unidata.util.FileUploadDownload,
            getParams,
            url;

        getParams = Ext.Object.toQueryString({
            draft: draftMode
        });

        url = Unidata.Config.getMainUrl() + 'internal/measurementValues/import';
        url = Ext.urlAppend(url, getParams);

        fileUploadDownload = FileUploadDownloadUtil.create({
            listeners: {
                uploadstart: function () {
                    view.setLoading(Unidata.i18n.t('common:fileLoadingEllipsis'));
                },
                uploaderror: function () {
                    Unidata.showError(Unidata.i18n.t('common:fileLoadError'));

                    view.setLoading(false);
                    uploadFilefield.reset();
                },
                uploadsuccess: function (successful, errors) {
                    if (!successful) {
                        Unidata.showError(errors);
                    } else {
                        me.showMessage(Unidata.i18n.t('admin.measurement>loadEnumerationsSuccess'));
                    }

                    me.reloadMeasurementValuesStore();

                    view.setLoading(false);

                    uploadFilefield.suspendEvent('change');
                    uploadFilefield.reset();
                    uploadFilefield.resumeEvent('change');
                }
            }
        });

        fileUploadDownload.uploadFiles(uploadFilefield, url);
    },

    onExportMeasurementValueButtonClick: function () {
        var view = this.getView(),
            draftMode = view.getDraftMode(),
            downloadCfg,
            url,
            ids;

        url = Unidata.Api.getMeasurementValueExportUrl();
        ids = this.getCheckedMeasurementValueIds();

        if (Ext.isEmpty(ids)) {
            return;
        }

        downloadCfg = {
            method: 'GET',
            url: url,
            params: {
                token: Unidata.Config.getToken(),
                valueId: ids,
                draft: draftMode
            }
        };

        Unidata.util.DownloadFile.downloadFile(downloadCfg);
    },

    onDeleteMeasurementValueButtonClick: function () {
        var title = Unidata.i18n.t('admin.measurement>removingNode'),
            msg = Unidata.i18n.t('admin.measurement>confirmRemoveMeasuredValue');

        Unidata.showPrompt(title, msg, this.deleteMeasurementValues, this);
    },

    deleteMeasurementValues: function () {
        var view = this.getView(),
            draftMode = view.getDraftMode(),
            promise,
            ids;

        ids = this.getCheckedMeasurementValueIds();

        promise = Unidata.util.api.MeasurementValues.removeMeasurementValues(ids, draftMode);

        promise.then(
            this.onSuccessDeleteMeasurementValue.bind(this)
        ).done();
    },

    onSuccessDeleteMeasurementValue: function (success) {
        if (success) {
            this.reloadMeasurementValuesStore();
        }
    },

    /**
     * Возвращает выбранную ноду
     *
     * @returns {*}
     */
    getSelectedNode: function () {
        var view = this.getView(),
            tree = view.measurementTree,
            sm   = tree.getSelectionModel(),
            node = null;

        if (sm.getCount()) {
            node = sm.getSelected().getAt(0);
        }

        return node;
    },

    onMeasurementNodeCheckChange: function () {
        var view = this.getView(),
            toggleAllCheckBox = view.toggleAllCheckBox,
            checkedCount,
            rootChildNodes,
            rootChildNodesCount;

        checkedCount = this.refreshCheckedCount();
        rootChildNodes = this.getRootChildNodes();
        rootChildNodesCount = rootChildNodes.length;

        toggleAllCheckBox.suspendEvent('change');
        toggleAllCheckBox.setValue(rootChildNodesCount !== 0 && rootChildNodesCount === checkedCount);
        toggleAllCheckBox.resumeEvent('change');
    },

    getCheckedCount: function () {
        var view = this.getView(),
            tree = view.measurementTree,
            checked;

        checked = tree.getChecked();

        return checked.length;
    },

    refreshCheckedCount: function () {
        var viewModel = this.getViewModel(),
            checkedCount;

        checkedCount = this.getCheckedCount();

        viewModel.set('checkedCount', checkedCount);

        return checkedCount;
    },

    getCheckedMeasurementValueIds: function () {
        var view = this.getView(),
            tree = view.measurementTree,
            checked,
            ids;

        checked = tree.getChecked();
        ids = Ext.Array.map(checked, function (node) {
            var record = node.get('record');

            return record.get('id');
        });

        return ids;
    },

    onToggleAllCheckBoxChange: function (self, checked) {
        this.toggleAllNodes(checked);
    },

    toggleAllNodes: function (checked) {
        var nodes;

        nodes = this.getRootChildNodes();

        Ext.Array.each(nodes, function (node) {
            node.set('checked', checked);

            this.onMeasurementNodeCheckChange();
        }, this);
    },

    getRootChildNodes: function () {
        var view = this.getView(),
            tree = view.measurementTree,
            nodes,
            rootNode;

        rootNode = tree.getRootNode();
        nodes = rootNode.childNodes;

        return nodes;
    },

    updateDraftMode: function (draftMode) {
        var viewModel = this.getViewModel();

        viewModel.set('draftMode', draftMode);

        this.reloadMeasurementValuesStore();
    }
});
