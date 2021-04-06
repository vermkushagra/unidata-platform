Ext.define('Unidata.view.admin.sourcesystems.resultset.ResultsetModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.sourcesystems.resultset',

    data: {
        resultsetCount: 0,
        isPagingEnable: false,
        readOnly: false
    },

    formulas: {
        /**
         * Определяет видимость панели инструментов с доступными операциями
         */
        createSourceSystemButtonVisible: {
            bind: {
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var visible = false;

                if (getter.readOnly === false && Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'create')) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        }
    }
});
