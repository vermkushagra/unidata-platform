Ext.define('Unidata.view.steward.dataviewer.card.data.DataCardController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewer.datacard',

    mixins: [
        'Unidata.view.steward.dataentity.mixin.TimeIntervalViewable'
    ],

    init: function () {
        var me        = this,
            view      = me.getView(),
            headerBar = view.headerBar;

        Unidata.event.manager.Approve.relayEvents(
            view,
            [
                'approvesuccess', 'approvefailure',
                'declinesuccess', 'declinefailure'
            ]
        );

        view.on('render', me.onComponentRender, me, {single: true});

        headerBar.dqPanel.on('changedq', me.onChangeDqErrorName, me);

        view.addComponentListener('checkworkflow', this.checkWorkflow, this);
        view.addComponentListener('datarecordclassifiernodechange', this.onClassifierNodesChange, this);
    },

    /**
     * Проверяет, не находится ли запись на согласовании и обновляет StateBar
     */
    checkWorkflow: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            etalonId = view.getEtalonId(),
            dataRecord = view.getDataRecord(),
            workflowState = dataRecord.workflowState(),
            taskSearchHitStore = viewModel.get('taskSearchHitStore'),
            proxy = taskSearchHitStore.getProxy(),
            dataViewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

        // если запись уже на согласовании, то ничего не делаем
        if (dataRecord.get('approval') === dataViewerConst.ETALON_APPROVAL_PENDING) {
            return;
        }

        // ищем задачи по etalonId
        proxy.setExtraParam('variables', {etalonId: etalonId});

        taskSearchHitStore.reload({
            scope: this,
            callback: function (records, operation, success) {
                if (success && records && records.length) {
                    workflowState.removeAll();
                    workflowState.add(records);

                    dataRecord.set('approval', dataViewerConst.ETALON_APPROVAL_PENDING);
                    dataRecord.commit(['approval']);

                    this.updateHeaderDataCardStatusInfo();
                }
            }
        }, this);
    },

    getAddTimeIntervalButton: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            addTimeIntervalButton = null;

        if (timeIntervalContainer) {
            addTimeIntervalButton = timeIntervalContainer.lookupReference('addTimeIntervalButton');
        }

        return addTimeIntervalButton;
    },

    onChangeDqErrorName: function (dqName) {
        var view = this.getView();

        if (dqName !== null) {
            view.dataEntity.showDqErrorIndicatorByDqName(dqName);
        } else {
            view.dataEntity.showDqErrorsIndicator();
        }
    },

    getDataRecord: function () {
        return this.getView().getDataRecord();
    },

    getTimeIntervalContainer: function () {
        var view = this.getView(),
            headerBar = view.headerBar,
            timeIntervalContainer = null;

        if (headerBar) {
            timeIntervalContainer = headerBar.lookupReference('timeIntervalContainer');
        }

        return timeIntervalContainer;
    },

    isTimeIntervalsChanged: function (dataRecord) {
        var view                  = this.getView(),
            dateFrom,
            dateTo,
            timeIntervalStore     = view.getTimeIntervalStore(),
            timeIntervals         = timeIntervalStore.getRange(),
            TimeIntervalUtil      = Unidata.util.TimeInterval,
            intersectedTimeIntervals;

        dateFrom = dataRecord.get('validFrom');
        dateTo   = dataRecord.get('validTo');

        intersectedTimeIntervals = TimeIntervalUtil.findIntersectedTimeIntervals(dateFrom, dateTo, timeIntervals);

        return intersectedTimeIntervals && intersectedTimeIntervals.length > 0;
    },

    checkValid: function () {
        var view                 = this.getView(),
            dataEntity           = view.dataEntity,
            referencePanel       = view.referencePanel,
            m2mPanel             = view.m2mPanel,
            securityLabelValid = dataEntity.isSecurityLabelValid(),
            fieldsValid        = dataEntity.isFieldsValid(),
            referenceRelationValid = referencePanel.isRelationsValid(),
            m2mRelationValid = m2mPanel.isRelationsValid(),
            errors = [];

        if (!securityLabelValid || !fieldsValid || !referenceRelationValid || !m2mRelationValid) {
            if (!fieldsValid) {
                errors.push(Unidata.i18n.t('validation:form.requiredFields'));
            }

            if (!securityLabelValid) {
                dataEntity.showSecurityLabelErrorIndicator();
                errors.push(Unidata.i18n.t('validation:form.securityLabel'));
            }

            if (!referenceRelationValid || !m2mRelationValid) {
                errors.push(Unidata.i18n.t('dataviewer>relationsIsEmpty'));
            }

            Unidata.showError(errors);

            view.fireEvent('refreshend');

            return false;
        }

        return true;
    },

    processTimeIntervals: function () {
        var view = this.getView(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            timeInterval          = timeIntervalContainer.getCreatedTimeInterval(),
            dataRecord = this.getDataRecord();

        if (timeIntervalContainer.getIsCopyMode()) {
            this.updateRecordCreatedTimeInterval(dataRecord, timeInterval);

            // определяем изменены ли временные интервалы
            if (this.isTimeIntervalsChanged(dataRecord)) {
                Unidata.showWarning(view.intersectionTimeIntervalMsgText);
            }
        }

    },

    saveDataRecord: function () {
        var view = this.getView(),
            dataRecord = this.getDataRecord();

        if (!this.checkValid()) {
            return;
        }

        this.processTimeIntervals();

        view.fireEvent('datacardlocked');

        dataRecord.save({
            success: this.onDataRecordSaveSuccess,
            failure: this.onDataRecordSaveFailure,
            scope: this
        });
    },

    deleteDataRecord: function () {
        var dataRecord = this.getDataRecord(),
            me = this,
            view = this.getView();

        view.fireEvent('refreshstart');
        dataRecord.getProxy().setDate(null);

        if (!dataRecord.phantom) {
            dataRecord.erase({
                success: this.onDataRecordDeleteSuccess.bind(this),
                failure: this.onDataRecordDeleteFailure.bind(this),
                scope: this
            });
        } else {
            me.onDataRecordDeleteSuccess(dataRecord);
        }
    },

    updateRecordCreatedTimeInterval: function (record, createdTimeInterval) {
        record.set('validFrom', createdTimeInterval.validFrom);
        record.set('validTo', createdTimeInterval.validTo);
    },

    onDataRecordSaveSuccess: function (dataRecord) {
        var view = this.getView(),
            etalonId;

        etalonId = dataRecord.get('etalonId');

        if (!etalonId) {
            // если не присвоен etalonId, то прекращаем обработку сохранения

            // если не пришел etalonId, то запись phantom
            dataRecord.phantom = true;

            view.fireEvent('datacardunlocked');
            view.fireEvent('refreshend');

            return;
        }

        view.setEtalonId(etalonId);
        this.saveReferenceRelations();
    },

    saveReferenceRelations: function () {
        var view = this.getView(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            cfg,
            options,
            promiseSaveRelations,
            me = this,
            dataRecord = view.getDataRecord(),
            etalonId;

        etalonId = dataRecord.get('etalonId');

        function onReloadDataRecordAfterSaveRelationsSuccess () {
            view.fireEvent('savesuccess', dataRecord);
            Unidata.showMessage(view.dataRecordSaveSuccessText);
        }

        function onReloadDataRecordAfterSaveRelationsRejected () {
            var error;

            error = view.referenceRelationsSaveFailureText;
            console.log(error);
        }

        function onPromiseSaveRelationsFulfilled () {
            var saveCallbackCfg;

            saveCallbackCfg = {
                fn: onReloadDataRecordAfterSaveRelationsSuccess
            };

            timeIntervalContainer.setIsCopyMode(false);
            me.reloadDataCard(cfg, options, saveCallbackCfg);
        }

        function onPromiseSaveRelationsRejected () {
            var failureCallbackCfg;

            failureCallbackCfg = {
                fn: onReloadDataRecordAfterSaveRelationsRejected
            };

            timeIntervalContainer.setIsCopyMode(false);
            me.reloadDataCard(cfg, options, failureCallbackCfg);
        }

        cfg = {
            metaRecord: view.getMetaRecord(),
            etalonId: etalonId,
            timeIntervalStore: view.getTimeIntervalStore()
        };

        options = {
            isReloadTimeline: true
        };

        // сохраняем связи типа References
        promiseSaveRelations = view.referencePanel.saveReferenceRelations();
        promiseSaveRelations.then(onPromiseSaveRelationsFulfilled, onPromiseSaveRelationsRejected);
        promiseSaveRelations.done();
    },

    /**
     * Построить полный json для atomic upsert
     *
     * @return {*}
     */
    buildAtomicRecordJson: function () {
        var cfg,
            atomicRecord,
            dataRecord,
            data,
            relationReference,
            relationManyToMany,
            filters = {};

        cfg            = this.buildAtomicRecordCfg();
        atomicRecord   = Unidata.util.api.DataRecord.buildAtomicRecord(cfg);
        relationReference = atomicRecord.getRelationReference();
        relationManyToMany = atomicRecord.getRelationManyToMany();
        filters['relationReference'] = this.applyFilters(relationReference.toUpdate());
        filters['relationManyToMany'] = this.applyFilters(relationManyToMany.toUpdate());
        dataRecord     = atomicRecord.getDataRecord();
        filters['dataRecord'] = dataRecord.applyAttributesFilterCascade(dataRecord);
        data           = atomicRecord.getAssociatedData(null, {serialize: true, associated: true, persist: true});
        dataRecord.revertFilters(filters['dataRecord']);
        this.revertFilters(relationReference.toUpdate(), filters['relationReference']);
        this.revertFilters(relationManyToMany.toUpdate(), filters['relationManyToMany']);

        return data;
    },

    /**
     * Применить фильтры для набора рекордов/связей
     * @param {Unidata.model.data.Record|Unidata.model.data.RelationReference} records
     * @return {Array} Массив фильтрова
     */
    applyFilters: function (records) {
        var filters = [];

        records.each(function (record) {
            filters.push(record.applyAttributesFilterCascade(record));
        });

        return filters;
    },

    /**
     * Откатить фильтры для набора рекордов/связей
     * @param {Unidata.model.data.Record|Unidata.model.data.RelationReference} records
     * @param  {Array} Массив фильтров
     */
    revertFilters: function (records, filters) {
        records.each(function (record, index) {
            record.revertFilters(filters[index]);
        });
    },

    /**
     * Построить atomic record для сохранения
     * @return {Object}
     */
    buildAtomicRecordCfg: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            timeIntervalContainer =  this.getTimeIntervalContainer(),
            cfg;

        cfg = {};
        cfg.dataRecord = dataRecord;
        cfg.relationReferenceDiff = this.getRelationReferenceDiff(timeIntervalContainer.getIsCopyMode());
        cfg.relationManyToManyDiff = this.getRelationManyToManyDiff();

        return cfg;
    },

    /**
     * Сохранить все составляющие карточки записи атомарно
     */
    saveAllAtomic: function () {
        var data,
            view = this.getView(),
            dataRecord = view.getDataRecord(),
            etalonId = dataRecord.get('etalonId');

        // Нужен таймаут, т.к. значения массивом могут измениться при событии blur,
        // которое происходит с задержкой
        Ext.defer(function () {
            if (!this.checkValid()) {
                return;
            }

            // если запись без изменений то необходимо сообщать об этом
            if (!this.checkDirty()) {
                this.showWarning(Unidata.i18n.t('dataviewer>recordWithoutChanges'));

                return;
            }

            view.fireEvent('refreshstart');
            view.fireEvent('datacardlocked');

            this.processTimeIntervals();
            data = this.buildAtomicRecordJson();
            Unidata.util.api.DataRecord.saveAtomic(etalonId, data, dataRecord.phantom).then(
                this.onSaveAtomicSuccess.bind(this),
                this.onSaveAtomicFailure.bind(this))
                .done();
        }, 500, this);
    },

    /**
     * Обработчик успешного атомарного сохранения
     *
     * @param {Unidata.model.data.Record} dataRecordResponse
     */
    onSaveAtomicSuccess: function (dataRecordResponse) {
        var view = this.getView(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            etalonId = dataRecordResponse.get('etalonId'),
            saveCallbackCfg,
            cfg,
            options;

        view.setEtalonId(etalonId);
        timeIntervalContainer.setIsCopyMode(false);

        saveCallbackCfg = {
            fn: function () {
                view.fireEvent('savesuccess', dataRecordResponse);
                view.fireEvent('refreshend');
                Unidata.showMessage(view.dataRecordSaveSuccessText);
            }
        };

        cfg = {
            metaRecord: view.getMetaRecord(),
            etalonId: etalonId,
            timeIntervalStore: view.getTimeIntervalStore()
        };

        options = {
            isReloadTimeline: true
        };

        this.reloadDataCard(cfg, options, saveCallbackCfg);
    },

    /**
     * Обработчик неуспешного атомарного сохранения
     *
     * @param {Unidata.model.data.Record} dataRecordResponse
     */
    onSaveAtomicFailure: function (dataRecordResponse) {
        var view = this.getView(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            dataRecord = view.getDataRecord(),
            errorText,
            dqErrors;

        if (dataRecordResponse) {
            dqErrors = dataRecordResponse.dqErrors();
            dataRecord.dqErrors().setData(dqErrors.getRange());
            this.showDqErrorsIndicator();

            if (this.isSomeDqErrorsBeforeUpsert(dqErrors.getRange())) {
                // если есть dq ошибки с фазой BEFORE_UPSERT или без фазы то прекращаем обработку сохранения
                errorText = view.dataRecordSaveDQFailureText;
            } else {
                errorText = view.dataRecordSaveFailureText;
            }

            // даём возможность редактировать запись, при попытке восстановления записи и наличия DQ ошибок
            if (dataRecordResponse.get('status') === viewerConst.ETALON_STATUS_INACTIVE) {
                view.setReadOnly(false);
                this.showWarning('Повторите восстановление после проверки значений');
            } else {
                Unidata.showError(errorText);
            }
        }

        view.fireEvent('savefailure', dataRecordResponse);
        view.fireEvent('datacardunlocked');
        view.fireEvent('refreshend');
    },

    /**
     * Является ли карточка записи dirty
     *
     * @return {boolean}
     */
    checkDirty: function () {
        var dirty = false,
            view = this.getView(),
            referencePanel = view.referencePanel,
            manyToManyPanel = view.m2mPanel,
            dataRecord = view.getDataRecord(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            dirtyDataRecord,
            dirtyRelationReference,
            dirtyRelationManyToMany;

        dirtyDataRecord = dataRecord.checkDirty() || timeIntervalContainer.getIsCopyMode();
        dirtyRelationReference = referencePanel.checkPanelsDirty();
        dirtyRelationManyToMany = manyToManyPanel.checkPanelsDirty();

        if (dirtyDataRecord || dirtyRelationReference || dirtyRelationManyToMany)  {
            dirty = {
                dataRecord: dirtyDataRecord,
                referenceRelation: dirtyRelationReference,
                manyToManyRelation: dirtyRelationManyToMany
            };
        }

        return dirty;
    },

    /**
     * Сохранить составляющие карточки записи (dataRecord, relationReference) одну за другой
     */
    saveAllOneByOne: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            referencePanel = view.referencePanel,
            timeIntervalContainer = this.getTimeIntervalContainer(),
            dirtyDataRecord,
            dirtyReferences;

        view.fireEvent('refreshstart');

        // Нужен таймаут, т.к. значения массивом могут измениться при событии blur,
        // которое происходит с задержкой
        Ext.defer(function () {
            dirtyDataRecord = dataRecord.checkDirty() || timeIntervalContainer.getIsCopyMode();
            dirtyReferences = referencePanel.checkPanelsDirty();
            // перенос функционала из версии 3.8
            // если запись без изменений то необходимо сообщать об этом
            if (!dirtyDataRecord && !dirtyReferences) {
                this.showWarning(Unidata.i18n.t('dataviewer>recordWithoutChanges'));

                view.fireEvent('refreshend');

                return;
            }

            if (dirtyDataRecord) {
                this.saveDataRecord();
            } else if (dirtyReferences) {
                this.saveReferenceRelations();
            }

            // originCard.resetCachedData();
        }, 500, this);
    },

    /**
     * @return {Unidata.model.data.RelationReferenceDiff}
     */
    getRelationReferenceDiff: function (forceGetAll) {
        var view = this.getView();

        return view.referencePanel.getRelationReferenceDiff(forceGetAll);
    },

    getRelationManyToManyDiff: function () {
        var view = this.getView();

        return view.m2mPanel.getRelationManyToManyDiff();
    },

    /**
     * Функция проверяет если есть dq ошибки с фазой BEFORE_UPSERT или без фазы
     *
     * @param dqErrors
     * @returns {*}
     */
    isSomeDqErrorsBeforeUpsert: function (dqErrors) {
        var someDqBeforeUpsert;

        someDqBeforeUpsert = Ext.Array.some(dqErrors, function (dqError) {
            return dqError.phase === 'BEFORE_UPSERT' || !dqError.phase;
        });

        return someDqBeforeUpsert;
    },

    onDataRecordSaveFailure: function (dataRecord) {
        var view = this.getView(),
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            errorText,
            dqErrors;

        view.fireEvent('datacardunlocked');
        view.fireEvent('refreshend');

        dqErrors = dataRecord.dqErrors().getRange();
        dataRecord.dqErrors().add(dqErrors);

        if (this.isSomeDqErrorsBeforeUpsert(dqErrors)) {
            // если есть dq ошибки с фазой BEFORE_UPSERT или без фазы то прекращаем обработку сохранения
            errorText = view.dataRecordSaveDQFailureText;
        } else {
            errorText = view.dataRecordSaveFailureText;
        }

        this.showDqErrorsIndicator();

        // даём возможность редактировать запись, при попытке восстановления записи и наличия DQ ошибок
        if (dataRecord.get('status') === viewerConst.ETALON_STATUS_INACTIVE) {
            view.setReadOnly(false);
            this.showWarning(Unidata.i18n.t('dataviewer>repeatRestoreAfterCheck'));
        } else {
            Unidata.showError(errorText);
        }

        view.fireEvent('savefailure', dataRecord);
        view.fireEvent('datacardunlocked');
        view.fireEvent('refreshend');
    },

    onDataRecordRestoreFailure: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord();

        view.fireEvent('restorefailure', dataRecord);
    },

    onComponentRender: function () {
        var dataRecord = this.getView().getDataRecord(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            me = this;

        me.setTimeIntervalValidityPeriod();

        timeIntervalContainer.setIsCopyMode(dataRecord.phantom);

        this.configListeners();
    },

    /**
     * Настроить компонент TimeIntervalDateView
     * В том числе произвести инициализацию выбора текущего time interval
     */
    configTimeIntervalDateView: function () {
        var view             = this.getView(),
            dataView         = this.getTimeIntervalContainer().getDataView(),
            selectModel,
            timeInterval,
            timeIntervalDate = view.getTimeIntervalDate(),
            me               = this;

        function findAndSelectTimeInterval () {
            timeInterval = dataView.findAndSelectTimeInterval(timeIntervalDate);
            selectModel  = dataView.getSelectionModel();
            me.getView().fireEvent('selecttimeinterval', selectModel, timeInterval);
            me.setTimeIntervalSelectListener();
        }

        this.deleteTimeIntervalSelectListener();

        if (!dataView.rendered) {
            dataView.on('render', function () {
                findAndSelectTimeInterval();
            }, {
                single: true
            });
        } else {
            findAndSelectTimeInterval();
        }
    },

    /**
     * Построение dataEntityViewer с предварительной подгрузкой данных
     */
    displayDataCard: function () {
        var view                  = this.getView(),
            dataEntity            = view.dataEntity,
            headerBar             = view.headerBar,
            metaRecord            = view.getMetaRecord(),
            dataRecord            = view.getDataRecord(),
            classifierNodes       = view.getClassifierNodes(),
            timeIntervalStore     = view.getTimeIntervalStore(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView              = timeIntervalContainer.getDataView(),
            hiddenAttributeCount  = Unidata.util.MetaRecord.getHiddenAttributeCount(metaRecord);

        //dataView.timeIntervalSelectedDate = timeIntervalDate;
        dataView.setStore(timeIntervalStore);
        this.configTimeIntervalDateView();

        // выводим запись
        dataEntity.setEntityData(metaRecord, dataRecord, classifierNodes);
        dataEntity.displayDataEntity();

        if (Unidata.util.MetaRecord.isEntity(metaRecord)) {
            // выводим связи типа "ссылка"
            view.referencePanel
                .setMetaRecord(metaRecord)
                .setDataRecord(dataRecord)
                .displayReferenceRelations(view.getReferenceRelations());

            // выводим связи типа включение
            view.containsPanel
                .setMetaRecord(metaRecord)
                .setDataRecord(dataRecord)
                .displayRelations('Contains');

            // выводим связи типа многие-ко-многим
            view.m2mPanel
                .setMetaRecord(metaRecord)
                .setDataRecord(dataRecord)
                .displayRelations('ManyToMany');
        }

        // скрываем панельки с ошибками правил качества данных и согласований
        headerBar.hideAllPanel();
        headerBar.dqPanel.setDataRecord(dataRecord);
        headerBar.setDataRecord(dataRecord);

        // обновляем состояние кнопки переключения скрытых атрибутов
        headerBar.updateToggleHiddenButtonState(dataEntity.getHiddenAttribute(), hiddenAttributeCount);

        // обновляем информацию на панельски согласования
        // this.updateApprovebarInfo();

        // обновляем панельку хидера отображающую статус в котором находится запись
        this.updateHeaderDataCardStatusInfo();
    },

    configListeners: function () {
        //var searchTextfield       = this.getSearchTextfield(),
        var timeIntervalContainer = this.getTimeIntervalContainer();

        timeIntervalContainer.on('timeintervaldelete', this.onTimeIntervalDelete, this);
        timeIntervalContainer.on('timeintervalrestore', this.onTimeIntervalRestore, this);
        timeIntervalContainer.on('undotimeintervalcreate', this.onUndoTimeIntervalCreate, this);
        // поиск по карточке записи временно отключен
        //searchTextfield.on('change', this.onSearchInputChange, this);
        //searchTextfield.on('keydown', this.onSearchKeyDown, this);
        timeIntervalContainer.tools[0].on('click', this.onAddTimeIntervalButtonClick, this);
    },

    onTimeIntervalDelete: function (timeInterval, btn) {
        var dataView = timeInterval.getDataView(),
            etalonId = dataView.getSelection()[0].get('etalonId');

        this.showTimeIntervalDeleteDialog(dataView, etalonId, this.deleteEtalonVersion, btn);
    },

    onTimeIntervalRestore: function (timeInterval, btn) {
        var dataView = timeInterval.getDataView(),
            etalonId = dataView.getSelection()[0].get('etalonId');

        this.showTimeIntervalRestoreDialog(dataView, etalonId, this.restoreEtalonVersion, btn);
    },

    deleteTimeIntervalSelectListener: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView              = timeIntervalContainer.getDataView();

        dataView.removeListener('select', this.onTimeIntervalDataViewSelect, this);
        dataView.removeListener('beforeselect', this.onTimeIntervalDataViewBeforeSelect, this);
    },

    setTimeIntervalSelectListener: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            dataView              = timeIntervalContainer.getDataView();

        dataView.on('select', this.onTimeIntervalDataViewSelect, this);
        dataView.on('deselect', this.onTimeIntervalDataViewDeselect, this);
        dataView.on('beforeselect', this.onTimeIntervalDataViewBeforeSelect, this);
    },

    //update

    updateReadOnly: function (readOnly) {
        var view                = this.getView(),
            dataEntity          = view.dataEntity,
            referencePanel      = view.referencePanel,
            containsPanel       = view.containsPanel,
            m2mPanel            = view.m2mPanel;

        if (dataEntity) {
            dataEntity.setReadOnly(readOnly);
        }

        if (referencePanel) {
            referencePanel.setReadOnly(readOnly);
        }

        if (containsPanel) {
            containsPanel.setReadOnly(readOnly);
        }

        if (m2mPanel) {
            m2mPanel.setReadOnly(readOnly);
        }

        this.updateHeaderDataCardStatusInfo();
    },

    onAddTimeIntervalButtonClick: function () {
        var timeIntervalContainer = this.getTimeIntervalContainer();

        this.createTimeInterval(timeIntervalContainer, this.getView().getDirty());
    },

    createTimeInterval: function (timeIntervalContainer, dirty) {
        var datePickerPanel = timeIntervalContainer.getDatePickerPanel(),
            dataView                = timeIntervalContainer.getDataView();

        if (!dirty && !dataView.getIsCopyMode()) {
            datePickerPanel.resetDateLimits();
            timeIntervalContainer.setIsCopyMode(true);
            timeIntervalContainer.resetDataRecordValidData();
            timeIntervalContainer.setHidden(false);
        } else {
            Unidata.showError(this.createErrorText);
        }
    },

    onTimeIntervalDataViewSelect: function (selectModel, timeInterval) {
        var view = this.getView();

        // сообщаем во внешний мир что мы изменили период актуальности
        view.fireEvent('selecttimeinterval', selectModel, timeInterval);

        timeInterval.set('chosen', true);
        this.reloadAfterTimeIntervalSelect(timeInterval);
    },

    onTimeIntervalDataViewDeselect: function (selectModel, timeInterval) {
        timeInterval.set('chosen', false);
    },

    reloadAfterTimeIntervalSelect: function (timeInterval) {
        //TODO: убрать костыль .up('steward\\.dataviewer')
        var cfg,
            options,
            timeIntervalDate;

        cfg = this.getView().up('steward\\.dataviewer').buildConfig();
        timeIntervalDate = Unidata.view.component.timeinterval.TimeIntervalDataView.getTimeIntervalDate(timeInterval);

        cfg.dataRecord            = null;
        cfg.referenceRelations    = null;
        cfg.timeIntervalDate      = timeIntervalDate;
        cfg.timeInterval          = null;
        options = {isReloadTimeline: false};

        this.reloadDataCard(cfg, options);

    },

    reset: function () {
        var view = this.getView(),
            referencePanel = view.referencePanel,
            m2mPanel = view.m2mPanel;

        referencePanel.reset();
        m2mPanel.reset();
    },

    reloadDataCard: function (cfg, options, successCallbackCfg, failureCallbackCfg) {
        var view = this.getView();

        cfg.diffToDraft = view.getDiffToDraft();
        cfg.drafts = view.getDrafts();
        cfg.operationId = view.getOperationId();

        // если вью нет, то и грузить ничего не нужно
        if (view.isDestroyed) {
            return;
        }

        view.fireEvent('datacardlocked');
        view.fireEvent('refreshstart');

        Unidata.view.steward.dataviewer.DataViewerLoader.load(cfg, options)
            .then(function (cfg) {
                view.fireEvent('datacardunlocked');
                // сообщаем во внешний мир что мы изменились
                view.fireEvent('datacardload', cfg);
                view.fireEvent('refreshend');

                if (successCallbackCfg) {
                    successCallbackCfg.args = successCallbackCfg.args || [];
                    successCallbackCfg.context = successCallbackCfg.context || null;
                    successCallbackCfg.fn.apply(successCallbackCfg.context, successCallbackCfg.args);
                }
            }, function (failData) {
                view.fireEvent('datacardunlocked');
                view.fireEvent('refreshend');
                // сообщаем, что загрузка провалилась
                view.fireEvent('datacardloadfail', failData);

                if (failureCallbackCfg) {
                    failureCallbackCfg.args = failureCallbackCfg.args || [];
                    failureCallbackCfg.context = failureCallbackCfg.context || null;
                    failureCallbackCfg.fn.apply(failureCallbackCfg.context, failureCallbackCfg.args);
                }
            })
            .done();
    },

    search: function (text) {

        var view = this.getView(),
            result = [],
            prevSearchResult,
            i;

        text = Ext.String.trim(text).toLowerCase().replace(/\s+/g, ' ');

        if (text) {
            result = view.dataEntity.search(text);
            result.push.apply(result, view.referencePanel.search(text));
        }

        prevSearchResult = this.searchResult;

        if (prevSearchResult) {
            for (i = 0; i < prevSearchResult.length; i++) {
                prevSearchResult[i].setSearched(false);
            }
        }

        if (this.previousActiveItem) {
            this.previousActiveItem.setActive(false);
        }

        if (text) {
            for (i = 0; i < result.length; i++) {
                result[i].setSearched(true);
            }
        }

        this.searchResult = result;
        this.scrollToItemIndex = 0;
        this.searchNext();
    },

    searchNext: function () {
        if (this.searchResult && this.searchResult[this.scrollToItemIndex]) {
            this.scrollToItem(this.searchResult[this.scrollToItemIndex++]);
        } else {
            if (this.scrollToItemIndex > 0) {
                this.scrollToItemIndex = 0;
                this.searchNext();
            }
        }
    },

    scrollToItem: function (item) {

        var me = this,
            view = me.getView(),
            contentContainer = view.contentContainer,
            position = calcPosition(item);

        function calcPosition (item) {

            var xy = item.getLocalXY(),
                oxy = [0, 0];

            if (item.ownerCt && item.ownerCt !== contentContainer) {
                oxy = calcPosition(item.ownerCt);
            }

            return [
                xy[0] + oxy[0],
                xy[1] + oxy[1]
            ];
        }

        if (this.previousActiveItem) {
            this.previousActiveItem.setSearchedAndActive(false);
        }

        item.setSearchedAndActive(true);

        this.previousActiveItem = item;

        contentContainer.scrollTo(position[0], position[1]);

    },

    onTimeIntervalDataViewBeforeSelect: function (dataViewModel, dataRecord) {
        return this.showBeforeSelectTimeIntervalPrompt(dataViewModel.view, dataRecord);
    },

    onSearchInputChange: function (input, newValue) {
        this.search(newValue);
    },

    onSearchKeyDown: function (input, e) {
        if (e.getKey() === Ext.event.Event.ENTER) {
            this.searchNext();
        }
    },

    deleteEtalonVersion: function () {
        var TimeIntervalUtil      = Unidata.util.api.TimeInterval,
            me                    = this,
            view                  = this.getView(),
            metaRecord            = view.getMetaRecord(),
            timeIntervalStore     = view.getTimeIntervalStore(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            dateView              = timeIntervalContainer.getDataView(),
            etalonId              = view.getEtalonId(),
            selection             = dateView.getSelection(),
            timeInterval,
            dateFrom,
            dateTo,
            cfg,
            options;

        cfg = {
            metaRecord: metaRecord,
            etalonId: etalonId,
            timeIntervalStore: timeIntervalStore
        };

        options = {
            isReloadTimeline: true
        };

        function onDeleteTimeIntervalFulfilled () {
            Unidata.showMessage(view.deleteTimeIntervalSuccessText);
            me.reloadDataCard(cfg, options);
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
        TimeIntervalUtil.deleteTimeInterval(etalonId, dateFrom, dateTo)
                .then(onDeleteTimeIntervalFulfilled, onDeleteTimeIntervalRejected)
                .done();
    },

    restoreEtalonVersion: function () {
        var TimeIntervalUtil      = Unidata.util.api.TimeInterval,
            me                    = this,
            view                  = this.getView(),
            metaRecord            = view.getMetaRecord(),
            timeIntervalStore     = view.getTimeIntervalStore(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            dateView              = timeIntervalContainer.getDataView(),
            etalonId              = view.getEtalonId(),
            selection             = dateView.getSelection(),
            timeInterval,
            dateFrom,
            dateTo,
            cfg,
            options;

        cfg = {
            metaRecord: metaRecord,
            etalonId: etalonId,
            timeIntervalStore: timeIntervalStore
        };

        options = {
            isReloadTimeline: true
        };

        function onRestoreTimeIntervalFulfilled () {
            Unidata.showMessage(view.restoreTimeIntervalSuccessText);
            me.reloadDataCard(cfg, options);
        }

        function onRestoreTimeIntervalRejected () {
            Unidata.showError(view.restoreTimeIntervalFailedText);
        }

        if (!selection || selection.length !== 1) {
            return;
        }

        timeInterval = selection[0];

        dateFrom = timeInterval.get('dateFrom');
        dateTo = timeInterval.get('dateTo');

        TimeIntervalUtil.restoreTimeInterval(etalonId, dateFrom, dateTo)
            .then(onRestoreTimeIntervalFulfilled, onRestoreTimeIntervalRejected)
            .done();

    },

    //TODO: add fillDatePickerDefaults from old version

    // *** Time Interval methods END ***
    getSearchTextfield: function () {
        return this.lookupReference('headerBar').lookupReference('searchTextfield');
    },

    refreshDataCard: function () {
        var view              = this.getView(),
            metaRecord        = view.getMetaRecord(),
            etalonId          = view.getEtalonId(),
            timeIntervalStore = view.getTimeIntervalStore(),
            cfg,
            options;

        cfg = {
            metaRecord: metaRecord,
            etalonId: etalonId,
            timeIntervalStore: timeIntervalStore
        };

        options = {
            isReloadTimeline: true
        };

        // TODO: Добавить проверку dirty
        this.reloadDataCard(cfg, options);
    },

    onRefreshButtonClick: function (btn) {
        var view = this.getView(),
            title = Unidata.i18n.t('common:confirmation'),
            msg;

        if (view.getDirty()) {
            msg   = Unidata.i18n.t('dataviewer>confirmRecordRefreshDirty');
        } else {
            msg   = Unidata.i18n.t('dataviewer>confirmRecordRefresh');
        }

        Unidata.showPrompt(title, msg, this.refreshDataCard, this, btn, null);
    },

    updateApprovebarhidden: function () {
        // this.updateApprovebarInfo();
    },

    /**
     * Обновляет отображаемую информацию на панельке согласования
     */
    updateApprovebarInfo: function () {
        var view       = this.getView(),
            viewModel  = this.getViewModel(),
            hidden     = view.getApprovebarhidden(),
            headerBar  = view.headerBar,
            dataRecord = viewModel.get('dataRecord'),
            approveInfo,
            workflowState,
            wfTaskTitle,
            wfInitiatorLogin,
            wfCreateDate;

        if (headerBar) {
            headerBar.setApprovebarhidden(hidden);

            if (dataRecord) {
                workflowState = dataRecord.workflowState().getAt(0);
            }

            if (workflowState) {
                wfTaskTitle      = workflowState.get('taskTitle');
                wfInitiatorLogin = workflowState.get('initiator');
                wfCreateDate     = workflowState.get('createDate');
                wfCreateDate     = Ext.Date.format(wfCreateDate, Unidata.Config.getDateFormat());
            }

            headerBar.setApprovebarhidden(hidden);

            approveInfo = {
                date: wfCreateDate,
                login: wfInitiatorLogin,
                message: wfTaskTitle
            };
            headerBar.setApproveInfo(approveInfo);
        }
    },

    updateDqbarhidden: function (hidden) {
        var view      = this.getView(),
            headerBar = view.headerBar;

        if (headerBar) {
            headerBar.setDqbarhidden(hidden);
        }
    },

    updateDottedmenubuttonhidden: function (hidden) {
        var view      = this.getView(),
            headerBar = view.headerBar;

        if (headerBar) {
            headerBar.setDottedmenubuttonhidden(hidden);
        }
    },

    updateCreatetimeintervalbuttonhidden: function (hidden) {
        var addTimeIntervalButton = this.getAddTimeIntervalButton();

        if (addTimeIntervalButton) {
            addTimeIntervalButton.setHidden(hidden);
        }
    },

    updateDeletetimeintervalbuttonhidden: function (hidden) {
        var timeIntervalContainer = this.getTimeIntervalContainer(),
            timeIntervalDataView;

        if (timeIntervalContainer) {
            timeIntervalDataView = timeIntervalContainer.getDataView();

            if (timeIntervalDataView) {
                timeIntervalDataView.setReadOnly(hidden);
            }
        }

    },

    setTimeIntervalValidityPeriod: function () {
        var view                  = this.getView(),
            timeIntervalContainer = this.getTimeIntervalContainer(),
            datePickerPanel = timeIntervalContainer.getDatePickerPanel(),
            metaRecord            = view.getMetaRecord(),
            validityPeriod        = metaRecord.get('validityPeriod'),
            minDate               = null,
            maxDate               = null;

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
     * Отображает ошибки правил качества
     */
    showDqErrorsIndicator: function () {
        var view       = this.getView(),
            dataEntity = view.dataEntity;

        // если дата рекорд не задан то и показывать собственно еще нечего
        if (dataEntity.getDataRecord()) {
            view.dataEntity.showDqErrorsIndicator();
        }
    },

    checkRecordConsistencyAndRestore: function () {
        var view = this.getView(),
            dataEntity = view.dataEntity,
            dataRecord = view.getDataRecord(),
            etalonId = dataRecord.get('etalonId'),
            status = dataRecord.get('status'),
            date = null,
            viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            cfg,
            promise,
            me = this;

        date = view.getTimeIntervalDate();

        view.fireEvent('refreshstart');

        if (status === viewerConst.ETALON_STATUS_RESTORE /** && isAllFieldsRepaired **/) {
            this.restoreDataRecord();
        } else if (status === viewerConst.ETALON_STATUS_INACTIVE) {
            cfg = {
                etalonId: etalonId,
                date: date
            };
            promise = Unidata.util.api.DataRecord.preRestoreValidateDataRecord(cfg);
            promise.then(function (inconsistentAttributePaths) {
                if ((Ext.isArray(inconsistentAttributePaths) && inconsistentAttributePaths.length > 0) || !me.checkValid()) {
                    dataEntity.setInconsistentAttributePaths(inconsistentAttributePaths);
                    dataEntity.showInconsistentAttributesIndicator();
                    dataRecord.set('status', viewerConst.ETALON_STATUS_RESTORE);
                    // TODO: переделать с использованием механизма статусов
                    dataEntity.setReadOnly(false);
                    view.fireEvent('refreshend');
                } else {
                    me.restoreDataRecord();
                }
            }, function () {
                view.fireEvent('refreshend');
                throw new Error('not implemented');
            });
            promise.done();
        }
    },

    restoreDataRecord: function () {
        var view       = this.getView(),
            dataRecord = view.getDataRecord(),
            oldUrl,
            url,
            mainUrl    = Unidata.Config.getMainUrl(),
            proxy      = dataRecord.getProxy();

        url = mainUrl + 'internal/data/entities/restore/';
        oldUrl = proxy.getUrl();
        proxy.setUrl(url);

        // TODO: необходимо корректно проставлять свойство modified для restore
        // Сейчас свойство modified всегда true (проставлено в Unidata.model.data.AbstractRecord)
        // Modified = true, если dataRecord надо сохранить при restore
        dataRecord.set('modified', true);
        dataRecord.save({
            success: this.onDataRecordSaveSuccess,
            failure: this.onDataRecordSaveFailure,
            scope: this
        });
        // TODO: use specific handlers for restore
        proxy.setUrl(oldUrl);
    },

    updateDqErrorCount: function (count) {
        var view = this.getView();

        if (view.headerBar) {
            view.headerBar.setDqErrorCount(count);
        }
    },

    updateClusterCount: function (count) {
        var view = this.getView();

        if (view.headerBar) {
            view.headerBar.setClusterCount(count);
        }
    },

    onToggleHiddenAttribute: function (showHidden) {
        var view       = this.getView(),
            dataEntity = view.dataEntity;

        if (showHidden) {
            dataEntity.showHiddenAttribute();
        } else {
            dataEntity.hideHiddenAttribute();
        }
    },

    onDataRecordDeleteSuccess: function (dataRecord) {
        var view = this.getView();

        Unidata.showMessage(view.dataRecordDeleteSuccessText);
        view.fireEvent('deletesuccess', dataRecord);
        view.fireEvent('refreshend');
    },

    onDataRecordDeleteFailure: function (dataRecord) {
        var view = this.getView();

        this.refreshDataCard();
        view.fireEvent('deletefailure', dataRecord);
    },

    updateHeaderDataCardStatusInfo: function () {
        var view          = this.getView(),
            header        = view.headerBar,
            readOnly      = view.getReadOnly(),
            dataRecord    = view.getDataRecord(),
            cardConst     = Unidata.view.steward.dataviewer.card.data.DataCardConst,
            viewerConst   = Unidata.view.steward.dataviewer.DataViewerConst,
            dataCardState = null;

        if (!header || !dataRecord) {
            return;
        }

        if (dataRecord.get('approval') === viewerConst.ETALON_APPROVAL_PENDING) {
            dataCardState = cardConst.DATACARD_STATE_PENDING;
        } else if (readOnly === true) {
            dataCardState = cardConst.DATACARD_STATE_READONLY;
        }

        header.updateDataCardStateInfo(dataCardState);
    },

    onClassifierNodesChange: function () {
        var view       = this.getView(),
            dataRecord = this.getDataRecord(),
            promise;

        view.fireEvent('datacardlocked');
        view.fireEvent('refreshstart');

        promise = Unidata.view.steward.dataviewer.DataViewerLoader.loadClassifierNodes(dataRecord);

        promise.then(
            function (classifierNodes) {
                view.fireEvent('datacardunlocked');
                view.fireEvent('refreshend');

                view.fireEvent('classifiernodesload', classifierNodes);
            },
            function () {
                view.fireEvent('datacardunlocked');
                view.fireEvent('refreshend');
            }
        ).done();
    },

    onEtalonInfoMenuItemClick: function () {
        this.showEtalonInfoWindow();
    },

    onJmsPublishButtonClick: function (btn) {
        var title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('dataviewer>confirmJmsRefresh');

        Unidata.showPrompt(title, msg, this.doJmsPublish, this, btn, null);
    },

    doJmsPublish: function () {
        var DataRecordUtil = Unidata.util.api.DataRecord,
            view       = this.getView(),
            dataRecord = view.getDataRecord(),
            etalonId   = dataRecord.get('etalonId'),
            date       = view.getTimeIntervalDate(),
            cfg;

        cfg = {
            etalonId: etalonId,
            date: date
        };

        DataRecordUtil.publishJms(cfg)
            .then(function () {
                Unidata.showMessage(view.publishJmsSuccessText);
            })
            .done();
    },

    /**
     * Отобразить окно с доп.информацией об эталонной записи
     */
    showEtalonInfoWindow: function () {
        var window,
            etalonInfo;

        etalonInfo = this.buildEtalonInfo();

        window = Ext.create('Unidata.view.steward.dataviewer.card.data.EtalonInfoWindow', {
                etalonInfo: etalonInfo
            }
        );

        window.show();
    },

    /**
     * Построить информацию об эталоне
     * @returns {{updateDate: *, updatedBy: *, createDate: *, createdBy: *, etalonId: *, entityName: *, entityDisplayName: *, entityTypeDisplayName: (*|String), sourceSystems: (*|sourceSystems)}|*}
     */
    buildEtalonInfo: function () {
        var view = this.getView(),
            TimeIntervalUtil = Unidata.util.TimeInterval,
            MetaRecordUtil = Unidata.util.MetaRecord,
            dataRecord = view.getDataRecord(),
            metaRecord = view.getMetaRecord(),
            timeIntervalStore = view.getTimeIntervalStore(),
            timeIntervals = timeIntervalStore.getRange(),
            etalonInfo,
            sourceSystems;

        sourceSystems = TimeIntervalUtil.pluckSourceSystems(timeIntervals);

        etalonInfo = {
            updateDate: dataRecord.get('updateDate'),
            updatedBy: dataRecord.get('updatedBy'),
            createDate: dataRecord.get('createDate'),
            createdBy: dataRecord.get('createdBy'),
            etalonId: dataRecord.get('etalonId'),
            gsn: dataRecord.get('gsn'),
            entityName: metaRecord.get('name'),
            entityDisplayName: metaRecord.get('displayName'),
            entityTypeDisplayName: MetaRecordUtil.getTypeDisplayName(metaRecord),
            sourceSystems: sourceSystems
        };

        return etalonInfo;
    }
});