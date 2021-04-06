Ext.define('Unidata.view.admin.entity.metarecord.consolidation.ConsolidationModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.consolidation',

    data: {
        adminSystemName: '',
        bvtRecord: null,
        bvrRecord: null
    },

    stores: {
        attributeWeight: 'ext-empty-store',
        sourceSystemWeight: 'ext-empty-store',
        sourceSystems: {
            model: 'Unidata.model.sourcesystem.SourceSystem',
            autoLoad: false,
            listeners: {
                load: 'onSourceSystemsStoreLoad'
            },
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',
                reader: {
                    type: 'json',
                    rootProperty: 'sourceSystem'
                }
            }
        }
    }
});
