/**
 * Прокси для списка шагов операции
 *
 * @author Ivan Marshalkin
 * @date 2016-05-06
 */

Ext.define('Unidata.proxy.job.JobsProxy', {
    extend: 'Ext.data.proxy.Ajax',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.un.jobs',

    url: Unidata.Api.getJobsUrl(),

    urlParams: {
        status: null
    },

    reader: {
        type: 'json',
        rootProperty: 'content',
        totalProperty: 'total_count'
    },

    buildUrl: function (request) {
        var me    = this,
            start = (request.getParam('page') - 1) * request.getParam('limit'),
            count = request.getParam('limit'),
            url,
            urlParts,
            urlBase,
            urlParams;

        url = me.callParent(arguments);

        if (Ext.isNumber(start) || Ext.isNumber(count)) {
            urlParts  = url.split('?');
            urlBase   = urlParts[0];
            urlParams = urlParts[1];

            url = urlBase + '/' + start + '/' + count + '<tpl if="status">/{status}</tpl>' + '?' + urlParams;
        }

        return url;
    }

});
