/**
 * Класс реализует представление для просмотра / редактирования записи реестра / справочника
 *
 * @author Sergey Shishigin
 * @date 2016-05-23
 */

Ext.define('Unidata.view.steward.dataviewerpanel.DataViewerPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.dataviewerpanel.DataViewerPanelController',
        'Unidata.view.steward.dataviewerpanel.DataViewerPanelModel',

        'Unidata.view.steward.dataviewer.DataViewerLoader'
    ],

    alias: 'widget.steward.dataviewerpanel',

    controller: 'steward.dataviewerpanel',

    config: {
        dataViewer: null,
        dataRecordTitle: null,
        saveCallback: null,
        dataRecord: null,
        metaRecord: null,
        etalonId: null,
        readOnly: false,
        alwaysHideMergeButtons: false,
        relationReferenceDirty: null,
        relationManyToManyDirty: null,
        drafts: false
    },

    plugins: [
        {
            ptype: 'dirtytabcloseprompt',
            pluginId: 'dirtytabcloseprompt',
            closeUnsavedTabText: Unidata.i18n.t('dataviewer>confirmCloseUnsavedRecord')
        }
    ],

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'showDataViewer'
        },
        {
            method: 'loadAndShowDataViewer'
        },
        {
            method: 'onDataRecordManualMerge'
        }
    ],

    referenceHolder: true,

    DATA_VIEWER_REFERENCE: 'dataViewer',

    layout: {
        type: 'fit',
        align: 'stretch'
    },

    viewModel: {
        type: 'steward.dataviewerpanel'
    },

    loadingText: Unidata.i18n.t('common:loading'),

    viewModelAccessors: ['alwaysHideMergeButtons', 'relationReferenceDirty', 'relationManyToManyDirty'],

    /**
     * Сконфигурировать dataViewer перед вставкой в DataViewerPanel
     * @param dataViewer
     */
    doConfigDataViewer: function (dataViewer) {
        dataViewer.setReference(this.DATA_VIEWER_REFERENCE);
        dataViewer.setListeners(
            {
                savesuccess: 'onDataRecordSaveSuccess',
                deletesuccess: 'onDataRecordDeleteSuccess',
                datacardloadfail: 'onDataRecordDatacardloadfail',
                declinesuccess: 'onDataRecordDeclineSuccess',
                merge: 'onDataRecordMerge',
                datacardload: 'onDataCardLoad',
                datarecordmanualmerge: 'onDataRecordManualMerge',
                referencedirtychange: 'onRelationReferenceDirtyChange',
                m2mdirtychange: 'onRelationManyToManyDirtyChange'
                // refreshstart: 'onRefreshStart',
                // refreshend: 'onRefreshEnd'
            }
        );
    },

    initComponent: function () {
        var dataViewer,
            oldDataViewer;

        this.callParent(arguments);

        dataViewer = this.config.dataViewer;
        oldDataViewer = this.lookupReference(this.DATA_VIEWER_REFERENCE);

        if (dataViewer && !oldDataViewer) {
            this.setDataViewer(dataViewer);
        }
    },

    setDataViewer: function (dataViewer) {
        var oldDataViewer = this.lookupReference(this.DATA_VIEWER_REFERENCE),
            relayEventsList = [
                'approvesuccess',
                'approvefailure',
                'declinefailure',
                'datarecordstatuschanged',
                'savefailure'
            ];

        if (oldDataViewer) {
            this.remove(oldDataViewer);
        }

        this.relayEvents(dataViewer, relayEventsList);

        this.setMetaRecord(dataViewer.getMetaRecord());
        this.setDataRecord(dataViewer.getDataRecord());
        this.doConfigDataViewer(dataViewer);
        this.setBind({
            title: '{dataRecordTitle}'
        });
        this.add(dataViewer);
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

    setDataRecord: function (dataRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('dataRecord', dataRecord);

        if (dataRecord) {
            this.setEtalonId(dataRecord.get('etalonId'));
        }

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

    setEtalonId: function (etalonId) {
        var viewModel = this.getViewModel();

        viewModel.set('etalonId', etalonId);
        viewModel.notify();
    },

    getEtalonId: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('etalonId');
    },

    getDirty: function () {
        var dirty = false,
            dataRecord = this.getDataRecord(),
            relationReferenceDirty = this.getRelationReferenceDirty(),
            relationManyToManyDirty = this.getRelationManyToManyDirty();

        if (dataRecord) {
            dirty = dataRecord.checkDirty();
        }

        dirty = dirty || relationReferenceDirty || relationManyToManyDirty;

        return dirty;
    }
});
