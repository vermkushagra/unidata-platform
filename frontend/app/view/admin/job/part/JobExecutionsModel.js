/**
 * Модель компонента для просмотра запусков операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */
Ext.define('Unidata.view.admin.job.part.JobExecutionsModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.job.executions',

    data: {
        job: null,
        lockButtons: false,
        startAllowed: true,
        stopAllowed: false,
        refreshAllowed: false,
        startExecutionAllowed: false
    },

    stores: {
        /**
         * Хранилище со списком запусков операции
         */
        executions: {
            autoLoad: false,
            model: 'Unidata.model.job.execution.Execution',
            proxy: {
                type: 'un.jobexecution',
                url: Unidata.Api.getJobExecutionsUrl()
            }
        },
        /**
         * Хранилище со списком шагов запуска операции
         */
        executionSteps: {
            model: 'Unidata.model.job.execution.ExecutionStep',
            proxy: {
                type: 'un.jobstep',
                url: Unidata.Api.getJobExecutionStepsUrl()
            }
        }
    }
});
