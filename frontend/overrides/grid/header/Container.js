/**
 * @author Aleksandr Bavin
 * @date 2016-11-25
 */
Ext.define('Unidata.overrides.grid.header.Container', {
    override: 'Ext.grid.header.Container',

    privates: {
        onHeaderActivate: function (e) {
            var column = this.getFocusableFromEvent(e);

            // дальнейшие действия должны быть только с isColumn
            if (column && column.isColumn) {
                // Sort the column is configured that way.
                // sortOnClick may be set to false by SpreadsheelSelectionModel to allow click to select a column.
                if (column.sortable && this.sortOnClick) {
                    column.toggleSortState();
                }
                // onHeaderClick is a necessary part of accessibility processing, sortable or not.
                this.onHeaderClick(column, e, column.el);
            }
        }
    }

});
