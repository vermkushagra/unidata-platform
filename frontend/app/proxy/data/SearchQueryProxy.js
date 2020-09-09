/**
 * Прокси для работы поискового модуля
 * @see {Unidata.module.search.SearchQuery.privates.searchResultStore}
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.proxy.data.SearchQueryProxy', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.data.searchquery',

    url: Unidata.Config.getMainUrl() + 'internal/search',

    actionMethods: {
        read: 'POST'
    },

    paramsAsJson: true,

    writer: {
        type: 'json',
        writeAllFields: true
    },

    limitParam: 'count',

    reader: 'json.search',

    getDefaultExtraParams: function () {
        return this.defaultExtraParams;
    }
});
