/**
 * Прокси операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-23
 */

Ext.define('Unidata.proxy.job.JobProxy', {

    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.job',

    actionMethods: {
        create:  'PUT',
        read:    'GET',
        update:  'PUT',
        destroy: 'DELETE'
    },

    api: {
        create:  Unidata.Api.getJobsUrl(),
        read:    Unidata.Api.getJobsUrl(),
        update:  Unidata.Api.getJobsUrl(),
        destroy: Unidata.Api.getJobsUrl()
    },

    writer: {
        type: 'json',
        writeAllFields: true,
        writeRecordId: true,
        transform: {
            fn: function (data, request) {

                if (request.getAction() === 'create') {
                    delete data.id;
                }

                return data;

            }
        }
    },

    reader: {
        type: 'json',
        rootProperty: 'content'
    }

});
