/**
 * @author Aleksandr Bavin
 * @date 2017-08-22
 *
 * @property elContent
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridGroup', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGrid',

    alias: 'widget.component.grid.masonry.group',

    onItemAdd: function (row) {
        this.callParent(arguments);

        row.rowListeners = row.on({
            rowcellremove: this.onRowCellRemove,
            destroyable: true,
            scope: this
        });
    },

    onRowCellRemove: function (row, cell) {
        var rowsCount = this.getItemsCollection().getCount(),
            rowCellsCount;

        if (rowsCount === 1) {

            rowCellsCount = row.getItemsCollection().getCount();

            if (rowCellsCount === 1) {
                //TODO: как-то вытащить из группы
            }
        }
    },

    onItemRemove: function (row) {
        var rowsCount;

        this.callParent(arguments);

        Ext.destroy(row.rowListeners);

        rowsCount = this.getItemsCollection().getCount();

        if (rowsCount === 0) {
            this.destroy();
        }
    }

});
