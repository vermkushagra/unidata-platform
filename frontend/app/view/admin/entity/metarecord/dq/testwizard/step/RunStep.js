/**
 * Шаг просмотра результата тестирования для визарда тестирования DQ
 *
 * @author Ivan Marshalkin
 * @date 2018-02-20
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.step.RunStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.steward.dataviewer.card.data.header.notice.bar.DqBar',
        'Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel',
        'Unidata.view.component.search.resultset.Resultset',
        'Unidata.view.admin.entity.metarecord.dq.testwizard.component.EmptySelectionComponent'
    ],

    alias: 'widget.dqtest.wizard.runstep',

    statics: {
    },

    referenceHolder: true,

    previewContainer: null,
    dataEntity: null,
    emptySelectionCmp: null,
    resultPanel: null,
    dqBar: null,
    dqPanel: null,

    resultSetStore: null,

    config: {
        metaRecord: null,
        operationType: null,
        dqRules: null,
        searchHits: null,
        testResultData: null
    },

    cls: 'un-dq-testwzrd-runstep',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'container',
            margin: 0,
            padding: 0,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'component.search.resultset',
                    reference: 'resultPanel',
                    enableSelectionQueryMode: false,
                    allowChangePageSize: false,
                    cls: 'animated fadeIn',
                    width: 300,
                    flex: 1,
                    collapsible: true,
                    collapseDirection: 'left',
                    collapseMode: 'header',
                    titleCollapse: true,
                    animCollapse: false
                }
            ]
        },
        {
            xtype: 'panel',
            reference: 'previewContainer',
            cls: 'animated fadeIn',
            ui: 'un-dq-testwzrd-panel',
            header: {
                items: [
                    {
                        xtype: 'tbspacer',
                        flex: 1
                    },
                    {
                        xtype: 'steward.datacard.header.dqbar',
                        reference: 'dqBar',
                        hidden:  true
                    }
                ]
            },
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
                    hidden: true,
                    flex: 1,
                    scrollable: 'vertical'
                },
                {
                    xtype: 'un.testwizard.emptyselection',
                    reference: 'emptySelectionCmp',
                    flex: 1
                }
            ]
        }
    ],

    listeners: {
        activate: function () {
            // событие activate бросается дважды одно самим шагом, другое tab panel
            // TODO: сделать нормально
            if (arguments.length === 2) {
                this.setDefaultComponentVisibility();

                this.runDqTest();
            }
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();

        this.initResultPanel();
        this.initButtonToolbar();

        this.resultSetStore = this.createResultSetStore();
        this.resultPanel.setSourceStore(this.resultSetStore);

        this.resultPanel.setEditMode(this.resultPanel.editModeType.NONE); // NONE QUERY SELECTION DISABLED
    },

    initComponentReference: function () {
        this.previewContainer = this.lookupReference('previewContainer');
        this.dataEntity = this.lookupReference('dataEntity');
        this.emptySelectionCmp = this.lookupReference('emptySelectionCmp');
        this.resultPanel = this.lookupReference('resultPanel');
    },

    initComponentEvent: function () {
        this.on('afterrender', this.onStepAfterRender, this);

        this.resultPanel.resultsetGrid.on('itemclick', this.onResultItemClick, this);
    },

    onDestroy: function () {
        this.previewContainer = null;
        this.dataEntity = null;
        this.emptySelectionCmp = null;
        this.resultPanel = null;

        this.dqBar = null;
        this.dqPanel = null;

        // эти панели рендерятся внутри компонента ручками поэтому их нужно дестроить руками и заранее
        if (this.dqPanel) {
            this.dqPanel.destroy();
            this.dqPanel = null;
        }

        this.callParent(arguments);
    },

    /**
     * Инициализирует панель результатов поиска
     */
    initResultPanel: function () {
        var me = this,
            view = this.resultPanel.resultsetGrid.getView(),
            sm = this.resultPanel.resultsetGrid.getSelectionModel(),
            getRowClassOld;

        // предотвращаем выделение
        sm.on('beforeselect', function () {
            return false;
        });

        getRowClassOld = view.getRowClass || Ext.emptyFn;

        // делаем финт ушами - чекбоксы должны быть скрыты, вместо них необходимо отображать
        // иконку - индикатор наличия ошибок правил качества
        view.getRowClass = function (record) {
            var cls = getRowClassOld.apply(view, arguments),
                className = 'has-dqerrors',
                etalonId = me.getEtalonIdByRecord(record),
                dqErrors;

            dqErrors = me.getTestResultDqErrorsByEtalonId(etalonId);

            if (dqErrors.length) {
                cls = cls || '';

                if (Ext.isArray(cls)) {
                    cls.push(className);
                } else {
                    cls += ' ' + className;
                }
            }

            return cls;
        };

        // необходимо дополнительно отображать идентификатор записи
        Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardUtil.transformResultPanelColumnRenderer(this.resultPanel);
    },

    /**
     * Инициализирует кнопки тулбара
     */
    initButtonToolbar: function () {
        var toolbar = this.getButtonToolbar(),
            button;

        // кнопка завершения работы визарда
        button = toolbar.add({
            xtype: 'button',
            text: Unidata.i18n.t('admin.dqtest>closeWizard')
        });

        button.on('click', this.onCloseWizardButtonClick, this);
    },

    /**
     * Обработчик клика по кнопке завершения работы визарда
     */
    onCloseWizardButtonClick: function () {
        var wizard = this.getWizard();

        wizard.destroy();
    },

    onStepAfterRender: function () {
        this.dqBar = this.lookupReference('dqBar');
        this.dqBar.on('click', this.onDqBarClick, this);

        this.initDqPanel();
    },

    /**
     * Помещает записи в стор для отображения в результатах поисковой выдачи
     */
    putSearchHitsToResultStore: function () {
        var searchHits = this.getSearchHits(),
            proxy;

        this.resultSetStore.removeAll();

        // memory proxy не позволяет сделать просто store.add(searchHits) (пропадают записи если в стор добавляются записи до рендеринга grid)
        proxy = this.resultSetStore.getProxy();
        proxy.data = Ext.Array.clone(searchHits);

        this.resultSetStore.load();

        this.resultPanel.setStorePageSize(searchHits.length);
    },

    /**
     * Создает стор для результатов поисковой выдачи
     *
     * @returns {Ext.data.Store|*}
     */
    createResultSetStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.search.SearchHit',
            proxy: {
                type: 'memory'
            }
        });

        return store;
    },

    updateMetaRecord: function (metaRecord) {
        this.resultPanel.setMetaRecord(metaRecord);
    },

    /**
     * Запускает операцию тестирование правил качества
     */
    runDqTest: function () {
        var me = this,
            operationType = this.getOperationType(),
            metaRecord = this.getMetaRecord(),
            searchHits = this.getSearchHits(),
            selectedByIds = [],
            entityName = metaRecord.get('name'),
            isSandbox = false,
            dqRules = this.getDqRules(),
            promise;

        if (operationType === 'SANDBOX') {
            isSandbox = true;
        }

        Ext.Array.each(searchHits, function (searchHit) {
            if (operationType === 'SANDBOX') {
                selectedByIds.push(searchHit.get('id'));
            } else {
                selectedByIds.push(searchHit.get('etalonId'));
            }
        });

        this.setLoading(true);

        promise = Unidata.util.api.DataQualitySandbox.runDqTest(isSandbox, selectedByIds, entityName, dqRules);
        promise
            .then(
                function (testResultData) {
                    me.setTestResultData(testResultData);
                    me.putSearchHitsToResultStore();
                    me.setLoading(false);
                },
                function () {
                    me.setLoading(false);
                }
            )
            .done();
    },

    /**
     * Обработчик события клика по записи в результатах поисковой выдачи
     *
     * @param cmp
     * @param record
     */
    onResultItemClick: function (cmp, record) {
        var me = this,
            metaRecord = this.getMetaRecord(),
            dqErrors = [],
            etalonId,
            dataRecord;

        etalonId = me.getEtalonIdByRecord(record);

        dqErrors = this.getTestResultDqErrorsByEtalonId(etalonId);

        dataRecord = this.buildTestResultDataRecord(etalonId);

        me.dqPanel.setDataRecord(dataRecord);

        me.dqBar.setDqErrorCount(dqErrors.length);

        me.dataEntity.setEntityData(metaRecord, dataRecord);
        me.dataEntity.displayDataEntity();
        me.dataEntity.setHidden(false);
        me.emptySelectionCmp.setHidden(true);

        me.dqBar.setHidden(dqErrors.length ? false : true);

        me.refreshTitle(dataRecord);
    },

    refreshTitle: function (dataRecord) {
        var metaRecord = this.getMetaRecord(),
            opt,
            title;

        opt = {
            displayEtalonId: true
        };

        title = Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardUtil.buildDataRecordTitle(metaRecord, dataRecord, opt);

        this.previewContainer.setTitle(title);
    },

    /**
     * Возвращает идентификатор записи
     *
     * @param record
     * @returns {*}
     */
    getEtalonIdByRecord: function (record) {
        var operationType = this.getOperationType(),
            etalonId;

        if (operationType === 'SANDBOX') {
            // тестовые записи
            etalonId = record.get('id');
        } else {
            // существующие записи
            etalonId = record.get('etalonId');
        }

        return etalonId;
    },

    /**
     * Возвращает ошибки правил качества по идентификатору записи
     *
     * @param etalonId
     * @returns {Array}
     */
    getTestResultDqErrorsByEtalonId: function (etalonId) {
        var dqErrors = [],
            dataItem;

        dataItem = this.findTestResultDataItem(etalonId);

        if (dataItem) {
            dqErrors = dataItem.dqErrors;
        }

        return dqErrors;
    },

    /**
     * Возвращает
     *
     * @param etalonId
     * @returns {*}
     */
    findTestResultDataItem: function (etalonId) {
        var testResultData = this.getTestResultData(),
            data = null;

        Ext.Array.each(testResultData, function (item) {
            if (item.record.etalonId === etalonId) {
                data = item;

                return false;
            }
        });

        return Ext.clone(data);
    },

    buildTestResultDataRecord: function (etalonId) {
        var dqErrors,
            resultSet,
            dataRecord = null,
            dataItem,
            reader;

        reader = Ext.create('Ext.data.JsonReader', {
            model: 'Unidata.model.data.Record'
        });

        dataItem = this.findTestResultDataItem(etalonId);

        if (!dataItem) {
            return dataRecord;
        }

        dqErrors = dataItem.dqErrors;

        resultSet = reader.readRecords(dataItem.record);
        dataRecord = resultSet.getRecords()[0];

        dataRecord.dqErrors().removeAll();
        dataRecord.dqErrors().add(dqErrors);

        return dataRecord;
    },

    onDqBarClick: function () {
        var dqPanel = this.dqPanel;

        if (dqPanel.isVisible()) {
            this.hideDqPanel();
        } else {
            this.showDqPanel();
        }
    },

    showDqPanel: function () {
        var panel = this.dqPanel;

        if (panel && !panel.isDestroyed) {
            panel.show();
        }
    },

    hideDqPanel: function () {
        var panel = this.dqPanel;

        if (panel && !panel.isDestroyed) {
            panel.hide();
        }
    },

    initDqPanel: function () {
        var dqBar = this.dqBar,
            panel,
            panelCfg;

        panelCfg = {
            floating: true,
            width: 300,
            hidden: true,
            showByComponent: dqBar
        };

        panel = Ext.create('Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel', panelCfg);
        panel.render(this.getEl());

        panel.on('changedq', this.onChangeDqErrorName, this);

        this.dqPanel = panel;
    },

    onChangeDqErrorName: function (dqName) {
        if (dqName !== null) {
            this.dataEntity.showDqErrorIndicatorByDqName(dqName);
        } else {
            this.dataEntity.showDqErrorsIndicator();
        }
    },

    /**
     * Отображает компоненты в дефолтном состоянии
     */
    setDefaultComponentVisibility: function () {
        this.dataEntity.hide();
        this.emptySelectionCmp.show();

        // панель существует только после того как будет отрендерена панель
        if (this.dqBar) {
            this.dqBar.hide();
        }

        this.hideDqPanel();
    }
});
