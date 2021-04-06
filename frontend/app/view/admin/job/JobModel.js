/**
 * Модель компонента для управления операциями
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.JobModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.job',

    stores: {
        /**
         * Хранилище списка операций
         */
        jobsStore: {
            autoLoad: true,
            model: 'Unidata.model.job.Job',
            pageSize: 25,
            proxy: {
                type: 'un.jobs',
                urlParams: {
                    status: 'all'
                }
            }
        },

        /**
         * Хранилище списка метаинформации о операциях
         */
        jobMetaStore: {
            model: 'Unidata.model.job.JobMeta',
            autoLoad: false,
            proxy: {
                type: 'rest',
                url: Unidata.Api.getJobMetaNamesUrl(),
                reader: {
                    type: 'json',
                    // костыль, появившийся из-за того, что в момент разработки бэкенда ещё небыло
                    transform: {
                        fn: function (data) {
                            var result = [],
                                i,
                                item;

                            for (i = 0; i < data.length; i++) {

                                item = data[i];

                                result.push({
                                    id: item.jobNameReference,
                                    name: item.jobNameReference,
                                    parameters: item.parameters || []
                                });
                            }

                            return result;
                        }
                    }
                }
            }
        }
    }
});
