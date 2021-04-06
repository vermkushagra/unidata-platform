Ext.define('Unidata.view.admin.entity.resultset.ResultsetModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.resultset',

    requires: [
        'Unidata.proxy.entity.CatalogProxy'
    ],

    data: {
        resultsetCount: 0,
        isPagingEnable: false,
        draftMode: false
    },

    stores: {
        resultsetStore: {
            type: 'tree',
            model: 'Unidata.model.entity.Catalog',
            proxy: {
                type: 'un.entity.catalog',
                showRoot: false,
                onlyCatalog: false,
                draftMode: false
            },
            root: {
                id: null
            }
        }
    },

    formulas: {
        draftAdminPanelButtonVisible: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var result = false;

                if (getter.draftMode && Unidata.Config.userHasRights('ADMIN_DATA_MANAGEMENT', ['create', 'update'])) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        },
        importExportButtonVisible: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var result = false;

                if (!getter.draftMode && Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'read')) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        },
        createButtonVisible: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var result = false;

                if (getter.draftMode && Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'create')) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        }
    }
});
