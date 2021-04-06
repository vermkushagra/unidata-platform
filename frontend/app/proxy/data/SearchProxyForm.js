Ext.define('Unidata.proxy.data.SearchProxyForm', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.data.searchproxyform',

    constructor: function () {
        this.callParent(arguments);
    },

    url: Unidata.Config.getMainUrl() + 'internal/search/form',

    limitParam: 'count',

    actionMethods: {
        read: 'POST'
    },
    paramsAsJson: true,

    extraParams: {
        '@type': 'FORM',    // нечто необходимое backend

        entity: '',

        asOf: null,         // дата на которую ищем

        /*
         * не строковые значение сравниваются строго
         * строковые значения всегда сравниваются не строго
         */
        formFields: [],     // пустая поисковая форма
        returnFields: [],   // пустой список возвращаемых полей
        sortFields: [],     // пустой список порядка сортировки
        /**
         * Допустимые значения в фасетах: duplicates_only, errors_only, inactive_only, pending_only
         */
        facets: [],         // пустой список фасетов
        qtype: 'MATCH',     // qtype = MATCH | TERM - при поиске по форме в нем нет необходимости
        operator: 'AND',    // operator = OR | AND
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

    getDefaultExtraParams: function () {
        return {};
    }
});
