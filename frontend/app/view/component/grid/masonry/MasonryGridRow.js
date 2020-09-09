/**
 * @author Aleksandr Bavin
 * @date 2017-08-24
 *
 * @property elAvatar
 * @property elContent
 * @property elNewCell
 * @property elDropTop
 * @property elDropBottom
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridRow', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGridComponent',

    alias: 'widget.component.grid.masonry.row',

    requires: [
        'Unidata.view.component.grid.masonry.MasonryGridRowDropTarget'
    ],

    baseCls: 'un-masonry-grid-row',

    config: {
        columnsCount: 10 // количество колонок
    },

    defaults: {
        xtype: 'component.grid.masonry.cell'
    },

    childEls: [
        {
            itemId: 'avatar',
            name: 'elAvatar'
        },
        {
            itemId: 'content',
            name: 'elContent'
        },
        {
            itemId: 'new-cell',
            name: 'elNewCell'
        },
        {
            itemId: 'drop-top',
            name: 'elDropTop'
        },
        {
            itemId: 'drop-bottom',
            name: 'elDropBottom'
        }
    ],

    targetEl: 'elContent',

    renderTpl: [
        '<div class="{baseCls}-avatar" id="{id}-avatar" data-ref="avatar"></div>',
        '<div class="{baseCls}-content" id="{id}-content" data-ref="content"></div>',
        '<div class="{baseCls}-new-cell" id="{id}-new-cell" data-ref="new-cell" style="width: {elNewCellWidth}; left: {elNewCellLeft};"></div>',
        '<div class="{baseCls}-drop-top" id="{id}-drop-top" data-ref="drop-top"></div>',
        '<div class="{baseCls}-drop-bottom" id="{id}-drop-bottom" data-ref="drop-bottom"></div>'
    ],

    onComponentRender: function () {
        this.callParent(arguments);

        this.initNewCellButton();
        this.initDropTargets();
    },

    initNewCellButton: function () {
        this.elNewCell.on('click', function () {
            var lastCell = this.getItemsCollection().last(),
                lastCellColumnsIndex = lastCell.getColumnIndex(),
                lastCellColumnsCount = lastCell.getColumnsCount();

            this.addItem({
                columnsIndex: lastCellColumnsIndex + lastCellColumnsCount,
                columnsCount: this.getColumnsCount() - (lastCellColumnsIndex + lastCellColumnsCount)
            });
        }, this);
    },

    initDropTargets: function () {
        Ext.create(
            'Unidata.view.component.grid.masonry.MasonryGridRowDropTarget',
            this.elDropTop.dom,
            {
                dropArea: Unidata.view.component.grid.masonry.MasonryGridRowDropTarget.dropArea.TOP,
                row: this
            }
        );

        Ext.create(
            'Unidata.view.component.grid.masonry.MasonryGridRowDropTarget',
            this.elDropBottom.dom,
            {
                dropArea: Unidata.view.component.grid.masonry.MasonryGridRowDropTarget.dropArea.BOTTOM,
                row: this
            }
        );
    },

    getGrid: function () {
        return this.ownerCt;
    },

    onItemAdd: function (cell) {
        this.callParent(arguments);

        cell.cellListeners = cell.on({
            startresize: this.onCellResizerStartResize,
            endresize: this.onCellResizerEndResize,
            destroyable: true,
            scope: this
        });
    },

    onItemRemove: function (cell) {
        this.callParent(arguments);

        Ext.destroy(cell.cellListeners);

        // если ряд пустой - удаляем
        if (!this.getItemsCollection().getCount()) {
            this.destroy();
        }

        this.fireEvent('rowcellremove', this, cell);
    },

    onCellResizerStartResize: function (cell, resizer) {
        this.updateResizerConstraints(cell, resizer);
    },

    onCellResizerEndResize: function () {
        this.updateCellsColumnIndex();
    },

    /**
     * Обновляет привязку перемещения ресайзера к осям
     *
     * @private
     */
    updateResizerConstraints: function (cell, resizer) {
        var columnWidth = this.getColumnWidth(),
            columnsCount = this.getColumnsCount(),
            cellColumnsCount = cell.getColumnsCount(),
            columnsToLeft = 0,
            columnsToRight = 0,
            addToLeft = true,
            iLeft = 0,
            iRight = 0;

        this.getItemsCollection().each(function (collectionCell) {
            var collectionCellColumnsCount;

            if (collectionCell === cell) {
                addToLeft = false;

                return;
            }

            collectionCellColumnsCount = collectionCell.getColumnsCount();

            if (addToLeft) {
                columnsToLeft += collectionCellColumnsCount;
            } else {
                columnsToRight += collectionCellColumnsCount;
            }
        });

        iLeft = Math.ceil(columnWidth * cellColumnsCount - columnWidth);
        iRight = Math.ceil(columnWidth * (columnsCount - (columnsToLeft + cellColumnsCount)));

        resizer.clearConstraints();
        resizer.resetConstraints();
        resizer.setYConstraint(0, 0);
        resizer.setXConstraint(
            iLeft,
            iRight,
            columnWidth
        );
    },

    /**
     * Обновляет расположение ячеек
     */
    updateCellsColumnIndex: function () {
        var grid = this.getGrid(),
            gridRowsCollection = grid.getItemsCollection(),
            columnsCounter = this.getColumnsCount(),
            newRowCells = [],
            rowColumnsCount = 0,
            elNewCellWidth = 0,
            elNewCellLeft = 0,
            rowIndex,
            prevCell;

        this.getItemsCollection().each(function (cell) {
            var cellColumnsCount = cell.getColumnsCount();

            if (prevCell) {
                cell.setColumnIndex(prevCell.getColumnIndex() + prevCell.getColumnsCount());
            } else {
                cell.setColumnIndex(0);
            }

            columnsCounter -= cellColumnsCount;

            if (columnsCounter < 0) {
                newRowCells.push(cell);
            } else {
                rowColumnsCount += cellColumnsCount;
            }

            prevCell = cell;
        }, this);

        // переносим на новый ряд всё, что не поместилость
        if (newRowCells.length) {
            rowIndex = gridRowsCollection.indexOf(this);
            grid.insertItem(rowIndex + 1, {
                items: newRowCells
            });
        }

        elNewCellWidth = ((this.getColumnsCount() - rowColumnsCount) * 100) + '%';
        elNewCellLeft = (rowColumnsCount * 100) + '%';

        // подгоняем размеры плейсхолдера для новой ячейки
        if (this.rendered) {
            this.elNewCell.setStyle({
                width: elNewCellWidth,
                left: elNewCellLeft
            });
        } else {
            this.setTplValue('elNewCellWidth', elNewCellWidth);
            this.setTplValue('elNewCellLeft', elNewCellLeft);
        }
    },

    renderItem: function (cell) {
        var cellAvatar = cell.getAvatar();

        this.callParent(arguments);

        if (!cellAvatar.rendered) {
            cellAvatar.render(this.elAvatar);
        }
    },

    /**
     * Устанавливаем размеры ячеек после рендера
     */
    renderItems: function () {
        if (this.isDestroyed || this.isConfiguring) {
            return;
        }

        this.updateCellsColumnIndex();
        this.callParent(arguments);
    },

    getColumnWidth: function () {
        return this.getEl().getWidth();
    },

    editSaveData: function (saveData) {
        saveData.columnsCount = this.getColumnsCount();
    }

});
