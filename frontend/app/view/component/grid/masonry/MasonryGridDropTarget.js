/**
 * Область для дропа
 *
 * @author Aleksandr Bavin
 * @date 2017-10-12
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridDropTarget', {

    extend: 'Ext.dd.DropTarget',

    ddGroup: 'masonryGrid',

    overClass: Ext.baseCSSPrefix + 'dd-over',

    dropHandler: Ext.emptyFn,
    scope: null,

    constructor: function (el, config) {
        var el = Ext.get(el);

        this.callParent(arguments);
        el.addCls('un-drop-target');
        // el.setVisibilityMode(Ext.dom.Element.DISPLAY);
        // el.hide();
    },

    notifyDrop: function (source, e, data) {
        this.callParent(arguments);

        this.dropHandler.apply(this.scope || this, [this, source]);

        return true;
    },

    handleCellDrop: function (cell) {

    },

    notifyEnter: function (source, e, data) {
        return this.callParent(arguments);
    },

    notifyOut: function (source, e, data) {
        return this.callParent(arguments);
    },

    notifyOver: function (source, e, data) {
        return this.callParent(arguments);
    }

});
