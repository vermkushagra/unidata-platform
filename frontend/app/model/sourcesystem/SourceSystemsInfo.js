Ext.define('Unidata.model.sourcesystem.SourceSystemsInfo', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'adminSystemName',
            type: 'string'
        }
    ],

    hasMany: [
        {
            name: 'sourceSystem',
            model: 'sourcesystem.SourceSystem'
        }
    ],

    proxy: {
        type: 'rest',

        url: Unidata.Config.getMainUrl() + 'internal/meta/source-systems',
        appendId: false
    }
});
