/**
 * Компонент отображающий превью для выбора тестовых записей
 *
 * @author Ivan Marshalkin
 * @date 2018-02-22
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.step.PreviewSandbox', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.testwizard.component.EmptySelectionComponent'
    ],

    referenceHolder: true,

    queryPanel: null,

    resultPanel: null,
    resultStore: null,

    previewContainer: null,
    dataEntity: null,
    emptySelectionCmp: null,

    createSandboxDataRecordButton: null,
    saveSandboxDataRecordButton: null,
    deleteSandboxDataRecordButton: null,
    deleteSelectedSandboxDataRecordButton: null,
    buttonToolBar: null,

    config: {
        metaRecord: null,
        dataRecord: null,
        wizardStep: null,
        operationType: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'component.search.query',
            title: Unidata.i18n.t('search>query.title'),
            reference: 'queryPanel',
            hideRelationsSearch: true,
            width: 300,
            collapsible: true,
            collapseDirection: 'left',
            collapseMode: 'header',
            titleCollapse: true,
            animCollapse: false,
            collapsed: true,
            hideCollapseTool: true,
            listeners: {
                beforeexpand: function () {
                    return false;
                }
            }
        },
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
                    collapsible: false,
                    collapseDirection: 'left',
                    collapseMode: 'header',
                    titleCollapse: true,
                    animCollapse: false,
                    hideCollapseTool: true,
                    listeners: {
                        beforeexpand: function () {
                            return false;
                        },
                        beforecollapse: function () {
                            return false;
                        }
                    }
                }
            ]
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
            dockedItems: [
                {
                    xtype: 'toolbar',
                    ui: 'un-dq-testwzrd-toolbar',
                    reference: 'buttonToolBar',
                    cls: 'right-toolbar',
                    dock: 'right',
                    width: 45,
                    defaults: {
                        xtype: 'button',
                        ui: 'un-toolbar-admin',
                        scale: 'medium'
                    },
                    hidden: true,
                    items: [
                        {
                            reference: 'saveSandboxDataRecordButton',
                            iconCls: 'icon-floppy-disk',
                            tooltip: Unidata.i18n.t('common:save'),
                            hidden: true
                        },
                        '->',
                        {
                            reference: 'deleteSandboxDataRecordButton',
                            iconCls: 'icon-trash2',
                            tooltip: Unidata.i18n.t('common:delete'),
                            margin: '0 0 80 0',
                            hidden: true
                        }
                    ]
                }
            ],
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

    initComponent: function () {
        var metaRecord;

        this.callParent(arguments);

        metaRecord = this.getMetaRecord();

        this.initComponentReference();
        this.initComponentEvent();

        this.initResultPanel();

        this.resultStore = this.createResultStore();
        this.resultPanel.setSourceStore(this.resultStore);
        this.resultPanel.setEditMode(this.resultPanel.editModeType.NONE); // NONE QUERY SELECTION
        this.resultPanel.setMetaRecord(metaRecord);

        this.loadResultStorePage(1);
    },

    initComponentReference: function () {
        this.queryPanel = this.lookupReference('queryPanel');
        this.resultPanel = this.lookupReference('resultPanel');
        this.previewContainer = this.lookupReference('previewContainer');
        this.dataEntity = this.lookupReference('dataEntity');
        this.emptySelectionCmp = this.lookupReference('emptySelectionCmp');

        this.createSandboxDataRecordButton = this.lookupReference('createSandboxDataRecordButton');
        this.saveSandboxDataRecordButton = this.lookupReference('saveSandboxDataRecordButton');
        this.deleteSandboxDataRecordButton = this.lookupReference('deleteSandboxDataRecordButton');
        this.buttonToolBar = this.lookupReference('buttonToolBar');
    },

    initComponentEvent: function () {
        this.resultPanel.resultsetGrid.on('itemclick', this.onResultPanelItemClick, this);
        this.resultPanel.on('selectionchange', this.onResultPanelSelectionChange, this);
        this.resultPanel.on('changepagesize', this.onResultPanelChangePageSize, this);

        this.saveSandboxDataRecordButton.on('click', this.onSaveSandboxDataRecordButtonClick, this);
        this.deleteSandboxDataRecordButton.on('click', this.onDeleteSandboxDataRecordButtonClick, this);
    },

    /**
     * Инициализирует панель результатов поиска
     */
    initResultPanel: function () {
        var buttonContainer = this.resultPanel.toolbarButtonContainer,
            sm = this.resultPanel.resultsetGrid.getSelectionModel();

        buttonContainer.setBind({
            hidden: null
        });

        buttonContainer.show();

        this.initButtonForResultPanel();

        buttonContainer.insert(0, this.createSandboxDataRecordButton);
        buttonContainer.add(this.deleteSelectedSandboxDataRecordButton);

        this.resultPanel.deselectAllButton.setHidden(true);

        // не запоминать удаленные записи
        sm.pruneRemoved = true;

        // необходимо дополнительно отображать идентификатор записи
        Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardUtil.transformResultPanelColumnRenderer(this.resultPanel);
    },

    /**
     * Инициализирует дополнительные кнопки для панели результатов поиска
     */
    initButtonForResultPanel: function () {
        this.createSandboxDataRecordButton = Ext.widget({
            xtype: 'un.roundbtn',
            margin: 10,
            buttonSize: 'medium',
            iconCls: 'icon-plus',
            tooltip: Unidata.i18n.t('admin.dqtest>createSandboxDataRecordTooltip'),
            color: 'gray'
        });

        this.createSandboxDataRecordButton.on('click', this.onCreateSandboxDataRecordButtonClick, this);

        this.deleteSelectedSandboxDataRecordButton = Ext.widget({
            xtype: 'un.roundbtn',
            margin: 10,
            buttonSize: 'medium',
            iconCls: 'icon-trash',
            tooltip: Unidata.i18n.t('admin.dqtest>removeSandboxDataRecordTooltip'),
            color: 'gray',
            hidden: true
        });

        this.deleteSelectedSandboxDataRecordButton.on('click', this.onDeleteSelectedSandboxDataRecordButtonClick, this);
    },

    onDestroy: function () {
        this.queryPanel = null;
        this.resultPanel = null;
        this.previewContainer = null;
        this.dataEntity = null;
        this.emptySelectionCmp = null;
        this.createSandboxDataRecordButton = null;
        this.saveSandboxDataRecordButton = null;
        this.deleteSandboxDataRecordButton = null;
        this.deleteSelectedSandboxDataRecordButton = null;
        this.buttonToolBar = null;

        this.callParent(arguments);
    },

    /**
     * Обработка изменения значения свойства dataRecord
     *
     * @param dataRecord
     */
    updateDataRecord: function (dataRecord) {
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
     * Обработка клика по записи поисковой выдачи
     *
     * @param cmp
     * @param record
     */
    onResultPanelItemClick: function (cmp, record) {
        var me = this,
            metaRecord = this.getMetaRecord(),
            etalonId = record.get('id'),
            promise;

        me.setLoading(true);

        promise = Unidata.util.api.DataQualitySandbox.getSandboxDataRecord(etalonId);

        promise.then(
            function (dataRecord) {
                me.setDataRecord(dataRecord);

                me.dataEntity.setEntityData(metaRecord, dataRecord);
                me.dataEntity.displayDataEntity();
                me.dataEntity.setHidden(false);
                me.buttonToolBar.setHidden(false);
                me.emptySelectionCmp.setHidden(true);

                me.dataEntity.setReadOnly(false);

                me.deleteSandboxDataRecordButton.setHidden(false);
                me.saveSandboxDataRecordButton.setHidden(false);

                me.setLoading(false);
            },
            function () {
                me.dataEntity.setHidden(true);
                me.buttonToolBar.setHidden(true);
                me.emptySelectionCmp.setHidden(true);
                me.deleteSandboxDataRecordButton.setHidden(true);
                me.saveSandboxDataRecordButton.setHidden(true);

                me.setLoading(false);
            }
        ).done();
    },

    /**
     * Обработка события выбора записей в поисковой выдаче
     */
    onResultPanelSelectionChange: function () {
        this.updateComponentsStateOnSelectionChange();
    },

    /**
     * Обрабатываем событие изменения pageSize
     */
    onResultPanelChangePageSize: function () {
        this.loadResultStorePage(1);
    },

    /**
     * Обновляет состояние компонентов после изменения выбора записей
     */
    updateComponentsStateOnSelectionChange: function () {
        var sm = this.resultPanel.resultsetGrid.getSelectionModel(),
            wizardStep = this.getWizardStep(),
            nextStep = wizardStep.getNextStep(),
            nextStepAllowed = false;

        if (sm.getCount()) {
            nextStepAllowed = true;
        }

        nextStep.setStepAllowed(nextStepAllowed);

        this.resultPanel.deselectAllButton.setHidden(!nextStepAllowed);
        this.deleteSelectedSandboxDataRecordButton.setHidden(!nextStepAllowed);
    },

    /**
     * Загружает данные для страницы page
     *
     * @param page
     */
    loadResultStorePage: function (page) {
        var extraParams = this.getResultStoreExtraParams(),
            proxy = this.resultStore.getProxy();

        proxy.setExtraParams(extraParams);

        this.resultStore.loadPage(page, {});
    },

    /**
     * Возвращает дополнительные параметры для запроса
     *
     * @returns {{}}
     */
    getResultStoreExtraParams: function () {
        var extraParams = {},
            metaRecord = this.getMetaRecord(),
            entityName = metaRecord.get('name'),
            returnFields;

        returnFields = Unidata.util.UPathMeta.buildAttributePaths(metaRecord, [{
            property: 'displayable',
            value: true
        }]);

        extraParams['entity'] = entityName;
        extraParams['returnFields'] = returnFields;
        extraParams['fetchAll'] = false;
        extraParams['operator'] = 'AND';
        extraParams['qtype'] = 'FUZZY';
        extraParams['count'] = this.resultStore.getPageSize();

        return extraParams;
    },

    /**
     * Создает стор для результата поисковой выдачи
     *
     * @returns {Ext.data.Store|*}
     */
    createResultStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.search.SearchHit',
            pageSize: Unidata.Config.getCustomerCfg()['SEARCH_ROWS'],
            proxy: {
                type: 'data.searchproxysimple',
                url: Unidata.Config.getMainUrl() + 'internal/dq-sandbox/record/search',
                reader: {
                    type: 'json',
                    rootProperty: 'hits',
                    totalProperty: 'total_count'
                }
            }
        });

        return store;
    },

    /**
     * Создаем новую тестовую запись
     */
    onCreateSandboxDataRecordButtonClick: function () {
        var metaRecord = this.getMetaRecord(),
            dataRecord,
            proxy;

        proxy = new Ext.data.proxy.Ajax({
            type: 'data.recordproxy',
            model: 'Unidata.model.data.Record',
            url: Unidata.Config.getMainUrl() + 'internal/dq-sandbox/record/',
            dateFormat: Unidata.Config.getDateTimeFormatProxy(),
            writer: {
                type: 'json',
                allDataOptions: {
                    persist: true,
                    associated: true
                }
            }
        });

        dataRecord = Unidata.util.DataRecord.buildDataRecord(metaRecord, {
            proxy: proxy
        });

        this.setDataRecord(dataRecord);

        this.dataEntity.setHidden(false);
        this.buttonToolBar.setHidden(false);
        this.emptySelectionCmp.setHidden(true);
        this.saveSandboxDataRecordButton.setHidden(false);
        this.deleteSandboxDataRecordButton.setHidden(false);

        this.dataEntity.setEntityData(metaRecord, dataRecord);
        this.dataEntity.displayDataEntity();

        this.dataEntity.setReadOnly(false);
    },

    /**
     * Обработчик подтверждения удаления выбранных тестовых записей
     */
    onDeleteSelectedSandboxDataRecordButtonClick: function () {
        var title = Unidata.i18n.t('admin.dqtest>removeSandboxDataRecordTitle'),
            message = Unidata.i18n.t('admin.dqtest>removeSandboxDataRecordMessage'),
            searchHits = this.getSelectedSearchHits();

        if (!searchHits.length) {
            return;
        }

        Unidata.showPrompt(title, message, this.deleteSelectedSandboxDataRecord, this);
    },

    /**
     * Удаляет выбранные тестовые записи
     */
    deleteSelectedSandboxDataRecord: function () {
        var me = this,
            metaRecord = this.getMetaRecord(),
            searchHits = this.getSelectedSearchHits(),
            entityName = metaRecord.get('name'),
            etalonIds = [],
            promise;

        Ext.Array.each(searchHits, function (searchHit) {
            etalonIds.push(searchHit.get('id'));
        });

        me.setLoading(true);

        promise = Unidata.util.api.DataQualitySandbox.deleteSandboxDataRecords(entityName, etalonIds);
        promise
            .then(
                function () {
                    me.loadResultStorePage(1);
                    me.resultPanel.deselectAll();

                    me.setDataRecord(null);

                    me.setLoading(false);
                },
                function () {
                    me.setLoading(false);
                }
            )
            .done();
    },

    /**
     * Сохранение тестовой записи на сервере
     */
    onSaveSandboxDataRecordButtonClick: function () {
        var me = this,
            dataRecord = this.getDataRecord(),
            isNewDataRecord = Boolean(dataRecord.phantom),
            page = this.resultStore.currentPage;

        me.setLoading(true);

        dataRecord.save({
            callback: function (record, operation, success) {
                if (success === true) {
                    me.loadResultStorePage(page);

                    me.dataEntity.setHidden(true);
                    me.buttonToolBar.setHidden(true);
                    me.emptySelectionCmp.setHidden(false);
                    me.saveSandboxDataRecordButton.setHidden(true);
                    me.deleteSandboxDataRecordButton.setHidden(true);

                    // для новых записе должны вывести сообщение о том что запись сохранена с таким то идентификатором
                    if (isNewDataRecord) {
                        dataRecord.setId(record.data.content.etalonId);

                        me.showNewDataRecordNotification(dataRecord);
                    }

                    me.setDataRecord(null);
                }

                me.setLoading(false);
            }
        });
    },

    /**
     * Отображает всплывашку о том что новая запись сохранена
     */
    showNewDataRecordNotification: function (dataRecord) {
        var metaRecord = this.getMetaRecord(),
            etalonId = dataRecord.getId(),
            title,
            msg;

        title = Unidata.util.DataAttributeFormatter.buildEntityTitleFromDataRecord(metaRecord, dataRecord, undefined, undefined,
            undefined, {
            applyDirtyPrefix: false
        });

        if (Ext.isEmpty(title)) {
            msg = Unidata.i18n.t('admin.dqtest>newSandboxDataRecordSuccessSaveWithoutDisplayable', {
                etalonId: etalonId
            });
        } else {
            msg = Unidata.i18n.t('admin.dqtest>newSandboxDataRecordSuccessSave', {
                mainDisplayable: title,
                etalonId: etalonId
            });
        }

        Unidata.showMessage(msg);
    },

    /**
     * Обработчик клика по кнопке удаления тестовой записи
     */
    onDeleteSandboxDataRecordButtonClick: function () {
        var me = this,
            dataRecord = this.getDataRecord(),
            page = this.resultStore.currentPage;

        if (page > 1 && this.resultStore.getCount() === 1) {
            page -= 1;
        }

        me.setLoading(true);

        dataRecord.erase({
            callback: function (record, operation, success) {
                if (success === true) {
                    me.loadResultStorePage(page);

                    me.dataEntity.setHidden(true);
                    me.buttonToolBar.setHidden(true);
                    me.emptySelectionCmp.setHidden(false);
                    me.saveSandboxDataRecordButton.setHidden(true);
                    me.deleteSandboxDataRecordButton.setHidden(true);

                    // с задержкой удаляются записи из списка выбранных см pruneRemoved
                    setTimeout(function () {
                        me.updateComponentsStateOnSelectionChange();
                    }, 50);

                    me.setDataRecord(null);

                    me.setLoading(false);
                }
            }
        });
    },

    /**
     * Возвращает выбранные записи в поисковой выдаче
     *
     * @returns {*|Unidata.model.search.SearchHit[]}
     */
    getSelectedSearchHits: function () {
        var searchHits = this.resultPanel.getSelectedSearchHits();

        return searchHits;
    }
});
