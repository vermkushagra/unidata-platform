Ext.define('Unidata.model.Base', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.identifier.*',
        'Ext.data.validator.*'
    ],

    schema: {
        namespace: 'Unidata.model'
    },

    /**
     * Универсальный метод для получения доступных hasMany сторов
     * @param {Array} associationNames - массив с именами hasMany
     * @returns {Ext.data.Store[]}
     */
    getHasManyStores: function (associationNames) {
        var stores = [];

        Ext.Array.each(associationNames, function (associationName) {
            var store;

            if (Ext.isDefined(this.associations[associationName])) {
                store = this[associationName]();

                if (store instanceof Ext.data.Store) {
                    stores.push(store);
                }
            }

        }, this);

        return stores;
    }

});
