/**
 * Представление для ячейки на всю высоту ряда
 *
 * @author Aleksandr Bavin
 * @date 2017-10-11
 *
 * @property elDropLeft
 * @property elDropRight
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridCellAvatar', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGridComponent',

    requires: [
        'Unidata.view.component.grid.masonry.MasonryGridCellDropTarget'
    ],

    alias: 'widget.component.grid.masonry.cell.avatar',

    baseCls: 'un-masonry-grid-cell-avatar',

    childEls: [
        {
            itemId: 'drop-left',
            name: 'elDropLeft'
        },
        {
            itemId: 'drop-right',
            name: 'elDropRight'
        }
    ],

    renderTpl: [
        '<div class="{baseCls}-drop-left" id="{id}-drop-left" data-ref="drop-left"></div>',
        '<div class="{baseCls}-drop-right" id="{id}-drop-right" data-ref="drop-right"></div>'
    ],

    cell: null,

    constructor: function () {
        this.callParent(arguments);

        this.cell.on('columnindexchange', this.updateStyle, this);
        this.cell.on('columnscountchange', this.updateStyle, this);
    },

    onDestroy: function () {
        this.cell = null;
        this.callParent(arguments);
    },

    onRender: function () {
        this.updateStyle();
        this.callParent(arguments);
        this.initDropTargets();
    },

    initDropTargets: function () {
        Ext.create(
            'Unidata.view.component.grid.masonry.MasonryGridCellDropTarget',
            this.elDropLeft.dom,
            {
                dropArea: Unidata.view.component.grid.masonry.MasonryGridCellDropTarget.dropArea.LEFT,
                cell: this.cell
            }
        );

        Ext.create(
            'Unidata.view.component.grid.masonry.MasonryGridCellDropTarget',
            this.elDropRight.dom,
            {
                dropArea: Unidata.view.component.grid.masonry.MasonryGridCellDropTarget.dropArea.RIGHT,
                cell: this.cell
            }
        );
    },

    updateStyle: function () {
        var columnIndex = this.cell.getColumnIndex(),
            columnsCount = this.cell.getColumnsCount(),
            percentLeft = columnIndex * 100,
            percentWidth = columnsCount * 100;

        this.setStyle({
            left: percentLeft + '%',
            width: percentWidth + '%'
        });
    }

});
