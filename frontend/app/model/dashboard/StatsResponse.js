Ext.define('Unidata.model.dashboard.StatsResponse', {
    extend: 'Unidata.model.Base',

    hasMany: [
        {
            name: 'stats',
            model: 'Unidata.model.dashboard.Stats'
        }
    ],

    proxy: {
        type: 'rest'
        //url: Unidata.Config.getMainUrl() + 'internal/data/entities/origin/'
    }
});
