/**
 * Экран "Консолидация записей кластера"
 *
 * @author Sergey Shishigin
 * @date 2016-10-25
 */
Ext.define('Unidata.view.steward.cluster.merge.Merge', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.cluster.merge.MergeController',
        'Unidata.view.steward.cluster.merge.MergeModel',

        'Unidata.view.steward.cluster.merge.FooterBar',
        'Unidata.view.steward.cluster.merge.MergeDataRecordViewer',
        'Unidata.view.steward.cluster.merge.MergeStatusConstant'
    ],

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    alias: 'widget.steward.cluster.merge',

    viewModel: {
        type: 'steward.cluster.merge'
    },

    controller: 'steward.cluster.merge',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    //cls: 'un-merge un-panel',
    cls: 'un-merge',

    methodMapper: [
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'onRenderInit'
        },
        {
            method: 'onMergeButtonClick'
        },
        {
            method: 'onCancelButtonClick'
        },
        {
            method: 'addMergeDataRecordBundle'
        },
        {
            method: 'onMergeDataChanged'
        },
        {
            method: 'onDataRecordBundleAdd'
        },
        {
            method: 'onMergeDataRecordBundleDelete'
        },
        {
            method: 'updateMergePreview'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'refreshDataRecord'
        },
        {
            method: 'deleteMergeDataRecordByKey'
        }
    ],

    viewModelAccessors: ['status', 'mergeDataRecordCount', 'readOnly', 'clusterRecord', 'metaRecord'],

    config: {
        dataRecordKeys: null,
        dataRecordBundles: null,
        mergePreview: null,
        metaRecord: null,
        metaRecordKey: null,
        /**
         * @const
         */
        clusterRecord: null,
        matchingRule: null,
        matchingGroup: null,
        status: Unidata.view.steward.cluster.merge.MergeStatusConstant.NONE,
        mergeDataRecordCount: 0,
        winnerEtalonId: null,
        readOnly: false,
        mergeRecordDisplayCount: Unidata.Config.getMergeRecordDisplayCount(),
        previewClassifierNodes: null
    },

    contentContainer: null,
    footerBar: null,         // подвал

    eventBusHolder: true,
    referenceHolder: true,

    errorsTxtList: {
        data: Unidata.i18n.t('cluster>notSetEtalonIds'),
        meta: Unidata.i18n.t('cluster>notSetMetaRecord')
    },

    mergeSuccessText: Unidata.i18n.t('cluster>merge.success'),
    mergePreviewLoadFailedText: Unidata.i18n.t('cluster>merge.previewLoadFailed'),

    mergePreviewPanel: null,
    mergeDataRecordViewers: [],

    bind: {
        title: '{mergeTitle}'
    },

    listeners: {
        afterlayout: 'onAfterLayout'
    },

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretchmax'
            },
            flex: 1,
            scrollable: true,
            cls: 'un-content-inner',
            items: [
                {
                    xtype: 'container',
                    reference: 'contentContainer',
                    layout: {
                        type: 'table',
                        tdAttrs: {
                            valign: 'top',
                            width: '33%'
                        }
                    },
                    items: []
                }
            ]
        },
        {
            xtype: 'steward.cluster.merge.footer',
            reference: 'footerBar',
            height: 60,
            bind: {
                hidden: '{!footerBarVisible}'
            }
        }

    ],

    initComponent: function () {
        var dataRecordKeys = this.getDataRecordKeys();

        this.on('render', this.onRenderInit, this, {
            single: true
        });

        this.callParent(arguments);

        this.initComponentReference();
        this.initListeners();
        this.bindFormulas();
        this.bindHandlersToButtons();
        this.setMergeDataRecordCount(dataRecordKeys.length);
        this.updateTdAttrWidth();
        this.checkClusterRecordsCount();
    },

    checkClusterRecordsCount: function () {
        var clusterRecord = this.getClusterRecord(),
            recordsCount,
            actualRecordsCount,
            text;

        if (clusterRecord) {
            recordsCount = clusterRecord.get('recordsCount');
            actualRecordsCount = clusterRecord.records().getCount();

            if (recordsCount > actualRecordsCount) {
                text = Unidata.i18n.t('cluster>merge.recordsCount', {count: actualRecordsCount, total: recordsCount});
                Unidata.showWarning(text, false);
            }
        }
    },

    initComponentReference: function () {
        this.footerBar     = this.lookupReference('footerBar');
        this.contentContainer = this.lookupReference('contentContainer');
    },

    initListeners: function () {
        this.addComponentListener('mergedatarecordbundledelete', this.onMergeDataRecordBundleDelete, this);
        this.addComponentListener('datarecordbundleadd', this.onDataRecordBundleAdd, this);
    },

    bindFormulas: function () {
        var viewModel = this.getViewModel(),
            footerBar    = this.footerBar,
            FooterBar =  Unidata.view.steward.cluster.merge.FooterBar,
            mergeButton   = footerBar.getButton(FooterBar.MERGE_BUTTON),
            cancelButton = footerBar.getButton(FooterBar.CANCEL_BUTTON);

        viewModel.bind('{!mergeButtonVisible}', function (value) {
            mergeButton.setHidden(value);
        }, this, {deep: true});

        viewModel.bind('{!cancelButtonVisible}', function (value) {
            cancelButton.setHidden(value);
        }, this, {deep: true});
    },

    /**
     * Привязать обработчики действий к кнопкам
     * @private
     */
    bindHandlersToButtons: function () {
        var footerBar    = this.footerBar,
            FooterBar =  Unidata.view.steward.cluster.merge.FooterBar,
            mergeButton   = footerBar.getButton(FooterBar.MERGE_BUTTON),
            cancelButton = footerBar.getButton(FooterBar.CANCEL_BUTTON);

        mergeButton.on('click', this.onMergeButtonClick, this);
        cancelButton.on('click', this.onCancelButtonClick, this);
    },

    /**
     * Вычислить количество отображаемых блоков на основании числа записей и предельного числа отображаемых блоков
     *
     * @returns {*}
     */
    calcMergeDataRecordDisplayCountReal: function () {
        var mergeRecordDisplayCount = this.getMergeRecordDisplayCount(),
            mergeDataRecordCount    = this.getMergeDataRecordCount(),
            mergeRecordDisplayCountReal;

        mergeRecordDisplayCountReal = mergeDataRecordCount < mergeRecordDisplayCount ? mergeDataRecordCount : mergeRecordDisplayCount;

        return mergeRecordDisplayCountReal;
    },

    calcTdAttrWidth: function () {
        var mergeRecordDisplayCountReal,
            tdAttrWidth;

        mergeRecordDisplayCountReal = this.calcMergeDataRecordDisplayCountReal();

        tdAttrWidth = Math.floor(100 / (mergeRecordDisplayCountReal + 1));

        return tdAttrWidth;
    },

    updateTdAttrWidth: function () {
        var contentContainer = this.contentContainer,
            layout = contentContainer.getLayout(),
            tdAttrs = layout.tdAttrs,
            width;

        width = this.calcTdAttrWidth();

        tdAttrs.width = width;
    },

    /**
     * Вычисляем ширину одной из трех панелей
     * @returns {number|*}
     */
    calcPanelWidth: function () {
        var viewWidth               = this.getWidth(),
            mergeRecordDisplayCount = this.calcMergeDataRecordDisplayCountReal(),
            panelWidth;

        // вычитаем паддинги из общей ширины
        // TODO: в будущем параметризовать числом панелей
        panelWidth = Math.floor((viewWidth - 50) / (mergeRecordDisplayCount + 1));

        return panelWidth;
    },

    updatePanelWidths: function () {
        var mergePreviewPanel,
            mergeDataRecordViewers,
            panelWidth;

        panelWidth = this.calcPanelWidth();

        mergePreviewPanel      = this.mergePreviewPanel;
        mergeDataRecordViewers = this.mergeDataRecordViewers;

        if (mergePreviewPanel) {
            mergePreviewPanel.setWidth(panelWidth);
        }

        if (mergeDataRecordViewers) {
            mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
                mergeDataRecordViewer.setWidth(panelWidth);
            });
        }
    },

    statics: {
        buildMergeTab: function (customCfg) {
            var cfg,
                cmp;

            customCfg = customCfg || {};

            cfg = {};

            Ext.apply(cfg, customCfg);

            cmp = Ext.create('Unidata.view.steward.cluster.merge.Merge', cfg);

            return cmp;
        }
    },

    setMatchingRule: function (matchingRule) {
        var viewModel = this.getViewModel();

        viewModel.set('matchingRule', matchingRule);
        viewModel.notify();
    },

    getMatchingRule: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('matchingRule');
    },

    setMatchingGroup: function (matchingGroup) {
        var viewModel = this.getViewModel();

        viewModel.set('matchingGroup', matchingGroup);
        viewModel.notify();
    },

    getMatchingGroup: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('matchingGroup');
    },

    setWinnerEtalonId: function (winnerEtalonId) {
        var viewModel = this.getViewModel();

        viewModel.set('winnerEtalonId', winnerEtalonId);
        viewModel.notify();
    },

    getWinnerEtalonId: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('winnerEtalonId');
    },

    updateStatus: function (status, oldStatus) {
        var StatusConstantClass = Unidata.view.steward.cluster.merge.MergeStatusConstant,
            footerBar = this.footerBar;

        if (status === oldStatus) {
            return;
        }

        this.mergeDataRecordViewers.forEach(function (mergeDataRecordViewer) {
            mergeDataRecordViewer.setMergeStatus(status);
        });

        switch (status) {
            case StatusConstantClass.NONE:
                if (footerBar) {
                    footerBar.setDisabled(false);
                }
                this.setLoading(false);
                break;
            case StatusConstantClass.LOADING:
                if (footerBar) {
                    footerBar.setDisabled(true);
                }
                this.setLoading(this.loadingText);
                break;
            case StatusConstantClass.NOTMERGED:
                if (footerBar) {
                    footerBar.setDisabled(false);
                }
                this.setLoading(false);
                break;
            case StatusConstantClass.MERGED:
                if (footerBar) {
                    footerBar.setDisabled(false);
                }
                this.setLoading(false);
                break;
            default:
                throw new Error(Unidata.i18n.t('glossary:badStatus'));
                //TODO: implement me better
                break;
        }
    }
});
