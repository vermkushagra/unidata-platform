/**
 * Вью для больших деревьев с быстрым скроллом
 *
 * @author Aleksandr Bavin
 * @date 2016-10-13
 */
Ext.define('Unidata.view.component.LargeTreeView', {
    extend: 'Ext.tree.View',

    alias: 'widget.largetree',

    rowsInBlock: 20,

    blocksInCluster: 4,

    currentClusterIndex: 0,

    initComponent: function () {
        this.callParent(arguments);

        this.on('refresh', this.onRefresh, this);
    },

    doRemove: function (records, index, ignoreVisibility) {
        var indexToCheck = index ? index - 1 : index;

        if (!ignoreVisibility) {
            if (!this.all.item(indexToCheck)) {
                return;
            }
        }

        // вызываем рефреш после уделения всех элементов
        clearTimeout(this.refreshOnRemoveTimer);
        this.refreshOnRemoveTimer = Ext.defer(this.refresh, 1, this);

        return this.callParent(arguments);
    },

    doAdd: function (records, index, ignoreVisibility) {
        var indexToCheck = index ? index - 1 : index;

        if (records.length == 0) {
            return;
        }

        if (!ignoreVisibility) {
            if (!this.all.item(indexToCheck)) {
                return;
            }
        }

        // Ограничиваем вставку узлов размером кластера если он не последний
        if (records.length > this.getRowsInCluster() && this.getLastClusterIndex() > this.currentClusterIndex) {
            arguments[0] = records.slice(0, this.getRowsInCluster());
        }

        return this.callParent(arguments);
    },

    onRefresh: function () {
        var clusterStartRow = this.getClusterStartRow(this.currentClusterIndex);

        if (this.all.getCount() && clusterStartRow != this.all.startIndex) {
            this.all.moveBlock(clusterStartRow);
        }

        this.updateClusterOffsets();
    },

    updateClusterOffsets: function () {
        var currentClusterIndex = this.currentClusterIndex,
            clusterStartRow = this.getClusterStartRow(currentClusterIndex),
            rowsCount = this.getRowsCount(),
            rowHeight = this.getRowHeight(),
            rowsInCluster = this.getClusterRecords(currentClusterIndex).length,
            topPadding = clusterStartRow * rowHeight,
            bottomRowsCount = rowsCount - clusterStartRow - rowsInCluster,
            bottomPadding = bottomRowsCount * rowHeight,
            nodeContainer = this.getNodeContainer();

        if (bottomPadding < 0) {
            bottomPadding = 0;
        }

        if (nodeContainer) {
            nodeContainer = Ext.fly(nodeContainer);
            nodeContainer.setPadding(topPadding + ' 0 ' + bottomPadding + ' 0');
        }
    },

    redrawCluster: function () {
        var currentClusterIndex = this.currentClusterIndex,
            clusterStartRow = this.getClusterStartRow(currentClusterIndex),
            clusterRecords = this.getClusterRecords(currentClusterIndex),
            scrollY = this.getScrollY();

        this.clearViewEl(true);
        this.doAdd(clusterRecords, clusterStartRow, true);
        this.updateClusterOffsets();

        // иногда нужно корректировать позицию скролла
        if (scrollY != this.getScrollY()) {
            this.setScrollY(scrollY);
        }

        this.refreshScroll();
    },

    onRender: function () {
        this.callParent(arguments);
        this.getEl().on('scroll', this.onScroll, this);
    },

    onScroll: function () {
        var scrollClusterIndex = this.getScrollClusterIndex();

        if (scrollClusterIndex != this.currentClusterIndex) {
            this.currentClusterIndex = scrollClusterIndex;
            this.redrawCluster();
        }
    },

    getLastClusterIndex: function () {
        return Math.floor(this.store.getCount() / this.getRowsInCluster());
    },

    getScrollClusterIndex: function () {
        var blocksInCluster = this.getBlocksInCluster(),
            blockHeight = this.getBlockHeight(),
            scrollY = this.getScrollY(),
            clusterIndex = Math.floor(scrollY / (blockHeight * (blocksInCluster - 1))),
            rowsCount = this.getRowsCount(),
            rowsInBlock = this.getRowsInBlock(),
            rowsTop = clusterIndex * (this.getRowsInCluster() - this.getRowsInBlock()),
            rowsInCluster = rowsCount - rowsTop;

        if (blockHeight == 0) {
            return 0;
        }

        if (clusterIndex > 0 && rowsInCluster <= rowsInBlock) {
            return clusterIndex - 1;
        }

        return clusterIndex;
    },

    getRowsInCluster: function () {
        return this.getBlocksInCluster() * this.getRowsInBlock();
    },

    getRowsInBlock: function () {
        return this.rowsInBlock;
    },

    getBlocksInCluster: function () {
        return this.blocksInCluster;
    },

    getClusterRecords: function (clusterIndex) {
        var start = this.getClusterStartRow(clusterIndex),
            end = start + this.getRowsInCluster() - 1,
            store = this.store;

        if (store.data.getCount()) {
            if (clusterIndex === this.getLastClusterIndex()) {
                return store.getRange(start);
            } else {
                return store.getRange(start, end);
            }
        } else {
            return [];
        }
    },

    getClusterStartRow: function (clusterIndex) {
        return clusterIndex * (this.getRowsInCluster() - this.getRowsInBlock());
    },

    getRowHeight: function () {
        var rowItem = this.all.first();

        return rowItem ? rowItem.getHeight() : 0;
    },

    getBlockHeight: function () {
        return this.getRowHeight() * this.getRowsInBlock();
    },

    getClusterHeight: function () {
        return this.getBlocksInCluster() * this.getBlockHeight();
    },

    getRowsCount: function () {
        return this.store.data.getCount();
    },

    getBlocksCount: function () {
        return Math.ceil(this.getRowsCount() / this.getRowsInBlock());
    },

    getViewRange: function () {
        return this.getClusterRecords(this.currentClusterIndex ? this.currentClusterIndex : 0);
    }

});
