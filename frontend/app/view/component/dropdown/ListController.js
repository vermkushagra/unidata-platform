Ext.define('Unidata.view.component.dropdown.ListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.dropdownpickerfield.list',

    nameCellRenderer: function (value, metaData, searchHit) {
        return this.getDisplayValue(searchHit);
    },

    /**
     * Инициализируем постоянные параметры запроса
     */
    initExtraParams: function () {
        var viewModel      = this.getViewModel(),
            view = this.getView(),
            displayAttributes = view.displayAttributes,
            metaRecord     = viewModel.get('entity'),
            isLookupEntity = viewModel.get('isLookupEntity'),
            store          = viewModel.getStore('dropdownList'),
            proxy          = store.getProxy(),
            searchFields,
            returnFields,
            sortFields,
            facets         = [];

        facets.push('published_only');

        sortFields = this.buildSortFields(metaRecord);

        if (isLookupEntity) {
            searchFields = this.getSearchFieldsLookupEntity();
        } else {
            searchFields = this.getSearchFieldsEntity();
        }

        returnFields = Unidata.util.UPathMeta.buildAttributePaths(metaRecord, [{
            fn: Ext.bind(
                this.displayableAttributesFilter,
                this,
                [displayAttributes],
                true
            )
        }]);

        returnFields = Ext.Array.merge(searchFields, returnFields);

        proxy.setExtraParam('facets', facets);
        proxy.setExtraParam('searchFields', searchFields);
        proxy.setExtraParam('returnFields', returnFields);
        proxy.setExtraParam('sortFields', sortFields);
        proxy.setExtraParam('entity', metaRecord.get('name'));
    },

    /**
     * Фильтр для отображаемых атрибутов
     *
     * @param attribute
     * @param {string[]} [displayAttributes] - атрибуты для отображения
     * @returns {boolean}
     */
    displayableAttributesFilter: function (attribute, displayAttributes) {
        var mainDisplayable = attribute.get('mainDisplayable'),
            attributeName = attribute.get('name');

        if (!Ext.isEmpty(displayAttributes)) {
            return (displayAttributes.indexOf(attributeName) !== -1);
        }

        return mainDisplayable;
    },

    /**
     * Возвращает список критериев для сортировки
     *
     * @param metaRecord
     * @returns {Array}
     */
    buildSortFields: function (metaRecord) {
        var sortFields = [],
            attributesPath,
            attributes;

        // все главные отображаемые поля - пути
        attributesPath = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [
            {
                property: 'mainDisplayable',
                value: true
            },
            {
                property: 'typeCategory',
                value: 'simpleDataType'
            }
        ]);

        // все главные отображаемые - атрибуты
        attributes = Unidata.util.UPathMeta.findAttributesByPaths(metaRecord, attributesPath);

        //
        Ext.Array.each(attributes, function (attribute) {
            sortFields.push({
                field: Unidata.util.UPathMeta.buildAttributePath(metaRecord, attribute),
                type: attribute.get('typeValue'),
                order: 'ASC'
            });
        });

        return sortFields;
    },

    abortLastRequest: function () {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList'),
            proxy     = store.getProxy();

        if (store.isLoading()) {
            proxy.abort();
        }
    },

    setSaytProxy: function () {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList'),
            proxy;

        this.abortLastRequest();

        proxy = Ext.createByAlias('proxy.data.searchproxysayt');

        store.setProxy(proxy);
        this.initExtraParams();
    },

    setSimpleProxy: function () {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList'),
            proxy;

        this.abortLastRequest();

        proxy = Ext.createByAlias('proxy.data.searchproxysimple');

        store.setProxy(proxy);
        this.initExtraParams();
    },

    /**
     * Возвращает поисковые поля для реестров (инициализация значения выпадашки)
     *
     * @returns {string[]}
     */
    getSearchFieldsEntity: function () {
        var searchFields = ['$etalon_id'];

        return searchFields;
    },

    /**
     * Возвращает поисковые поля для справочников (инициализация значения выпадашки)
     *
     * @returns {string[]}
     */
    getSearchFieldsLookupEntity: function () {
        var viewModel    = this.getViewModel(),
            searchFields = [viewModel.get('codeAttributeName')];

        return searchFields;
    },

    /**
     * Возвращает массив поисковых полей
     */
    getSearchFieldsByMetaRecord: function () {
        var viewModel  = this.getViewModel(),
            metaRecord = viewModel.get('entity'),
            searchFields;

        searchFields = Unidata.util.UPathMeta.buildAttributePaths(metaRecord, [
            {
                property: 'searchable',
                value: true
            },
            // поля с типом boolean не включаем см. UN-1477
            {
                filterFn: function (record) {
                    return record.get('typeValue') !== 'Boolean';
                }
            }
        ]);

        return searchFields;
    },

    loadStore: function (value, onLoad, onFailure) {
        var viewModel   = this.getViewModel(),
            view        = this.getView(),
            store       = viewModel.getStore('dropdownList'),
            proxy       = store.getProxy(),
            extraParams = proxy.getExtraParams(),
            loadCfg;

        view.fireEvent('loading', true);

        onLoad = onLoad || Ext.emptyFn;
        onFailure = onFailure || Ext.emptyFn;

        Ext.apply(extraParams, {
            searchFields: this.getSearchFieldsByMetaRecord(),
            text: value,
            fetchAll: !value,
            asOf: this.getAsOfExtraParam()
        });

        proxy.setExtraParams(extraParams);

        loadCfg = {
            callback: function (records, operation, success) {
                success ? onLoad(records, operation, success) : onFailure(records, operation, success);
                view.fireEvent('loading', false);
            }
        };

        this.abortLastRequest();

        store.loadPage(1, loadCfg);
    },

    loadStoreByCodeValue: function (codeValue, onLoad, onFailure) {
        var me        = this,
            viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList'),
            proxy;

        onLoad = onLoad || Ext.emptyFn;
        onFailure = onFailure || Ext.emptyFn;

        this.setSimpleProxy();
        proxy = store.getProxy();
        proxy.setQTypeTerm();

        function onActiveLoad (records, operation, success) {
            if (!records.length) {
                me.loadFacetStoreByCodeValue(codeValue, false, onInactiveLoad, onInactiveFailure);
            } else {
                onLoad(records, operation, success);

                proxy.setQTypeMatch();
                me.setSaytProxy();
            }
        }

        function onActiveFailure (records, operation, success) {
            onFailure(records, operation, success);
        }

        function onInactiveLoad (records, operation, success) {
            onLoad(records, operation, success);

            proxy.setQTypeMatch();
            me.setSaytProxy();
        }

        function onInactiveFailure (records, operation, success) {
            onFailure(records, operation, success);
        }

        // сначала производим поиск среди активных записей
        // если запись не найдена - производим поиск по удаленным
        this.loadFacetStoreByCodeValue(codeValue, true, onActiveLoad, onActiveFailure);
    },

    loadFacetStoreByCodeValue: function (codeValue, activeFlag, onLoad, onFailure) {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList'),
            loadCfg,
            params,
            facets;

        onLoad = onLoad || Ext.emptyFn;
        onFailure = onFailure || Ext.emptyFn;

        facets = ['published_only'];

        if (!activeFlag) {
            facets.push('inactive_only');
        }

        params = {
            fetchAll: false,
            text: codeValue,
            facets: facets,
            asOf: this.getAsOfExtraParam()
        };

        loadCfg = {
            callback: function (records, operation, success) {
                success ? onLoad(records, operation, success) : onFailure(records, operation, success);
            },
            params: params
        };

        this.abortLastRequest();

        store.load(loadCfg);
    },

    getRecordByCodeValue: function (codeValue) {
        var viewModel      = this.getViewModel(),
            isLookupEntity = viewModel.get('isLookupEntity'),
            result;

        if (isLookupEntity) {
            result = this.getRecordByCodeValueLookupEntity(codeValue);
        } else {
            result = this.getRecordByCodeValueEntity(codeValue);
        }

        return result;
    },

    getRecordByCodeValueEntity: function (codeValue) {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList'),
            index;

        index = store.findBy(function (searchHit) {
            var obj = searchHit.mapToObjectValues(),
                recordCodeValue = obj['$etalon_id'];

            //TODO: We use == here instead of ===, cause backend not return type properly
            return recordCodeValue == codeValue;
        });

        return index !== -1 ? store.getAt(index) : null;
    },

    getRecordByCodeValueLookupEntity: function (codeValue) {
        var viewModel         = this.getViewModel(),
            store             = viewModel.getStore('dropdownList'),
            codeAttributeName = viewModel.get('codeAttributeName'),
            index;

        index = store.findBy(function (searchHit) {
            var obj = searchHit.mapToObjectValues(),
                mapValues = obj[codeAttributeName];

            //TODO: We use == here instead of ===, cause backend not return type properly
            return Ext.Array.contains(mapValues, codeValue);
        });

        return index !== -1 ? store.getAt(index) : null;
    },

    getDisplayValues: function (meta, searchHit) {
        var preview = searchHit.getAssociatedData()['preview'],
            view = this.getView(),
            displayAttributes = view.displayAttributes,
            useAttributeNameForDisplay = view.useAttributeNameForDisplay,
            result = [],
            paths,
            parseFormats,
            metaAttribute,
            sorted;

        paths = Unidata.util.UPathMeta.buildAttributePaths(meta, [{
            fn: Ext.bind(
                this.displayableAttributesFilter,
                this,
                [displayAttributes],
                true
            )
        }]);

        parseFormats = {
            Date: 'Y-m-d',
            Timestamp: 'Y-m-d\\TH:i:s.uP',
            Time: '\\TH:i:s'
        };

        preview = preview.filter(function (item) {
            return Ext.Array.contains(paths, item.field);
        });

        preview = preview.map(function (item) {
            var DataAttributeFormatterUtil = Unidata.util.DataAttributeFormatter;

            metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(meta, item.field);
            item.displayName = metaAttribute.get('displayName');
            item.value = DataAttributeFormatterUtil.formatValueByAttribute(metaAttribute, item.value, parseFormats);
            item.values = DataAttributeFormatterUtil.formatValuesByAttribute(metaAttribute, item.values, parseFormats);
            item.order = metaAttribute.get('order') || 0;

            return item;
        });

        // сортируем отображаемые в порядке определения в модели
        sorted = Ext.Array.sort(preview, function (a, b) {
            if (a.order < b.order) {
                return -1;
            } else if (a.order > b.order) {
                return 1;
            }

            return 0;
        });

        Ext.Array.each(sorted, function (item) {
            var values = item.values,
                value,
                tpl,
                formatValue;

            tpl = Ext.create('Ext.Template' , [
                '{firstValue:htmlEncode}',
                ' ',
                '({otherwiseValues:htmlEncode})'
            ]);
            tpl.compile();

            if (Ext.isArray(values) && values.length > 1) {
                formatValue = tpl.apply({
                    firstValue: Ext.Array.slice(values, 0, 1),
                    otherwiseValues: Ext.Array.slice(values, 1).join(', ')
                });

                value = formatValue;
            } else {
                value = item.value;
            }

            if (useAttributeNameForDisplay) {
                value = Ext.String.format('{0}: {1}', item.displayName, value);
            }

            result.push(value);
        });

        return result;
    },

    getDisplayValue: function (record) {
        var viewModel     = this.getViewModel(),
            view = this.getView(),
            entity        = viewModel.get('entity'),
            displayValues = [''],
            delimiter,
            useAttributeNameForDisplay = view.useAttributeNameForDisplay,
            result;

        if (record) {
            displayValues = this.getDisplayValues(entity, record);
        }

        if (useAttributeNameForDisplay) {
            delimiter = ' ';
        } else {
            delimiter = ' ';
        }

        result = displayValues.length ? displayValues.join(delimiter) : Unidata.i18n.t('other>noDisplayAttributes');

        if (record.get('status') === 'INACTIVE') {
            result = '(' + Unidata.i18n.t('other>removed').toUpperCase() + ') ' + result;
        }

        return result;
    },

    getCodeValue: function (record) {
        var viewModel      = this.getViewModel(),
            isLookupEntity = viewModel.get('isLookupEntity'),
            result;

        if (isLookupEntity) {
            result = this.getCodeValueLookupEntity(record);
        } else {
            result = this.getCodeValueEntity(record);
        }

        return result;
    },

    getCodeValueEntity: function () {
        return null;
    },

    /**
     * Возвращает все кодовые значения
     *
     * @param record
     * @returns {Array}
     */
    getCodeValuesLookupEntity: function (record) {
        var viewModel = this.getViewModel(),
            preview = record.getAssociatedData()['preview'],
            codeAttributeName = viewModel.get('codeAttributeName'),
            isLookupEntity = viewModel.get('isLookupEntity'),
            result = [];

        if (!isLookupEntity) {
            return result;
        }

        Ext.Array.each(preview, function (item) {
            if (item.field === codeAttributeName) {
                result = item.values;

                return false;
            }
        });

        return result;
    },

    getCodeValueLookupEntity: function (record) {
        var viewModel         = this.getViewModel(),
            codeAttributeName = viewModel.get('codeAttributeName'),
            obj               = record.mapToObject();

        return obj[codeAttributeName];
    },

    onItemClick: function (grid, record, item, index, e, eOpts) {
        this.getView().fireEvent('itemselect', grid, record, item, index, e, eOpts);
    },

    initTooltip: function (grid) {
        var me       = this,
            gridView = grid.view;

        this.toolTip = Ext.create('Ext.tip.ToolTip', {
            target: gridView.el,
            delegate: '.x-grid-cell',
            trackMouse: true,
            renderTo: Ext.getBody(),
            listeners: {
                beforeshow: function (tip) {
                    var view                   = me.getView(),
                        displayAttributes      = view.displayAttributes,
                        DataAttributeFormatter = Unidata.util.DataAttributeFormatter,
                        //trigger = tip.triggerElement,
                        row                    = tip.triggerElement.parentElement,
                        searchHit              = gridView.getRecord(row),
                        //column = view.getHeaderByCell(trigger),
                        viewModel              = me.getViewModel(),
                        metaRecord             = viewModel.get('entity'),
                        value,
                        tipTemplate,
                        tipHtml;

                    if (!searchHit) {
                        return false;
                    }

                    value = DataAttributeFormatter.buildEntityTitleFromSearchHit(metaRecord, searchHit, displayAttributes);

                    if (!value) {
                        return false;
                    }

                    tipTemplate = Ext.create('Ext.Template', [
                        '{value:htmlEncode}'
                    ]);
                    tipTemplate.compile();
                    tipHtml = tipTemplate.apply({
                        value: value
                    });

                    tip.update(tipHtml);
                }
            }
        });
    },

    onViewReady: function (grid) {
        var view = this.getView(),
            showListTooltip = view.getShowListTooltip();

        if (showListTooltip) {
            this.initTooltip(grid);
        }
    },

    setStoreInstance: function (store) {
        var grid   = this.lookupReference('resultGrid'),
            paging = this.lookupReference('resultPaging');

        grid.reconfigure(store);
        paging.setStore(store);
    },

    /**
     * Устанавливает пустую версию стора
     */
    setEmptyStore: function () {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('emptyStore');

        this.setStoreInstance(store);
    },

    /**
     * Устанавливает рабочую копию стора
     */
    setWorkStore: function () {
        var viewModel = this.getViewModel(),
            store     = viewModel.getStore('dropdownList');

        this.setStoreInstance(store);
    },

    getAsOfExtraParam: function () {
        var view = this.getView(),
            asOf = view.getAsOf();

        if (Ext.isEmpty(asOf)) {
            asOf = null;
        } else {
            asOf = Ext.Date.format(view.getAsOf(), Unidata.Config.getDateTimeFormatProxy());
        }

        return asOf;
    }
});
