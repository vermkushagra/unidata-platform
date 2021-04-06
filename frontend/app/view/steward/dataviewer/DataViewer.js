/**
 * Класс реализует представление для просмотра / редактирования записи реестра / справочника
 *
 * @author Ivan Marshalkin
 * @date 2016-02-21
 */

Ext.define('Unidata.view.steward.dataviewer.DataViewer', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.DataViewerController',
        'Unidata.view.steward.dataviewer.DataViewerModel',
        'Unidata.view.steward.dataviewer.DataViewerConst',

        'Unidata.view.steward.dataviewer.card.data.DataCard',
        'Unidata.view.steward.dataviewer.card.backrel.BackRelCard',
        'Unidata.view.steward.dataviewer.card.history.HistoryCard',
        'Unidata.view.steward.dataviewer.card.origin.OriginCard',

        'Unidata.view.steward.dataviewer.footer.FooterBar',
        'Unidata.view.steward.dataviewer.card.CardDottedMenuButton'
    ],

    alias: 'widget.steward.dataviewer',

    controller: 'steward.dataviewer',
    viewModel: {
        type: 'steward.dataviewer'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'showDataViewer'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'updateDataRecord'
        },
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'getSimpleAttributeContainers'
        },
        {
            method: 'updateTimeIntervalStore'
        }
    ],

    referenceHolder: true,

    loadingText: Unidata.i18n.t('common:loading'),

    footerBar: null,      // подвал (новый)
    cardContainer: null,     // контейнер обертка (все что не подвал и не шапка)

    dataCard: null,          // карточка данных
    backRelCard: null,       // карточка обратных ссылок
    historyCard: null,       // карточка истории записи
    originCard: null,        // карточка исходных записей

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-dataviewer un-container',

    config: {
        drafts: false, // параметр для получении записи - черновика
        operationId: null,
        etalonId: null,
        metaRecord: null,
        dataRecord: null,
        timeInterval: null,
        timeIntervalDate: null,
        timeIntervalStore: null,
        referenceRelations: null,
        classifierNodes: null,
        readOnly: null,
        relationsDigest: null,
        allowMergeOperation: true,              // флаг указывает разрешено ли выполнять операцию merge
        allowSaveOperation: true,               // флаг указывает разрешено ли выполнять операцию save
        allowDeleteOperation: true,              // флаг указывает разрешено ли выполнять операцию delete
        clusterCount: 0,
        alwaysHideMergeButtons: false
    },

    viewModelAccessors: ['alwaysHideMergeButtons'],

    initComponent: function () {
        var eventLists = {
            'datacard': [
                'datacardloadfail',
                'approvesuccess',
                'approvefailure',
                'declinesuccess',
                'declinefailure',
                'savefailure',
                'deletesuccess',
                'savesuccess',
                'refreshstart',
                'refreshend',
                'referencedirtychange'
            ],
            'origincard': [
                'refreshstart',
                'refreshend'
            ],
            'backrelcard': [
                'refreshstart',
                'refreshend'
            ]
        };

        this.callParent(arguments);
        this.initComponentReference();
        this.relayEvents(this.dataCard, eventLists.datacard);
        this.relayEvents(this.originCard, eventLists.origincard);
        this.relayEvents(this.backRelCard, eventLists.backrelcard);
    },

    onDestroy: function () {
        var me = this;

        me.footerBar     = null;
        me.cardContainer = null;
        me.dataCard      = null;
        me.backRelCard   = null;
        me.historyCard   = null;
        me.originCard    = null;

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.footerBar  = me.lookupReference('footerBar');
        me.cardContainer = me.lookupReference('cardContainer');

        me.dataCard    = me.lookupReference('dataCard');
        me.backRelCard = me.lookupReference('backRelCard');
        me.historyCard = me.lookupReference('historyCard');
        me.originCard  = me.lookupReference('originCard');
    },

    initItems: function () {
        var readOnly = this.getReadOnly(),
            metaRecord = this.getMetaRecord();

        this.callParent(arguments);

        this.add([
            {
                xtype: 'container',
                reference: 'cardContainer',
                layout: 'card',
                flex: 1,
                items: [
                    {
                        xtype: 'steward.dataviewer.datacard',
                        reference: 'dataCard',
                        readOnly: readOnly,
                        drafts: this.getDrafts(),
                        operationId: this.getOperationId(),
                        metaRecord: metaRecord,
                        listeners: {
                            datacardload: 'onDataCardLoad',
                            classifiernodesload: 'onClassifierNodesdLoad',
                            selecttimeinterval: 'onSelectTimeInterval',
                            datacardlocked: 'onDataCardLocked',
                            datacardunlocked: 'onDataCardUnLocked'
                        },
                        bind: {
                            // биндим свойства после вызова this.initComponentReference(); см код ниже
                        }
                    },
                    {
                        xtype: 'steward.dataviewer.backrelcard',
                        reference: 'backRelCard',
                        readOnly: readOnly,
                        listeners: {
                            load: 'onLoadBackRelCard',
                            datarecordopen: 'onDataRecordOpen'
                        }
                    },
                    {
                        xtype: 'steward.dataviewer.historycard',
                        reference: 'historyCard',
                        readOnly: readOnly
                    },
                    {
                        xtype: 'steward.dataviewer.origincard',
                        reference: 'originCard',
                        readOnly: readOnly,
                        listeners: {
                            load: 'onLoadOriginCard',
                            etalondetached: 'onEtalonDetached'
                        }
                    }
                ]
            },
            {
                xtype: 'steward.dataviewer.footer',
                reference: 'footerBar',
                docked: 'bottom'
            }
        ]);
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

        // вызываем метод вручную, т.к. setter переопределен, а вызов callParent отсутствует
        // не дергается при deep change (даже если вызывать тут callParent)
        this.updateDataRecord(dataRecord);
    },

    getDataRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('dataRecord');
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

    setTimeIntervalDate: function (timeIntervalDate) {
        var viewModel = this.getViewModel();

        viewModel.set('timeIntervalDate', timeIntervalDate);
        viewModel.notify();
    },

    getTimeIntervalDate: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeIntervalDate');
    },

    setTimeInterval: function (timeInterval) {
        var viewModel = this.getViewModel();

        viewModel.set('timeInterval', timeInterval);
        viewModel.notify();
    },

    getTimeInterval: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeInterval');
    },

    setReferenceRelations: function (referenceRelations) {
        var viewModel = this.getViewModel();

        viewModel.set('referenceRelations', referenceRelations);
        viewModel.notify();
    },

    getReferenceRelations: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('referenceRelations');
    },

    setTimeIntervalStore: function (timeIntervalStore) {
        var viewModel = this.getViewModel();

        viewModel.set('timeIntervalStore', timeIntervalStore);
        viewModel.notify();

        this.updateTimeIntervalStore(timeIntervalStore);
    },

    getTimeIntervalStore: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeIntervalStore');
    },

    setRelationsDigest: function (relationsDigest) {
        var viewModel = this.getViewModel();

        viewModel.set('relationsDigest', relationsDigest);
        viewModel.notify();
    },

    getRelationsDigest: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('relationsDigest');
    },

    setOriginRecords: function (originRecords) {
        var viewModel = this.getViewModel();

        viewModel.set('originRecords', originRecords);
        viewModel.notify();
    },

    getOriginRecords: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('originRecords');
    },

    buildConfig: function () {
        var cfg;

        cfg = {
            metaRecord: this.getMetaRecord(),
            dataRecord: this.getDataRecord(),
            etalonId: this.getEtalonId(),
            referenceRelations: this.getReferenceRelations(),
            timeInterval: this.getTimeInterval(),
            timeIntervalDate: this.getTimeIntervalDate(),
            timeIntervalStore: this.getTimeIntervalStore(),
            relationsDigest: this.getRelationsDigest()
        };

        return cfg;
    },

    getDirty: function () {
        //TODO: implement me
        return false;
    },

    setClusterCount: function (clusterCount) {
        var viewModel = this.getViewModel();

        viewModel.set('clusterCount', clusterCount);
        viewModel.notify();
    },

    getClusterCount: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('clusterCount');
    },

    updateAllowMergeOperation: function (value) {
        var viewModel = this.getViewModel();

        viewModel.set('allowMergeOperation', value);
        viewModel.notify();
    },

    updateAllowSaveOperation: function (value) {
        var viewModel = this.getViewModel();

        viewModel.set('allowSaveOperation', value);
        viewModel.notify();
    },

    updateAllowDeleteOperation: function (value) {
        var viewModel = this.getViewModel();

        viewModel.set('allowDeleteOperation', value);
        viewModel.notify();
    }
});
