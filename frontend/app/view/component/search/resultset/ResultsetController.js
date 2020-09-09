Ext.define('Unidata.view.component.search.resultset.ResultsetController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.resultset',

    useQueryCount: 0, // использовать ли поисковой запрос
    store: null,

    onPageSizeChange: function (combobox, pageSize) {
        var view = this.getView(),
            store = this.store,
            queryPanel = view.getQueryPanel();

        if (store && !Ext.isEmpty(pageSize)) {
            store = store.getSource();

            store.setPageSize(parseInt(pageSize));

            if (queryPanel) {
                queryPanel.doSearch();
            }

            view.fireEvent('changepagesize', pageSize);
        }
    },

    onResultSetGridViewReady: function (grid) {
        var view = this.getView(),
            gridView = grid.view;

        view.toolTip = Ext.create('Ext.tip.ToolTip', {
            target: gridView.el,
            delegate: '.x-grid-cell li span.un-result-grid-item-data',
            trackMouse: true,
            renderTo: Ext.getBody(),
            listeners: {
                beforeshow: function (tip) {
                    var tipText;

                    tipText = Ext.String.ellipsis(tip.triggerElement.innerHTML, 255);
                    // на данный момент не требуется кодировать в данном месте т.к. кодируется в при рендере результатов поиска
                    // tipText = Ext.htmlEncode(tipText); // не забываем про XSS

                    tip.update(tipText);
                }
            }
        });
    },

    buildSearchHitIcon: function (status) {
        var icon = '';

        switch (status) {
            case 'INACTIVE':
                icon = '<i class="fa fa-trash-o"></i>';
                break;
        }

        return icon;
    },

    // TODO: refactoring
    renderColumn: function (value, metadata, record) {
        var me = this,
            view = this.getView(),
            str = '',
            s = '',
            metaRecord = view.getMetaRecord(),
            template = '',
            preview = record.getAssociatedData()['preview'],
            icon = this.buildSearchHitIcon(record.get('status')),
            mainItems,
            simpleItems,
            toEntityDefaultDisplayAttributes = view.getToEntityDefaultDisplayAttributes(),
            pathsMainDisplayable,
            pathsDisplayable,
            parseFormats,
            metaAttribute,
            toDate,
            fromDate,
            useDefaultDisplayAttributes;

        parseFormats = {
            Date: 'Y-m-d',
            Timestamp: 'Y-m-d\\TH:i:s',
            Time: '\\TH:i:s'
        };

        preview = preview.map(function (item) {
            var metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.field);

            item.order = 0;

            if (metaAttribute) {
                item.order = metaAttribute.get('order') || 0;
            }

            return item;
        });

        // сортируем отображаемые в порядке определения в модели
        preview = Ext.Array.sort(preview, function (a, b) {
            if (a.order < b.order) {
                return -1;
            } else if (a.order > b.order) {
                return 1;
            }

            return 0;
        });

        pathsMainDisplayable = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
                property: 'mainDisplayable',
                value: true
            }]);

        pathsDisplayable = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
            property: 'displayable',
            value: true
        }]);

        pathsDisplayable = Ext.Array.difference(pathsDisplayable, pathsMainDisplayable);

        useDefaultDisplayAttributes = Ext.isArray(toEntityDefaultDisplayAttributes) && toEntityDefaultDisplayAttributes.length > 0;

        // если используем список отображаемых атрибуты, то выводим только их под видом гл.отображаемых атрибутов
        if (useDefaultDisplayAttributes) {
            pathsMainDisplayable = toEntityDefaultDisplayAttributes;
        }

        mainItems = Ext.Array.filter(preview, function (item) {
            return Ext.Array.contains(pathsMainDisplayable, item.field);
        }, this);

        mainItems.forEach(function (item, index) {
            var formattedValue,
                formattedValues,
                fullValue;

            template        = '<li>{0}<span class="un-result-grid-item-data un-result-grid-item-data__important">{1}</span></li>';
            metaAttribute   = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.field);
            formattedValue  = Unidata.util.DataAttributeFormatter.formatValueByAttribute(metaAttribute, item.value, parseFormats);
            formattedValues = Unidata.util.DataAttributeFormatter.formatValuesByAttribute(metaAttribute, item.values, parseFormats);
            fullValue       = me.buildFullAttributeValue(formattedValue, formattedValues);

            fullValue = Ext.htmlEncode(fullValue); // не забываем про XSS

            s = Ext.String.format(template, index === 0 ? icon : '', fullValue);
            str += s;
        });

        // если не используем список отображаемых атрибуты, то выводим отображаемые атрибуты
        if (!useDefaultDisplayAttributes) {
            simpleItems = Ext.Array.filter(preview, function (item) {
                return Ext.Array.contains(pathsDisplayable, item.field);
            }, this);

            simpleItems.forEach(function (item) {
                var formattedValue,
                    formattedValues,
                    fullValue;

                template        = '<li><span class="un-result-grid-item-data">{0}</span></li>';
                metaAttribute   = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.field);
                formattedValue  = Unidata.util.DataAttributeFormatter.formatValueByAttribute(metaAttribute, item.value, parseFormats);
                formattedValues = Unidata.util.DataAttributeFormatter.formatValuesByAttribute(metaAttribute, item.values, parseFormats);
                fullValue       = me.buildFullAttributeValue(formattedValue, formattedValues);

                fullValue = Ext.htmlEncode(fullValue); // не забываем про XSS

                s = Ext.String.format(template, fullValue);
                str += s;
            });
        }

        if (view.getAllPeriodSearch()) {
            template = '<li><span class="un-result-grid-item-data">{0}&nbsp;-&nbsp;{1}</span></li>';

            toDate = Ext.Date.parse(record.getValueFromObjectMap('$to'), Unidata.Config.getDateTimeFormatServer());
            toDate = Ext.Date.format(toDate, Unidata.Config.getDateFormat());

            if (!toDate) {
                toDate = '<span class="un-timeinterval-infinity-text">' + Ext.String.htmlEncode(Unidata.Config.getMaxDateSymbol()) + '</span>';
            }

            fromDate = Ext.Date.parse(record.getValueFromObjectMap('$from'), Unidata.Config.getDateTimeFormatServer());
            fromDate = Ext.Date.format(fromDate, Unidata.Config.getDateFormat());

            if (!fromDate) {
                fromDate = '<span class="un-timeinterval-infinity-text">' + Ext.String.htmlEncode(Unidata.Config.getMinDateSymbol()) + '</span>';
            }

            s = Ext.String.format(template, fromDate, toDate);

            str += s;
        }

        return '<ul>' + str + '</ul>';
    },

    buildFullAttributeValue: function (value, values) {
        var tpl,
            result;

        tpl = Ext.create('Ext.Template' , [
            '{firstValue:htmlEncode}',
            ' ',
            '({otherwiseValues:htmlEncode})'
        ]);
        tpl.compile();

        if (Ext.isArray(values) && values.length > 1) {
            result = tpl.apply({
                firstValue: Ext.Array.slice(values, 0, 1),
                otherwiseValues: Ext.Array.slice(values, 1).join(', ')
            });

        } else {
            result = value;
        }

        return result;
    },

    /**
     * Убираем выделение и использование поискового запроса
     */
    deselectAll: function () {
        var view = this.getView(),
            selectionModel = this.lookupReference('resultsetGrid').getSelectionModel();

        view.suspendEvent('selectionchange');
        selectionModel.deselectAll();
        view.resumeEvent('selectionchange');

        this.fireSelectionchange(selectionModel, 0);
    },

    /**
     * Выделение всех результатов поиска (используем поисковой запрос для визарда)
     */
    selectAll: function () {
        var view = this.getView(),
            selectionModel = this.lookupReference('resultsetGrid').getSelectionModel(),
            useQueryCount = this.getStoreSourceTotalCount();

        view.suspendEvent('selectionchange');
        selectionModel.deselectAll();
        view.resumeEvent('selectionchange');

        this.fireSelectionchange(selectionModel, useQueryCount);
    },

    selectPage: function () {
        var selectionModel = this.lookupReference('resultsetGrid').getSelectionModel();

        selectionModel.selectAll();
    },

    /**
     *
     * @public
     * @returns {Unidata.model.search.SearchHit[]}
     */
    getSelectedSearchHits: function () {
        var resultSetGrid  = this.lookupReference('resultsetGrid'),
            selectionModel = resultSetGrid.getSelectionModel(),
            selected;

        selected = selectionModel.getSelected().getRange();

        return selected;
    },

    /**
     * При выделении/отмене выделения
     */
    onSelectionchange: function (selectionModel) {
        this.fireSelectionchange(selectionModel, 0);
    },

    fireSelectionchange: function (selectionModel, useQueryCount) {
        this.useQueryCount = useQueryCount;
        this.fireViewEvent('selectionchange', selectionModel, useQueryCount);
    },

    /**
     * Обрабатываем эвент перед кликом на результат,
     * при некоторых ситуациях отменяем дефолтное поведение
     */
    onBeforeitemclick: function (view, record, item, index, e) {
        var selectionModel = view.getSelectionModel();

        if (selectionModel.locked) {
            return;
        }

        // если кликаем чекбокс, то не открываем запись
        if (Ext.get(e.target).hasCls('x-grid-row-checker')) {
            return false;
        }

        // если кликаем зажав Ctrl, то выделяем запись, но не открываем её
        if (e.ctrlKey && !this.getView().isEditModeDisabled()) {
            if (selectionModel.isSelected(record)) {
                selectionModel.deselect(record);
            } else {
                selectionModel.select(record, true);
            }

            return false;
        }
    },

    updateSelectionMode: function () {
        var view = this.getView(),
            selectionModel = this.lookupReference('resultsetGrid').getSelectionModel(),
            useQuery = (this.useQueryCount > 0),
            selectionMode = (selectionModel.getCount() > 0 || useQuery),
            selectAllButton = this.lookupReference('selectAllButton');

        this.getViewModel().set('selectionMode', selectionMode);

        if (view.isEditModeDisabled()) {
            return;
        }

        if (useQuery) {
            view.setEditMode(view.editModeType.QUERY);
        } else if (selectionMode) {
            view.setEditMode(view.editModeType.SELECTION);
        } else {
            view.setEditMode(view.editModeType.NONE);
        }

        selectAllButton.setPressed(useQuery);

    },

    onBeforeload: function () {
        this.getViewModel().set('selectionMode', false);
    },

    onResultsetLoad: function (store, records, success) {
        if (success) {
            this.refreshPaging();
            this.fireViewEvent('changed', this.getView(), store, records);
        }

        this.updateSelectionMode();
    },

    createChainedStore: function (source) {
        var view = this.getView(),
            pageSize = Unidata.Config.getCustomerCfg()['SEARCH_ROWS'];

        if (view.getStorePageSize() > 0) {
            pageSize = view.getStorePageSize();
        }

        this.store =  Ext.create('Ext.data.ChainedStore', {
            pageSize: pageSize,
            source: source,
            listeners: {
                load: this.onStoreLoad,
                scope: this
            }
        });

        this.store.on('datachanged', this.onStoreDataChanged.bind(this));

        view.resultsetGrid.setStore(this.store);
        view.pagingToolbar.setStore(this.store.getSource());

        return this.store;
    },

    onStoreLoad: function (store, records, successful, operation) {
        var response,
            responseText,
            totalCount,
            totalCountLimit;

        if (successful) {
            response = operation.getResponse();

            // данные могут быть загружены не через ajax запрос
            if (response) {
                responseText = Ext.JSON.decode(response.responseText);
                totalCount = responseText['total_count'];
                totalCountLimit = responseText['total_count_limit'];
            }

            if (totalCountLimit) {
                this.lookupReference('pagingToolbar').setOutOfLimit(totalCount > totalCountLimit);
            }
        }
    },

    updateStorePageSize: function (pageSize) {
        if (this.store) {
            this.store.setPageSize(pageSize);
            this.refreshPaging();
        }
    },

    onStoreDataChanged: function () {
        this.refreshPaging();
    },

    updateSourceStore: function (source) {
        var store = this.store;

        if (!store) {
            this.createChainedStore(source);
        } else {
            store.setSource(source);
        }
    },

    // TODO: причесать clearResultset & refreshPaging
    clearResultset: function () {
        var store = this.store,
            reader;

        store.getSource().removeAll();
        store.getSource().totalCount = 0;
        store.getFilters().clear();

        reader = store.getSource().getProxy().getReader();
        reader.rawData = null;
    },

    refreshPaging: function () {
        var view       = this.getView(),
            store      = this.store,
            totalCount = this.getStoreSourceTotalCount(),
            pageSize   = store.getPageSize(),
            resultSetCount = 0,
            reader;

        reader = store.getSource().getProxy().getReader();

        if (reader.rawData) {
            resultSetCount = reader.rawData['total_count_real'];
        }
        view.setResultsetCount(resultSetCount);
        view.setIsPagingEnable(totalCount > pageSize);
    },

    updateMetaRecord: function () {
        this.clearResultset();
        this.refreshPaging();
    },

    onDataImportClick: function (button, event) {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            wizardWindow;

        event.stopEvent();

        wizardWindow = Ext.widget({
            xtype: 'dataimport.window',
            entityName: metaRecord ? metaRecord.get('name') : null
        });

        wizardWindow.show();
    },

    getStoreSourceTotalCount: function () {
        var store = this.store,
            source     = store.getSource();

        return source.getTotalCount();
    },

    updateAllPeriodSearch: function () {
    }
});
