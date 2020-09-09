/**
 * Область для дропа
 *
 * @author Aleksandr Bavin
 * @date 2017-10-12
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridRowDropTarget', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGridDropTarget',

    statics: {
        dropArea: {
            TOP: 'top',
            BOTTOM: 'bottom'
        }
    },

    dropArea: 'top',
    row: null,

    constructor: function () {
        this.callParent(arguments);
    },

    dropHandler: function (dropTarget, draggable) {
        var dropRow = this.row,
            grid = dropRow.getGrid(),
            dragCell = draggable.cell,
            dragCellRow = dragCell.getRow();

        if (dropRow === dragCellRow && dragCellRow.getItemsCollection().getCount() === 1) {
            return;
        }

        if (this.dropArea === this.self.dropArea.TOP) {
            grid.insertItem(
                grid.getItemsCollection().indexOf(dropRow),
                {
                    items: [dragCell]
                }
            );
        }

        if (this.dropArea === this.self.dropArea.BOTTOM) {
            grid.insertItem(
                grid.getItemsCollection().indexOf(dropRow) + 1,
                {
                    items: [dragCell]
                }
            );
        }
    }

});
