/**
 * Плагин для колонок грида - прячет колонку, если все ячейки пустые.
 * За основу взято решение из этого ответа:
 * @link http://stackoverflow.com/questions/15102677/exjts-hide-column-if-all-cells-of-the-column-are-empty/15128291#15128291
 *
 * @author Aleksandr Bavin
 * @date 2016-12-21
 */
Ext.define('Unidata.plugin.grid.HideColumnIfEmpty', {
    extend: 'Ext.AbstractPlugin',
    alias: 'plugin.grid.hideColumnIfEmpty',

    init: function (grid) {
        this.setCmp(grid);
        grid.on('reconfigure', this.onReconfigure, this);
    },

    onReconfigure: function () {
        var grid = this.getCmp(),
            store = grid.getStore();

        this.hideEmptyColums();
        store.on('load', this.hideEmptyColums, this);
    },

    hideEmptyColums: function () {
        var grid = this.getCmp(),
            columns = grid.columns,
            store = grid.getStore(),
            columnKeysMc = new Ext.util.MixedCollection();

        if (store.getCount() == 0) {
            return;
        }

        Ext.Array.forEach(columns, function (column) {
            columnKeysMc.add(column.dataIndex, column);
            column.show();
        });

        Ext.Array.some(store.getRange(), function (record) {
            var keysToRemove = [];

            columnKeysMc.eachKey(function (key) {
                if (!Ext.isEmpty(record.get(key))) {
                    keysToRemove.push(key);
                }
            });

            Ext.Array.forEach(keysToRemove, function (k) {
                columnKeysMc.removeAtKey(k);
            });

            return columnKeysMc.getCount() === 0;
        });

        columnKeysMc.each(function (column) {
            column.hide();
        });
    }

});
