/**
 * Контроллер для контейнера, реализующего отображение связи типа reference
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.contains.ContainsController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.contains',

    /**
     * Обновляет состояние "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel(),
            carousel  = this.lookupReference('carouselRelation'),
            items     = null;

        viewModel.set('readOnly', readOnly);

        if (carousel) {
            items = carousel.getCarouselItems();
        }

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    },

    /**
     * Загружает metaRelationRecord и возвращает промис
     * @returns {*|null|promise.promise|jQuery.promise|Ext.promise|d.promise}
     */
    getMetaRelationRecord: function () {

        if (!this.promiseMetaRelationRecord) {
            this.promiseMetaRelationRecord = Unidata.util.api.MetaRecord.getMetaRecord({
                entityName: this.getView().getMetaRelation().get('toEntity'),
                entityType: 'Entity'
            });
        }

        return this.promiseMetaRelationRecord;
    },

    /**
     * При разворачивании панельки - загружаем связи
     */
    onBeforeExpandPanel: function () {
        this.loadRelationRecords().done();

        return true;
    },

    /**
     * Создание новой связи
     *
     * @param button
     * @param e
     */
    onCreateRelationClick: function (button, e) {
        var me           = this,
            carousel     = this.lookupReference('carouselRelation'),
            view         = this.getView();

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        if (!view.expanded) {
            view.expand();
        }

        // добавляем связь только после загрузки имеющихся связей
        this.loadRelationRecords().then(function () {
            me.addCarouselItem(null, null).then(function () {
                carousel.moveLast();
            });
        }).done();
    },

    /**
     * Загружает связи-включения и инициализирует их отрисовку.
     *
     * Возвращает промис, который резолвится когда связи загружены,
     * если не загружены, то начинает загрузку и потом резолвит.
     *
     * Если связи загружен, то сразу резолвит.
     *
     * @returns {null|Ext.promise|Ext.promise.Promise|*}
     */
    loadRelationRecords: function () {
        var view     = this.getView(),
            deferred = Ext.create('Ext.Deferred'),
            loadingPromise;

        if (view.relationsLoaded) {
            // связи загружены
            deferred.resolve();
        } else {
            if (view.relationsLoading) {
                // связи не загружены, но грузятся
                loadingPromise = this.relationsLoadingPromise;
            } else {
                // связи не загружены и не грузятся
                loadingPromise = this.loadRelationRecordsForced();
            }

            // после загрузки связей
            loadingPromise.then(function () {
                deferred.resolve();
            });
        }

        return deferred.promise;
    },

    /**
     * Принудительно загружает связи-включения и инициализирует их отрисовку
     * Всё, что было отрисовано ранее - очищается
     */
    loadRelationRecordsForced: function () {
        var me               = this,
            deferred         = Ext.create('Ext.Deferred'),
            view             = this.getView(),
            metaRelation     = view.getMetaRelation(),
            metaRelationName = metaRelation.get('name'),
            dataRecord       = view.getDataRecord(),
            etalonId         = dataRecord.get('etalonId'),
            carousel         = this.lookupReference('carouselRelation'),
            drafts           = view.getDrafts(),
            operationId      = view.getOperationId(),
            promiseRelationTimeline;

        view.relationsLoaded = false;
        view.relationsLoading = true;
        this.relationsLoadingPromise = deferred.promise;

        carousel.removeAllCarouselItems();
        view.setLoading(true);

        promiseRelationTimeline = Unidata.util.api.RelationContains.loadRelationTimeline(
            metaRelationName,
            etalonId,
            null, // dateFrom,
            null, // dateTo,
            drafts,
            operationId
        );

        promiseRelationTimeline.then(
            function (relations) {
                me.onSuccessTimelineLoad(relations);
                view.relationsLoaded = true;
                deferred.resolve();
            },
            function () {
                me.onFailureTimelineLoad();
                Unidata.showError(Unidata.i18n.t('relation>includeTypeError'));

                if (!view.relationsLoaded) {
                    view.collapse();
                }

                deferred.reject();
            }
        ).always(function () {
            view.relationsLoading = false;
        }).done();

        return deferred.promise;
    },

    /**
     * После успешной загрузки связей-включений - отрисовываем их
     * @param relations
     */
    onSuccessTimelineLoad: function (relations) {
        var me = this,
            view = this.getView(),
            promises = [];

        Ext.Array.each(relations, function (relation) {
            var etalonId = relation.get('etalonId'),
                timeIntervalStore = relation.timeline();

            // фильтруем таймлайн
            Unidata.util.api.RelationContains.applyFilterToTimeIntervalStore(timeIntervalStore);

            promises.push(
                me.addCarouselItem(etalonId, timeIntervalStore)
            );
        });

        Ext.Deferred.all(promises).always(function () {
            view.setLoading(false);
        }).done();
    },

    onFailureTimelineLoad: function () {
        this.getView().setLoading(false);
    },

    /**
     * Добавляет включение в карусель
     * @param etalonId
     * @param timeIntervalStore
     */
    addCarouselItem: function (etalonId, timeIntervalStore) {
        var me           = this,
            view         = this.getView(),
            drafts       = view.getDrafts(),
            operationId  = view.getOperationId(),
            deferred     = Ext.create('Ext.Deferred'),
            readOnly     = view.getReadOnly(),
            metaRelation = view.getMetaRelation(),
            metaRecord   = view.getMetaRecord(),
            dataRecord   = view.getDataRecord(),
            carousel     = this.lookupReference('carouselRelation'),
            newContainsRecordPanel;

        this.getMetaRelationRecord().then(function (metaRelationRecord) {
            var validityPeriod = metaRelationRecord.get('validityPeriod'),
                extraParams = {
                    drafts: drafts
                },
                minDate,
                maxDate;

            if (operationId) {
                extraParams.operationId = operationId;
            }

            minDate = Unidata.util.ValidityPeriod.getMinDate(validityPeriod);
            maxDate = Unidata.util.ValidityPeriod.getMaxDate(validityPeriod);

            if (!timeIntervalStore) {
                timeIntervalStore = Ext.create('Ext.data.Store', {
                    model: 'Unidata.model.data.TimeInterval',
                    sorters: [{
                        property: 'dateFrom',
                        direction: 'ASC'
                    }],
                    filters: [],
                    data: [
                        {
                            dateFrom: null, //dataRecord.get('validFrom'),
                            dateTo: null //dataRecord.get('validTo')
                        }
                    ],
                    proxy: {
                        type: 'rest',
                        extraParams: extraParams,
                        reader: {
                            rootProperty: 'content.timeline'
                        }
                    }
                });
            }

            newContainsRecordPanel = Ext.create(
                'Unidata.view.steward.relation.contains.ContainsRecord',
                {
                    drafts: drafts,
                    operationId: operationId,
                    metaRecord: metaRecord,
                    dataRecord: dataRecord,
                    metaRelation: metaRelation,
                    metaRelationRecord: metaRelationRecord,
                    etalonId: etalonId,
                    collapsed: false,
                    readOnly: readOnly,
                    viewModel: {
                        stores: {
                            timeIntervalStore: timeIntervalStore
                        }
                    },
                    minDate: null, //minDate,
                    maxDate: null // maxDate
                }
            );

            me.attachEventHandler(newContainsRecordPanel);
            carousel.addCarouselItem(newContainsRecordPanel);
            deferred.resolve();
        }).done();

        return deferred.promise;
    },

    /**
     * При перерисовке включения, смотрим, есть ли ошибки,
     * и если есть - показываем индикатор
     */
    onRelationRecordDraw: function () {
        this.redrawDqErrorsIndicator();
    },

    redrawDqErrorsIndicator: function () {
        var viewModel     = this.getViewModel(),
            carousel      = this.lookupReference('carouselRelation'),
            carouselItems,
            anyDqErrors   = false;

        if (carousel) {
            carouselItems = carousel.getCarouselItems();
            carouselItems.each(function (item) {
                if (item.getViewModel().get('dqErrors')) {
                    anyDqErrors = true;

                    return false;
                }
            });
        }

        viewModel.set('anyDqErrors', anyDqErrors);
    },

    /**
     * Производит настройку  обработчиков событий для связей
     *
     * @param panel
     */
    attachEventHandler: function (panel) {
        var carousel = this.lookupReference('carouselRelation');

        // при отрисовке включения
        panel.on('relationrecorddraw', this.onRelationRecordDraw, this);

        // здесь мы еще можем запретить удалять запись возвратив false
        panel.on('beforeremove', function () {
        }, this);

        // обработчик удаления связи
        panel.on('remove', function () {
            // удаляем панельку из карусельки
            carousel.removeCarouselItem(panel);
            this.redrawDqErrorsIndicator();
        }, this);

    },

    onCarouselPanelItemCountChanged: function (carouselPanel, count) {
        var view = this.getView();

        view.setCarouselItemCount(count);
    },

    refreshTitle: function () {
        var view              = this.getView(),
            metaRelation      = view.getMetaRelation(),
            displayName       = metaRelation.get('displayName'),
            carouselItemCount = view.getCarouselItemCount(),
            title;

        displayName = Ext.String.htmlEncode(displayName);

        if (carouselItemCount > 0) {
            title = Ext.String.format('{0} <span class = "un-simple-title-text">(' + Unidata.i18n.t('relation>ofRecords') + ': {1})</span>', displayName, carouselItemCount);
        } else {
            title = displayName;
        }

        view.setTitle(title);
    },

    updateCarouselItemCount: function () {
        // делаем не через viewModel, чтобы не было проблем из-за наследования от AbstractAttributeTable
        this.refreshTitle();
    }
});
