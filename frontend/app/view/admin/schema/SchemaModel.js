Ext.define('Unidata.view.admin.schema.SchemaModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.schema',

    data: {
        entities: [],
        relations: {}
    },

    stores: {
        schemaStore: {
            model: 'Unidata.model.search.SearchHit',
            pageSize: 10000,
            proxy: {
                type: 'data.searchproxy',
                //limitParam: '',
                startParam: '',
                pageParam: '',
                url: Unidata.Config.getMainUrl() + 'internal/search/meta',
                extraParams: {
                    fields: 'type'
                }
            },
            listeners: {
                load: 'onSchemaLoad'
            }
        },
        relationsStore: {
            model: 'Unidata.model.entity.Relation',
            proxy: {
                extraParams: {
                    draft: false
                },
                url: Unidata.Config.getMainUrl() + 'internal/meta/relations',
                type: 'ajax',
                reader: {
                    type: 'json'
                }
            },
            listeners: {
                load: 'onRelationLoad'
            }
        }
    }

});
