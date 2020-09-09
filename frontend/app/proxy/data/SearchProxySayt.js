Ext.define('Unidata.proxy.data.SearchProxySayt', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.data.searchproxysayt',

    constructor: function () {
        this.callParent(arguments);
    },

    url: Unidata.Config.getMainUrl() + 'internal/search/sayt',

    limitParam: 'count',

    actionMethods: {
        read: 'POST'
    },
    paramsAsJson: true,

    extraParams: {
        '@type': 'SIMPLE',
        entity: '',
        text: '',
        searchFields: [],
        returnFields: [],   // пустой список возвращаемых полей
        sortFields: [],     // пустой список порядка сортировки
        //count: Number,
        //page:  Number,
        fetchAll: false
    },

    reader: 'json.search',

    writer: {
        type: 'json',
        writeAllFields: true
    },

    buildUrl: function (request) {
        var me = this,
            searchFields = request.getParam('searchFields'),
            returnFields = request.getParam('returnFields'),
            sortFields = request.getParam('sortFields'),
            url;

        if (Ext.isArray(searchFields)) {
            request.setParam('searchFields', searchFields);
        }

        if (Ext.isArray(returnFields)) {
            request.setParam('returnFields', returnFields);
        }

        if (Ext.isArray(sortFields)) {
            request.setParam('sortFields', sortFields);
        }

        url = me.callParent(arguments);

        return url;
    }
});
