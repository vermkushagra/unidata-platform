/**
 * Прокси для получения статистики
 *
 * @author Aleksandr Bavin
 * @date 2017-06-22
 */
Ext.define('Unidata.proxy.stat.StatsProxy', {

    extend: 'Ext.data.proxy.Ajax',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.stat.stats',

    model: 'Unidata.model.dashboard.Stats',

    limitParam: '',
    startParam: '',
    pageParam: '',
    paramsAsJson: true,

    baseUrl: Unidata.Config.getMainUrl(),

    url: 'internal/data/stat/get-stats',

    extraParams: {
        // startDate: null,
        // endDate: null,
        // entityName: null,
        // sourceSystemName: null,
        // granularity: null
    },

    reader: {
        type: 'json',
        rootProperty: 'stats'
    }

});
