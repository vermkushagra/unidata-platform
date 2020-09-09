/**
 * Компонент отображающий превью для выбора существующих записей
 *
 * @author Ivan Marshalkin
 * @date 2018-02-22
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewData', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.testwizard.component.EmptySelectionComponent'
    ],

    referenceHolder: true,

    searchPanel: null,
    previewContainer: null,
    dataEntity: null,
    emptySelectionCmp: null,

    config: {
        metaRecord: null,
        wizardStep: null,
        operationType: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'component.search.searchpanel',
            reference: 'searchPanel',
            width: 600
        },
        {
            xtype: 'panel',
            reference: 'previewContainer',
            title: '&nbsp;',
            cls: 'animated fadeIn',
            ui: 'un-dq-testwzrd-panel',
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            scrollable: 'vertical',
            margin: 0,
            bodyPadding: '10 10 0 10',
            items: [
                {
                    xtype: 'dataentity',
                    reference: 'dataEntity',
                    useCarousel: true,
                    useAttributeGroup: true,
                    hiddenAttribute: false,
                    readOnly: true,
                    hidden: true
                },
                {
                    xtype: 'un.testwizard.emptyselection',
                    reference: 'emptySelectionCmp',
                    flex: 1
                }
            ]
        }
    ],

    initComponent: function () {
        var metaRecord;

        this.callParent(arguments);

        metaRecord = this.getMetaRecord();

        this.initComponentReference();
        this.initComponentEvent();

        this.searchPanel.setEntityReadOnly(true);
        this.searchPanel.setSelectedEntityName(metaRecord.get('name'));

        // отключаем поиск по классификатору
        this.searchPanel.disableSearchSection('classifiers');
    },

    initComponentReference: function () {
        this.searchPanel = this.lookupReference('searchPanel');
        this.previewContainer = this.lookupReference('previewContainer');
        this.dataEntity = this.lookupReference('dataEntity');
        this.emptySelectionCmp = this.lookupReference('emptySelectionCmp');
    },

    initComponentEvent: function () {
        this.searchPanel.on('resultsetitemclick', this.onResultItemClick, this);
        this.searchPanel.on('resultsetselectionchange', this.onResultSelectionChange, this);
    },

    onDestroy: function () {
        this.searchPanel = null;
        this.previewContainer = null;
        this.dataEntity = null;
        this.emptySelectionCmp = null;

        this.callParent(arguments);
    },

    /**
     * Обработка клика по записи поисковой выдачи
     *
     * @param cmp
     * @param record
     */
    onResultItemClick: function (cmp, record) {
        var me = this,
            etalonId = record.get('etalonId'),
            metaRecord = me.getMetaRecord(),
            promise;

        promise = Unidata.util.api.DataRecord.getDataRecord({
            etalonId: etalonId
        });

        me.setLoading(true);

        promise.then(
            function (dataRecord) {
                var title;

                me.dataEntity.setHidden(false);
                me.emptySelectionCmp.setHidden(true);

                me.dataEntity.setEntityData(metaRecord, dataRecord);
                me.dataEntity.displayDataEntity();

                title = Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardUtil.buildDataRecordTitle(metaRecord, dataRecord);

                me.previewContainer.setTitle(title);

                me.setLoading(false);
            },
            function () {
                me.setLoading(false);
            }
        ).done();
    },

    /**
     * Обработка события выбора записей в поисковой выдаче
     *
     * @param sm
     */
    onResultSelectionChange: function (sm) {
        var wizardStep = this.getWizardStep(),
            nextStep = wizardStep.getNextStep(),
            nextStepAllowed = false;

        if (sm.getCount()) {
            nextStepAllowed = true;
        }

        nextStep.setStepAllowed(nextStepAllowed);
    },

    /**
     * Возвращает выбранные записи в поисковой выдаче
     *
     * @returns {*|Unidata.model.search.SearchHit[]}
     */
    getSelectedSearchHits: function () {
        var searchHits = this.searchPanel.resultsetPanel.getSelectedSearchHits();

        return searchHits;
    }
});
