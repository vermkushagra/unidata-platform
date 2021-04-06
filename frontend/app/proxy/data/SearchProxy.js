/**
 * Proxy for search queries
 */
Ext.define('Unidata.proxy.data.SearchProxy', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.data.searchproxy',

    constructor: function (params) {
        this.callParent(arguments);

        if (params.url) {
            this.setUrl(params.url);
        }

        if (params.extraParams) {
            this.setExtraParams(params.extraParams);
        }
    },

    // default base extra params for all search queries with default values
    defaultExtraParams: {
        qtype: 'MATCH',
        source: false,
        total_count: true,
        count_only: false,
        errors_only: false
    },

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
