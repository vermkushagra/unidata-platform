Ext.define('Unidata.view.admin.sourcesystems.layout.LayoutModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.sourcesystems.layout',

    data: {
        sourceSystemsInfo: null,
        draftMode: null
    },

    formulas: {
        resultsetStore: {
            bind: {
                bindTo: '{sourceSystemsInfo}',
                deep: true
            },
            get: function (record) {
                return record && record.sourceSystem();
            }
        },
        adminSystemName: {
            bind: {
                bindTo: '{sourceSystemsInfo}',
                deep: true
            },
            get: function (record) {
                return record && record.get('adminSystemName');
            }
        }
    }
});
