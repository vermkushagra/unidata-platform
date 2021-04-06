/**
 * Прокси для списка кластеров
 *
 * @author Ivan Marshalkin
 * @date 2016-10-31
 */

Ext.define('Unidata.proxy.cluster.ClusterList', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.un.clusterlist',

    constructor: function () {
        var me = this;

        me.callParent(arguments);

        me.setReader({
            type: 'json',
            transform: me.transformIncData.bind(me),
            rootProperty: 'content',
            totalProperty: 'total_count'
        });
    },

    transformIncData: function (data) {
        // backend не присылает этот поле т.к. расчет его ресурсоемок
        // количество кластеров возвращается отдельным запросм
        data['total_count'] = this.totalCount;

        return data;
    },

    buildUrl: function (request) {
        var me    = this,
            url   = Unidata.Api.getClusterListUrl(request.getParam('entityName')),
            proxy = request.getProxy();

        proxy.setUrl(url);

        url = me.callParent(arguments);

        url += '&' + Ext.Object.toQueryString({fields: request.getParam('fields')});

        return url;
    }
});
