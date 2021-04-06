/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.edit.M2mRecordModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.relation.m2mrecord',

    data: {
        dataRecord: null,
        readOnly: null,
        allowClickSave: true,   // признак того что в данный момент допустимо кликать на кнопку сохранения записи
        allowClickRemove: true,  // признак того что в данный момент допустимо кликать на кнопку удаления записи
        dataRelation: null
    },

    formulas: {
        dataRelationPhantom: {
            bind: {
                bindTo: '{dataRelation}',
                deep: true
            },
            get: function (dataRelation) {
                var phantom = true;

                if (dataRelation) {
                    phantom = dataRelation.phantom;
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },
        titleFormula: {
            bind: {
                pickerFieldRawValue: '{pickerField.rawValue}',
                dataRelationPhantom: '{dataRelationPhantom}'
            },
            get: function (getter) {
                var DataAttributeFormatter = Unidata.util.DataAttributeFormatter,
                    dataRelationPhantom = getter.dataRelationPhantom,
                    title;

                title = getter.pickerFieldRawValue || '';

                if (dataRelationPhantom) {
                    title = title || Unidata.i18n.t('glossary:newRelation');
                    title = title + DataAttributeFormatter.getDirtyPrefix();
                }

                return title;
            }
        },
        /**
         * Определяет видимость кнопки сохранения связи
         */
        saveActionVisible: {
            bind: {
                dataRecord: '{dataRecord}',
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var dataRecord = getter.dataRecord,
                    dataRecordRights;

                if (getter.readOnly) {
                    return false;
                }

                if (dataRecord && dataRecord.getRights) {
                    dataRecordRights = dataRecord.getRights();

                    if (dataRecordRights.get('update')) {
                        return true;
                    }

                    if (dataRecordRights.get('create') && dataRecord.phantom) {
                        return true;
                    }
                }

                return false;
            }
        },

        /**
         * Определяет доступность кнопки сохранения связи
         */
        saveButtonEnabled: {
            bind: {
                allowClickSave: '{allowClickSave}',
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var enabled = true;

                if (getter.readOnly) {
                    enabled = false;
                }

                if (!getter.allowClickSave) {
                    enabled = false;
                }

                return enabled;
            }
        },

        /**
         * Определяет видимость кнопки удаления связи
         */
        removeButtonVisible: {
            bind: {
                dataRecord: '{dataRecord}',
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var dataRecord = getter.dataRecord,
                    dataRecordRights;

                if (getter.readOnly) {
                    return false;
                }

                if (dataRecord && dataRecord.getRights) {
                    dataRecordRights = dataRecord.getRights();

                    if (dataRecordRights.get('delete') || dataRecordRights.get('update')) {
                        return true;
                    }
                }

                return false;
            }
        },

        /**
         * Определяет доступность кнопки удаления связи
         */
        removeButtonEnabled: {
            bind: {
                allowClickRemove: '{allowClickRemove}',
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var enabled = true;

                if (getter.readOnly) {
                    enabled = false;
                }

                if (!getter.allowClickRemove) {
                    enabled = false;
                }

                return enabled;
            }
        }
    }
});
