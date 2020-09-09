/**
 * Данные для построения таблицы
 *
 * @author Aleksandr Bavin
 * @date 2017-03-29
 */
Ext.define('Unidata.model.table.Table', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.table.Row',
        'Unidata.model.table.Column',
        'Unidata.model.table.Cell'
    ],

    fields: [
        {
            name: 'rowName',
            type: 'string'
        },
        {
            name: 'rowDisplayName',
            type: 'string'
        },
        {
            name: 'columnName',
            type: 'string'
        },
        {
            name: 'columnDisplayName',
            type: 'string'
        }
    ],

    hasOne: [
        {
            name: 'variables',
            model: 'table.Variables'
        }
    ],

    hasMany: [
        {
            name: 'rows',
            model: 'table.Row'
        },
        {
            name: 'columns',
            model: 'table.Column'
        },
        {
            name: 'cells',
            model: 'table.Cell'
        }
    ],

    proxy: {
        type: 'dataquality.info.aggregation',
        reader: {
            type: 'json'
        }
    },

    statics: {
        getProxy: function () {
            var proxy = this.proxy;

            if (!proxy) {
                proxy = Ext.create('Unidata.proxy.data.dataquality.info.Aggregation');

                this.setProxy(proxy);
            }

            return proxy;
        }
    },

    gridStore: null,

    constructor: function () {
        this.callParent(arguments);
    },

    load: function (options) {
        var callback = options.callback,
            scope = options.scope;

        options.callback = Ext.bind(function (model) {
            this.initTableData(model);
            callback.apply(scope, arguments);
        }, this);

        return this.callParent(arguments);
    },

    initTableData: function (model) {
        model.rows().each(this.initRowCells, this);
    },

    initRowCells: function (row) {
        var cellsStore = this.cells(),
            rowCellsStore = row.cells(),
            rowName = row.get('name');

        rowCellsStore.removeAll();

        cellsStore.setRemoteFilter(false);

        cellsStore.addFilter({
            filterFn: function (cell) {
                return (cell.get('row') === rowName);
            }
        });

        rowCellsStore.add({
            value: row.get('name'),
            displayValue: row.get('displayName')
        });

        rowCellsStore.add(cellsStore.getRange());

        cellsStore.clearFilter();
        cellsStore.setRemoteFilter(true);

        this.updateGridStoreData();
    },

    getGridStore: function () {
        if (!this.gridStore) {
            this.gridStore = Ext.create('Ext.data.Store', {
                fields: []
            });
            this.updateGridStoreData();
        }

        return this.gridStore;
    },

    updateGridStoreData: function () {
        var gridStore = this.gridStore;

        if (!gridStore) {
            return;
        }

        this.rows().each(function (row) {
            var data = {
                rowName: row.get('name'),
                rowDisplayName: row.get('displayName')
            };

            row.cells().each(function (rowCell) {
                data[rowCell.get('column')] = rowCell.get('displayValue');
            });

            gridStore.add(data);
        });
    }

});
