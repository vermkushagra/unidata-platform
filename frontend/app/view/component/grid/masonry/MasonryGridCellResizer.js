/**
 * Компонент, при помощи которого, происходит ресайз ячеек
 *
 * @author Aleksandr Bavin
 * @date 2017-08-24
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridCellResizer', {

    extend: 'Ext.dd.DD',

    mixins: {
        observable: 'Ext.util.Observable'
    },

    isTarget: false,
    moveOnly: true,

    startDragX: 0,
    startDragY: 0,

    dragX: 0,
    dragY: 0,

    extDomEl: null,

    isDragging: false,

    constructor: function () {
        this.callParent(arguments);

        this.mixins.observable.constructor.call(this, arguments);
    },

    startDrag: function () {
        this.extDomEl = Ext.get(this.getEl());

        this.isDragging = true;

        this.dragX = this.startDragX = this.extDomEl.getLeft();
        this.dragY = this.startDragY = this.extDomEl.getTop();

        this.fireEvent('startdrag', this);
    },

    onDrag: function () {
        var deltaX, deltaY;

        this.dragX = this.extDomEl.getLeft();
        this.dragY = this.extDomEl.getTop();

        this.fireEvent('drag', this);

        if (this.isDragging) {
            deltaX = this.dragX - this.startDragX;
            deltaY = this.dragY - this.startDragY;

            this.fireEvent('dragdeltachange', this, deltaX, deltaY);
        }
    },

    endDrag: function () {
        var deltaX = this.dragX - this.startDragX,
            deltaY = this.dragY - this.startDragY;

        this.isDragging = false;

        this.fireEvent('enddrag', this, deltaX, deltaY);

        this.getEl().removeAttribute('style');
    }

});
