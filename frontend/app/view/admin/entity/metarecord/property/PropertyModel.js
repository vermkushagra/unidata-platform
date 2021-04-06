Ext.define('Unidata.view.admin.entity.metarecord.property.PropertyModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.property',

    stores: {
        classifierStore: {
            type: 'un.classifier',
            autoLoad: true
        }
    },

    formulas: {
        /**
         *
         */
        viewReadOnly: {
            bind: {
                readOnly: '{metaRecordViewReadOnly}',
                deep: true
            },
            get: function (getter) {
                var readOnly = true;

                if (getter.readOnly === false) {
                    readOnly = false;
                }

                return Ext.coalesceDefined(readOnly, true);
            }
        },

        /**
         * Определяет допустимость редактирования кодового имени реестра / справочника
         */
        metaRecordNameReadOnly: {
            bind: {
                phantom: '{isMetaRecordPhantom}',
                readOnly: '{metaRecordViewReadOnly}',
                deep: true
            },
            get: function (getter) {
                var readOnly = false;

                if (!getter.phantom || getter.readOnly) {
                    readOnly = true;
                }

                return Ext.coalesceDefined(readOnly, true);
            }
        },

        fieldReadOnlyIfMetaRecordHasData: {
            bind: {
                readOnly: '{metaRecordViewReadOnly}',
                metaRecord: '{currentRecord}',
                deep: true
            },
            get: function (getter) {
                var readOnly = false,
                    metaRecord = getter.metaRecord;

                if (getter.readOnly) {
                    readOnly = true;
                }

                if (metaRecord && metaRecord.get('hasData')) {
                    readOnly = true;
                }

                return Ext.coalesceDefined(readOnly, true);
            }
        }
    }
});
