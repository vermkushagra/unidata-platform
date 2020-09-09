Ext.define('Unidata.view.steward.dataviewer.card.origin.OriginCardController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewer.origincard',

    originCount: null,

    init: function () {
        var me = this,
            view = me.getView();

        view.on('render', me.onComponentRender, me, {single: true});
    },

    onComponentRender: function () {
        this.configListeners();
    },

    updateReadOnly: function () {
        // TODO: Sergey Shishigin - разребитесь нужен RO или нет
    },

    /**
     * Загрузка данных для origin карточки
     */
    loadAndDisplayOriginCard: function () {
        var view = this.getView(),
            loadCfg;

        //TODO: fill a correct date
        loadCfg = {
            drafts: view.getDrafts(),
            operationId: view.getOperationId(),
            etalonId: view.getEtalonId(),
            date: view.getTimeIntervalDate()
        };

        Unidata.view.steward.dataviewer.DataViewerLoader.loadOriginWithClassifierNodes(loadCfg)
            .then(function (results) {
                    view.setOriginRecords(results.origins);
                    view.setOriginsClassifierNodes(results.originsClassifierNodes);

                    view.displayOriginCard();

                    view.fireEvent('load', view, results.origins);
                },
                function () {
                    view.fireEvent('refreshend');
                    throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:originRecords')}));
                })
            .done();
    },

    /**
     * Построение origin картоки
     *
     * @param originRecords
     */
    displayOriginCard: function () {
        var view = this.getView(),
            timeIntervalStore = view.getTimeIntervalStore(),
            metaRecord = view.getMetaRecord(),
            originRecords = view.getOriginRecords() || [],
            originsClassifierNodes = view.getOriginsClassifierNodes(),
            dataView = this.getTimeIntervalContainer().getDataView(),
            originItems;

        dataView.setStore(timeIntervalStore);
        this.configTimeIntervalDateView();

        this.originCount = originRecords.length;

        // вырезаем первые 100 записей для отображения
        originRecords = Ext.Array.slice(originRecords, 0, Unidata.Config.getOriginCarouselMaxCount());

        view.suspendLayouts();
        view.carouselPanel.removeAllCarouselItems();
        originItems = this.buildOriginItems(metaRecord, originRecords, originsClassifierNodes);
        view.carouselPanel.addCarouselItem(originItems);
        view.resumeLayouts(true);

        this.updateCarouselLegend();

        view.fireEvent('refreshend');
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
        var view = this.getView(),
            OriginSystemAttributeEntityUtil = Unidata.util.OriginSystemAttributeEntity,
            allowDetachOriginOperation = view.getAllowDetachOriginOperation(),
            detachAllowed = false,
            originItem,
            originItemCfg,
            systemAttrEntity;

        systemAttrEntity = OriginSystemAttributeEntityUtil.buildSystemEntityParams(originRecord);

        if (allowDetachOriginOperation && originRecords.length > 1) {
            detachAllowed = true;
        }

        originItemCfg = {
            flex: 1,
            detachAllowed: detachAllowed,
            metaRecord: metaRecord,
            originDataRecord: originRecord
        };

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

                me.loadAndDisplayOriginCard();
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
        var view = this.getView();

        if (timeInterval.get('active')) {
            this.reloadAfterTimeIntervalSelect(timeInterval);
        } else {
            view.setOriginRecords([]);
            this.displayOriginCard();
        }
    },

    reloadAfterTimeIntervalSelect: function (timeInterval) {
        var view = this.getView(),
            timeIntervalDate;

        timeIntervalDate = Unidata.view.component.timeinterval.TimeIntervalDataView.getTimeIntervalDate(timeInterval);
        view.setTimeIntervalDate(timeIntervalDate);

        view.fireEvent('refreshstart');

        this.loadAndDisplayOriginCard();
    },

    resetCachedData: function () {
        var view = this.getView();

        view.setOriginRecords(null);
        view.setOriginsClassifierNodes(null);
    },

    isDataCached: function () {
        var view = this.getView(),
            cached = false;

        if (view.getOriginRecords() && view.getOriginsClassifierNodes()) {
            cached = true;
        }

        return cached;
    },

    updateCarouselLegend: function () {
        var view = this.getView(),
            carouselPosition = view.carouselPanel.getCarouselPosition(),
            text;

        if (this.originCount > Unidata.Config.getOriginCarouselDisplayCount()) {
            text = Unidata.i18n.t('dataviewer>originCard>carouselLegend', {
                from: carouselPosition.first + 1,
                to: carouselPosition.first + carouselPosition.displayed,
                count: this.originCount
            });
        } else {
            text = Unidata.i18n.t('dataviewer>originCard>carouselLegendShort', {
                count: this.originCount
            });
        }

        view.carouselLegend.update({
            text: text
        });
    }
});
