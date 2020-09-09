Ext.define('Unidata.view.component.search.query.etaloncluster.recordlist.EtalonClusterRecordListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query.etaloncluster.recordlist.etalonclusterrecordlist',

    data: {
        selectedCount: 0
    },

    formulas: {
        deleteEtalonClusterRecordEnabled: {
            bind: {
                selectedCount: '{selectedCount}'
            },
            get: function (getter) {
                var selectedCount = getter.selectedCount;

                return selectedCount > 0;
            }
        }
    }
});
