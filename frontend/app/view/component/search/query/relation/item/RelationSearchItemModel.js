Ext.define('Unidata.view.component.search.query.relation.item.RelationSearchItemModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query.relation.item.relationsearchitem',

    data: {
        searchQuery: null,
        entityTo: null,
        etalonCluster: null,
        selectionMode: null
    },

    formulas: {
        relationPickerEnabled: {
            bind: {
                metaRecord: '{searchQuery.term.entity.metaRecord}'
            },
            get: function (getter) {
                var MetaRecordUtil = Unidata.util.MetaRecord,
                    metaRecord = getter.metaRecord;

                return Boolean(metaRecord) && MetaRecordUtil.isEntity(metaRecord);
            }
        },
        recordGridEnabled: {
            bind: {
                entityTo: '{entityTo}'
            },
            get: function (getter) {
                return Boolean(getter.entityTo);
            }
        }
    }
});
