/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.DuplicatesModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.duplicates',

    data: {},

    stores: {
        resultsetStore: {
            type: 'tree',
            model: 'Unidata.model.entity.Catalog',
            proxy: {
                type: 'un.entity.catalog',
                showRoot: false,
                onlyCatalog: false
            },
            root: {
                id: null
            }
        }
    },

    formulas: {}
});
