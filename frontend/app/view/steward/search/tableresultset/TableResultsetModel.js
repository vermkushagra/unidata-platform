Ext.define('Unidata.view.steward.search.tableresultset.TableResultsetModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.search.tableresultset',

    data: {
        metaRecord: null,
        storeCount: 0
    },

    stores: {},

    formulas: {
        pagingToolbarVisible: {
            bind: {
                storeCount: '{storeCount}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (getter.storeCount > 0) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        }
    }
});
