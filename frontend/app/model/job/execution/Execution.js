/**
 * Модель выполнения операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.model.job.execution.Execution', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.job.execution.ExecutionStep'
    ],

    statics: {
        /*
         * Статусы
         *
         * @see https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/BatchStatus.html
         */
        STATUS_ABANDONED: 'ABANDONED',
        STATUS_COMPLETED: 'COMPLETED',
        STATUS_FAILED:    'FAILED',
        STATUS_STARTED:   'STARTED',
        STATUS_STARTING:  'STARTING',
        STATUS_STOPPED:   'STOPPED',
        STATUS_STOPPING:  'STOPPING',
        STATUS_UNKNOWN:   'UNKNOWN',
        // для внутреннего использования, к springframework не относятся
        STATUS_WAIT:      'WAIT',

        getStatusMapper: function () {

            var me = this,
                result = {};

            result[me.STATUS_ABANDONED] = Unidata.i18n.t('model>job.statusAbandoned');
            result[me.STATUS_COMPLETED] = Unidata.i18n.t('glossary:completed', {context: 'female'});
            result[me.STATUS_FAILED]    = Unidata.i18n.t('common:error');
            result[me.STATUS_STARTED]   = Unidata.i18n.t('model>job.statusStarted');
            result[me.STATUS_STARTING]  = Unidata.i18n.t('model>job.statusStarting');
            result[me.STATUS_STOPPED]   = Unidata.i18n.t('model>job.statusStopped');
            result[me.STATUS_STOPPING]  = Unidata.i18n.t('model>job.statusStopping');
            result[me.STATUS_UNKNOWN]   = Unidata.i18n.t('model>job.statusUnknown');

            result[me.STATUS_WAIT]      = Unidata.i18n.t('model>job.statusWait');

            return result;
        },

        /**
         * Получение названия статуса выполнения операции
         *
         * @returns {String}
         */
        getStatusText: function (status) {

            var statusMapper = this.getStatusMapper();

            if (!statusMapper.hasOwnProperty(status)) {
                return status;
            }

            return statusMapper[status];

        }
    },

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'jobId',
            type: 'int'
        },
        {
            name: 'startTime',
            type: 'date',
            dateReadFormat: 'c'
        },
        {
            name: 'endTime',
            type: 'date',
            dateReadFormat: 'c'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'stepExecutions',
            type: 'auto'
        },
        {
            name: 'restartable',
            type: 'boolean'
        }
    ],

    getStatusText: function () {
        return this.self.getStatusText(this.getStatus());
    },

    getStatus: function () {
        return this.get('status');
    }

});
