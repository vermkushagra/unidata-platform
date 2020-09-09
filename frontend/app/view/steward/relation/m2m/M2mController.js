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
            saveAtomic = view.getSaveAtomic(),
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
            relationName: view.getRelationName(), // QA использует имя связи для поиска
            saveAtomic: saveAtomic,
            listeners: {
                m2mdirtychange: this.onM2mDirtyChange.bind(this),
                m2mvaliditychange: this.onM2mValidityChange.bind(this),
                removem2m: this.onRemoveM2m.bind(this),
                timeintervalcheckstart: this.tiIntersectTypeStartCheck.bind(this),
                timeintervalcheckstop: this.tiIntersectTypeStopCheck.bind(this)
            }
        });

        relationView.on('changerelationcount', this.onChangeRelationCount, this);

        view.relationView = relationView;
        view.relationViewType = Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL;

        view.add(relationView);
    },

    onRemoveM2m: function (panel, dataRelationRecord) {
        var view = this.getView(),
            removedRelationReferences;

        if (!dataRelationRecord.phantom) {
            removedRelationReferences = view.getRemovedRelationReferences();
            removedRelationReferences.push(dataRelationRecord);
        }

        this.checkPanelsDirty();

        view.setValid(this.checkPanelsValid());
    },

    onM2mDirtyChange: function () {
        this.checkPanelsDirty();
    },

    onM2mValidityChange: function (self, valid) {
        if (!valid) {
            view.setValid(!valid);
        } else {
            valid = this.checkPanelsValid();
            view.setValid(valid);
        }
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

    refreshTitle: function () {
        this.getView().setTitle(this.buildTitle());
    },

    /**
     * Обработка события клика по кнопке создания новой связи
     *
     * @param button
     * @param e
     */
    onCreateRelationClick: function (button, e) {
        var me = this,
            view = this.getView(),
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
            me.checkPanelsDirty();
            me.checkPanelsValid();
        }).done();
    },

    /**
     * Обработка события клика по кнопке переключения представления вида связи
     *
     * @param button
     * @param e
     */
    onToggleRelationViewTypeButtonClick: function () {
        if (this.toggleRelationView()) {
            this.displayRelation();
        }
    },

    /**
     * Переключает представление связи: табличный / режим редактирования
     */
    toggleRelationView: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord();

        view.expand();

        if (view.relationViewType === Unidata.view.steward.relation.RelationViewType.m2m.CAROUSEL) {
            if (!this.checkPanelsDirty() && !dataRecord.phantom) {
                this.initTableRelationView();
            } else {
                Unidata.showWarning(Unidata.i18n.t('relation>m2m>recordWasEditCantSwitchViewType'));

                return false;
            }
        } else {
            this.initCarouselRelationView();
        }

        this.updateToggleRelationViewButtonIconCls();

        return true;
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
    buildTitle: function () {
        var view = this.getView(),
            metaRelation = view.getMetaRelation(),
            dataRelation = view.getDataRelation(),
            count = 0,
            required      = metaRelation.get('required'),
            displayName   = metaRelation.get('displayName'),
            valid         = view.getValid(),
            invalidIcon,
            title;

        if (dataRelation) {
            count = dataRelation.length;
        }

        displayName = Ext.String.htmlEncode(displayName);

        if (required) {
            displayName = displayName + '<span class="un-dataentity-relation-required">*</span>';
        }

        if (count > 0) {
            title = Ext.String.format('{0} <span class = "un-simple-title-text">(' + Unidata.i18n.t('relation>ofRecords') + ': {1})</span>',
                displayName,
                count);
        } else {
            title = displayName;
        }

        if (!valid) {
            invalidIcon = '<span class="un-dataentity-relation-notification-title"><object data-ref="notification-icon" class="un-dataentity-relation-notification-icon" type="image/svg+xml" data="resources/icons/icon-notification-circle.svg"></object></span>';
            title = invalidIcon + title;
        }

        return title;
    },

    /**
     * Обработчик изменения состава записей (добавление / удаление)
     */
    onChangeRelationCount: function () {
        this.refreshTitle();
    },

    checkPanelsDirty: function (silent) {
        var view = this.getView(),
            toUpdate,
            toDelete,
            dirty;

        silent = Ext.isBoolean(silent) ? silent : false;

        toUpdate = this.getRelationReferenceToUpdate();
        toDelete = this.getRelationReferenceToDelete();

        dirty = Boolean(toUpdate.length) || Boolean(toDelete.length);

        if (!silent) {
            view.fireEvent('m2mdirtychange', view, dirty);
        }

        return dirty;
    },

    checkRelationReferencesExist: function () {
        var relationReferences = this.getRelationReferences();

        return relationReferences.length > 0;
    },

    checkPanelsValid: function () {
        var view = this.getView(),
            metaRelation = view.getMetaRelation(),
            valid = true,
            relationView = view.relationView,
            required = metaRelation.get('required');

        if (required) {
            valid = this.checkRelationReferencesExist() && valid;
        }

        if (relationView instanceof Unidata.view.steward.relation.m2m.carousel.M2mCarousel) {
            valid = relationView.checkPanelsValid() && valid;
        }

        view.setValid(valid);

        return valid;
    },

    getRelationReferenceToUpdate: function () {
        var relationReferences = this.getRelationReferences();

        return Ext.Array.filter(relationReferences, function (relationReference) {
            return relationReference.phantom || relationReference.checkDirty();
        });
    },

    getRelationReferenceToDelete: function () {
        var view = this.getView(),
            removedRelationReferences,
            relationReferenceToDelete;

        removedRelationReferences = view.getRemovedRelationReferences();
        relationReferenceToDelete = Ext.Array.map(removedRelationReferences, function (relationReference) {
            return Ext.create('Unidata.model.data.RelationReferenceDelete', {
                etalonId: relationReference.get('etalonId'),
                relName: relationReference.get('relName')
            });
        });

        return relationReferenceToDelete;
    },

    getRelationReferences: function () {
        var view = this.getView(),
            relationView = view.relationView;

        return relationView.getRelationReferences();
    },

    clearRemovedRelationReferences: function () {
        view.setRemovedRelationReferences(null);
    },

    updateValid: function (valid) {
        var view = this.getView(),
            invalidCls;

        invalidCls = Ext.String.format('{0}-{1}', view.cls, 'invalid');

        this.refreshTitle();

        if (valid) {
            view.removeCls(invalidCls);
        } else {
            view.addCls(invalidCls);
        }
    },

    reset: function () {
        var view = this.getView();

        view.setRemovedRelationReferences([]);
    },

    tiIntersectTypeStartCheck: function () {
        var view = this.getView();

        view.setLoading(Unidata.i18n.t('relation>m2m>timeIntervalValidationWaitMsg'));
    },

    tiIntersectTypeStopCheck: function () {
        var view = this.getView();

        view.setLoading(false);
    }
});
