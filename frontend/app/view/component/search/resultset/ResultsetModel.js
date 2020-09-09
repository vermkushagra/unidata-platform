Ext.define('Unidata.view.component.search.resultset.ResultsetModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.resultset',

    data: {
        metaRecord: null,
        selectionMode: false,
        resultsetCount: 0,
        isPagingEnable: false,
        enableSelectionQueryMode: null
    },

    formulas: {
        importDataButtonHidden: {
            bind: {
                enableSelectionQueryMode: '{enableSelectionQueryMode}',
                selectionMode: '{selectionMode}'
            },
            get: function (getter) {
                // если в режиме выбора записей - скрываем
                if (getter.selectionMode) {
                    return true;
                }

                // отключаем во окне
                if (!getter.enableSelectionQueryMode) {
                    return true;
                }

                return false;
            }
        }
    }
});
