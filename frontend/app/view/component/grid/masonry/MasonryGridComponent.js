/**
 * Родительский класс для всех элементов грида
 *
 * @author Aleksandr Bavin
 * @date 2017-10-10
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridComponent', {

    extend: 'Unidata.view.component.AbstractComponentItems',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    config: {
        resizing: false, // элемент ресайзится
        dragging: false // элемент перетаскивается
    },

    constructor: function () {
        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);
    },

    /**
     * Рекурсивно собирает все виджеты
     * @returns {Array}
     */
    getWidgets: function () {
        var itemsCollection = this.getItemsCollection(),
            widgets = [];

        itemsCollection.each(function (item) {
            if (item instanceof Unidata.view.component.grid.masonry.MasonryGridComponent) {
                widgets = widgets.concat(item.getWidgets());
            } else {
                widgets.push(item);
            }
        });

        return widgets;
    },

    notifyWidgetSet: function (cell, widget) {
        this.onWidgetSet(cell, widget);

        if (this.ownerCt && this.ownerCt instanceof Unidata.view.component.grid.masonry.MasonryGridComponent) {
            this.ownerCt.notifyWidgetSet(cell, widget);
        }
    },

    onWidgetSet: function (cell, widget) {
    },

    isDragging: function () {
        return this.getDragging();
    },

    updateDragging: function (dragging) {
        if (this.isConfiguring) {
            return;
        }

        if (dragging) {
            this.addCls('un-dragging');
        } else {
            this.removeCls('un-dragging');
        }

        if (this.ownerCt && this.ownerCt instanceof Unidata.view.component.grid.masonry.MasonryGridComponent) {
            this.ownerCt.onChildItemDragging(this, dragging);
        }
    },

    onChildItemDragging: function (item, dragging) {
        if (this.ownerCt && this.ownerCt instanceof Unidata.view.component.grid.masonry.MasonryGridComponent) {
            this.ownerCt.onChildItemDragging(item, dragging);
        }
    },

    isResizing: function () {
        return this.getResizing();
    },

    updateResizing: function (resizing) {
        if (this.isConfiguring) {
            return;
        }

        if (resizing) {
            this.addCls('un-resizing');
        } else {
            this.removeCls('un-resizing');
        }

        if (this.ownerCt && this.ownerCt instanceof Unidata.view.component.grid.masonry.MasonryGridComponent) {
            this.ownerCt.onChildItemResizing(this, resizing);
        }
    },

    onChildItemResizing: function (item, resizing) {
        if (this.ownerCt && this.ownerCt instanceof Unidata.view.component.grid.masonry.MasonryGridComponent) {
            this.ownerCt.onChildItemResizing(item, resizing);
        }
    }

});
