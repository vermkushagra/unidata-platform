Ext.define('Ext.overrides.event.Event', {

    override: 'Ext.event.Event',

    /**
     * В extjs нет обработки ie
     *
     * @returns {Object}
     */
    getWheelDeltas: function () {
        var browserEvent = this.browserEvent;

        if (browserEvent.deltaX === undefined) {
            return this.callParent(arguments);
        }

        return {
            x: this.correctWheelDelta(-browserEvent.deltaX),
            y: this.correctWheelDelta(-browserEvent.deltaY)
        };

    }

});
