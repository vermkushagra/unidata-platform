/**
 * Прокси для получения списка категорий правил качества
 *
 * @author Aleksandr Bavin
 * @date 2017-03-29
 */
Ext.define('Unidata.proxy.data.dataquality.info.Categories', {

    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.dataquality.info.categories',

    limitParam: '',
    startParam: '',
    pageParam: '',

    extraParams: {
        entityName: null
    },

    url: Unidata.Config.getMainUrl() + 'internal/info/meta/dq/categories',

    reader: {
        type: 'json',
        rootProperty: 'content'
    }

});
