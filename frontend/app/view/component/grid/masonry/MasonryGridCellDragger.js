/**
 * Компонент, при помощи которого, происходит перетаскивание ячеек
 *
 * @author Aleksandr Bavin
 * @date 2017-10-11
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridCellDragger', {

    extend: 'Ext.dd.DD',

    mixins: {
        observable: 'Ext.util.Observable'
    },

    cell: null,

    isTarget: false,

    isDragging: false,
    isDrop: false,
    dropId: null,

    constructor: function (el, group, config) {
        this.initConfig(config);
        this.callParent(arguments);
        this.mixins.observable.constructor.call(this, arguments);
    },

    startDrag: function () {
        this.isDragging = true;

        this.fireEvent('startdrag', this);
    },

    onDrag: function () {
        this.fireEvent('drag', this);
    },

    endDrag: function (e) {
        this.isDragging = false;

        if (this.isDrop) {
            this.getEl().removeAttribute('style');
            this.getTarget(this.dropId).notifyDrop(this, e, {});
            this.fireEvent('drop', this);
        }

        this.isDrop = false;

        this.fireEvent('enddrag', this);
    },

    onDragEnter: function (e, id) {
        this.getTarget(id).notifyEnter(this, e, {});
    },

    onDragOut: function (e, id) {
        this.getTarget(id).notifyOut(this, e, {});
    },

    onDragDrop: function (e, id) {
        this.isDrop = true;
        this.dropId = id;
    },

    onInvalidDrop: function (e) {
        this.getEl().removeAttribute('style');
        this.fireEvent('invaliddrop', this);
    },

    /**
     * Возвращает dropTarget
     *
     * @param id
     * @returns {Unidata.view.component.grid.masonry.MasonryGridDropTarget}
     */
    getTarget: function (id) {
        return Ext.dd.DragDropManager.getDDById(id);
    }

});
