/**
 * Утилитный класс для работы с записью
 *
 * @author Ivan Marshalkin
 * @date 2016-03-16
 */

Ext.define('Unidata.util.DataRecord', {
    singleton: true,

    // TODO: вынести в Unidata.util.Model
    /**
     * Изменяем значение свойства record.localVersion по событию datachanged, update для
     * каждого ManyToOne association (store) и вложенных associations
     *
     * Вызывать данный метод нужно сразу после создания record. В этом случае на associations stores будут
     * повешены обработчики событий datachanged, update.
     * При возникновении этих событий значение localVersion для bindRecord инкрементируется.
     *
     */
    bindManyToOneAssociationListeners: function (record, bindRecord) {
        var associations;
        //isLocalVersionBinded;

        bindRecord = bindRecord || record;
        //isLocalVersionBinded = bindRecord.get('isLocalVersionBinded');
        //isLocalVersionBinded = isLocalVersionBinded !== undefined ? isLocalVersionBinded : false;

        //if (isLocalVersionBinded) {
        //    return;
        //} else {
        //    bindRecord.set('isLocalVersionBinded', true);
        //}

        function createLocalVersionField (bindRecord) {
            var clazz = Ext.getClass(bindRecord);

            clazz.addFields({
                name: 'localVersion',
                type: 'integer',
                persist: false
            });
        }

        function findFieldByName (record, searchFieldName) {
            var fields = record.fields,
                foundField;

            foundField = Ext.Array.findBy(fields, function (field) {
                var fieldName = field.name;

                return fieldName === searchFieldName;
            });

            return foundField;
        }

        function getManyToOneAssociations (record) {
            var associations;

            associations = Object.keys(record.associations).map(function (key) {
                return record.associations[key];
            });

            associations = Ext.Array.filter(associations, function (association) {
                return association.association instanceof Ext.data.schema.ManyToOne;
            });

            return associations;
        }

        function bindStoreEventListeners (record, bindRecord, association) {
            var associationStore;

            // магия, чтобы убедиться, что модель действительно содержит association как исходящий
            if (association.type !== association.model) {
                return;
            }

            associationStore = record[association.getterName].call(record);

            if (associationStore && associationStore.isStore) {
                associationStore.on('datachanged', onStoreDataChanged.bind(this, bindRecord));
                associationStore.on('update', onStoreDataChanged.bind(this, bindRecord));
                associationStore.on('add', onStoreAdd.bind(this, bindRecord));

                associationStore.each(function (innerRecord) {
                    Unidata.util.DataRecord.bindManyToOneAssociationListeners(innerRecord, bindRecord);
                });
            }
        }

        function onStoreDataChanged (bindRecord) {
            var localVersion = bindRecord.get('localVersion');

            bindRecord.set('localVersion', ++localVersion);
        }

        function onStoreAdd (bindRecord, store, records) {
            records.forEach(function (record) {
                Unidata.util.DataRecord.bindManyToOneAssociationListeners(record, bindRecord);
            });
        }

        // Функция создания field'a localVersion в случае его отсутствия отключено из соображений производительности
        //if (!findFieldByName(bindRecord, LOCAL_VERSION_FIELD_NAME)) {
        //    createLocalVersionField(bindRecord);
        //}

        associations = getManyToOneAssociations(record);
        associations.forEach(bindStoreEventListeners.bind(record, record, bindRecord));
    },

    /**
     * Возвращает массив ошибок правил качества по критичности
     *
     * @param dataRecord   - экземпляр записи
     * @param severityName - кодовое имя критичности
     */
    getAllDqErrorsBySeverity: function (dataRecord, severityName) {
        var result = [];

        dataRecord.dqErrors().each(function (dqError) {
            if (dqError.get('severity') === severityName) {
                result.push(dqError);
            }
        });

        return Ext.Array.unique(result);
    },

    /**
     * Фабричный класс для создания нового датарекорда на базе метарекорда
     *
     * @param metaRecord
     * @returns {Unidata.model.data.Record|*}
     */
    buildDataRecord: function (metaRecord) {
        var dataRecord,
            rights,
            validityPeriod        = metaRecord.get('validityPeriod'),
            minDate               = null,
            maxDate               = null;

        minDate = Unidata.util.ValidityPeriod.getMinDate(validityPeriod);
        maxDate = Unidata.util.ValidityPeriod.getMaxDate(validityPeriod);

        dataRecord = Ext.create('Unidata.model.data.Record', {
            entityName: metaRecord.get('name'),
            validFrom: minDate,
            validTo: maxDate,
            status: 'NEW'
        });

        rights = Ext.create('Unidata.model.user.Right', {
            create: true,
            read: true,
            update: true,
            delete: false
        });

        dataRecord.setRights(rights);

        Unidata.util.DataRecord.bindManyToOneAssociationListeners(dataRecord);

        dataRecord.setId(null);

        return dataRecord;
    },

    /**
     * Получить массив etalonId
     *
     * @param dataRecordKeys
     * @returns {*|Ext.promise.Promise|{}|Array}
     */
    pluckEtalonIds: function (dataRecordKeys) {
        var etalonIds;

        etalonIds = Ext.Array.map(dataRecordKeys, function (dataRecordKey) {
            return dataRecordKey.get('etalonId');
        });

        return etalonIds;
    },

    /**
     * Создать DataRecordKey на базе dataRecord
     *
     * @param dataRecord
     * @returns {*}
     */
    buildDataRecordKey: function (cfg) {
        var dataRecordKey,
            dataRecord = cfg.dataRecord,
            etalonId = cfg.etalonId,
            etalonDate = cfg.etalonDate;

        if (!dataRecord && !etalonId) {
            return null;
        }

        if (dataRecord) {
            etalonId = dataRecord.get('etalonId');
            etalonDate = dataRecord.get('dateFrom') || dataRecord.get('dateTo');
        }

        etalonDate = etalonDate || null;

        dataRecordKey = Ext.create('Unidata.model.data.DataRecordKey', {
            etalonId: etalonId,
            etalonDate: etalonDate
        });

        return dataRecordKey;
    },

    /**
     * Проверяет относится ли dataRecord к типу Unidata.model.data.Record
     * @param dataRecord
     * @returns {boolean}
     */
    isDataRecord: function (dataRecord) {
        return dataRecord instanceof Unidata.model.data.Record;
    },

    /**
     * Проверяет относится ли dataRecord к типу Unidata.model.data.OriginRecord
     * @param dataRecord
     * @returns {boolean}
     */
    isOriginDataRecord: function (dataRecord) {
        return dataRecord instanceof Unidata.model.data.OriginRecord;
    }
});
