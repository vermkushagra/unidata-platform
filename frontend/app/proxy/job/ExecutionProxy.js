/**
 * Прокси для списка запусков операции
 *
 * @author Ivan Marshalkin
 * @date 2016-05-04
 */

Ext.define('Unidata.proxy.job.ExecutionProxy', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.un.jobexecution',

    reader: {
        type: 'json',
        rootProperty: 'content',
        totalProperty: 'total_count'
    },

    buildUrl: function (request) {
        var me    = this,
            jobId = request.getParam('jobId'),
            start = (request.getParam('page') - 1) * request.getParam('limit'),
            count = request.getParam('limit'),
            url,
            urlParts,
            urlBase,
            urlParams;

        url = me.callParent(arguments);

        urlParts  = url.split('?');
        urlBase   = urlParts[0];
        urlParams = urlParts[1];

        url = urlBase + '/' + jobId + '/' + start + '/' + count + '?' + urlParams;

        return url;
    }

});
