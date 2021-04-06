/**
 * Область для дропа
 *
 * @author Aleksandr Bavin
 * @date 2017-10-12
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridCellDropTarget', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGridDropTarget',

    statics: {
        dropArea: {
            LEFT: 'left',
            RIGHT: 'right',
            GROUP: 'group'
        }
    },

    dropArea: 'left',
    cell: null,

    constructor: function (el, config) {
        this.callParent(arguments);
    },

    dropHandler: function (dropTarget, draggable) {
        var dropCell = this.cell,
            dropRow = dropCell.getRow(),
            dragCell = draggable.cell;

        if (dropCell === dragCell) {
            return;
        }

        if (this.dropArea === this.self.dropArea.LEFT) {
            dropRow.insertItem(
                dropRow.getItemsCollection().indexOf(dropCell),
                dragCell
            );
        }

        if (this.dropArea === this.self.dropArea.RIGHT) {
            dropRow.insertItem(
                dropRow.getItemsCollection().indexOf(dropCell) + 1,
                dragCell
            );
        }

        if (this.dropArea === this.self.dropArea.GROUP) {
            dropCell.setWidget({
                xtype: 'component.grid.masonry.group',
                items: [
                    {
                        items: [
                            {
                                columnsCount: 10,
                                items: dropCell.getItemsCollection().getRange()
                            }
                        ]
                    },
                    {
                        items: [
                            dragCell
                        ]
                    }
                ]
            });
        }
    }

});
