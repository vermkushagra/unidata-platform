Ext.define('Unidata.proxy.data.SearchProxySimple', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.data.searchproxysimple',

    constructor: function () {
        this.callParent(arguments);
    },

    url: Unidata.Config.getMainUrl() + 'internal/search/simple',

    limitParam: 'count',

    actionMethods: {
        read: 'POST'
    },
    paramsAsJson: true,

    extraParams: {
        '@type': 'SIMPLE',  // нечто необходимое backend

        entity: '',
        text: '',
        searchFields: [],
        returnFields: [],   // пустой список возвращаемых полей
        sortFields: [],     // пустой список порядка сортировки
        /**
         * Допустимые значения в фасетах: duplicates_only, errors_only, inactive_only, pending_only
         */
        facets: [],
        qtype: 'MATCH', // qtype = MATCH | TERM (по подстроке | строгий поиск)
        operator: 'AND',
        source: false,
        //count: Number,
        //page:  Number,
        totalCount: true,
        countOnly: false,
        fetchAll: false
    },

    reader: 'json.search',

    writer: {
        type: 'json',
        writeAllFields: true
    },

    setQTypeTerm: function () {
        this.extraParams.qtype = 'TERM';
    },

    setQTypeMatch: function () {
        this.extraParams.qtype = 'MATCH';
    }
});
