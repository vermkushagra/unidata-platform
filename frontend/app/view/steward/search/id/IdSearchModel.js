/**
 * @author Aleksandr Bavin
 * @date 2016-12-21
 */
Ext.define('Unidata.view.steward.search.id.IdSearchModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.idsearch',

    stores: {
        sourceSystems: {
            model: 'Unidata.model.sourcesystem.SourceSystem',
            autoLoad: true,
            proxy: {
                type: 'rest',

                url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',
                reader: {
                    type: 'json',
                    rootProperty: 'sourceSystem'
                }
            }
        }
    }

});
