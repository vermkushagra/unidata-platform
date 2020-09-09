Ext.define('Ext.overrides.data.Store', {
    override: 'Ext.data.Store',

    /**
     * Returns the complete unfiltered collection.
     * @private
     */
    getDataSource: function () {
        var data = this.getData();

        return data.getSource() || data;
    },

    /**
     * Completely remove all store data, even unfiltered
     * @public
     */
    clearStoreData: function (silent) {
        var me = this,
            data = me.getDataSource(),
            hasClear = me.hasListeners.clear,
            records = data.getRange();

        // We want to remove and mute any events here
        if (data.length) {
            // Explicit true here, we never want to fire remove events
            me.removeIsSilent = true;
            me.callObservers('BeforeRemoveAll');
            data.removeAll();
            me.removeIsSilent = false;

            if (!silent) {
                me.fireEvent('clear', me, records);
                me.fireEvent('datachanged', me);
            }
            me.callObservers('AfterRemoveAll', [!!silent]);
        }

        return records;
    }
});
