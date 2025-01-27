Ext.define('Unidata.view.steward.dataviewerpanel.DataViewerPanelModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataviewerpanel',

    data: {
        /**
         * Данные устанавливаемые методами
         */
        //dataRecordTitle: null,
        dataRecord: null,
        metaRecord: null,
        etalonId: null,
        relationReferenceDirty: false

        /**
         * Вычисляемые данные, проставляются где то в коде
         */

        /**
         * Формулы текущей модели
         */
    },

    stores: {},

    formulas: {
        dataVersion: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function () {
                return Ext.timestamp();
            }
        },

        metaVersion: {
            bind: {
                bindTo: '{metaRecord}',
                deep: true
            },
            get: function () {
                return Ext.timestamp();
            }
        },

        dataRecordTitle: {
            bind: {
                dataRecord: '{dataRecord}',
                metaRecord: '{metaRecord}',
                dataVersion: '{dataVersion}',
                metaVersion: '{metaVersion}',
                relationReferenceDirty: '{relationReferenceDirty}'
            },
            get: function (getter) {
                var title                      = '',
                    metaRecord                 = getter.metaRecord,
                    dataRecord                 = getter.dataRecord,
                    relationReferenceDirty     = getter.relationReferenceDirty,
                    DataAttributeFormatterUtil = Unidata.util.DataAttributeFormatter;

                if (dataRecord && metaRecord) {
                    if (dataRecord.phantom) {
                        title = Unidata.i18n.t('dataviewer>selectNewRecord');
                    } else {
                        title = DataAttributeFormatterUtil.buildEntityTitleFromDataRecord(metaRecord, dataRecord, relationReferenceDirty);
                    }
                } else {
                    title =  Unidata.i18n.t('dataviewer>loading');
                }

                title = Ext.coalesceDefined(title, '');

                return title;
            }
        }
    }
});
