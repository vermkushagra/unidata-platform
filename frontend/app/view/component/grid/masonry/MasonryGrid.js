/**
 * @author Aleksandr Bavin
 * @date 2017-08-22
 *
 * @property elContent
 * @property elNewRow
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGrid', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGridComponent',

    alias: 'widget.component.grid.masonry',

    requires: [
        'Ext.util.Collection',
        'Unidata.view.component.grid.masonry.MasonryGridRow',
        'Unidata.view.component.grid.masonry.MasonryGridCell'
    ],

    childEls: [
        {
            itemId: 'content-column',
            name: 'elContentColumn'
        },
        {
            itemId: 'new-row',
            name: 'elNewRow'
        }
    ],

    targetEl: 'elContentColumn',

    renderTpl: [
        '<div class="{baseCls}-size">',
            '<div class="{baseCls}-wrap">',
                '<div class="{baseCls}-content-column" id="{id}-content-column" data-ref="content-column"></div>',
            '</div>',
        '</div>',
        '<div class="{baseCls}-new-row" id="{id}-new-row" data-ref="new-row"></div>'
    ],

    baseCls: 'un-masonry-grid',

    config: {
        editMode: true
    },

    publishes: [
        'editMode'
    ],

    defaults: {
        xtype: 'component.grid.masonry.row'
    },

    updateEditMode: function (editMode) {
        if (editMode) {
            this.addCls(this.baseCls + '-edit-mode-on');
            this.removeCls(this.baseCls + '-edit-mode-off');
        } else {
            this.addCls(this.baseCls + '-edit-mode-off');
            this.removeCls(this.baseCls + '-edit-mode-on');
        }

        this.updateLayoutDelayed();
    },

    onComponentRender: function () {
        this.callParent(arguments);

        this.initNewRowButton();
    },

    initNewRowButton: function () {
        this.elNewRow.on('click', function () {
            this.addItem({
                items: [
                    {
                        columnsIndex: 0,
                        columnsCount: 10
                    }
                ]
            });
        }, this);
    },

    onChildItemDragging: function (item, dragging) {
        this.callParent(arguments);

        if (dragging) {
            this.addCls('child-dragging', this.baseCls);
        } else {
            this.removeCls('child-dragging', this.baseCls);
        }
    },

    onChildItemResizing: function (item, resizing) {
        this.callParent(arguments);

        if (resizing) {
            this.addCls('child-resizing', this.baseCls);
        } else {
            this.removeCls('child-resizing', this.baseCls);
        }
    },

    onWidgetSet: function (cell, widget) {
        this.fireEvent('widgetset', this, cell, widget);
    }

});
