Ext.define('Unidata.view.steward.dataviewer.DataViewerController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewer',

    mixins: [
        'Unidata.view.steward.dataentity.mixin.TimeIntervalViewable'
    ],

    requires: [
      'Unidata.uiuserexit.dataviewer.DataViewer'
    ],

    init: function () {
        var me = this,
            view = me.getView();

        view.on('render', me.onComponentRender, me, {single: true});
        view.on('refreshstart', me.onRefreshStart, me);
        view.on('refreshend', me.onRefreshEnd, me);
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

    getDataRecord: function () {
        return this.getView().getDataRecord();
    },

    setActiveCard: function (cardName) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            cardContainer = view.cardContainer,
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            card;

        switch (cardName) {
            case viewerConst.DATA_CARD:
                card = view.dataCard;
                break;
            case viewerConst.HISTORY_CARD:
                card = view.historyCard;
                break;
            case viewerConst.BACKREL_CARD:
                card = view.backRelCard;
                break;
            case viewerConst.ORIGIN_CARD:
                card = view.originCard;
                break;
            default:
                card = null;
                break;
        }

        if (card) {
            // активируем карточку
            cardContainer.setActiveItem(card);

            // проставляем имя активной карточки
            viewModel.set('activeCard', cardName);
        }
    },

    /**
     * Получить ассоциативный массив карточек
     * @returns {}
     */
    getCards: function () {
        var view = this.getView(),
            cards;

        cards = {
            data: view.dataCard,
            history: view.historyCard,
            backrelation: view.backRelCard,
            origin: view.originCard
        };

        return cards;
    },

    /**
     * Получить ассоциативный массив кнопок меню
     * @returns {}
     */
    getCardDottedMenuButtons: function () {
        var cards,
            buttons = {};

        cards = this.getCards();

        Ext.Object.each(cards, function (name, card) {
            buttons[name] = card.getDottedMenuButton();
        });

        return buttons;
    },

    /**
     * Установить свойство hidden для определенного menuItem меню для всех карточек
     * @param menuItemName
     * @param hidden
     */
    setHiddenMenuItemsForAllCards: function (menuItemName, hidden) {
        var buttons = this.getCardDottedMenuButtons();

        Ext.Object.each(buttons, function (key, button) {
            button.getMenuItem(menuItemName).setHidden(hidden);
        });
    },

    /**
     * Установить свойство hidden для определенного сепаратора меню для всех карточек
     * @param separatorName
     * @param hidden
     */
    setHiddenMenuSeparatorsForAllCards: function (separatorName, hidden) {
        var buttons = this.getCardDottedMenuButtons();

        Ext.Object.each(buttons, function (key, button) {
            button.getMenuSeparators()[separatorName].setHidden(hidden);
        });
    },

    /**
     * Установить свойство clusterCount для всех карточек
     * @param separatorName
     * @param hidden
     */
    setClusterCountMenuForAllCards: function (count) {
        var buttons = this.getCardDottedMenuButtons();

        Ext.Object.each(buttons, function (key, button) {
            button.setClusterCount(count);
        });
    },

    onComponentRender: function () {
        var me = this,
            ViewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            viewModel = me.getViewModel();

        // в данный момент считаем что вьювер готов
        viewModel.set('viewerStatus', ViewerConst.VIEWER_STATUS_DONE);

        this.provideViewModelFormulas();

        this.bindHandlersToButtons();
        this.bindFormulas();
        this.bindHandlersToMenuItems();

        this.provideMenuButtonUiUserExit();

        this.setActiveCard(ViewerConst.DATA_CARD);

        this.onRefreshStart();
    },

    provideViewModelFormulas: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            metaRecord = view.getMetaRecord(),
            dataRecord = view.getDataRecord(),
            opts;

        opts = {
            metaRecord: metaRecord,
            dataRecord: dataRecord
        };

        Unidata.uiuserexit.viewmodel.FormulaProvider.provideActiveUiUserExit(
            viewModel,
            Unidata.uiuserexit.viewmodel.FormulaTypes.DATAVIEWER,
            opts);
    },

    provideMenuButtonUiUserExit: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            dataRecord = view.getDataRecord(),
            baseOpts,
            opst;

        baseOpts = {
            dataViewer: view,
            metaRecord: metaRecord,
            dataRecord: dataRecord
        };

        // дата карточка
        opst = {
            card: view.dataCard
        };

        Unidata.uiuserexit.dataviewer.MenuButtonProvider.provideActiveUiUserExit(
            view.dataCard.getDottedMenuButton(),
            Unidata.uiuserexit.dataviewer.MenuButtonTypes.DATA_CARD,
            Ext.apply(opst, baseOpts)
        );

        // исходных записей карточка
        opst = {
            card: view.originCard
        };

        Unidata.uiuserexit.dataviewer.MenuButtonProvider.provideActiveUiUserExit(
            view.originCard.getDottedMenuButton(),
            Unidata.uiuserexit.dataviewer.MenuButtonTypes.ORIGIN_CARD,
            Ext.apply(opst, baseOpts)
        );

        // карточка истории записи
        opst = {
            card: view.historyCard
        };

        Unidata.uiuserexit.dataviewer.MenuButtonProvider.provideActiveUiUserExit(
            view.historyCard.getDottedMenuButton(),
            Unidata.uiuserexit.dataviewer.MenuButtonTypes.HISTORY_CARD,
            Ext.apply(opst, baseOpts)
        );

        // карточка обратных ссылок
        opst = {
            card: view.backRelCard
        };

        Unidata.uiuserexit.dataviewer.MenuButtonProvider.provideActiveUiUserExit(
            view.backRelCard.getDottedMenuButton(),
            Unidata.uiuserexit.dataviewer.MenuButtonTypes.BACKRELATION_CARD,
            Ext.apply(opst, baseOpts)
        );
    },

    /**
     * Привязать формулы к действиям над компонентами экрана
     * @private
     */
    bindFormulas: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            CardDottedMenuButton = Unidata.view.steward.dataviewer.card.CardDottedMenuButton,
            FooterBar = Unidata.view.steward.dataviewer.footer.FooterBar,
            footerBar = view.footerBar,
            saveButton = footerBar.getButton(FooterBar.SAVE_BUTTON),
            deleteButton = footerBar.getButton(FooterBar.DELETE_BUTTON),
            restoreButton = footerBar.getButton(FooterBar.RESTORE_BUTTON),
            headerBar = view.dataCard.headerBar,
            clusterBar = headerBar.clusterBar;

        viewModel.bind('{!actionsVisible}', function (hidden) {
            this.setHiddenMenuSeparatorsForAllCards(CardDottedMenuButton.MENU_MERGE_ACTIONS_SEPARATOR, hidden);
        }, this, {deep: true});

        viewModel.bind('{!dataCardSelected}', function (hidden) {
            this.setHiddenMenuSeparatorsForAllCards(CardDottedMenuButton.MENU_CARD_SECTIONS_SEPARATOR, hidden);
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_ETALON_INFO, hidden);
        }, this, {deep: true});

        viewModel.bind('{!cardSectionsVisible}', function (hidden) {
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_DATA_CARD, hidden);
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_HISTORY_CARD, hidden);
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_BACKRELATION_CARD, hidden);
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_ORIGIN_CARD, hidden);
            this.setHiddenMenuSeparatorsForAllCards(CardDottedMenuButton.MENU_CARD_SECTIONS_SEPARATOR, hidden);
        }, this, {deep: true});

        // MENU ACTIONS

        viewModel.bind('{!manualMergeActionVisible}', function (hidden) {
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_MANUAL_MERGE, hidden);
        }, this, {deep: true});

        viewModel.bind('{!mergeActionVisible}', function (hidden) {
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_MERGE, hidden);
        }, this, {deep: true});

        viewModel.bind('{mergePermited}', function (mergePermited) {
            clusterBar.setDisabled(!mergePermited);
            //this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_MERGE, hidden);
        }, this, {deep: true});

        viewModel.bind('{!refreshActionVisible}', function (hidden) {
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_REFRESH, hidden);
        }, this, {deep: true});

        viewModel.bind('{!jmsPublishActionVisible}', function (hidden) {
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_JMS_PUBLISH, hidden);
        }, this, {deep: true});

        viewModel.bind('{clusterCount}', function (clusterCount) {
            this.setClusterCountMenuForAllCards(clusterCount);
            view.dataCard.setClusterCount(clusterCount);
        }, this, {deep: true});

        // BUTTON ACTIONS (FOOTER BAR)

        viewModel.bind('{!deleteActionVisible}', function (hidden) {
            deleteButton.setHidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!saveActionVisible}', function (hidden) {
            saveButton.setHidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!restoreActionVisible}', function (hidden) {
            restoreButton.setHidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!footerBarVisible}', function (hidden) {
            footerBar.setHidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!footerEnabled}', function (disabled) {
            footerBar.setDisabled(disabled);
        }, this, {deep: true});

        // SECTIONS

        viewModel.bind('{!backrelButtonVisible}', function (hidden) {
            this.setHiddenMenuItemsForAllCards(CardDottedMenuButton.MENU_ITEM_BACKRELATION_CARD, hidden);
        }, this, {deep: true});

        // COMPONENTS IN DATA CARD

        viewModel.bind('{!dottedMenuButtonVisible}', function (hidden) {
            view.dataCard.setDottedmenubuttonhidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!approveBarVisible}', function (hidden) {
            view.dataCard.setApprovebarhidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!dqBarVisible}', function (hidden) {
            view.dataCard.setDqbarhidden(hidden);
            view.dataCard.showDqErrorsIndicator();
        }, this, {deep: true});

        viewModel.bind('{dqErrorCount}', function (count) {
            view.dataCard.updateDqErrorCount(count);
        }, this, {deep: true});

        viewModel.bind('{!createTimeIntervalButtonVisible}', function (hidden) {
            view.dataCard.setCreatetimeintervalbuttonhidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{!deleteTimeIntervalButtonVisible}', function (hidden) {
            view.dataCard.setDeletetimeintervalbuttonhidden(hidden);
        }, this, {deep: true});

        viewModel.bind('{dataCardReadOnly}', function (readOnly) {
            view.dataCard.setReadOnly(readOnly);
        }, this, {deep: true});
    },

    /**
     * Привязать обработчики действий к элементам меню
     * @private
     */
    bindHandlersToMenuItems: function () {
        var view = this.getView(),
            dataCard = view.dataCard,
            originCard = view.originCard,
            historyCard = view.historyCard,
            backRelCard = view.backRelCard,
            cards,
            handlers;

        cards = {
            dataCard: dataCard,
            originCard: originCard,
            historyCard: historyCard,
            backRelCard: backRelCard
        };

        handlers = {
            // main actions
            merge: this.onMergeAction.bind(this),
            manualMerge: this.onManualMergeAction.bind(this),
            //restore: this.onRestoreAction.bind(this),

            // card sections
            dataCard: this.onDataCardAction.bind(this),
            historyCard: this.onHistoryCardAction.bind(this),
            backrelationCard: this.onBackRelAction.bind(this),
            originCard: this.onOriginCardAction.bind(this),

            // additional actions
            etalonInfo: dataCard.onEtalonInfoMenuItemClick.bind(dataCard),
            refresh: dataCard.onRefreshButtonClick.bind(dataCard),
            jmsPublish: dataCard.onJmsPublishButtonClick.bind(dataCard)
        };

        Ext.Object.each(cards, function (name, card) {
            // check if functions exists
            if (!Ext.isFunction(card.getDottedMenuButton)) {
                throw new Error(Ext.String.format('Method {0}.getDottedMenuButton is not implemented', name));
            }
        });

        Ext.Object.each(cards, function (name, card) {
            var menu = card.getDottedMenuButton();

            if (!menu) {
                console.log(Ext.String.format('Menu doesn\'t exist for card {0}', name));

                return;
            }

            menu.setHandlers(handlers);
        });
    },

    /**
     * Привязать обработчики действий к кнопкам
     * @private
     */
    bindHandlersToButtons: function () {
        var view = this.getView(),
            footerBar = view.footerBar,
            FooterBar = Unidata.view.steward.dataviewer.footer.FooterBar,
            saveButton = footerBar.getButton(FooterBar.SAVE_BUTTON),
            deleteButton = footerBar.getButton(FooterBar.DELETE_BUTTON),
            restoreButton = footerBar.getButton(FooterBar.RESTORE_BUTTON),
            headerBar = view.dataCard.headerBar,
            clusterBar = headerBar.clusterBar;

        saveButton.on('click', this.onSaveButtonClick, this);
        deleteButton.on('click', this.onDeleteButtonClick, this);
        restoreButton.on('click', this.onRestoreAction, this);
        clusterBar.on('click', this.redirectToClusters, this);
    },

    changeWatcher: null,
    hasDqError: false,

    initDataRecordChangeWatcher: function () {
        var me = this,
            view = this.getView(),
            dataRecord = view.getDataRecord();

        if (this.changeWatcher) {
            dataRecord.unjoin(this.changeWatcher);
        }

        this.changeWatcher = {
            afterEdit: function () {
                if (me.hasDqError) {
                    return;
                }

                if (!dataRecord.checkDirty()) {
                    dataRecord.unjoin(me.changeWatcher);
                    dataRecord.dqErrors().setData([]);
                    dataRecord.join(me.changeWatcher);
                }
            }
        };

        dataRecord.join(this.changeWatcher);
    },

    initCards: function () {
        var view = this.getView(),
            dataCard = view.dataCard,
            originCard = view.originCard,
            historyCard = view.historyCard,
            backRelCard = view.backRelCard,
            etalonId = view.getEtalonId(),
            metaRecord = view.getMetaRecord(),
            dataRecord = view.getDataRecord(),
            classifierNodes = view.getClassifierNodes();

        this.hasDqError = Boolean(dataRecord.dqErrors().getCount());

        this.initDataRecordChangeWatcher(dataRecord);

        // *** Инициализируем параметры в карточках ***

        // карточка записи
        dataCard.setEtalonId(etalonId);
        dataCard.setMetaRecord(metaRecord);
        dataCard.setDataRecord(dataRecord);
        dataCard.setClassifierNodes(classifierNodes);

        dataCard.setTimeIntervalDate(view.getTimeIntervalDate());
        dataCard.setTimeInterval(view.getTimeInterval());
        dataCard.setReferenceRelations(view.getReferenceRelations() || []);
        dataCard.setTimeIntervalStore(view.getTimeIntervalStore());

        // карточка истории записи
        historyCard.setEtalonId(etalonId);
        historyCard.setMetaRecord(metaRecord);
        // для карточки истории записи ноды классификаторов должны быть "оригинальными"
        if (!historyCard.getClassifierNodes()) {
            historyCard.setClassifierNodes(classifierNodes);
        }
        historyCard.clearHistoryCard();

        // карточка origin view
        originCard.setTimeIntervalDate(view.getTimeIntervalDate());
        originCard.setTimeIntervalStore(view.getTimeIntervalStore());
        originCard.setMetaRecord(view.getMetaRecord());
        originCard.setOriginRecords(view.getOriginRecords());
        originCard.setEtalonId(view.getEtalonId());

        // карточка back rel view
        backRelCard.setMetaRecord(view.getMetaRecord());
        backRelCard.setEtalonId(view.getEtalonId());
        backRelCard.setRelationsDigest(view.getRelationsDigest());
    },

    showDataViewer: function () {
        var view = this.getView(),
            dataCard = view.dataCard;

        this.initCards();

        // Отображаем карточку эталона
        dataCard.displayDataCard();
        view.fireEvent('refreshend');

        // обработчик для интеграции с логикой кастомера
        Unidata.uiuserexit.callback.CallbackProvider.provideActiveUiUserExit(
            Unidata.uiuserexit.callback.CallbackTypes.DATAVIEWER_SHOW,
            this,
            {
                dataViewer: view
            }
        );
    },

    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            dataCard = view.dataCard,
            backRelCard = view.backRelCard,
            originCard = view.originCard;

        viewModel.set('readOnly', readOnly);

        // Примечание. Карточка истории сама по себе должна быть только для чтения поэтому ее не обрабатываем

        if (dataCard) {
            dataCard.setReadOnly(readOnly);
        }

        if (backRelCard) {
            backRelCard.setReadOnly(readOnly);
        }

        if (originCard) {
            originCard.setReadOnly(readOnly);
        }
    },

    onDataRecordOpen: function (cfg) {
        var view = this.getView();

        view.fireEvent('datarecordopen', cfg);
    },

    // *** Button click event handlers ***
    onSaveButtonClick: function () {
        var userExit = Unidata.uiuserexit.dataviewer.DataViewer,
        view = this.getView();

        if (Ext.isFunction(userExit.onSave)) {
            userExit.onSave(view);
        } else {
            this.saveAll();
        }
    },

    saveAll: function () {
        var view = this.getView(),
            dataCard = view.dataCard,
            saveAtomic;

        saveAtomic = view.getSaveAtomic();
        saveAtomic = Ext.isBoolean(saveAtomic) ? saveAtomic : false;

        if (saveAtomic) {
            dataCard.saveAllAtomic();
        } else {
            dataCard.saveAllOneByOne();
        }
    },

    onDeleteButtonClick: function (btn) {
        var view = this.getView(),
            dataCard = view.dataCard,
            dataRecord = view.getDataRecord(),
            title,
            msg;

        if (dataRecord.phantom) {
            title = Unidata.i18n.t('dataviewer>cancelCreateRecord');
            msg = Unidata.i18n.t('dataviewer>confirmCancelCreateRecord');
        } else {
            title = Unidata.i18n.t('glossary:removeRecord');
            msg = Unidata.i18n.t('dataviewer>confirmRemoveRecord');
        }

        this.showPrompt(title, msg, dataCard.deleteDataRecord, dataCard, btn);
    },

    onRestoreAction: function (btn) {
        var view = this.getView(),
            title = Unidata.i18n.t('glossary:restoreRecord'),
            msg = Unidata.i18n.t('dataviewer>confirmRestoreRecord'),
            dataCard = view.dataCard;

        this.showPrompt(title, msg, dataCard.checkRecordConsistencyAndRestore, dataCard, btn);
    },

    onMergeAction: function () {
        this.redirectToClusters();
    },

    onManualMergeAction: function () {
        this.manualMergeDataRecord();
    },

    onDataCardAction: function () {
        var viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        this.setActiveCard(viewerConst.DATA_CARD);
    },

    onHistoryCardAction: function () {
        var viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        this.setActiveCard(viewerConst.HISTORY_CARD);
    },

    onBackRelAction: function () {
        var view = this.getView(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        view.backRelCard.on('activate', function () {
            view.fireEvent('refreshstart');

            if (!view.getRelationsDigest()) {
                view.backRelCard.loadAndDisplayBackRelsCard();
            } else {
                view.backRelCard.displayBackRels();
            }

        }, this, {single: true});

        this.setActiveCard(viewerConst.BACKREL_CARD);
    },

    onOriginCardAction: function () {
        var view = this.getView(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            originCard = view.originCard;

        originCard.on('activate', function () {
            view.fireEvent('refreshstart');

            originCard.setDrafts(view.getDrafts());
            originCard.setOperationId(view.getOperationId());

            if (!view.getSelectedInactiveTimeInterval() && !originCard.isDataCached()) {
                originCard.loadAndDisplayOriginCard();
            } else {
                originCard.displayOriginCard();
            }
        }, this, {single: true});

        this.setActiveCard(viewerConst.ORIGIN_CARD);
    },

    // *** Load event handlers ***

    /**
     * Обрабатывает событие загрузки данных карточки с данными записью
     */
    onDataCardLoad: function (cfg) {
        var view = this.getView();

        // карточка загрузила данные и мы их обновляем во viewer
        view.setClusterCount(cfg.clusterCount);
        view.setDataRecord(cfg.dataRecord);
        view.setReferenceRelations(cfg.referenceRelations);
        view.setTimeIntervalDate(cfg.timeIntervalDate);
        view.setTimeInterval(cfg.timeInterval);
        view.setClassifierNodes(cfg.classifierNodes);
        view.setOriginRecords(null);
        view.setRelationsDigest(null);

        this.initCards();

        view.fireEvent('datacardload', cfg);

        this.showDataViewer();
    },

    /**
     * Обрабатывает событие загрузки данных карточки с обратными ссылками
     */
    onLoadBackRelCard: function (card, relationsDigest) {
        var view = this.getView();

        view.setRelationsDigest(relationsDigest);
    },

    /**
     * Обрабатывает событие загрузки данных карточки с исходных записей
     */
    onLoadOriginCard: function (card, originRecords) {
        var view = this.getView();

        view.setOriginRecords(originRecords);
    },

    mergeDataRecord: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            metaRecord = view.getMetaRecord();

        view.fireEvent('merge', dataRecord, metaRecord);
    },

    redirectToClusters: function () {
        var view = this.getView(),
            etalonId = view.getEtalonId(),
            metaRecord = view.getMetaRecord(),
            entityName = metaRecord.get('name'),
            entityType = metaRecord.getType(),
            clusterCount = view.getClusterCount();

        if (clusterCount > 0) {
            Unidata.util.Router
                .setToken('main', {section: 'cluster'})
                .setToken('cluster', {
                    etalonId: etalonId,
                    entityName: entityName,
                    entityType: entityType
                });
        }
    },

    manualMergeDataRecord: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            metaRecord = view.getMetaRecord(),
            dataRecordBundle,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle;

        dataRecordBundle = DataRecordBundleUtil.buildDataRecordBundle({
            dataRecord: dataRecord
            // TODO: bundle может состоять из нескольких объектов
        });

        view.fireEvent('datarecordmanualmerge', dataRecordBundle, metaRecord);
    },

    updateTimeIntervalStore: function (timeIntervalStore) {
        var viewModel = this.getViewModel(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            someIntervalNeedAccept = false;

        timeIntervalStore.each(function (record) {
            var contributors;

            contributors = record.contributors().getRange();
            // признак того что какой-либо интервал был удален
            someIntervalNeedAccept = Ext.Array.some(contributors, function (contributor) {
                var result;

                result = contributor.status === viewerConst.ETALON_STATUS_INACTIVE &&
                    contributor.approval === viewerConst.ETALON_APPROVAL_PENDING;

                return result;
            });
        });

        viewModel.set('someIntervalNeedAccept', someIntervalNeedAccept);
        viewModel.notify();
    },

    onSelectTimeInterval: function (selectModel, timeInterval) {
        var viewModel = this.getViewModel(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            intervalNeedAccept = false,
            intervalDeleted = false,
            contributors;

        if (timeInterval) {
            contributors = timeInterval.contributors().getRange();

            intervalNeedAccept = Ext.Array.some(contributors, function (contributor) {
                return contributor.approval === viewerConst.ETALON_APPROVAL_PENDING;
            });

            intervalDeleted = Ext.Array.some(contributors, function (contributor) {
                var result;

                result = contributor.status === viewerConst.ETALON_STATUS_INACTIVE &&
                    contributor.approval === viewerConst.ETALON_APPROVAL_PENDING;

                return result;
            });

            if (timeInterval.get('active') === true) {
                this.getView().setSelectedInactiveTimeInterval(false);
            } else {
                this.getView().setSelectedInactiveTimeInterval(true);
            }

        }

        viewModel.set('intervalNeedAccept', intervalNeedAccept);
        viewModel.set('intervalDeleted', intervalDeleted);
        viewModel.notify();
    },

    updateDataRecord: function (dataRecord, oldDataRecord) {
        var view = this.getView(),
            status = dataRecord.get('status'),
            oldStatus,
            etalonId = dataRecord.get('etalonId');

        oldStatus = oldDataRecord ? oldDataRecord.get('status') : '';

        if (status !== oldStatus) {
            view.fireEvent('datarecordstatuschanged', etalonId, status);
        }
    },

    onDataRecordDeclineSuccess: function () {
        var view = this.getView(),
            args;

        args = Array.prototype.slice.call(arguments, 0, -1);
        args.unshift('declinesuccess');

        view.fireEvent.apply(view, args);
    },

    onDataCardLocked: function () {
        var viewModel = this.getViewModel(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        viewModel.addViewerStatus(viewerConst.VIEWER_STATUS_DATACARDLOCKED);
    },

    onDataCardUnLocked: function () {
        var viewModel = this.getViewModel(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        viewModel.removeViewerStatus(viewerConst.VIEWER_STATUS_DATACARDLOCKED);
    },

    onClassifierNodesdLoad: function (classifierNodes) {
        var view = this.getView();

        view.setClassifierNodes(classifierNodes);

        this.showDataViewer();
    },

    onDataCardSaveSuccess: function (dataRecord) {
        var view = this.getView(),
            originCard = view.originCard,
            dataCard = view.dataCard;

        originCard.resetCachedData();
        dataCard.reset();
        view.fireEvent('savesuccess', dataRecord);
    },

    onDataCardSaveFailure: function () {
        var view = this.getView(),
            originCard = view.originCard;

        originCard.resetCachedData();
        view.fireEvent('savefailure');
    },

    onEtalonDetached: function (etalonId) {
        var view = this.getView();

        view.dataCard.refreshDataCard();

        Unidata.util.Router
            .setToken('main', {section: 'data', reset: true})
            .setToken('etalon', {
                etalonId: etalonId
            });
    }
});
