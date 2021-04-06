Ext.define('Unidata.proxy.data.SearchProxyCombo', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.data.searchproxycombo',

    constructor: function () {
        this.callParent(arguments);
    },

    url: Unidata.Config.getMainUrl() + 'internal/search/combo',

    limitParam: 'count',

    actionMethods: {
        read: 'POST'
    },
    paramsAsJson: true,

    extraParams: {
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
    }
});
