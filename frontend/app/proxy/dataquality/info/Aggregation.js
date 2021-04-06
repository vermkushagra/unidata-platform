/**
 * Прокси для получения списка агрегированных данных по dq
 *
 * @author Aleksandr Bavin
 * @date 2017-03-29
 */
Ext.define('Unidata.proxy.data.dataquality.info.Aggregation', {

    extend: 'Unidata.proxy.rest.Extended',

    alias: 'proxy.dataquality.info.aggregation',

    model: 'Unidata.model.table.Table',

    appendId: false,

    limitParam: '',
    startParam: '',
    pageParam: '',

    extraParams: {
        entityName: null
    },

    url: Unidata.Config.getMainUrl() + 'internal/data/stat/get-dq-aggregation',

    reader: {
        type: 'json',
        rootProperty: 'content'
    }

});
