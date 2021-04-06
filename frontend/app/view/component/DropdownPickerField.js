/**
 * Выпадающий список с содержимым справочника / реестра
 *
 * ПРИМЕРЫ ИСПОЛЬЗОВАНИЯ
 *
 * Для справочника:
 *
 * Ext.create('Unidata.view.component.DropdownPickerField', {
 *               entityType: 'lookupentity',
 *               entityName: 'ZHDORMIR',
 *               codeValue:288,
 *               value: '288'
 *           });
 *
 * Для реестра:
 *
 * Ext.create('Unidata.view.component.DropdownPickerField', {
 *               entityType: 'entity',
 *               entityName: 'H_STAN_OB',
 *               codeValue: '01b63b01-42e9-479c-9199-ba59abb8d117',
 *               value: '01b63b01-42e9-479c-9199-ba59abb8d117'
 *           });
 *
 * Конфигурационные поля:
 * entityName - Наименование реестра / справочника по которому производится поиск
 * entityType - тип сущности для отображения справочник (true) / реестр (false)
 *
 * @author Sergey Shishigin
 *
 * @author Ivan Marshalkin
 * @date 2015-12-20 практически полный рефакторинг компонента
 *
 */

/**
 * TODO: возможные улучшения
 * *) Реализация метода setValue который принимает кодовый атрибут и делает всю грязную работу сам
 * *) Сохранение отображаемого рекорда в свойство компонента
 * *) Проработка реакции на ошибочные ситуации
 * *) При вызове getCodeValue в случае с реестром возваращать etalonId
 */

