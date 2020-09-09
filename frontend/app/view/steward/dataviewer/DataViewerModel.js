Ext.define('Unidata.view.steward.dataviewer.DataViewerModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataviewer',

    constructor: function (options) {
        // отменяем наследование значений из родительской viewModel
        options        = options || {};
        options.parent = null;

        this.callParent([options]);
    },

    data: {
        /**
         * Данные устанавливаемые методами
         */
        etalonId: null,
        metaRecord: null,
        dataRecord: null,
        referenceRelations: null,
        timeInterval: null,
        timeIntervalDate: null,
        timeIntervalStore: null,
        relationsDigest: null,
        originRecords: null,
        drafts: false,                    // параметр для получении записи - черновика
        operationId: null,
        readOnly: null,                   // readonly вьювера

        /**
         * Разрешенные операции для вьювера
         */
        allowMergeOperation: null,        // вьюверу разрешено выполнять операцию merge
        allowSaveOperation: null,         // вьюверу разрешено выполнять операцию save
        allowDeleteOperation: null,       // вьюверу разрешено выполнять операцию delete

        /**
         * Вычисляемые данные, проставляются где то в коде
         */
        activeCard: null,                 // имя активной карточки
        someIntervalNeedAccept: false,    // какой-либо интервал требует согласования
        intervalNeedAccept: false,        // текущий период актуальности требует согласования
        intervalDeleted: false,           // текущий период был удален и требует согласования
        viewerStatus: null,               // статус в котором находится viewer

        clusterCount: 0,
        alwaysHideMergeButtons: null // связана со одноименным свойством в DataViewer

        /**
         * Формулы текущей модели
         */
        //etalonStatus
        //etalonPhantom
        //etalonApproval
        //etalonHasWorkflowTask
        //entityName
        //entityDisplayName
        //userHasCreateRight
        //userHasUpdateRight
        //userHasReadRight
        //userHasDeleteRight
        //userHasEntitySecurityLabel
        //isMetaEntity
        //isMetaLookup
        //dataCardSelected
        //approveBarVisible
        //dqBarVisible
        //dqErrorCount
        //saveActionVisible
        //mergeButtonVisible
        //deleteActionVisible
        //сreateTimeIntervalButtonVisible
        //deleteTimeIntervalButtonVisible
    },

    stores: {},

    formulas: {
        /**
         * Наличие у пользователя права create на справочник / реестр с именем entityName
         */
        userHasCreateRight: {
            bind: {
                bindTo: '{dataRecord.rights}'
            },
            /**
             * @param {Unidata.model.user.Right} rights
             * @returns {boolean}
             */
            get: function (rights) {
                return rights ? rights.get('create') : false;
            }
        },

        /**
         * Наличие у пользователя права read на справочник / реестр с именем entityName
         */
        userHasReadRight: {
            bind: {
                bindTo: '{dataRecord.rights}'
            },
            /**
             * @param {Unidata.model.user.Right} rights
             * @returns {boolean}
             */
            get: function (rights) {
                return rights ? rights.get('read') : false;
            }
        },

        /**
         * Наличие у пользователя права update на справочник / реестр с именем entityName
         */
        userHasUpdateRight: {
            bind: {
                bindTo: '{dataRecord.rights}'
            },
            /**
             * @param {Unidata.model.user.Right} rights
             * @returns {boolean}
             */
            get: function (rights) {
                return rights ? rights.get('update') : false;
            }
        },

        /**
         * Наличие у пользователя права delete на справочник / реестр с именем entityName
         */
        userHasDeleteRight: {
            bind: {
                bindTo: '{dataRecord.rights}'
            },
            /**
             * @param {Unidata.model.user.Right} rights
             * @returns {boolean}
             */
            get: function (rights) {
                return rights ? rights.get('delete') : false;
            }
        },

        /**
         * Признак наличия у пользователя назначеных меток безопасности  на справочник / реестр с именем entityName
         */
        userHasEntitySecurityLabel: {
            bind: {
                entityName: '{entityName}',
                deep: true
            },
            get: function (getter) {
                var hasSecurityLabel = false,
                    entityName       = getter.entityName;

                if (entityName) {
                    hasSecurityLabel = Unidata.util.SecurityLabel.hasEntitySecurityLabel(entityName);
                }

                hasSecurityLabel = Ext.coalesceDefined(hasSecurityLabel, false);

                return hasSecurityLabel;
            }
        },

        /**
         * Статус эталона текущей записи
         */
        etalonStatus: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function (dataRecord) {
                var status     = null;

                if (dataRecord) {
                    status = dataRecord.get('status');
                }

                status = Ext.coalesceDefined(status, null);

                return status;
            }
        },

        /**
         * Значение флага phantom текущей записи
         */
        etalonPhantom: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function (dataRecord) {
                var phantom = true;

                if (dataRecord) {
                    phantom = dataRecord.phantom || !dataRecord.get('etalonId');
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        /**
         * Значение флага approval текущей записи
         */
        etalonApproval: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function (dataRecord) {
                var approval = null;

                if (dataRecord) {
                    approval = dataRecord.get('approval');
                }

                approval = Ext.coalesceDefined(approval, null);

                return approval;
            }
        },

        /**
         * Флаг наличия задач на текущей записи
         */
        etalonHasWorkflowTask: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function (dataRecord) {
                var result = false,
                    workflowState;

                if (!dataRecord) {
                    return false;
                }

                workflowState = dataRecord.workflowState();

                if (workflowState && workflowState.getCount() > 0) {
                    result = true;
                }

                result = Ext.coalesceDefined(result, false);

                return result;
            }
        },

        /**
         * Кодовое имя текущего справочника / реестра
         */
        entityName: {
            bind: {
                metaRecord: '{metaRecord}',
                deep: true
            },
            get: function (getter) {
                var name       = null,
                    metaRecord = getter.metaRecord;

                if (metaRecord) {
                    name = metaRecord.get('name');
                }

                name = Ext.coalesceDefined(name, null);

                return name;
            }
        },

        /**
         * Признак того что отображаем реестр
         */
        isMetaEntity: {
            bind: {
                metaRecord: '{metaRecord}',
                deep: true
            },
            get: function (getter) {
                var result = false;

                result = Unidata.util.MetaRecord.isEntity(getter.metaRecord);
                result = Ext.coalesceDefined(result, false);

                return result;
            }
        },

        /**
         * Признак того что отображаем справочник
         */
        isMetaLookup: {
            bind: {
                metaRecord: '{metaRecord}',
                deep: true
            },
            get: function (getter) {
                var result = false;

                result = Unidata.util.MetaRecord.isLookup(getter.metaRecord);
                result = Ext.coalesceDefined(result, false);

                return result;
            }
        },

        /**
         * Отображаемое имя текущего справочника / реестра
         */
        entityDisplayName: {
            bind: {
                metaRecord: '{metaRecord}',
                deep: true
            },
            get: function (getter) {
                var displayName = null,
                    metaRecord  = getter.metaRecord;

                if (metaRecord) {
                    displayName = metaRecord.get('displayName');
                }

                displayName = Ext.coalesceDefined(displayName, null);

                return displayName;
            }
        },

        /**
         * Определяет видимость панели с кнопками управления состояним записи:
         * (кнопки: сохранения / удаления / консолидации / ...)
         */
        dataCardSelected: {
            bind: {
                activeCard: '{activeCard}'
            },
            get: function (getter) {
                var visible     = false,
                    viewerConst = Unidata.view.steward.dataviewer.DataViewerConst;

                // кнопки видны только, когда активна вкладка с данными записи
                if (getter.activeCard === viewerConst.DATA_CARD) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет видимость панели с кнопками перехода по разделам:
         * (кнопки: эталонная запись / история / исходные записи / ...)
         */
        cardSectionsVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (!getter.etalonPhantom) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет видимость кнопки обновления записи
         */
        refreshButtonVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                deep: true
            },
            get: function (getter) {
                var visible     = false;

                if (!getter.etalonPhantom) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Вспомогательная формула - текущий timestamp.
         * Используется для того чтоб гарантированно срабатывали другие формулы завязаные на {dataRecord}
         *
         * TODO: Ivan Marshalkin необходимо подумать над generic решением
         */
        dataVersion: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function () {
                return Ext.timestamp();
            }
        },

        /**
         * Вспомогательная формула - текущий timestamp.
         * Используется для того чтоб гарантированно срабатывали другие формулы завязаные на {metaRecord}
         *
         * TODO: Ivan Marshalkin необходимо подумать над generic решением
         */
        metaVersion: {
            bind: {
                bindTo: '{metaRecord}',
                deep: true
            },
            get: function () {
                return Ext.timestamp();
            }
        },

        /**
         * Количество ошибок правил качества данных
         */
        dqErrorCount: {
            bind: {
                dataRecord: '{dataRecord}',
                dataVersion: '{dataVersion}',
                deep: true
            },
            get: function (getter) {
                var count      = 0,
                    dataRecord = getter.dataRecord,
                    errors;

                if (dataRecord) {
                    errors = dataRecord.dqErrors();
                    count  = errors.getCount();
                }

                count = Ext.coalesceDefined(count, 0);

                return count;
            }
        },

        /**
         * Определяет видимость панели правила качества данных
         */
        dqBarVisible: {
            bind: {
                dqErrorCount: '{dqErrorCount}',
                deep: true
            },
            get: function (getter) {
                var visible = false,
                    count   = getter.dqErrorCount;

                if (count) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет видимость панели с кнопками перехода по разделам:
         * (кнопки: эталонная запись / история / исходные записи / ...)
         */
        dottedMenuButtonVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (!getter.etalonPhantom) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет видимость кнопки сохранения записи
         */
        saveActionVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                userHasUpdateRight: '{userHasUpdateRight}',
                userHasCreateRight: '{userHasCreateRight}',
                allowSaveOperation: '{allowSaveOperation}',
                readOnly: '{readOnly}',
                dataCardSelected: '{dataCardSelected}',
                selectedInactiveTimeInterval: '{selectedInactiveTimeInterval}',
                deep: true
            },
            get: function (getter) {
                var dataCardSelected = getter.dataCardSelected;

                if (!dataCardSelected) {
                    return false;
                }

                // если запрещена операция сохранения => кнопка недоступна
                // или сам вьювер только для просмотра
                if (!getter.allowSaveOperation || getter.readOnly) {
                    return false;
                }

                if (getter.selectedInactiveTimeInterval) {
                    return false;
                }

                // пользователь имеет права на создание и запись новая
                if (getter.userHasCreateRight && getter.etalonPhantom) {
                    return true;
                }

                // пользователь имеет права на редактирование
                if (getter.userHasUpdateRight) {
                    return true;
                }

                return false;
            }
        },

        /**
         * Опредяляет read only для карточки с данными
         */
        dataCardReadOnly: {
            bind: {
                saveActionVisible: '{saveActionVisible}',
                deep: true
            },
            get: function (getter) {
                var readOnly = !getter.saveActionVisible;

                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        /**
         * Определяет видимость кнопки консолидации записей
         */
        manualMergeActionVisible: {
            bind: {
                metaRecord: '{metaRecord}',
                dataRecord: '{dataRecord}',
                allowMergeOperation: '{allowMergeOperation}',
                readOnly: '{readOnly}',
                alwaysHideMergeButtons: '{alwaysHideMergeButtons}',
                dataCardSelected: '{dataCardSelected}'
            },
            get: function (getter) {
                var rights,
                    dataCardSelected = getter.dataCardSelected;

                if (!dataCardSelected) {
                    return false;
                }

                // если запрещена операция мержа => кнопка недоступна
                // или сам вьювер только для просмотра
                if (getter.alwaysHideMergeButtons || !getter.allowMergeOperation || getter.readOnly) {
                    return false;
                }

                rights = getter.dataRecord.getRights();

                if (!rights) {
                    return false;
                }

                return rights.get('merge');
            }
        },

        mergePermited: {
            bind: {
                bindTo: '{dataRecord.rights}',
                deep: true
            },
            get: function (rights) {
                if (!rights) {
                    return false;
                }

                return rights.get('merge');
            }
        },

        jmsPublishActionVisible: {
            bind: {
                dataCardSelected: '{dataCardSelected}'
            },
            get: function (getter) {
                var dataCardSelected = getter.dataCardSelected,
                    jmsPublishActionVisible;

                jmsPublishActionVisible = dataCardSelected;

                return jmsPublishActionVisible;
            }
        },

        refreshActionVisible: {
            bind: {
                dataCardSelected: '{dataCardSelected}'
            },
            get: function (getter) {
                var dataCardSelected = getter.dataCardSelected,
                    refreshActionVisible;

                refreshActionVisible = dataCardSelected;

                return refreshActionVisible;
            }
        },

        actionsVisible: {
            bind: {
                mergeActionVisible: '{mergeActionVisible}',
                manualMergeActionVisible: '{manualMergeActionVisible}'
            },
            get: function (getter) {
                var mergeActionVisible = getter.mergeActionVisible,
                    manualMergeActionVisible = getter.manualMergeActionVisible,
                    jmsPublishActionVisible = getter.jmsPublishActionVisible,
                    refreshActionVisible = getter.refreshActionVisible,
                    actionsVisible;

                actionsVisible =    mergeActionVisible ||
                                    manualMergeActionVisible ||
                                    jmsPublishActionVisible ||
                                    refreshActionVisible;

                return actionsVisible;
            }
        },

        /**
         * Определяет видимость кнопки консолидации записей
         */
        mergeActionVisible: {
            bind: {
                manualMergeActionVisible: '{manualMergeActionVisible}',
                clusterCount: '{clusterCount}',
                alwaysHideMergeButtons: '{alwaysHideMergeButtons}'
            },
            get: function (getter) {
                var manualMergeActionVisible = getter.manualMergeActionVisible,
                    clusterCount = getter.clusterCount,
                    alwaysHideMergeButtons = getter.alwaysHideMergeButtons,
                    visible;

                // если запрещена операция мержа => кнопка недоступна
                // или сам вьювер только для просмотра
                if (alwaysHideMergeButtons || !manualMergeActionVisible || !clusterCount) {
                    return false;
                }

                visible = manualMergeActionVisible && clusterCount > 0;

                visible = Ext.coalesceDefined(visible, true);

                return visible;
            }
        },

        /**
         * Определяет видимость кнопки удаления записи
         */
        deleteActionVisible: {
            bind: {
                userHasDeleteRight: '{userHasDeleteRight}',
                allowDeleteOperation: '{allowDeleteOperation}',
                readOnly: '{readOnly}',
                dataCardSelected: '{dataCardSelected}',
                selectedInactiveTimeInterval: '{selectedInactiveTimeInterval}',
                deep: true
            },
            get: function (getter) {
                var dataCardSelected = getter.dataCardSelected;

                if (!dataCardSelected) {
                    return false;
                }

                // если запрещена операция удаления => кнопка недоступна
                // или сам вьювер только для просмотра
                if (!getter.allowDeleteOperation || getter.readOnly) {
                    return false;
                }

                if (getter.selectedInactiveTimeInterval) {
                    return false;
                }

                if (getter.userHasDeleteRight) {
                    return true;
                }

                return false;
            }
        },

        /**
         * Определяет видимость кнопки восстановления записи
         */
        restoreActionVisible: {
            bind: {
                etalonStatus: '{etalonStatus}',
                dataRecord: '{dataRecord}',
                userHasUpdateRight: '{userHasUpdateRight}',
                userHasCreateRight: '{userHasCreateRight}',
                readOnly: '{readOnly}',
                dataCardSelected: '{dataCardSelected}'
            },
            get: function (getter) {
                var DataViewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
                    rights,
                    dataCardSelected = getter.dataCardSelected,
                    etalonStatus = getter.etalonStatus;

                if (!dataCardSelected) {
                    return false;
                }

                // сам вьювер только для просмотра
                if (getter.readOnly) {
                    return false;
                }

                rights = getter.dataRecord.getRights();

                if (!rights) {
                    return false;
                }

                if (etalonStatus !== DataViewerConst.ETALON_STATUS_INACTIVE &&
                    etalonStatus !== DataViewerConst.ETALON_STATUS_RESTORE) {
                    return false;
                }

                return rights.get('restore');
            }
        },

        footerBarVisible: {
            bind: {
                saveActionVisible: '{saveActionVisible}',
                deleteActionVisible: '{deleteActionVisible}',
                restoreActionVisible: '{restoreActionVisible}',
                dataCardSelected: '{dataCardSelected}'
            },
            get: function (getter) {
                var visible,
                    saveActionVisible = getter.saveActionVisible,
                    deleteActionVisible = getter.deleteActionVisible,
                    restoreActionVisible = getter.restoreActionVisible,
                    dataCardSelected = getter.dataCardSelected;

                visible = dataCardSelected && (saveActionVisible || deleteActionVisible || restoreActionVisible);

                return visible;
            }
        },

        /**
         * Определяет видимость кнопки создания периода актуальности
         */
        createTimeIntervalButtonVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                userHasUpdateRight: '{userHasUpdateRight}',
                dataCardReadOnly: '{dataCardReadOnly}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                // сам вьювер только для просмотра
                if (getter.readOnly) {
                    return false;
                }

                if (getter.etalonPhantom || getter.dataCardReadOnly) {
                    return false;
                } else if (getter.userHasUpdateRight) {
                    return true;
                }

                return false;
            }
        },

        /**
         * Определяет видимость кнопки удаления периода актуальности
         */
        deleteTimeIntervalButtonVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                etalonStatus: '{etalonStatus}',
                etalonApproval: '{etalonApproval}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
                    visible     = false;

                // сам вьювер только для просмотра
                if (getter.readOnly) {
                    return false;
                }

                if (getter.etalonPhantom) {
                    return false;
                }

                // если неизвестен статус эталона => кнопка недоступна
                if (!getter.etalonStatus) {
                    return false;
                }

                if (getter.etalonApproval === viewerConst.ETALON_APPROVAL_PENDING) {
                    return false;
                }

                if (getter.etalonStatus === viewerConst.ETALON_STATUS_ACTIVE) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет видимость кнопки просмотра обратных ссылок
         */
        backrelButtonVisible: {
            bind: {
                isMetaLookup: '{isMetaLookup}',
                deep: true
            },
            get: function (getter) {
                var visible = true;

                if (getter.isMetaLookup) {
                    visible = false;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет видимость футера записи
         */
        footerEnabled: {
            bind: {
                viewerStatus: '{viewerStatus}',
                deep: true
            },
            get: function () {
                var enabled = false;

                // находимся ли в статусе DONE?
                if (this.isViewerDone()) {
                    enabled = true;
                }

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        }
    },

    /**
     * Возвращает истину если вьювер находится только в статусе "готов"
     *
     * @returns {boolean}
     */
    isViewerDone: function () {
        var viewerConst = Unidata.view.steward.dataviewer.DataViewerConst,
            result      = false,
            statuses;

        statuses = this.get('viewerStatus');

        // вьювер либо имеет просто статус "готов" или массив из одного элемента статуса "готов"
        if (statuses === viewerConst.VIEWER_STATUS_DONE) {
            result = true;
        } else if (Ext.isArray(statuses) && statuses.length === 1 && statuses[0] === viewerConst.VIEWER_STATUS_DONE) {
            result = true;
        }

        return result;
    },

    /**
     * Возвращает нормализованное представление статуса вьювера (массив уникальных статусов)
     *
     * @returns {*|Array|{displayName, editor, renderer}}
     */
    getNormalizeViewerStatus: function () {
        var viewerStatus = this.get('viewerStatus');

        if (Ext.isString(viewerStatus)) {
            viewerStatus = [viewerStatus];
        }

        if (!Ext.isArray(viewerStatus)) {
            viewerStatus = [];
        }

        // Ext.Array.unique возвращает новый массив поэтому формулы использующие viewerStatus сработают
        return Ext.Array.unique(viewerStatus);
    },

    /**
     * Добавляет статус к вьюверу
     *
     * @param statusConst
     */
    addViewerStatus: function (statusConst) {
        var viewerStatus = this.getNormalizeViewerStatus();

        viewerStatus.push(statusConst);
        // Ext.Array.unique возвращает новый массив поэтому формулы использующие viewerStatus сработают
        viewerStatus = Ext.Array.unique(viewerStatus);

        this.set('viewerStatus', viewerStatus);
    },

    /**
     * Удаляет статус у вьювера
     *
     * @param statusConst
     */
    removeViewerStatus: function (statusConst) {
        var viewerStatus = this.getNormalizeViewerStatus();

        viewerStatus = Ext.Array.remove(viewerStatus, statusConst);
        // Ext.Array.unique возвращает новый массив поэтому формулы использующие viewerStatus сработают
        viewerStatus = Ext.Array.unique(viewerStatus);

        this.set('viewerStatus', viewerStatus);
    }
});
