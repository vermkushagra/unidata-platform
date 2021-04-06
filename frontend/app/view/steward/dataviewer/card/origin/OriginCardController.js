Ext.define('Unidata.view.steward.dataviewer.card.origin.OriginCardController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewer.origincard',

    originStoreInited: false,
    originStore: false,

    init: function () {
        var me = this,
            view = me.getView();

        view.on('render', me.onComponentRender, me, {single: true});
    },

    /**
     * Инициализация стора для хранения ориджинов
     */
    initOriginsStore: function () {
        var view,
            pagingToolbar,
            store;

        if (this.originStoreInited) {
            return;
        }

        view = this.getView();
        pagingToolbar = view.pagingToolbar;

        store = Unidata.util.api.OriginRecord.createStore({
            paged: true,
            drafts: view.getDrafts(),
            operationId: view.getOperationId(),
            etalonId: view.getEtalonId(),
            date: view.getTimeIntervalDate()
        });

        this.originStore = store;

        pagingToolbar.setStore(store);

        store.on('load', this.onOriginsLoad, this);
        store.on('beforeload', function () {
            view.fireEvent('refreshstart');
        }, this);

        pagingToolbar.doRefresh();

        this.originStoreInited = true;
    },

    onComponentRender: function () {
        this.configListeners();
    },

    updateReadOnly: function () {
        // TODO: Sergey Shishigin - разребитесь нужен RO или нет
    },

    /**
     * При загрузке ориджинов в стор
     *
     * @param store
     * @param originRecords
     */
    onOriginsLoad: function (store, originRecords) {
        var me = this,
            view = this.getView(),
            promises = [];

        view.setOriginRecords(originRecords);

        Ext.Array.each(originRecords, function (originRecord) {
            var promise;

            promise = Unidata.view.steward.dataviewer.DataViewerLoader.loadClassifierNodes(originRecord);

            promises.push(promise);
        });

        Ext.Deferred.all(promises).then(
            function (originRecordsClassifierNodes) {
                view.setOriginsClassifierNodes(originRecordsClassifierNodes);
            }
        ).then(
            function () {
                view.fireEvent('load', view, originRecords);
                me.drawOriginItems();
            }
        ).otherwise(
            function () {
                throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:originRecords')}));
            }
        ).always(
            function () {
                view.fireEvent('refreshend');
            }
        ).done();
    },

    /**
     * Построение origin картоки
     *
     * @param {boolean} [reload]
     */
    displayOriginCard: function (reload) {
        var view = this.getView(),
            timeIntervalStore = view.getTimeIntervalStore(),
            dataView = this.getTimeIntervalContainer().getDataView();

        dataView.setStore(timeIntervalStore);
        this.configTimeIntervalDateView();

        this.initOriginsStore();

        if (reload && !this.originStore.isLoading()) {
            view.pagingToolbar.moveFirst();
        }
    },

    drawOriginItems: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            originRecords = view.getOriginRecords(),
            originsClassifierNodes = view.getOriginsClassifierNodes(),
            originItems;

        originItems = this.buildOriginItems(metaRecord, originRecords, originsClassifierNodes);
        view.originsContainer.removeAll();
        view.originsContainer.add(originItems);
    },

    /**
     * Настроить компонент TimeIntervalDateView
     * В том числе произвести инициализацию выбора текущего time interval
     */
    configTimeIntervalDateView: function () {
        var view = this.getView(),
            dataView = this.getTimeIntervalContainer().getDataView(),
            timeIntervalDate = view.getTimeIntervalDate(),
            me = this;

        this.deleteTimeIntervalSelectListener();

        if (!dataView.rendered) {
            dataView.on('render', function () {
                dataView.findAndSelectTimeInterval(timeIntervalDate);
                me.setTimeIntervalSelectListener();
            }, {
                single: true
            });
        } else {
            dataView.findAndSelectTimeInterval(timeIntervalDate);
            this.setTimeIntervalSelectListener();
        }
    },

    /**
     * Генерация контейнеров с origin контейнерами
     *
     * @param originRecords
     * @param metaRecord
     * @returns {*|Ext.promise.Promise|Array|{}}
     */
    buildOriginItems: function (metaRecord, originRecords, originsClassifierNodes) {
        var me = this,
            originItems = [];

        Ext.Array.each(originRecords, function (originRecord, index) {
            var originItem,
                originClassifierNodes = originsClassifierNodes[index];

            originItem = me.buildOriginItem(originRecords, metaRecord, originRecord, originClassifierNodes);
            originItem.on('detachorigin', me.onDetachOrigin, me);

            originItems.push(originItem);
        });

        return originItems;
    },

    buildOriginItem: function (originRecords, metaRecord, originRecord, classifierNodes) {
        var originStore = this.originStore,
            totalCount = originStore.getTotalCount(),
            OriginSystemAttributeEntityUtil = Unidata.util.OriginSystemAttributeEntity,
            originItem,
            originItemCfg,
            systemAttrEntity;

        systemAttrEntity = OriginSystemAttributeEntityUtil.buildSystemEntityParams(originRecord);

        originItemCfg = {
            detachAllowed: totalCount > 1,
            metaRecord: metaRecord,
            originDataRecord: originRecord
        };

        if (totalCount > 1) {
            originItemCfg = Ext.apply(originItemCfg, {
                flex: 1
            });
        }

        originItem = Ext.create('Unidata.view.steward.dataviewer.card.origin.OriginItem', originItemCfg);

        originItem.systemAttributeEntity.setEntityData(systemAttrEntity.metaRecord, systemAttrEntity.dataRecord);
        originItem.systemAttributeEntity.displayDataEntity();

        originItem.dataEntity.setEntityData(metaRecord, originRecord, classifierNodes);
        originItem.dataEntity.displayDataEntity();

        return originItem;
    },

    onDetachOrigin: function (container, originId) {
        var me = this,
            view = this.getView(),
            promise;

        promise = Unidata.util.api.OriginRecord.detachOrigin(originId);
        promise
            .then(function (detachedEtalonId) {
                view.fireEvent('etalondetached', detachedEtalonId);

                // нужна задержка перед началом отрисовки,
                // иначе из-за загрузки новой карточки, не отображается тайминтервал
                Ext.defer(me.displayOriginCard, 200, me, [true]);
            })
            .done();
    },

    getTimeIntervalContainer: function () {
        var view = this.getView(),
            headerBar = view.headerBar;

        return headerBar.lookupReference('timeIntervalContainer');
    },

    configListeners: function () {
        //this.setTimeIntervalSelectListener();
    },

    deleteTimeIntervalSelectListener: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView = timeIntervalContainer.getDataView();

        dataView.removeListener('select', this.onTimeIntervalDataViewSelect, this);
    },

    setTimeIntervalSelectListener: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView = timeIntervalContainer.getDataView();

        dataView.on('select', this.onTimeIntervalDataViewSelect, this);
    },

    onTimeIntervalDataViewSelect: function (selectModel, timeInterval) {
        this.reloadAfterTimeIntervalSelect(timeInterval);
    },

    reloadAfterTimeIntervalSelect: function (timeInterval) {
        var view = this.getView(),
            timeIntervalDate;

        timeIntervalDate = Unidata.view.component.timeinterval.TimeIntervalDataView.getTimeIntervalDate(timeInterval);
        view.setTimeIntervalDate(timeIntervalDate);

        this.displayOriginCard(true);
    },

    updateTimeIntervalDate: function (timeIntervalDate) {
        if (this.originStore) {
            timeIntervalDate = timeIntervalDate || null;
            this.originStore.getProxy().setDate(timeIntervalDate);
        }
    },

    resetCachedData: function () {
        var view = this.getView();

        view.setOriginRecords(null);
        view.setOriginsClassifierNodes(null);
    }
});
