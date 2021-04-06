/**
 * @author Aleksandr Bavin
 * @date 19.05.2016
 */
Ext.define('Unidata.view.steward.relation.contains.ContainsRecordController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.containsrecord',

    mixins: [
        'Unidata.view.steward.dataentity.mixin.TimeIntervalViewable'
    ],

    timeIntervalStore: null,

    init: function () {
        var view = this.getView();

        this.timeIntervalStore = Unidata.util.api.TimeInterval.createStore();

        view.on('render', this.onComponentRender, this, {single: true});
    },

    onComponentRender: function () {
        this.lookupReference('dqBar').on('changedq', this.onChangeDqErrorName, this);

        this.setTimeIntervalValidityPeriod();

        this.configListeners();
        this.configTimeIntervalDateView();
    },

    onChangeDqErrorName: function (dqName) {
        var view = this.getView();

        if (dqName !== null) {
            view.dataEntity.showDqErrorIndicatorByDqName(dqName);
        } else {
            view.dataEntity.showDqErrorsIndicator();
        }
    },

    getTimeIntervalContainer: function () {
        return this.getView().timeIntervalContainer;
    },

    setTimeIntervalValidityPeriod: function () {
        var view = this.getView(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            datePickerPanel = timeIntervalContainer.getDatePickerPanel(),
            metaRelationRecord = view.getMetaRelationRecord(),
            validityPeriod = metaRelationRecord.get('validityPeriod'),
            minDate,
            maxDate;

        minDate = Unidata.util.ValidityPeriod.getMinDate(validityPeriod);
        maxDate = Unidata.util.ValidityPeriod.getMaxDate(validityPeriod);

        timeIntervalContainer.setMinDate(minDate);
        timeIntervalContainer.setMaxDate(maxDate);

        // если запись новая то проставляем значения по умолчанию after render
        // это необходимо чтобы использовался emptyText для datefields, содержащий крайние даты диапазона
        // TODO: изменить при переделке timeintervals
        datePickerPanel.on('afterrender', function (self) {
            self.setValidFrom(null);
            self.setValidTo(null);
        }, this, {single: true});
    },

    /**
     * Всякие настройки для timeInterval
     */
    configListeners: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer();

        timeIntervalContainer.on('timeintervaldelete', this.onTimeIntervalDelete, this);
        timeIntervalContainer.on('undotimeintervalcreate', this.onUndoTimeIntervalCreate, this);
    },

    onTimeIntervalDelete: function (timeInterval, btn) {
        var dataView = timeInterval.getDataView(),
            etalonId = dataView.getSelection()[0].get('etalonId');

        this.showTimeIntervalDeleteDialog(dataView, etalonId, this.deleteEtalonVersion, btn);
    },

    deleteEtalonVersion: function () {
        var me = this,
            view = this.getView(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            dateView = timeIntervalContainer.getDataView(),
            selection = dateView.getSelection(),
            dataRelationRecord = view.getDataRelationRecord(),
            dataRelationEtalonId = dataRelationRecord.get('etalonId'),
            timeInterval,
            dateFrom,
            dateTo,
            promise;

        function onDeleteTimeIntervalFulfilled () {
            Unidata.showMessage(view.deleteTimeIntervalSuccessText);
            me.reloadTimeline();
        }

        function onDeleteTimeIntervalRejected () {
            Unidata.showError(view.deleteTimeIntervalFailedText);
        }

        if (!selection || selection.length !== 1) {
            return;
        }

        timeInterval = selection[0];

        dateFrom = timeInterval.get('dateFrom');
        dateTo = timeInterval.get('dateTo');

        promise = Unidata.util.api.TimeInterval.deleteRelationTimeInterval(dataRelationEtalonId, dateFrom, dateTo);
        promise.then(onDeleteTimeIntervalFulfilled, onDeleteTimeIntervalRejected);
    },

    configTimeIntervalDateView: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView              = timeIntervalContainer.getDataView(),
            selectModel,
            timeInterval,
            timeIntervalDate      = null,
            view                  = this.getView(),
            etalonId              = view.getEtalonId(),
            me                    = this,
            timeIntervalStore     = this.timeIntervalStore,
            dataRelationRecord;

        function findAndSelectTimeInterval () {
            timeInterval = dataView.findAndSelectTimeInterval(timeIntervalDate);
            selectModel  = dataView.getSelectionModel();
            me.getView().fireEvent('selecttimeinterval', selectModel, timeInterval);
        }

        dataView.setStore(timeIntervalStore);

        me.setTimeIntervalSelectListener();

        // создаем relation если новый
        if (!etalonId) {
            if (Unidata.Config.getTimeintervalEnabled()) {
                timeIntervalContainer.setIsCopyMode(true, true);
            }
            dataRelationRecord = me.createDataRelationRecord();
            view.setDataRelationRecord(dataRelationRecord);
            this.drawRecord();
        }

        if (!dataView.rendered) {
            dataView.on('render', function () {
                me.reloadTimeline();
            }, {
                single: true
            });
        } else {
            me.reloadTimeline();
        }
    },

    setTimeIntervalSelectListener: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView = timeIntervalContainer.getDataView();

        dataView.on('select', this.onTimeIntervalContainsDataViewSelect, this);
    },

    /**
     * Обновляет состояние "только для чтения"
     *
     * @param readOnly
     */
    updateReadOnly: function (readOnly) {
        this.getViewModel().set('readOnly', readOnly);
    },

    /**
     * Сохранение включения
     */
    saveRelationRecord: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            dataEntity = view.dataEntity,
            dataRecord = view.getDataRecord(),
            dataRelationRecord = view.getDataRelationRecord(),
            etalonRecord = dataRelationRecord.getEtalonRecord(),
            etalonId = dataRecord.get('etalonId'),
            timeIntervalContainer = view.timeIntervalContainer,
            validFrom = timeIntervalContainer.getValidFrom(),
            validTo = timeIntervalContainer.getValidTo(),
            isSecurityLabelValid = dataEntity.isSecurityLabelValid(),
            isFieldsValid = dataEntity.isFieldsValid(),
            etalonRecordData,
            proxy;

        if (!isSecurityLabelValid || !isFieldsValid) {
            if (!isFieldsValid) {
                Unidata.showError(view.fieldsInvalidText);
            } else if (!isSecurityLabelValid) {
                dataEntity.showSecurityLabelErrorIndicator();
                Unidata.showError(view.securityInvalidText);
            }

            return;
        }

        view.setLoading(true);
        viewModel.set('allowClickSave', false);
        viewModel.set('allowClickRemove', false);

        // берём данные validFrom/validTo из формы, при создании тайминтервала
        if (validFrom) {
            etalonRecord.set('validFrom', validFrom);
        }

        if (validTo) {
            etalonRecord.set('validTo', validTo);
        }

        etalonRecordData = etalonRecord.getFilteredData({serialize: true, associated: true, persist: true});

        if (dataRelationRecord.phantom) {
            etalonRecordData.etalonId = null;
        }

        // TODO: getData не должен возвращать etalonRecordDataRelationContain
        delete etalonRecordData.etalonRecordDataRelationContain;

        dataRelationRecord.set('etalonRecord', etalonRecordData);

        proxy = dataRelationRecord.getProxy();

        proxy.setUrl(Unidata.Config.getMainUrl() +
            'internal/data/relations/relation/integral/' + etalonId);

        proxy.etalonId = null;
        proxy.dateFrom = null;

        dataRelationRecord.save({
            success: function (record) {
                etalonRecord.dqErrors().setData(record.data.etalonRecord.dqErrors);

                if (etalonId = record.get('etalonId')) {
                    view.setEtalonId(etalonId);
                } else {
                    dataRelationRecord.phantom = true;
                }

                // перезагружаем таймлайн
                if (etalonRecord.dqErrors().getCount() > 0) {
                    this.drawRecord();
                    Unidata.showError(Unidata.i18n.t('relation>saveRelationError'));
                } else {
                    this.reloadTimeline();
                    Unidata.showMessage(Unidata.i18n.t('relation>saveRelationSuccess'));

                    view.fireComponentEvent('checkworkflow');
                }
            },
            failure: function (record, operation) {
                var response = operation.getResponse(),
                    responseData,
                    dqErrors;

                if (response) {
                    responseData = Ext.JSON.decode(response.responseText, true);
                }

                if (responseData && responseData.content && responseData.content.etalonRecord &&
                    responseData.content.etalonRecord.dqErrors) {
                    dqErrors = responseData.content.etalonRecord.dqErrors;

                    if (dqErrors.length) {
                        etalonRecord.dqErrors().setData(dqErrors);

                        this.drawRecord();
                    }
                }

                Unidata.showError(Unidata.i18n.t('relation>saveRelationError'));

                view.setLoading(false);
                viewModel.set('allowClickSave', true);
                viewModel.set('allowClickRemove', true);
            },
            callback: function () {
            },
            scope: this
        });
    },

    /**
     * Перезагрузка таймлайна.
     * Например, после создания нового тайминтервала.
     */
    reloadTimeline: function () {
        var view = this.getView(),
            drafts = view.getDrafts(),
            operationId = view.getOperationId(),
            dateFrom = 'null',
            dateTo = 'null',
            etalonId = view.getEtalonId(),
            timeIntervalStore = this.timeIntervalStore,
            timeIntervalStoreProxy = timeIntervalStore.getProxy();

        if (etalonId) {
            timeIntervalStoreProxy.url = Unidata.Config.getMainUrl() +
                'internal/data/relations/relation/timeline/' + etalonId + '/' + dateFrom + '/' + dateTo;

            timeIntervalStoreProxy.reader.setRootProperty('content.timeline');

            timeIntervalStoreProxy.setExtraParam('drafts', drafts);

            if (operationId) {
                timeIntervalStoreProxy.setExtraParam('operationId', operationId);
            } else {
                delete timeIntervalStoreProxy.extraParams.operationId;
            }

            timeIntervalStore.load({
                scope: this,
                callback: Ext.bind(this.onTimeIntervalReloaded, this)
            });
        }
    },

    /**
     * После перезагрузки тайминтервала, выделяем созданный интервал.
     * @param records
     * @param operation
     * @param success
     */
    onTimeIntervalReloaded: function (records, operation, success) {
        var view = this.getView(),
            timeIntervalContainer = view.timeIntervalContainer,
            dataView = timeIntervalContainer.getDataView();

        if (!success) {
            //TODO: on fail
            return;
        }

        if (Unidata.Config.getTimeintervalEnabled()) {
            timeIntervalContainer.setIsCopyMode(false, false);
        }
        dataView.findAndSelectTimeInterval();
        dataView.refresh();
    },

    onSaveRelationClick: function (btn, e) {
        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        this.saveRelationRecord();
    },

    /**
     * Обработка клика по кнопке удаления записи
     *
     * @param button
     * @param e
     */
    onRemoveRelationClick: function (button, e) {
        var view = this.getView();

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        if (view.fireEvent('beforeremove') != false) {
            this.removeRelation();
        }
    },

    onAddTimeIntervalButtonClick: function () {
        var view = this.getView(),
            timeIntervalContainer = view.timeIntervalContainer;

        if (Unidata.Config.getTimeintervalEnabled()) {
            this.createTimeInterval(timeIntervalContainer, this.getView().getDirty());
        }
    },

    createTimeInterval: function (timeIntervalContainer, dirty) {
        var datePickerPanel = timeIntervalContainer.getDatePickerPanel(),
            dataView        = timeIntervalContainer.getDataView();

        if (!dirty && !dataView.getIsCopyMode()) {
            datePickerPanel.resetDateLimits();
            timeIntervalContainer.setIsCopyMode(true, false);
        } else {
            Unidata.showError(this.createErrorText);
        }
    },

    /**
     * Удаление инстанса связи
     */
    removeRelation: function () {
        var view         = this.getView(),
            viewModel    = this.getViewModel(),
            etalonId     = view.getEtalonId();

        function removeHandler () {
            view.fireEvent('remove');

            Unidata.showMessage(Unidata.i18n.t('relation>removeRelationSuccess'));
        }

        if (etalonId) {
            viewModel.set('allowClickRemove', false);

            Ext.Ajax.request({
                method: 'DELETE',
                url: Unidata.Config.getMainUrl() + 'internal/data/relations/relation/' + etalonId,
                success: function () {
                    viewModel.set('allowClickRemove', true);
                    view.fireComponentEvent('checkworkflow');
                    removeHandler();
                },
                failure: function () {
                    viewModel.set('allowClickRemove', true);
                },
                scope: this
            });
        } else {
            viewModel.set('allowClickRemove', true);
            removeHandler();
        }
    },

    /**
     * Отрисовка текущей связи-включения
     */
    drawRecord: function () {
        var dataAttributeFormatter = Unidata.util.DataAttributeFormatter,
            view                  = this.getView(),
            viewModel             = this.getViewModel(),
            readOnly              = view.getReadOnly(),
            metaRelation          = view.getMetaRelation(),
            metaRelationRecord    = view.getMetaRelationRecord(),
            dataRelationRecord    = view.getDataRelationRecord(),
            etalonRecord          = dataRelationRecord.getEtalonRecord(),
            dataEntity,
            title,
            isDqErrors;

        this.unlock();

        // очищаем то, что отрисовано
        dataEntity = view.dataEntity;
        dataEntity.clearDataEntity();
        dataEntity.setReadOnly(readOnly);

        // если нет эталона(новый, и еще не согласован), то убираем всё лишнее
        if (!etalonRecord) {
            viewModel.set(
                'containsRecordTitle',
                this.getApproveTitle(metaRelationRecord.get('displayName'))
            );
            viewModel.set('pending', true);
            view.fireComponentEvent('checkworkflow');

            return;
        }

        if (Unidata.Config.getTimeintervalEnabled()) {
            this.lookupReference('timeIntervalContainer').setHidden(false);
        }

        // выводим запись
        dataEntity.setEntityData(metaRelationRecord, etalonRecord);
        dataEntity.displayDataEntity();
        dataEntity.showDqErrorsIndicator();

        if (etalonRecord.phantom) {
            title = Unidata.i18n.t('glossary:newRelation') + dataAttributeFormatter.getDirtyPrefix();
        } else {
            // генерим заголовок на основе атрибутов для отображения
            title = dataAttributeFormatter.buildEntityTitleFromDataRecord(
                metaRelationRecord,
                etalonRecord,
                undefined,
                metaRelation.get('toEntityDefaultDisplayAttributes'),
                metaRelation.get('useAttributeNameForDisplay')
            );
        }

        // если не получилось сгенерить - отображаем дефолтный
        title = title ? title : '<span class="un-empty-text">' + Unidata.i18n.t('relation>attributeValuesNotSet') + '</span>';

        if (etalonRecord.get('approval') == 'PENDING') {
            title = this.getApproveTitle(title);
            viewModel.set('pending', true);
        } else {
            viewModel.set('pending', false);
        }

        view.setReadOnly(readOnly);

        viewModel.set('containsRecordTitle', title);

        // dqBar
        isDqErrors = Boolean(etalonRecord.dqErrors().getCount());

        if (isDqErrors) {
            this.lookupReference('dqBar').setDataRecord(etalonRecord);
            this.lookupReference('dqBar').updateDqErrorCount(etalonRecord.dqErrors().getCount());
        }

        // наличие dq ошибок
        viewModel.set('dqErrors', isDqErrors);
        view.fireEvent('relationrecorddraw');

        viewModel.set('etalonRecord', etalonRecord);
    },

    getApproveTitle: function (title) {
        return title + ' <b style="color: #ffc107;">(' + Unidata.i18n.t('relation>approveRelation') + ')</b>';
    },

    lock: function () {
        var view = this.getView(),
            viewModel = this.getViewModel();

        view.setLoading(true);
        viewModel.set('allowClickSave', false);
        viewModel.set('allowClickRemove', false);
    },

    unlock: function () {
        var view = this.getView(),
            viewModel = this.getViewModel();

        view.setLoading(false);
        viewModel.set('allowClickSave', true);
        viewModel.set('allowClickRemove', true);
    },

    /**
     * Обработчик выбора тайминтервала.
     * Загружает запись и отправляет на отрисовку, если записи нет - создаёт новую.
     *
     * @param dataview
     */
    onTimeIntervalContainsDataViewSelect: function (dataview) {
        var me = this,
            view = this.getView(),
            drafts = view.getDrafts(),
            operationId = view.getOperationId(),
            dataEntity = view.dataEntity,
            timeInterval = dataview.getLastSelected(),
            etalonId = view.getEtalonId(),
            dateFrom = timeInterval.get('dateFrom'),
            dateTo = timeInterval.get('dateTo'),
            promiseDataRecord;

        function onDataRelationRecordLoad (dataRelationRecord) {
            var view = me.getView();

            view.setDataRelationRecord(dataRelationRecord);
            me.drawRecord();
        }

        dataEntity.removeAll();
        view.setLoading(true);

        // если есть etalonId - загружаем запись
        if (etalonId) {
            promiseDataRecord = Unidata.util.api.RelationContains.loadRelationRecord(
                etalonId,
                dateFrom,
                dateTo,
                drafts,
                operationId
            );

            promiseDataRecord.then(onDataRelationRecordLoad).done();
        }
    },

    createDataRelationRecord: function () {
        var view = this.getView(),
            dataRelationRecord,
            metaRelation = view.getMetaRelation(),
            record,
            recordRights;

        // Созадём новую запись, если нет etalonId
        dataRelationRecord = Ext.create('Unidata.model.data.RelationContains', {
            relName: metaRelation.get('name')
        });

        record = Ext.create('Unidata.model.data.Record');
        // все права на новую запись
        recordRights = Ext.create('Unidata.model.user.Right', {
            create: true,
            read: true,
            update: true,
            delete: true
        });
        record.setRights(recordRights);

        dataRelationRecord.setEtalonRecord(record);

        return dataRelationRecord;
    },

    updateCreatetimeintervalbuttonhidden: function (hidden) {
        var addTimeIntervalButton = this.getAddTimeIntervalButton();

        if (addTimeIntervalButton && Unidata.Config.getTimeintervalEnabled()) {
            addTimeIntervalButton.setHidden(hidden);
        }
    },

    getAddTimeIntervalButton: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            addTimeIntervalButton = null;

        if (timeIntervalContainer) {
            addTimeIntervalButton = timeIntervalContainer.lookupReference('addTimeIntervalButton');
        }

        return addTimeIntervalButton;
    }
});
