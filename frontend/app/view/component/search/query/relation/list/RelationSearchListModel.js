Ext.define('Unidata.view.component.search.query.relation.list.RelationSearchListModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query.relation.list.relationsearchlist',

    data: {
        metaRecord: null
    },

    formulas: {
        addRelationSearchItemButtonEnabled: {
            bind: {
                metaRecord: '{metaRecord}'
            },
            get: function (getter) {
                var MetaRecordUtil = Unidata.util.MetaRecord,
                    metaRecord = getter.metaRecord,
                    relations;

                if (!Boolean(metaRecord) || !MetaRecordUtil.isEntity(metaRecord)) {
                    return false;
                }

                relations = metaRecord.getRelationsFilteredByPermission();

                return relations.length > 0;
            }
        }
    }
});