Ext.define('Unidata.view.component.DropdownPickerField', {
    extend: 'Ext.form.field.Picker',

    requires: [
        'Unidata.view.component.dropdown.List'
    ],

    xtype: 'dropdownpickerfield',

    config: {
        entityType: null,
        entityName: null
    },

    autoEnable: true, // ставит disabled = true в некоторых случаях

    codeValue: null,
    displayValue: null,

    etalonId: null,
    metaRecord: null,

    gridPanel: null,

    openLookupRecordHidden: true, // признак того, что триггер openLookupRecord не отображается
    addLookupRecordHidden: true,  // признак того, что триггер addLookupRecord не отображается

    editable: false,

    displayAttributes: null, // атрибуты для отображения
    showValueTooltip: true,  // отображать тултип выбранного значения (при свернутом выпадающем списке)
    showListTooltip: true,   // отображать тултип значения выпадающего списка (при развернутом выпадающем списке)
    useAttributeNameForDisplay: null,

    triggers: {
        openLookupRecord: {
            hideOnReadOnly: false,
            hidden: true,
            renderTpl: [
                '<div id="{triggerId}" class="{baseCls} {baseCls}-{ui} {cls} {cls}-{ui} {extraCls} {childElCls}" style="display: none; background-image: none; position: relative; width: 22px; line-height: 18px;" data-qtip="' + Unidata.i18n.t('common:open') + '">' +  // jscs:ignore maximumLineLength
                '<div class="" style="vertical-align: baseline; background: none; height: 12px; vertical-align: middle; display: table-cell; padding: 0 3px; font-size: 12px;">',  // jscs:ignore maximumLineLength
                '{[values.$trigger.renderBody(values)]}',
                '<span class="icon-launch" style="color: #666666;"></span>',
                '</div>' +
                '</div>'
            ]
            // handler: function () {} обработчик назначается по событию render
        },
        addLookupRecord: {
            hideOnReadOnly: true,
            hidden: true,
            cls: 'x-dropdownpickerfield-add-trigger',
            renderTpl: [
                '<div id="{triggerId}" class="{baseCls} {baseCls}-{ui} {cls} {cls}-{ui} {extraCls} {childElCls}" style="display: none; background-image: none; position: relative; width: -moz-min-content; line-height: 18px;" data-qtip="' + Unidata.i18n.t('common:create') + '">' +  // jscs:ignore maximumLineLength
                '<div class="" style="vertical-align: baseline; background: none; height: 12px; vertical-align: middle; display: table-cell; padding: 0 3px;">',  // jscs:ignore maximumLineLength
                '{[values.$trigger.renderBody(values)]}',
                Unidata.i18n.t('common:create').toLowerCase(),
                '</div>' +
                '</div>'
            ]
            // handler: function () {} обработчик назначается по событию render
        },
        clear: {
            hideOnReadOnly: true,
            cls: 'x-form-clear-trigger'
            // handler: function () {} обработчик назначается по событию render
        }
    },

    statics: {
        cache: {},                                           // кеш моделей
        lud: {},                                             // время получения информации по моделе
        deferreds: {},                                       // список обещаний в очереди на получение данных
        cacheTTL: 10 * Unidata.constant.Delay.MINUTE,        // TTL элементов кеша

        /**
         * Возвращает обещание на получение модели справочника / реестра
         *
         * @param entityType
         * @param entityName
         * @returns {null|Ext.promise|*}
         */
        cacheMetaRecord: function (entityType, entityName) {
            var me = this,
                deferred = new Ext.Deferred(),
                metaRecord,
                deferreds,
                loadPromise;

            this.checkCache(entityName);

            metaRecord = this.cache[entityName];
            deferreds = this.deferreds[entityName];

            if (Ext.isEmpty(metaRecord)) {
                if (Ext.isEmpty(deferreds)) {
                    this.deferreds[entityName] = [];

                    loadPromise = Unidata.util.api.MetaRecord.getMetaRecord({
                        entityName: entityName,
                        entityType: entityType
                    });

                    loadPromise.then(
                        function (metaRecord) {
                            me.cache[entityName] = metaRecord;
                            me.lud[entityName] = Ext.timestamp();

                            me.resolveMetaRecordPromise(entityName, metaRecord);
                        },
                        function () {
                            me.rejectMetaRecordPromise(entityName);
                        }
                    ).done();
                }

                this.deferreds[entityName].push(deferred);
            } else {
                deferred.resolve(metaRecord);
            }

            return deferred.promise;
        },

        /**
         * Возвращает обещание на получения модели реестра
         *
         * @param entityName
         * @returns {*}
         */
        cacheEntityMetaRecord: function (entityName) {
            return this.cacheMetaRecord('Entity', entityName);
        },

        /**
         * Возвращает обещание на получения модели справочника
         *
         * @param entityName
         * @returns {*}
         */
        cacheLookupEntityMetaRecord: function (entityName) {
            return this.cacheMetaRecord('LookupEntity', entityName);
        },

        /**
         * Резолвим все промисы из очереди на ожидание получения данных
         *
         * @param entityName
         */
        resolveMetaRecordPromise: function (entityName, metaRecord) {
            var deferreds = this.deferreds[entityName];

            if (Ext.isArray(deferreds)) {
                Ext.Array.each(deferreds, function (deferred) {
                    deferred.resolve(metaRecord);
                });
            }

            delete this.deferreds[entityName];
        },

        /**
         * Отклоняем все промисы из очереди на ожидание получения данных
         *
         * @param entityName
         */
        rejectMetaRecordPromise: function (entityName) {
            var deferreds = this.deferreds[entityName];

            if (Ext.isArray(deferreds)) {
                Ext.Array.each(deferreds, function (deferred) {
                    deferred.reject();
                });
            }

            delete this.deferreds[entityName];
        },

        /**
         * Валидируем актуальность кеша. Сбрасывает кеш если не валиден уже
         *
         * @param entityName
         */
        checkCache: function (entityName) {
            var lud = this.lud[entityName],
                ts = Number(new Date());

            if (lud) {
                if ((ts < lud) || (ts - lud - this.cacheTTL > 0)) {
                    delete this.cache[entityName];
                    delete this.lud[entityName];
                }
            }
        },

        /**
         * Сбрасывает кеш метамоделей по кодовому имени реестра / справочника
         */
        resetMetaRecordCacheByName: function (entityName) {
            delete this.cache[entityName];
            delete this.lud[entityName];
        },

        /**
         * Очищает полностью кеш метамоделей
         */
        resetMetaRecordCache: function () {
            this.cache = [];
            this.lud = [];
        }
    },

    initComponent: function () {
        this.originalRecordId = null;

        this.callParent(arguments);

        if (!Ext.Array.contains(['entity', 'lookupentity'], this.entityType)) {
            throw 'dropdownpickerfield: entityType not set or incorrect';
        }

        this.enableBubble('datarecordopen');
        this.enableBubble('wantaddetalonid');

        if (!this.readOnly) {
            this.setDisabled(true);
        }

        // создаем picker во время инициализации компонента, чтоб в дальнейшем использовать ссылку this.gridPanel
        this.getPicker();

        this.on('collapseIf', this.onCollapseIf, this);
        this.on('expand', this.onExpandDropdownPicker, this);
        this.on('collapse', this.onCollapseDropdownPicker, this);
        this.on('change', this.onChangeDropdownPicker, this);
        this.on('render', this.onRenderDropdownPicker, this);

        this.gridPanel.on('itemselect', this.onItemSelect, this);
    },

    onBlur: function (e) {
        if (e.within(this.getPicker().getEl(), true)) {
            e.stopEvent();

            return;
        }

        this.callParent(arguments);
    },

    /**
     * Переводим занчение в в нижний регистр, т.к. по коду происходит проверка в нижнем регистре
     *
     * @param value
     * @returns {*|Object}
     */
    setEntityType: function (value) {
        return this.callParent([value.toLowerCase()]);
    },

    /**
     * Создание выпадающего элемента
     *
     * @returns {Ext.panel.Panel|*}
     */
    createPicker: function () {
        var me = this,
            showListTooltip,
            picker;

        showListTooltip = this.showListTooltip;

        this.gridPanel = Ext.create('Unidata.view.component.dropdown.List', {
            displayAttributes: this.displayAttributes,
            showListTooltip: showListTooltip,
            useAttributeNameForDisplay: this.useAttributeNameForDisplay
        });

        picker = Ext.create('Ext.panel.Panel', {
            pickerField: me,
            floating: true,
            hidden: true,
            height: 400,
            width: 350,
            anchor: '100% 100',
            overflowY: 'auto',
            items: [
                this.gridPanel
            ]
        });

        this.gridPanel.on('loading', function (flag) {
            picker.setLoading(flag);
        });

        return picker;
    },

    updateDisplayValueByCodeValue: function (codeValue) {
        if (codeValue !== null) {
            this.setCodeValue(codeValue);
            this.displayValue = null;
            this.loadMetaAndInitPicker(this.getEntityType(), this.getEntityName());
        } else {
            this.clearValue();
        }
    },

    /**
     * Устанавливает текст для отображения без генрации события change
     *
     * @param value
     */
    setValueSilent: function (value) {
        this.suspendEvent('change');
        this.setValue(value);
        this.resumeEvent('change');
    },

    /**
     * Преобразовать codeValue в соответствии с типом (String|Integer)
     * @param value {String|Integer}
     * @returns {String|Integer}
     */
    castCodeValue: function (value) {
        var metaRecord     = this.metaRecord,
            metaCodeAttribute,
            MetaRecordUtil = Unidata.util.MetaRecord,
            isLookupMeta,
            isIntegerCodeAttribute,
            isStringValue;

        isLookupMeta  = MetaRecordUtil.isLookup(metaRecord);
        isStringValue = Ext.isString(value);

        if (isLookupMeta && isStringValue) {
            metaCodeAttribute      = metaRecord.getCodeAttribute();
            isIntegerCodeAttribute = metaCodeAttribute.get('simpleDataType') === 'Integer';

            if (isIntegerCodeAttribute) {
                value = parseInt(value);
            }
        }

        return value;
    },

    setCodeValue: function (value) {
        value = this.castCodeValue(value);
        this.codeValue = value;
        this.displayOpenRecordTriggerOnrender();
    },

    setCodeValues: function (value) {
        this.codeValues = value;
    },

    /**
     * Возвращает кодовое значение
     *
     * @returns {null}
     */
    getCodeValue: function () {
        return this.codeValue;
    },

    /**
     * Возвращает массив кодовых значений,
     * после мержа их больше 1
     *
     * @returns {Array}
     */
    getCodeValues: function () {
        return this.codeValues || [];
    },

    getDisplayValue: function () {
        return this.displayValue;
    },

    getRecord: function () {
        return this.record;
    },

    /**
     * Возвращает etalonId
     *
     * @returns {null}
     */
    getEtalonId: function () {
        return this.etalonId;
    },

    displayTriggerOnrender: function (triggerName, hideFlg) {
        var trigger = this.getTrigger(triggerName);

        // если триггер скрываем в режиме RO и сейчас RO то не отображаем
        if (trigger.hideOnReadOnly && this.readOnly) {
            hideFlg = true;
        }

        hideFlg ? trigger.hide() : trigger.show();
    },

    displayOpenRecordTriggerOnrender: function () {
        var hideFlg = this.openLookupRecordHidden,
            trigger = this.getTrigger('openLookupRecord');

        // если триггер скрываем в режиме RO и сейчас RO то не отображаем
        if (trigger.hideOnReadOnly && this.readOnly) {
            hideFlg = true;
        }

        if (!this.etalonId && !this.codeValue) {
            hideFlg = true;
        }

        hideFlg ? trigger.hide() : trigger.show();
    },

    onItemSelect: function (grid, record) {
        var etalonId = this.etalonId,
            valueChanged = false;

        if (etalonId) {
            valueChanged = (record.get('etalonId') !== etalonId);
        } else {
            valueChanged = true;
        }

        this.setValueByRecord(record);
        this.collapse();

        // обновляем layout смотри UN-3050
        this.updateLayout();

        if (valueChanged) {
            // значение изменилось
            this.fireEvent('valuechange', this);
        }
    },

    /**
     * Является ли текущее значение оригинальным (присвоенным при инициализации)
     * @returns {boolean}
     */
    isInitialValue: function () {
        var etalonId = this.getEtalonId();

        if (etalonId) {
            return (etalonId == this.originalRecordId);
        } else {
            return (this.originalRecordId === null);
        }
    },

    loadPickerStore: function (value) {
        value = value || this.getValue();

        this.gridPanel.getController().loadStore(value);
        //TODO: add failure handling
    },

    /**
     * Устанавливает значения по выбранной записи
     * @param record
     */
    setValueByRecord: function (record) {
        var gridPanelController = this.gridPanel.getController(),
            displayValue = gridPanelController.getDisplayValue(record),
            codeValue = gridPanelController.getCodeValue(record),
            codeValues = gridPanelController.getCodeValuesLookupEntity(record);

        if (this.autoEnable) {
            this.setDisabled(false);
        }

        this.etalonId = record.get('etalonId');

        this.displayValue = displayValue;
        this.setCodeValue(codeValue);
        this.setCodeValues(codeValues);

        this.setValueSilent(this.displayValue);

        this.fireEvent('changecodevalue', this, this.getCodeValue());

        this.displayOpenRecordTriggerOnrender();
    },

    /**
     * Загружает запись по кодовому значению и инициализирует отображаемое значение
     */
    loadPickerStoreAndInitDisplayValue: function () {
        var me = this,
            deferred = Ext.create('Ext.Deferred'),
            component = this,
            displayValue = component.displayValue,
            etalonId = component.etalonId,
            codeValue = component.codeValue;

        function onLoad () {
            var record = me.gridPanel.getController().getRecordByCodeValue(codeValue);

            if (record) {
                me.setValueByRecord(record);
                me.originalRecordId = record.get('etalonId');
                deferred.resolve();
            } else {
                me.setValueSilent(Unidata.i18n.t('common:recordNotFound'));

                if (me.autoEnable) {
                    me.setDisabled(false);
                }
                me.originalRecordId = null;
                deferred.reject();
            }

            me.displayOpenRecordTriggerOnrender();
        }

        if (codeValue !== null) {
            if (displayValue) {
                this.setValueSilent(displayValue);
                me.originalRecordId = etalonId;
                deferred.resolve();
            } else {
                this.gridPanel.getController().loadStoreByCodeValue(codeValue, onLoad);
            }
        } else {
            if (this.autoEnable) {
                this.setDisabled(false);
            }

            deferred.resolve(); // reject?
        }

        return deferred.promise;
    },

    /**
     * Обработка событие render для элемента
     */
    onRenderDropdownPicker: function () {
        var triggerOpenLookupRecord = this.getTrigger('openLookupRecord'),
            triggerAddLookupRecord = this.getTrigger('addLookupRecord'),
            triggerClear = this.getTrigger('clear');

        // handler триггера не подходит т.к. он не срабатывает, если поле readonly
        triggerOpenLookupRecord.getEl().on({
            click: this.onOpenLookupRecordTriggerClick.bind(this)
        });

        // handler триггера не подходит т.к. он не срабатывает, если поле readonly
        triggerAddLookupRecord.getEl().on({
            click: this.onTriggerAddLookupRecordTriggerClick.bind(this)
        });

        // handler триггера не подходит т.к. он не срабатывает, если поле readonly
        triggerClear.getEl().on({
            click: this.onClearTriggerClick.bind(this)
        });

        this.loadMeta(this.entityName, this.onSuccessLoadMeta, this.onFailureLoadMeta);

        if (this.showValueTooltip) {
            this.initToolTip();
        }
    },

    /**
     * Инициализация показа подсказки по наведению на поле ввода
     */
    initToolTip: function () {
        var me          = this,
            input       = me,
            baseTooltip,
            toolTip;

        if (!input) {
            return;
        }

        if (input.tip !== undefined) {
            return;
        }

        baseTooltip = me.buildBaseToolTip();

        if (!baseTooltip) {
            return;
        }

        toolTip = Ext.create('Ext.tip.ToolTip', {
            target: input.getEl(),
            html: '',
            dismissDelay: 8000,
            cls: this.baseCls + '-tip',
            listeners: {
                beforeshow: this.onTooltipBeforeshow.bind(this)
            }
        });

        me.baseTooltip = baseTooltip;
        input.tip = toolTip;
    },

    onTooltipBeforeshow: function () {
        var me    = this,
            input = me,
            valueHtml,
            value,
            html;

        value = this.getRawValue();

        if (!value) {
            return false;
        }

        value = Ext.String.htmlEncodeMulti(value, 1);

        valueHtml = Ext.String.format('<div class="{0}">{1}</div>',
            this.baseCls + '-tip-value',
            value
        );

        html = Ext.String.format(me.baseTooltip, valueHtml);

        input.tip.setHtml(html);
    },

    buildBaseToolTip: function () {
        return '{0}';
    },

    /**
     * Обработчик нажатия триггера "Очистка поля"
     */
    onClearTriggerClick: function () {
        this.clearValue();
    },

    /**
     * Очистка поля
     */
    clearValue: function () {
        this.displayValue = null;
        this.setCodeValue(null);
        this.setCodeValues([]);
        this.etalonId = null;
        this.setValueSilent(this.displayValue);

        this.displayOpenRecordTriggerOnrender();

        this.fireEvent('valueclear', this);
        this.fireEvent('changecodevalue', this, this.getCodeValue());
    },

    /**
     * Обновляет значение в пикере по переданному рекорду
     *
     * @param record
     */
    updateValueByRecord: function (record) {
        var codeAttrName,
            simpleAttributes,
            codeAttrIndex,
            codeValue;

        if (Unidata.util.MetaRecord.isLookup(this.metaRecord)) {
            // TODO: what is aboute aliasCodeAttr?
            codeAttrName     = this.metaRecord.getCodeAttribute().get('name');
            simpleAttributes = record.simpleAttributes();
            codeAttrIndex    = simpleAttributes.findExact('name', codeAttrName);
            codeValue        = simpleAttributes.getAt(codeAttrIndex).get('value');
        } else {
            codeValue = record.get('etalonId');
        }

        this.setCodeValue(codeValue);
        this.setValue(codeValue);
        this.loadPickerStoreAndInitDisplayValue();
    },

    /**
     * Клик по открытия связанной сущности
     */
    onOpenLookupRecordTriggerClick: function () {
        var me = this,
            saveCallback,
            dataRecordBundle,
            DataRecordBundleUtil = Unidata.util.DataRecordBundle;

        function saveCallbackFn (view, record) {
            me.updateValueByRecord(record);
        }

        saveCallback = {
            fn: saveCallbackFn,
            context: me
        };

        if (this.etalonId) {
            dataRecordBundle = DataRecordBundleUtil.buildDataRecordBundle({
                etalonId: this.etalonId
            });

            // datarecordopen event cfg:
            //
            // dataRecordBundle {Unidata.util.DataRecordBundle} Набор структур по отображению записей
            // searchHit {Unidata.model.search.SearchHit} Результат поиска записи
            // metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель (optional)
            // saveCallback {function} Функция, вызываемая после сохранения открытой записи
            this.fireEvent('datarecordopen', {
                metaRecord: this.metaRecord,
                dataRecordBundle: dataRecordBundle,
                saveCallback: saveCallback
            });
        } else {
            this.loadPickerStoreAndInitDisplayValue().then(function () {
                me.onOpenLookupRecordTriggerClick();
            });
        }
    },

    /**
     * Клик по триггеру добавления новой связанной сущности
     */
    onTriggerAddLookupRecordTriggerClick: function () {
        var me = this,
            saveCallback;

        function saveCallbackFn (view, record) {
            me.updateValueByRecord(record);
        }

        saveCallback = {
            fn: saveCallbackFn,
            context: me
        };

        this.fireEvent('wantaddetalonid', this.metaRecord, saveCallback);
    },

    /**
     * Обработчик успешной загрузки метамодели
     */
    onSuccessLoadMeta: function (metaRecord) {
        var viewModel;

        if (this.isDestroyed) {
            return;
        }

        viewModel = this.gridPanel.getViewModel();

        this.metaRecord = metaRecord;

        // если у пользователя нет прав на создание то ему не доступна кнопка создания
        //if (!Unidata.Config.userHasRight(metaRecord.get('name'), 'create')) {
        //    this.addLookupRecordHidden = true;
        //}

        // по задаче UN-2742 создание связанной записи пока скрыто
        // т.к. не понятно теперь по процессу согласования что произойдет
        this.addLookupRecordHidden = true;

        // если у пользователя нет прав на просмотр то кнопка просмотр недоступна
        if (!Unidata.Config.userHasRight(metaRecord.get('name'), 'read')) {
            this.openLookupRecordHidden = true;
        }

        viewModel.set('entity', metaRecord);
        viewModel.set('entityType', this.entityType);
        viewModel.notify();

        this.gridPanel.getController().initExtraParams();

        this.loadPickerStoreAndInitDisplayValue();

        this.displayOpenRecordTriggerOnrender();
        this.displayTriggerOnrender('addLookupRecord', this.addLookupRecordHidden);

        this.fireEvent('loadmetarecord', this, metaRecord);
    },

    /**
     * Обработчик не успешной загрузки метамодели
     */
    onFailureLoadMeta: function () {
    },

    /**
     * Загружает мету и инициализирует выбро записи
     *
     * @param entityType
     * @param entityName
     * @returns {Ext.promise.Promise}
     */
    loadMetaAndInitPicker: function (entityType, entityName) {
        var deferred = Ext.create('Ext.Deferred');

        this.setEntityType(entityType);
        this.loadMeta(
            entityName,
            function (metaRecord) {
                this.onSuccessLoadMeta(metaRecord);
                deferred.resolve(metaRecord);
            },
            function () {
                deferred.reject();
            }
        );

        return deferred.promise;
    },

    /**
     * Загружает запись по etalonId и проставляет нужные значения
     *
     * @param etalonId
     */
    loadRecordByEtalonId: function (etalonId) {
        var deferred = Ext.create('Ext.Deferred'),
            me = this;

        Unidata.util.api.DataRecord.getDataRecord({etalonId: etalonId})
            .then(
                function (dataRecord) {
                    var entityType = dataRecord.get('entityType'),
                        entityName = dataRecord.get('entityName');

                    me.loadMetaAndInitPicker(dataRecord.get('entityType'), dataRecord.get('entityName'))
                        .then(
                            function (metaRecord) {
                                var displayValue;

                                displayValue = Unidata.util.DataAttributeFormatter.buildEntityTitleFromDataRecord(
                                    metaRecord,
                                    dataRecord
                                );

                                me.setEntityName(entityName);
                                me.setEntityType(entityType);
                                me.displayValue = displayValue;
                                me.etalonId = etalonId;
                                me.setValue(displayValue);

                                deferred.resolve();
                            },
                            function () {
                                deferred.reject();
                            }
                        )
                        .done();
                },
                function () {
                    me.setValueSilent(Unidata.i18n.t('common:recordNotFound'));
                    me.etalonId = null;

                    deferred.reject();
                }
            )
            .done();

        return deferred.promise;
    },

    /**
     * Загрузчик метамодели
     *
     * @param entityName
     * @param onSuccess
     * @param onFailure
     */
    loadMeta: function (entityName, onSuccess, onFailure) {
        onSuccess = onSuccess ? onSuccess : Ext.emptyFn;
        onFailure = onFailure ? onFailure : Ext.emptyFn;

        switch (this.entityType) {
            case 'lookupentity':
                this.loadLookupEntityMeta(entityName, onSuccess, onFailure);
                break;
            case 'entity':
                this.loadEntityMeta(entityName, onSuccess, onFailure);
                break;
        }
    },

    /**
     * Загружает метамодель для реестра
     *
     * @param entityName
     * @param onSuccess
     * @param onFailure
     */
    loadEntityMeta: function (entityName, onSuccess, onFailure) {
        var me = this,
            promise;

        promise = Unidata.view.component.DropdownPickerField.cacheEntityMetaRecord(entityName);

        promise
            .then(
                function (metaRecord) {
                    onSuccess.apply(me, [metaRecord]);
                },
                function () {
                    onFailure.apply(me);
                }
            )
            .done();
    },

    /**
     * Загружает метамодель для справочника
     *
     * @param entityName
     * @param onSuccess
     * @param onFailure
     */
    loadLookupEntityMeta: function (entityName, onSuccess, onFailure) {
        var me = this,
            promise;

        promise = Unidata.view.component.DropdownPickerField.cacheLookupEntityMetaRecord(entityName);

        promise
            .then(
                function (metaRecord) {
                    onSuccess.apply(me, [metaRecord]);
                },
                function () {
                    onFailure.apply(me);
                }
            )
            .done();
    },

    /**
     * Обработчик потери фокуса компонентом
     * @param e
     */
    onFocusLeave: function (e) {
        var me = this;

        // do not collapse a picker if 'focusleave' event target is one of four pagingtoolbar buttons
        if (!Ext.Array.contains(['first', 'prev', 'next', 'last'], e.fromComponent.itemId)) {
            me.collapse();
            me.callParent([
                e
            ]);
        }
    },

    onCollapseIf: function (e) {
        var picker = this.getPicker();

        if (picker && picker.rendered && e.within(picker.el)) {
            if (e.type !== 'mousewheel') {
                e.preventDefault();
                e.stopPropagation();
            }
        } else {
            this.collapse();
        }
    },

    /**
     * Обработчик раскрытия picker
     */
    onExpandDropdownPicker: function () {
        this.setEditable(true);

        // рабочая версия стора
        // меняем стор т.к. если загружаем стор, когда грид скрыт то ловим багу - дом разламывается см UN-1617
        this.gridPanel.setWorkStore();

        this.setValueSilent('');
        this.loadPickerStore();
    },

    /**
     * Обработчик схлопывания picker
     */
    onCollapseDropdownPicker: function () {
        this.setEditable(false);

        // пустая версия стора
        // меняем стор т.к. если загружаем стор, когда грид скрыт то ловим багу - дом разламывается см UN-1617
        this.gridPanel.setEmptyStore();

        this.setValueSilent(this.displayValue);
    },

    /**
     * Обработка события изменения значения в picker
     *
     * @param component
     * @param fieldValue
     */
    onChangeDropdownPicker: function (component, fieldValue) {
        this.loadPickerStore(fieldValue);
    }
});
