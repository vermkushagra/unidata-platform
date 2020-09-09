/**
 * Прокси для получения актуальной статистики
 *
 * @author Aleksandr Bavin
 * @date 2017-06-26
 */
Ext.define('Unidata.proxy.stat.LastStatsProxy', {

    extend: 'Unidata.proxy.stat.StatsProxy',

    alias: 'proxy.stat.stats.last',

    url: 'internal/data/stat/get-last-stats',

    extraParams: {
        // entityName: null
    }

});
