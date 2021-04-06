/**
 * Миксин для стора, который, при загрузке данных обновляет существующие объекты (с теми же id), а не пересоздаёт их.
 * Удалённые объекты так же модифицируется теми, которые были загружены.
 * Работает при addRecords = true
 *
 * @author Aleksandr Bavin
 * @date 2017-11-01
 */
Ext.define('Unidata.mixin.data.StoreLoadUpdate', {

    extend: 'Ext.Mixin',

    mixinConfig: {
        before: {
            onProxyLoad: 'beforeOnProxyLoad',
            loadRecords: 'beforeLoadRecords'
        }
    },

    updateRecords: true,

    beforeOnProxyLoad: function (operation) {
        var operationRecords = operation.getRecords(),
            recordsMap;

        if (!operation.getAddRecords() && !this.updateRecords) {
            return;
        }

        if (operationRecords && operationRecords.length) {
            recordsMap = this.getRecordsMap(operationRecords);

            operation.setRecords(recordsMap.operation);
        }
    },

    /**
     * Сопоставляет существующие данные в сторе с records
     *
     * @typedef {Object} MapItem
     * @property {Object} operationRecord - данные, которые были созданы по результату операции
     * @property {Object} storeRecord - данные в сторе
     *
     * @typedef {Object} Map
     * @property {MapItem[]} existing - существующие записи
     * @property {MapItem[]} removed - удалённые записи
     * @property {MapItem[]} new - новые записи
     * @property {Ext.data.Model[]} operation - корректные записи для операции
     *
     * @param records
     * @returns {Map}
     */
    getRecordsMap: function (records) {
        var data = this.getData(),
            map = [];

        map = {
            existing: [],
            deleted: [],
            new: [],
            operation: []
        };

        Ext.Array.each(records, function (record) {
            var recordId = record.getId(),
                existingRecord = this.getById(recordId),
                removedRecord;

            // уже существующие
            if (existingRecord) {
                map.existing.push({
                    operationRecord: record,
                    storeRecord: existingRecord
                });

                map.operation.push(existingRecord);

                return;
            }

            // удалённые
            removedRecord = Ext.Array.findBy(this.getRemovedRecords(), function (removedRecord) {
                return (removedRecord.getId() === recordId);
            });

            if (removedRecord) {
                map.removed.push({
                    operationRecord: record,
                    storeRecord: removedRecord
                });

                map.operation.push(removedRecord);

                return;
            }

            // новые
            map.new.push({
                operationRecord: record,
                storeRecord: null
            });

            map.operation.push(record);
        }, this);

        return map;
    },

    beforeLoadRecords: function (records, options) {
        var recordsMap;

        if (!options.addRecords && !this.updateRecords) {
            return;
        }

        if (records && records.length) {
            recordsMap = this.getRecordsMap(records);

            // новые для вставки
            Ext.Array.replace(records, 0, records.length, Ext.Array.pluck(recordsMap.new, 'operationRecord'));

            // обновляем удалённые
            Ext.Array.each(recordsMap.removed, function (mapItem) {
                mapItem.storeRecord.copyFrom(mapItem.operationRecord);
                mapItem.operationRecord.destroy();
            });

            // обновляем существующие
            Ext.Array.each(recordsMap.existing, function (mapItem) {
                mapItem.storeRecord.copyFrom(mapItem.operationRecord);
                mapItem.operationRecord.destroy();
            });
        }
    }

});
