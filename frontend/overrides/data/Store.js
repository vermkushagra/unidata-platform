Ext.define('Ext.overrides.data.Store', {
    override: 'Ext.data.Store',

    /**
     * Returns the complete unfiltered collection.
     * @private
     */
    getDataSource: function () {
        var data = this.getData();

        return data.getSource() || data;
    }
});
