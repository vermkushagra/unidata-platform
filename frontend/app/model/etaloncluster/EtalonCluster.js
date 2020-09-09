/**
 * Модель набора записей
 *
 * @author Sergey Shishigin
 * @date 2017-02-02
 */
Ext.define('Unidata.model.etaloncluster.EtalonCluster', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'entityName',
            type: 'string'
        },
        {
            name: 'entityType',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'records',
            model: 'etaloncluster.EtalonClusterRecord'
        }
    ],

    statics: {
        /**
         * Создать EtalonCluster
         *
         * @param name {String}
         * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
         * @param searchHits {Unidata.model.search.SearchHit[]}
         * @return {Unidata.model.etaloncluster.EtalonCluster}
         */
        fromSearchHits: function (name, metaRecord, searchHits) {
            var EtalonClusterModel = Unidata.model.etaloncluster.EtalonCluster,
                etalonCluster,
                entityName,
                entityType,
                records;

            entityName = metaRecord.get('name');
            entityType = metaRecord.getType();

            etalonCluster = Ext.create('Unidata.model.etaloncluster.EtalonCluster', {
                name: name,
                entityName: entityName,
                entityType: entityType
            });

            records = etalonCluster.records();

            Ext.Array.each(searchHits, function (searchHit) {
                var record;

                record = EtalonClusterModel.mapSearchHitToEtalonClusterRecord(searchHit, metaRecord);
                records.add(record);
            });

            return etalonCluster;
        },

        /**
         *
         * @param searchHit {Unidata.model.search.SearchHit}
         * @param metaRecord {Unidata.model.entity.Entity}
         * @returns {Unidata.model.etaloncluster.EtalonClusterRecord}
         */
        mapSearchHitToEtalonClusterRecord: function (searchHit, metaRecord) {
            var DataAttributeFormatter = Unidata.util.DataAttributeFormatter,
                displayValue,
                item;

            displayValue = DataAttributeFormatter.buildEntityTitleFromSearchHit(metaRecord, searchHit);

            item = Ext.create('Unidata.model.etaloncluster.EtalonClusterRecord', {
                etalonId: searchHit.get('id'),
                etalonDate: null,
                displayValue: displayValue
            });

            return item;
        }
    }
});
