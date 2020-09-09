/**
 * Фикс пропадающих виджетов в treepanel при разворачивании ноды
 * Взято тут:
 * @link https://www.sencha.com/forum/showthread.php?288460-TreePanel-and-widgetcolumn&p=1072137&viewfull=1#post1072137
 *
 * @author Aleksandr Bavin
 * @date 2016-09-22
 */
Ext.define('Unidata.overrides.grid.column.Widget', {
    override: 'Ext.grid.column.Widget',

    privates: {

        onItemUpdate: function (record) {
            // new code culled from onViewRefresh method
            var me = this,
                widget = me.liveWidgets[record.internalId],
                treepanel = me.up('treepanel'),
                view,
                row,
                cell,
                el, lastBox, width;

            if (treepanel) {
                view = treepanel.getView();
                row = view.getRowById(record.internalId);
                cell = row.cells[me.getVisibleIndex()].firstChild;

                lastBox = me.lastBox;

                if (lastBox && !me.isFixedSize && width === undefined) {
                    width = lastBox.width - parseInt(me.getCachedStyle(cell, 'padding-left'), 10) - parseInt(me.getCachedStyle(cell, 'padding-right'), 10);
                }

                Ext.fly(cell).empty();

                if (el = (widget.el || widget.element)) {
                    cell.appendChild(el.dom);

                    if (!me.isFixedSize) {
                        widget.setWidth(width);
                    }
                } else {
                    if (!me.isFixedSize) {
                        widget.width = width;
                    }
                    widget.render(cell);
                }
            }
            // end new code

            this.updateWidget(record);
        }

    }
});
