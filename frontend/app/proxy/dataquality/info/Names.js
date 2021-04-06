/**
 * Прокси для получения списка имён правил качества
 *
 * @author Aleksandr Bavin
 * @date 2017-03-29
 */
Ext.define('Unidata.proxy.data.dataquality.info.Names', {

    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.dataquality.info.names',

    limitParam: '',
    startParam: '',
    pageParam: '',

    extraParams: {
        entityName: null
    },

    url: Unidata.Config.getMainUrl() + 'internal/info/meta/dq/names',

    reader: {
        type: 'json',
        rootProperty: 'content'
    }

});
