Ext.define('Unidata.view.steward.search.tableresultset.TableResultsetController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.search.tableresultset',

    stateVersion: 1,

    suspendStateSave: false,
    needReconfigure: false,

    onViewReady: function (grid) {
        var me = this,
            view = grid.view;

        this.toolTip = Ext.create('Ext.tip.ToolTip', {
            target: view.el,
            delegate: '.x-grid-cell',
            trackMouse: true,
            renderTo: Ext.getBody(),
            listeners: {
                beforeshow: function (tip) {
                    var trigger = tip.triggerElement,
                        row = tip.triggerElement.parentElement,
                        record = view.getRecord(row),
                        column = view.getHeaderByCell(trigger),
                        metaRecord = column.metaRecord,
                        value = '',
                        tipTemplate,
                        tipHtml;

                    if (column.column_id === '$period') {
                        value = me.getPeriodColumnDisplayValue(record.getValueFromObjectMap('$from'), record.getValueFromObjectMap('$to'));
                    } else {
                        record.preview().each(function (previewRecord) {
                            if (previewRecord.get('field') === column.name) {
                                value = previewRecord.get('value');

                                return false; //остановка итерации record.preview().each
                            }
                        });
                    }

                    tipTemplate = Ext.create('Ext.Template' , [
                        '<b>' + Unidata.i18n.t('search>tableresultset.value') + ':</b> {value:htmlEncode}',
                        '<br>',
                        '<b>' + Unidata.i18n.t('search>tableresultset.column') + ':</b> {columnName:htmlEncode}',
                        '<br>',
                        '<b>' + Unidata.i18n.t('search>tableresultset.description') + ':</b> {description:htmlEncode}'
                    ]);
                    tipTemplate.compile();
                    tipHtml = tipTemplate.apply({
                        value: value,
                        columnName: metaRecord.get('displayName'),
                        description: metaRecord.get('description')
                    });

                    tip.update(tipHtml);
                }
            }
        });
    },

    onViewResize: function (gridView, width) {
        var columns = gridView.getGridColumns(),
            columnWidth = 100,
            lastColumnIndex = columns.length - 1,
            lastColumnWidth = 100,
            gridHeaderCt = gridView.grid.headerCt;

        if (columns.length) {
            columnWidth = width / columns.length;
            lastColumnWidth = width - columnWidth * (columns.length - 1);
        }

        columnWidth = Ext.Number.constrain(columnWidth, 100);
        lastColumnWidth = Ext.Number.constrain(lastColumnWidth, 100);

        gridHeaderCt.suspendEvent('resize');
        gridHeaderCt.suspendEvent('columnresize');

        Ext.Array.each(columns, function (column, index) {
            var width = columnWidth;

            if (index === lastColumnIndex) {
                width = lastColumnWidth;
            }

            column.setWidth(width);
        });

        gridHeaderCt.resumeEvent('resize');
        gridHeaderCt.resumeEvent('columnresize');
    },

    getColumnMap: function (columns) {
        var me = this,
            map = [];

        Ext.Array.each(columns, function (column) {
            var nestedMap;

            map[column.column_id] = column;

            if (column.columns) {
                nestedMap = me.getColumnMap(column.columns);

                Ext.Object.each(nestedMap, function (key, value) {
                    map[key] = value;
                });
            }
        });

        return map;
    },

    updateColumnState: function (state) {
        var tableResultSet = this.lookupReference('tableResultset'),
            columnMgr = tableResultSet.getColumnManager(),
            columns = columnMgr.getColumns(),
            gridColumnsMap = this.getColumnMap(columns),
            stateColumnsMap;

        Ext.Array.each(state.columns, function (item) {
            item['column_id'] = item['id'];

            if (Ext.isArray(item.columns)) {
                Ext.Array.each(item.columns, function (item) {
                    item['column_id'] = item['id'];
                });
            }
        });

        stateColumnsMap = this.getColumnMap(state.columns);

        Ext.Object.each(stateColumnsMap, function (index, stateColumn) {
            var gridColumn = gridColumnsMap[index];

            if (gridColumn) {
                stateColumn['hidden'] = Boolean(gridColumn['hidden']);
                stateColumn['width'] = gridColumn.getWidth();
            }

            stateColumn['processed'] = true;
        });

        return state;
    },

    onBeforeStateSave: function (tableResultset, state) {
        var stateId = tableResultset.stateId,
            provider = Unidata.app.lsStateProvider;

        if (stateId === 'default') {
            return false;
        }

        if (this.suspendStateSave) {
            return false;
        }

        if (stateId) {
            state.version = this.stateVersion;

            state = this.updateColumnState(state);
            provider.set(stateId, state);
        }
    },

    onBeforeStateRestore: function () {
        return false;
    },

    /**
     * Восстанавливает состояние для текущего пользователя
     */
    restoreGridState: function (metaRecord, gridColumns) {
        var tableResultSet = this.lookupReference('tableResultset'),
            columnOrder = [],
            provider,
            stateId,
            state;

        stateId = this.buildStateId();
        tableResultSet.stateId = stateId;
        provider = Unidata.app.lsStateProvider;
        state = provider.state[stateId];

        if (state && state.version === this.stateVersion) {
            columnOrder = this.restoreColumnsState(gridColumns, state.columns);
        } else {
            columnOrder = this.restoreColumnsState(gridColumns, []);
        }

        return columnOrder;
    },

    restoreColumnsState: function (gridColumnsCfg, stateColumnsCfg) {
        var me = this,
            columnOrder = [];

        Ext.Array.each(stateColumnsCfg, function (stateColumnCfg) {
            var gridColumnCfg;

            gridColumnCfg = Ext.Array.findBy(gridColumnsCfg, function (item) {
                return item.column_id === stateColumnCfg.column_id;
            });

            if (!gridColumnCfg) {
                return;
            }

            me.restoreColumnState(gridColumnCfg, stateColumnCfg);

            gridColumnCfg.stateRestored = true;

            if (stateColumnCfg.columns) {
                gridColumnCfg.columns = me.restoreColumnsState(gridColumnCfg.columns || [] , stateColumnCfg.columns);
            }

            columnOrder.push(gridColumnCfg);
        });

        columnOrder = Ext.Array.merge(columnOrder, Ext.Array.difference(gridColumnsCfg, columnOrder));

        return columnOrder;
    },

    restoreColumnState: function (gridColumnCfg, stateColumnCfg) {
        if (stateColumnCfg.width) {
            gridColumnCfg.flex = 0;
            gridColumnCfg.width = stateColumnCfg.width;
        }

        gridColumnCfg['hidden'] = Boolean(stateColumnCfg['hidden']);

        return gridColumnCfg;
    },

    buildStateId: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            user = Unidata.Config.getUser(),
            userLogin = user.get('login'),
            entityName = metaRecord.get('name'),
            prefix = 'tblresultset';

        return prefix + '_' + userLogin + '_' + entityName;
    },

    /**
     * Обработчик клика
     */
    onResetTableStateButtonClick: function () {
        var stateId = this.buildStateId(),
            provider = Unidata.app.lsStateProvider;

        if (stateId !== 'default') {
            provider.set(stateId, null);
        }

        this.reconfigureGridColumnsByCurrentMetaRecord();
    },

    reconfigureGridColumnsByCurrentMetaRecord: function () {
        var view = this.getView(),
            metaRecord = view.getMetaRecord();

        this.reconfigureGridColumnsByMetaRecord(metaRecord);
    },

    reconfigureGridColumnsByMetaRecord: function (metaRecord) {
        var me = this,
            view = this.getView(),
            tableResultSet = this.lookupReference('tableResultset'),
            columns = [];

        this.suspendStateSave = true;

        Ext.defer(function () {
            this.suspendStateSave = false;
        }, 300, this);

        view.setMetaRecord(metaRecord);

        if (Ext.getClassName(metaRecord) === 'Unidata.model.entity.LookupEntity') {
            columns = Ext.Array.push(columns, this.getColumnFromAttribute(metaRecord.getCodeAttribute()), []);
            columns = Ext.Array.push(columns, this.getColumnFromAttribute(metaRecord.aliasCodeAttributes(), []));
        }

        columns = Ext.Array.push(columns, this.getColumnFromAttribute(metaRecord.simpleAttributes(), []));

        if (Ext.getClassName(metaRecord) === 'Unidata.model.entity.Entity') {
            metaRecord.complexAttributes().each(function (record) {
                var complexColumn = me.getColumnFromAttribute(record.getNestedEntity().simpleAttributes(), [record.get('name')]),
                    column;

                complexColumn = Ext.Array.sort(complexColumn, function (a, b) {
                    return a.order - b.order;
                });

                if (complexColumn.length) {
                    column = {
                        column_id: record.get('name'),
                        stateId: record.get('name'),
                        header: record.get('displayName'),
                        order: record.get('order'),
                        columns: complexColumn
                    };

                    columns.push(column);
                }
            });
        }

        columns = Ext.Array.sort(columns, function (a, b) {
            return a.order - b.order;
        });

        if (view.getAllPeriodSearch()) {
            columns = Ext.Array.merge(columns, this.createPeriodSearchColumn(metaRecord));
        }

        tableResultSet.stateId = null;
        columns = this.restoreGridState(metaRecord, columns);
        tableResultSet.reconfigure(columns);
    },

    createPeriodSearchColumn: function (metaRecord) {
        var me = this,
            column;

        column = {
            column_id: '$period',
            stateId: '$period',
            header: Unidata.i18n.t('glossary:timeintervals'),
            order: 9999,
            metaRecord: metaRecord,
            width: 200,
            sortable: false,
            renderer: function (value, meta) {
                var record = meta.record,
                    result;

                result = me.getPeriodColumnDisplayValue(record.getValueFromObjectMap('$from'), record.getValueFromObjectMap('$to'));

                return result;
            }
        };

        return column;
    },

    getPeriodColumnDisplayValue: function (from, to) {
        var template,
            toDate,
            fromDate;

        template = '{0} - {1}';

        fromDate = Ext.Date.parse(from, Unidata.Config.getDateTimeFormatServer());
        fromDate = Ext.Date.format(fromDate, Unidata.Config.getDateFormat());

        toDate = Ext.Date.parse(to, Unidata.Config.getDateTimeFormatServer());
        toDate = Ext.Date.format(toDate, Unidata.Config.getDateFormat());

        return Ext.String.format(template, fromDate, toDate);
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

    getColumnFromAttribute: function (simpleAttributes, arrayPath) {
        var columns = [],
            me = this;

        arrayPath = arrayPath || [];

        function getColumnCfg (record, arrayPath) {
            var columnCfg,
                tmpArrayPath = Ext.Array.clone(arrayPath),
                path;

            tmpArrayPath.push(record.get('name'));

            path = tmpArrayPath.join('.');

            columnCfg = {
                text: record.get('displayName'),
                column_id: tmpArrayPath.join('-'),
                stateId: tmpArrayPath.join('-'),
                name: path,
                hidden: record.get('displayable') ? false : true,
                order: record.get('order'),
                metaRecord: record,
                absoluteMinWidth: 50,
                gapWidth: 100,
                sortable: false,
                renderer: function (value, meta) {
                    var record = meta.record,
                        column = meta.column,
                        columnName = column.name,
                        meta = column.metaRecord,
                        value = '',
                        values = [],
                        parseFormats,
                        formattedValue,
                        formattedValues,
                        result;

                    parseFormats = {
                        Date: 'Y-m-d',
                        Timestamp: 'Y-m-d\\TH:i:s',
                        Time: '\\TH:i:s'
                    };

                    record.preview().each(function (previewRecord) {
                        if (previewRecord.get('field') === columnName) {
                            value = previewRecord.get('value');
                            values = previewRecord.get('values');

                            return false; // остановка итерации record.preview().each
                        }
                    });

                    formattedValue = Unidata.util.DataAttributeFormatter.formatValueByAttribute(meta, value, parseFormats);
                    formattedValues = Unidata.util.DataAttributeFormatter.formatValuesByAttribute(meta, values, parseFormats);
                    result = me.buildFullAttributeValue(formattedValue, formattedValues);
                    result = Ext.String.htmlEncode(result);

                    return result;
                }
            };

            return columnCfg;
        }

        function appendColumn (columns, record, arrayPath) {
            var columnCfg;

            columns = columns || [];

            columnCfg = getColumnCfg(record, arrayPath);

            if (columnCfg) {
                columns.push(columnCfg);
            }

            return columns;
        }

        if (Ext.getClassName(simpleAttributes) === 'Unidata.model.attribute.CodeAttribute') {
            columns = appendColumn(columns, simpleAttributes, arrayPath);
        } else {
            simpleAttributes.each(function (record) {
                // weblink не выводим т.к. атрибут является вычисляемым и его должен расчитывать BE но
                // не готовы пока это поддерживать см UN-5230
                if (record.isLinkDataType()) {
                    return;
                }

                columns = appendColumn(columns, record, arrayPath);
            });
        }

        return columns;
    },

    createChainedStore: function (source) {
        var view = this.getView();

        this.store =  Ext.create('Ext.data.ChainedStore', {
            source: source
        });

        view.tableResultset.setStore(this.store);
        view.pagingResultset.setStore(this.store.getSource());

        return this.store;
    },

    updateSourceStore: function (source) {
        var store = this.store;

        if (!store) {
            this.createChainedStore(source);
        } else {
            store.setSource(source);
        }

        source.on('refresh', function () {
            if (this.needReconfigure) {
                this.needReconfigure = false;
                this.reconfigureGridColumnsByCurrentMetaRecord();
            }
        }, this);
    },

    updateAllPeriodSearch: function () {
        this.needReconfigure = true;
    },

    onColumnShow: function (ct, column) {
        this.columnAutoWidth(column);
    },

    onGridReconfigure: function (cmp) {
        var me = this,
            columnManager = cmp.getColumnManager(),
            columns = columnManager.getColumns();

        Ext.Array.each(columns, function (column) {
            me.columnAutoWidth(column);
        });
    },

    columnAutoWidth: function (column) {
        var calcWidth;

        if (!column.rendered) {
            return;
        }

        if (column.stateRestored) {
            return;
        }

        calcWidth = column.textEl.getWidth() + column.gapWidth;

        if (calcWidth < column.absoluteMinWidth) {
            calcWidth = column.absoluteMinWidth;
        }

        if (calcWidth > 250) {
            calcWidth = 250;
        }

        column.setWidth(calcWidth);
    }
});
