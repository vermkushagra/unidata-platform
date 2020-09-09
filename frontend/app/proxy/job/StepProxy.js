/**
 * Прокси для списка шагов операции
 *
 * @author Ivan Marshalkin
 * @date 2016-04-29
 */

Ext.define('Unidata.proxy.job.StepProxy', {
    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.un.jobstep',

    reader: {
        type: 'json',
        rootProperty: 'content',
        totalProperty: 'total_count'
    },

    buildUrl: function (request) {
        var me             = this,
            jobExecutionId = request.getParam('jobExecutionId'),
            start          = (request.getParam('page') - 1) * request.getParam('limit'),
            count          = request.getParam('limit'),
            url,
            urlParts,
            urlBase,
            urlParams;

        url = me.callParent(arguments);

        urlParts  = url.split('?');
        urlBase   = urlParts[0];
        urlParams = urlParts[1];

        url = urlBase + '/' + jobExecutionId + '/' + start + '/' + count + '?' + urlParams;

        return url;
    }

});
