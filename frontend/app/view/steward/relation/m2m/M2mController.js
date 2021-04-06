/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.M2mController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.m2mrelation',

    init: function () {
        var me = this,
            promise;

        this.callParent(arguments);

        // загружаем все записи связи
        promise = this.loadRelationBulk();
        promise
            .then(
                function () {
                    me.initDefaultRelationView();

                    // отображаем связи
                    me.displayRelation();
                },
                function () {
                }
            )
            .done();
    },

    onViewRender: function () {
        this.updateToggleRelationViewButtonIconCls();
    },

    /**
     * Инициализирует компонент дефолтным видом связи
     */
    initDefaultRelationView: function () {
        var view = this.getView(),
            relationViewTypes;

        relationViewTypes = [
            Unidata.view.steward.relation.RelationViewType.m2m.TABLE,
            Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL
        ];

        // по умолчанию показываем табличный вид
        if (!Ext.Array.contains(relationViewTypes, view.relationViewType)) {
            view.relationViewType = Unidata.view.steward.relation.RelationViewType.m2m.TABLE;
        }

        if (view.relationViewType === Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL) {
            this.initCarouselRelationView();
        } else {
            this.initTableRelationView();
        }

        this.updateToggleRelationViewButtonIconCls();
    },

    /**
     * Инициализирует компонет для отображения свзи в табличном виде
     */
    initTableRelationView: function () {
        var view = this.getView(),
            relationView;

        this.destroyCurrentRelationView();

        relationView = Ext.create('Unidata.view.steward.relation.m2m.table.M2mTable', {
            drafts: view.getDrafts(),
            operationId: view.getOperationId(),
            metaRecord: view.getMetaRecord(),
            dataRecord: view.getDataRecord(),
            metaRelation: view.getMetaRelation(),
            dataRelation: view.getDataRelation(),
            readOnly: view.getReadOnly(),
            relationName: view.getRelationName() // QA использует имя связи для поиска
        });

        view.relationView = relationView;
        view.relationViewType = Unidata.view.steward.relation.RelationViewType.m2m.TABLE;

        view.add(relationView);
    },

    /**
     * Инициализирует компонет для отображения свзи в виде карусельки
     */
    initCarouselRelationView: function () {
        var view = this.getView(),
            relationView;

        this.destroyCurrentRelationView();

        relationView = Ext.create('Unidata.view.steward.relation.m2m.carousel.M2mCarousel', {
            drafts: view.getDrafts(),
            operationId: view.getOperationId(),
            metaRecord: view.getMetaRecord(),
            dataRecord: view.getDataRecord(),
            metaRelation: view.getMetaRelation(),
            dataRelation: view.getDataRelation(),
            readOnly: view.getReadOnly(),
            relationName: view.getRelationName() // QA использует имя связи для поиска
        });

        relationView.on('changerelationcount', this.onChangeRelationCount, this);

        view.relationView = relationView;
        view.relationViewType = Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL;

        view.add(relationView);
    },

    /**
     * Удаляет текущий вид связи
     */
    destroyCurrentRelationView: function () {
        var view = this.getView(),
            relationView = view.relationView;

        if (relationView) {
            view.relationView = null;

            view.remove(relationView);
            Ext.destroy(relationView);
        }
    },

    /**
     * Отображает связи
     *
     * @returns {null|promise|Ext.promise|*|Ext.promise.Promise}
     */
    displayRelation: function () {
        var deferred,
            view = this.getView();

        deferred = Ext.create('Ext.Deferred');

        view.setLoading(true);

        // хитрая конструкция т.к. setLoading не сработает иначе
        Ext.defer(function () {
            view.relationView.displayRelations();

            view.setLoading(false);

            this.refreshTitle();

            deferred.resolve();
        }, 10, this);

        return deferred.promise;
    },

    /**
     * Обновляет состояние "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            relationView = view.relationView;

        viewModel.set('readOnly', readOnly);

        if (relationView) {
            relationView.setReadOnly(readOnly);
        }
    },

    /**
     * Обработчик указания metaRelation из секции config
     *
     * @param metaRelation
     */
    updateMetaRelation: function () {
        this.refreshTitle();
    },

    /**
     * Обработка события клика по кнопке создания новой связи
     *
     * @param button
     * @param e
     */
    onCreateRelationClick: function (button, e) {
        var view = this.getView(),
            promise,
            deferred;

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        // переключаемся в режим отображения каруселью, если еще не в нем
        if (view.relationViewType !== Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL) {
            this.toggleRelationView();
            // при переключении вида отрисовываем связь с нуля
            promise = this.displayRelation();
        } else {
            deferred = new Ext.Deferred();
            deferred.resolve();
            promise = deferred.promise;
        }

        if (view.getCollapsed()) {
            view.expand();
        }

        promise.then(function () {
            view.relationView.createRelationTo(false);
        }).done();
    },

    /**
     * Обработка события клика по кнопке переключения представления вида связи
     *
     * @param button
     * @param e
     */
    onToggleRelationViewTypeButtonClick: function () {
        this.toggleRelationView();

        this.displayRelation();
    },

    /**
     * Переключает представление связи: табличный / режим редактирования
     */
    toggleRelationView: function () {
        var view = this.getView();

        view.expand();

        if (view.relationViewType === Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL) {
            this.initTableRelationView();
        } else {
            this.initCarouselRelationView();
        }

        this.updateToggleRelationViewButtonIconCls();
    },

    /**
     * Переключает иконку для кнопки в зависимости от представления связи: табличный / режим редактирования
     */
    updateToggleRelationViewButtonIconCls: function () {
        var view = this.getView(),
            tableViewCls = 'icon-grid',       // иконка обозначающий вид табличный вид отображения
            carouselViewCls = 'icon-icons2',  // иконка обозначающая вид отображения в карусели
            iconCls = tableViewCls,
            toggleButton = view.toggleRelationViewTypeButton;

        // иконка должна отображать вид связи на который будем переключаться
        if (view.relationViewType === Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL) {
            iconCls = carouselViewCls;
        }

        if (toggleButton) {
            toggleButton.setIconCls(iconCls);
        }
    },

    /**
     * Загружает все записи связи
     *
     * @returns {null|Ext.promise|*|Ext.promise.Promise}
     */
    loadRelationBulk: function () {
        var me = this,
            view = me.getView(),
            drafts = view.getDrafts(),
            operationId = view.getOperationId(),
            dataRecord = view.getDataRecord(),
            metaRelation = view.getMetaRelation(),
            deferred = Ext.create('Ext.Deferred'),
            promise,
            relationName,
            etalonId,
            dateFrom,
            dateTo;

        view.setLoading(true);

        relationName = metaRelation.get('name');
        etalonId     = dataRecord.get('etalonId');
        dateFrom     = dataRecord.get('validFrom');
        dateTo       = dataRecord.get('validTo');

        promise = Unidata.util.api.RelationM2m.getRelationBulk(
            relationName, etalonId, dateFrom, dateTo, drafts, operationId
        );
        promise
            .then(
                function (dataRelation) {
                    me.onSuccessRelationLoad(dataRelation);

                    deferred.resolve();
                },
                function () {
                    me.onFailureRelationLoad();

                    deferred.reject();
                }
            )
            .always(function () {
                view.setLoading(false);
            })
            .done();

        return deferred.promise;
    },

    /**
     * Обработка успешной загрузки данных
     *
     * @param data
     */
    onSuccessRelationLoad: function (dataRelation) {
        var me = this,
            view = me.getView();

        view.setDataRelation(dataRelation);
    },

    /**
     * Обработка неудачной загрузки данных по связям
     */
    onFailureRelationLoad: function () {
        // TODO: необходимо реализовать показ панельки с возможностью запустить отображение записей связи снова
        Unidata.showError(Unidata.i18n.t('relation>loadManyToManyRelationError'));
    },

    /**
     * Обновляет заголовок панели
     */
    // TODO: вынести метод refreshTitle в отдельный модуль для Contains, M2M, CarouselAttributeTable, FlatAttributeTable
    refreshTitle: function () {
        var view = this.getView(),
            metaRelation = view.getMetaRelation(),
            displayName = metaRelation.get('displayName'),
            dataRelation = view.getDataRelation(),
            count = 0,
            title;

        if (dataRelation) {
            count = dataRelation.length;
        }

        displayName = Ext.String.htmlEncode(displayName);

        if (count > 0) {
            title = Ext.String.format('{0} <span class = "un-simple-title-text">(' + Unidata.i18n.t('relation>ofRecords') + ': {1})</span>',
                displayName,
                count);
        } else {
            title = displayName;
        }

        view.setTitle(title);
    },

    /**
     * Обработчик изменения состава записей (добавление / удаление)
     */
    onChangeRelationCount: function () {
        this.refreshTitle();
    }
});
