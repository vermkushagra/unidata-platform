/**
 * @author Aleksandr Bavin
 * @date 19.05.2016
 */
Ext.define('Unidata.view.steward.relation.contains.ContainsRecordModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.containsrecord',

    data: {
        etalonRecord: null,
        dataRelationRecord: null,
        containsRecordTitle: '',
        dqErrors: false, // наличие dq ошибок
        readOnly: null,
        pending: false,         // на согласовании
        allowClickSave: true,   // признак того что в данный момент допустимо кликать на кнопку сохранения записи
        allowClickRemove: true  // признак того что в данный момент допустимо кликать на кнопку удаления записи
    },

    formulas: {
        /**
         * Определяет видимость кнопки сохранения связи
         */
        saveActionVisible: {
            bind: {
                etalonRecord: '{etalonRecord}'
            },
            get: function (getter) {
                var view = this.getView(),
                    dataRecord = view.getDataRecord(),
                    etalonRecord = getter.etalonRecord,
                    visible = false,
                    dataRecordRights,
                    rights;

                if (etalonRecord) {
                    //для новых контейнсов - проверяем права из dataRecord
                    if (etalonRecord.phantom && dataRecord.getRights) {
                        dataRecordRights = dataRecord.getRights();

                        if (!dataRecordRights.get('create')) {
                            return false;
                        }
                    }

                    rights = etalonRecord.getRights();

                    if (rights.get('update') || rights.get('create')) {
                        visible = true;
                    }
                }

                return visible;
            }
        },

        /**
         * Определяет доступность кнопки сохранения связи
         */
        saveButtonEnabled: {
            bind: {
                allowClickSave: '{allowClickSave}'
            },
            get: function (getter) {
                var enabled = true;

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
                etalonRecord: '{etalonRecord}'
            },
            get: function (getter) {
                var etalonRecord = getter.etalonRecord,
                    visible = false,
                    rights;

                if (etalonRecord) {
                    rights = etalonRecord.getRights();

                    if (rights.get('delete')) {
                        visible = true;
                    }
                }

                return visible;
            }
        },

        /**
         * Определяет доступность кнопки удаления связи
         */
        removeButtonEnabled: {
            bind: {
                allowClickRemove: '{allowClickRemove}'
            },
            get: function (getter) {
                var enabled = true;

                if (!getter.allowClickRemove) {
                    enabled = false;
                }

                return enabled;
            }
        },

        /**
         * Значение флага phantom текущей записи
         */
        dataRelationRecordPhantom: {
            bind: {
                bindTo: '{dataRelationRecord}',
                deep: true
            },
            get: function (dataRelationRecord) {
                var phantom = true;

                if (dataRelationRecord) {
                    phantom = dataRelationRecord.phantom || !dataRelationRecord.get('etalonId');
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        /**
         * Определяет видимость кнопки создания периода актуальности
         */
        createTimeIntervalButtonVisible: {
            bind: {
                saveActionVisible: '{saveActionVisible}',
                dataRelationRecordPhantom: '{dataRelationRecordPhantom}',
                deep: true
            },
            get: function (getter) {
                if (getter.dataRelationRecordPhantom) {
                    return true;
                }

                if (getter.saveActionVisible) {
                    return true;
                }

                return false;
            }
        }
    }
});
