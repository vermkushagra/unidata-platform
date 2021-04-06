Ext.define('Unidata.view.component.search.query.etaloncluster.list.EtalonClusterListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query.etaloncluster.list.etalonclusterlist',

    data: {
        etalonCluster: null
    },

    formulas: {
        deleteClusterButtonEnabled: {
            bind: {
                etalonCluster: '{etalonCluster}'
            },
            get: function (getter) {
                var etalonCluster = getter.etalonCluster;

                return Boolean(etalonCluster);
            }
        }
    }
});
