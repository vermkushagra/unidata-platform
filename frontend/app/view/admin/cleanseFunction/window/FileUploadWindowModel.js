Ext.define('Unidata.view.admin.cleanseFunction.window.FileUploadWindowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.cleanseFunction.window',

    data: {
        isSuccess: false,
        status: null,
        cleanseFunctionName: null
    },

    stores: {
        loadedCleanseFunctions: {
            model: 'Unidata.model.cleansefunction.thirdparty.CleanseFunctionLoadStatus',
            proxy: {
                type: 'memory'
            }
        }
    },

    formulas: {
        loadedCleanseFunction: {
            bind: {
                bindTo: '{loadedCleanseFunctions}',
                deep: true
            },
            get: function (records) {
                return records && records.first();
            }
        }
    }
});
