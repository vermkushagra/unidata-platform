/**
 * Прокси для получения статистики по ошибкам
 *
 * @author Aleksandr Bavin
 * @date 2017-06-27
 */
Ext.define('Unidata.proxy.stat.ErrorStatsProxy', {

    extend: 'Unidata.proxy.stat.StatsProxy',

    alias: 'proxy.stat.stats.error',

    url: 'internal/data/stat/get-error-stats',

    extraParams: {
        // entityName: null
    },

    reader: {
        type: 'json',
        rootProperty: 'data'
    }

});
