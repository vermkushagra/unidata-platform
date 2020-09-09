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

    formulas: {
        jobExecutionPanelTitle: {
            bind: {
                startAllowed: '{startAllowed}'
            },
            get:  function (getter) {
                var tpl = new Ext.XTemplate('{title} <span style="font-weight: normal;">({desc})</span>'),
                    title = Unidata.i18n.t('admin.job>startList');

                if (!getter.startAllowed) {
                    title = tpl.apply({
                        title: Unidata.i18n.t('admin.job>startList'),
                        desc: Unidata.i18n.t('admin.job>onRun')
                    });
                }

                return title;
            }
        }
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
