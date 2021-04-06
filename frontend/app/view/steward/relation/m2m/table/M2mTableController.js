/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2017-05-02
 */

Ext.define('Unidata.view.steward.relation.m2m.table.M2mTableController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.relation.m2mtable',

    /**
     * Отображает связи
     */
    displayRelations: function () {
        var me = this,
            view = me.getView(),
            viewModel = me.getViewModel(),
            allM2mRecordsStore = viewModel.getStore('m2mrecords'),
            filteredM2mRecordsStore = viewModel.getStore('filteredm2mrecords'),
            metaRecord = view.getMetaRecord(),
            dataRecord = view.getDataRecord(),
            metaRelation = view.getMetaRelation(),
            dataRelation = view.getDataRelation(),
            pageSize;

        pageSize = Unidata.uiuserexit.overridable.relation.M2m.getM2mRelationPageSize(metaRecord, dataRecord, metaRelation, dataRelation);

        allM2mRecordsStore.removeAll();
        allM2mRecordsStore.add(dataRelation);

        filteredM2mRecordsStore.pageSize = pageSize;
        filteredM2mRecordsStore.setSource(allM2mRecordsStore);
        filteredM2mRecordsStore.totalCount = dataRelation.length;
        this.createPagingStoreFilter(1);
    },

    /**
     * Обработчик раскрытия дополнительной информации по записи
     *
     * @param rowNode
     * @param dataRecord
     * @param expandRow
     * @param eOpts
     */
    onRecordRowExpand: function (rowNode, dataRecord) {
        var view = this.getView(),
            detailEl = this.getRowNodeDetailElement(rowNode),
            metaRelation = view.getMetaRelation(),
            values = [];

        metaRelation.simpleAttributes().each(function (metaAttribute) {
            var attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRelation, metaAttribute),
                dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(dataRecord, attributePath),
                formattedValue = '';

            if (dataAttribute) {
                formattedValue = Unidata.util.DataAttributeFormatter.formatValueByAttribute(
                    metaAttribute, dataAttribute.get('value'));
            }

            values.push({
                label: metaAttribute.get('displayName'),
                value: formattedValue,
                order: metaAttribute.get('order')
            });
        });

        // сортируем в порядке определения атрибутов на метамодели связи
        values = Ext.Array.sort(values, function (a, b) {
            return a.order - b.order;
        });

        if (detailEl && detailEl.dom) {
            // выводим список атрибутов связи или выводим сообщение о том что атрибуты отсуствуют
            if (!Ext.isEmpty(values)) {
                detailEl.setHtml(this.buildRowNodeDetailInfo(values, true));
            } else {
                values.push({
                    label: '',
                    value: Unidata.i18n.t('relation>noRelationAttributes'),
                    order: 0
                });

                detailEl.setHtml(this.buildRowNodeDetailInfo(values, false));
            }
        }

        // для нашего UX необходимо немного допилить rowexpander
        this.correctExpanderRowSpanAttribute(dataRecord);
    },

    /**
     * Корректирует объединение ячеек для rowexpander
     *
     * @param dataRecord
     */
    correctExpanderRowSpanAttribute: function (dataRecord) {
        var view = this.getView(),
            m2mlist = view.m2mlist,
            plugin = m2mlist.getPlugin('rowExpanderPlugin'),
            rowIdx = m2mlist.getStore().indexOf(dataRecord),
            expanderCell;

        // код выдран из метода Ext.grid.plugin.RowExpander -> toggleRow
        if (plugin.expanderColumn) {
            expanderCell = Ext.fly(m2mlist.getView().getRow(rowIdx)).down(plugin.expanderColumn.getCellSelector(), true);

            if (expanderCell) {
                // оригинальное значение 2
                expanderCell.rowSpan = 1;
            }
        }
    },

    /**
     * Возвращает html вертску с детальной информацией по записи
     *
     * @param values - объект с описанием отображаемых данных
     * @param showLabel - признак необходимости отображать лейблы (true - отображать)
     * @returns {*}
     */
    buildRowNodeDetailInfo: function (values, showLabel) {
        var tpl,
            html;

        tpl = new Ext.XTemplate(
            '<table style="width: 100%; line-height: 1.7;">',
            '<tpl for=".">',
            '<tr style="vertical-align: top;">',

            '<tpl if="this.isLabelVisible()">',
            '<td style="width: 20%;" class="un-attribute-label">{label:htmlEncode}:</td>',
            '</tpl>',

            '<td style="padding-left: 10px;" class="un-attribute-value">{value:htmlEncode}</td>',
            '</tr>',
            '</tpl>',
            '</table>',
            {
                isLabelVisible: function () {
                    return showLabel === true;
                }
            }
        );

        html = tpl.apply(values);

        return html;
    },

    /**
     * Обработчик схлопывания дополнительной информации по записи
     *
     * @param rowNode
     * @param record
     * @param expandRow
     * @param eOpts
     */
    onRecordRowCollapse: function (rowNode) {
        var detailEl = this.getRowNodeDetailElement(rowNode);

        if (detailEl && detailEl.dom) {
            detailEl.setHtml('');
        }
    },

    /**
     * Возвращает элемент для отображения дополнительной информации по записи ссылке
     *
     * @param rowNode
     * @returns {*}
     */
    getRowNodeDetailElement: function (rowNode) {
        return Ext.get(rowNode).down('.x-grid-rowbody div');
    },

    /**
     * Обновляет конфигурацию колонок грида
     */
    reconfigurelistRelationViewColumns: function () {
        var view = this.getView(),
            columns = [];

        // основная клолонка
        columns.push({
            text: Unidata.i18n.t('relation>records'),
            hideable: false,
            sortable: false,
            flex: 1,
            renderer: function (value, meta, record) {
                var DataAttributeFormatter = Unidata.util.DataAttributeFormatter,
                    displayValue = record.get('etalonDisplayNameTo');

                if (Ext.isEmpty(record.get('etalonIdTo'))) {
                    displayValue = Unidata.i18n.t('relation>relationNotSet');
                } else {}

                if (record.checkDirty() || record.phantom) {
                    displayValue = DataAttributeFormatter.getDirtyPrefix() + ' ' + displayValue;
                }

                return displayValue;
            }
        });

        // добавляем автогенерированые колонки
        columns = Ext.Array.merge(columns, this.getListRelationViewFirstColumn(2));

        columns.push({
            text: Unidata.i18n.t('relation>validity'),
            hideable: false,
            sortable: false,
            width: 200,
            renderer: function (value, meta, record) {
                var validFrom = record.get('validFrom'),
                    validTo = record.get('validTo'),
                    dateFormat = Unidata.Config.getDateFormat(),
                    resultParts = [];

                if (Ext.isDate(validFrom)) {
                    resultParts.push('c ' + Ext.Date.format(validFrom, dateFormat));
                }

                if (Ext.isDate(validTo)) {
                    resultParts.push(Unidata.i18n.t('relation>to').toLowerCase() + ' ' + Ext.Date.format(validTo, dateFormat));
                }

                return resultParts.join(' ');
            }
        });

        // колонка открытия связанной записи
        columns.push(
            {
                xtype: 'widgetcolumn',
                align: 'center',
                width: 30,
                hideable: false,
                widget: {
                    xtype: 'component',
                    hidden: true,
                    style: {
                        cursor: 'pointer'
                    },
                    tpl: Unidata.util.Icon.getLinearIcon('launch'),
                    tip: Unidata.i18n.t('common:open'),
                    data: {},
                    listeners: {
                        el: {
                            click: function () {
                                var record = this.component.getWidgetRecord(),
                                    dataRecordBundle,
                                    widgetCmp;

                                widgetCmp = this.component;

                                dataRecordBundle = Unidata.util.DataRecordBundle.buildDataRecordBundle({
                                    etalonId: record.get('etalonIdTo')
                                });

                                widgetCmp.enableBubble('datarecordopen');

                                widgetCmp.fireEvent('datarecordopen', {
                                    dataRecordBundle: dataRecordBundle
                                });
                            }
                        },
                        afterrender: function (cmp) {
                            // добавляем всплывашку
                            cmp.tip = Ext.create('Ext.tip.ToolTip', {
                                target: cmp.getEl(),
                                html: Unidata.i18n.t('common:open')
                            });
                        },
                        beforedestroy: function (cmp) {
                            // удаляем всплывашку
                            Ext.destroy(cmp.tip);
                        }
                    }
                }
            }
        );

        view.m2mlist.reconfigure(columns);
    },

    /**
     *
     * @param columnCount
     * @returns {Array}
     */
    getListRelationViewFirstColumn: function (columnCount) {
        var view = this.getView(),
            metaRelation = view.getMetaRelation(),
            values = [],
            columns = [];

        // обходим все простые атрибуты
        metaRelation.simpleAttributes().each(function (metaAttribute) {
            var attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRelation, metaAttribute);

            values.push({
                metaAttribute: metaAttribute,
                attributePath: attributePath,
                order: metaAttribute.get('order')
            });
        });

        // сортируем в порядке определения атрибутов на метамодели связи
        values = Ext.Array.sort(values, function (a, b) {
            return a.order - b.order;
        });

        // берем перве N столбцов
        values = Ext.Array.slice(values, 0, columnCount);

        // строим конфиг динамически добавляемых колонок
        Ext.Array.each(values, function (value) {
            columns.push({
                text: Ext.String.htmlEncode(value.metaAttribute.get('displayName')),
                metaAttribute: value.metaAttribute,
                attributePath: value.attributePath,
                //hideable: false,
                width: '20%',
                renderer: function (value, meta, record) {
                    var dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(record, meta.column.attributePath),
                        formattedValue = '';

                    if (dataAttribute) {
                        formattedValue = Unidata.util.DataAttributeFormatter.formatValueByAttribute(
                            meta.column.metaAttribute, dataAttribute.get('value'));
                    }

                    return formattedValue;
                }
            });
        });

        return columns;
    },

    /**
     * Обработка рендеринга компонента
     */
    onAfterRenderView: function () {
        // строим динамические колонки
        this.reconfigurelistRelationViewColumns();
    },

    /**
     * Обработчик события ввода мышки в строку грида
     *
     * @param gridView
     * @param record
     * @param item
     * @param rowIndex
     * @param e
     * @param eOpts
     */
    onRecordMouseEnter: function (gridView, record)  {
        var view = this.getView(),
            metaRelation = view.getMetaRelation(),
            column = this.getOpenRecordLinkColumn(),
            cell = gridView.getCell(record, column),
            link = this.getOpenRecordLinkComponent(cell);

        // у пользователя должны быть права на чтения для просмотра записи
        if (Unidata.Config.userHasRight(metaRelation.get('toEntity'), 'read')) {
            link.show();
        }
    },

    /**
     * Обработчик события ухода мышки со строки грида
     *
     * @param gridView
     * @param record
     * @param item
     * @param rowIndex
     * @param e
     * @param eOpts
     */
    onRecordMouseLeave: function (gridView, record)  {
        var column = this.getOpenRecordLinkColumn(),
            cell = gridView.getCell(record, column),
            link = this.getOpenRecordLinkComponent(cell);

        link.hide();
    },

    /**
     * Возвращает колонку в ячейках которой располагается компонент открытия связанной записи
     *
     * @returns {*}
     */
    getOpenRecordLinkColumn: function () {
        var view = this.getView(),
            m2mlist = view.m2mlist,
            widgetColumn = null,
            columnManager;

        columnManager = m2mlist.getColumnManager();

        // обходим все колонки в поисках widgetcolumn
        Ext.Array.each(columnManager.getColumns(), function (column) {
            if (column.getXType() === 'widgetcolumn') {
                widgetColumn = column;

                return false; // завершение итерации Ext.Array.each
            }
        });

        return widgetColumn;
    },

    /**
     * Возвращает компонент отображающий иконку открытия связанной записи, который расположен в ячейке грида
     *
     * @param cell - ячейка грида
     * @returns {*}
     */
    getOpenRecordLinkComponent: function (cell) {
        var resultCmp = null;

        if (cell && cell.query('div.x-component')) {
            resultCmp = Ext.getCmp(cell.query('div.x-component')[0].id);
        }

        return resultCmp;
    },

    /**
     * Обработчик события смены текущей страницы в paggingtoolbar
     *
     * @param pagingtoolbar
     * @param page
     * @param eOpts
     * @returns {boolean}
     */
    onPagingToolbarPageBeforeChange: function (pagingtoolbar, page) {
        this.createPagingStoreFilter(page);

        return false;
    },

    /**
     * Создает фильтр для chain стора фильтрующий по page
     *
     * @param page
     */
    createPagingStoreFilter: function (page) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            allM2mRecordsStore = viewModel.getStore('m2mrecords'),
            filteredM2mRecordsStore = viewModel.getStore('filteredm2mrecords'),
            pageSize = filteredM2mRecordsStore.pageSize,
            totalCount = filteredM2mRecordsStore.totalCount,
            filter;

        filter = new Ext.util.Filter({
            filterFn: function (record) {
                var fromRecord = ((page - 1) * pageSize) + 1,
                    toRecord = Math.min(page * pageSize, totalCount),
                    indexOf;

                indexOf = allM2mRecordsStore.indexOf(record);
                indexOf += 1;

                if (indexOf !== -1 && indexOf >= fromRecord && indexOf <= toRecord) {
                    return true;
                }

                return false;
            }
        });

        filteredM2mRecordsStore.clearFilter();
        filteredM2mRecordsStore.addFilter(filter);

        filteredM2mRecordsStore.currentPage = page;

        view.m2mlistPaging.updateInfo();
        view.m2mlistPaging.updateBarInfo();
    },

    /**
     * Обработчик одинарного клика
     *
     * @param gridView
     * @param record
     * @param row
     * @param rowIdx
     */
    onRecordRowClick: function (gridView, record, row, rowIdx) {
        var view = this.getView(),
            plugin = view.m2mlist.getPlugin('rowExpanderPlugin');

        // разворачиваем детальное представление
        plugin.toggleRow(rowIdx, record);
    }
});
