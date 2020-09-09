/**
 * Модель операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.model.job.Job', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.proxy.job.JobProxy',
        'Unidata.model.job.JobMeta',
        'Unidata.model.job.execution.Execution',
        'Unidata.model.job.parameter.Parameter'
    ],

    statics: {

        // Ошибки сохранения
        E_PARAMETER_VALIDATION:             'EX_JOB_PARAMETER_VALIDATION_ERROR',
        E_SAME_NAME:                        'EX_JOB_SAME_NAME',
        E_CRON_EXPRESSION:                  'EX_JOB_CRON_EXPRESSION',
        E_SAME_PARAMETERS:                  'EX_JOB_SAME_PARAMETERS',
        E_CRON_SUSPICIOUS_SECOND:           'EX_JOB_CRON_SUSPICIOUS_SECOND',
        E_CRON_SUSPICIOUS_MINUTE:           'EX_JOB_CRON_SUSPICIOUS_MINUTE',
        E_CRON_SUSPICIOUS_SHORT_CYCLES_DOM: 'EX_JOB_CRON_SUSPICIOUS_SHORT_CYCLES_DOM',

        getStatusMapper: function () {

            var JobExecution = Unidata.model.job.execution.Execution,
                result = {};

            result[JobExecution.STATUS_ABANDONED] = Unidata.i18n.t('model>job.statusAbandoned');
            result[JobExecution.STATUS_COMPLETED] = Unidata.i18n.t('glossary:completed', {context: 'female'});
            result[JobExecution.STATUS_FAILED]    = Unidata.i18n.t('common:error');
            result[JobExecution.STATUS_STARTED]   = Unidata.i18n.t('model>job.statusStarted');
            result[JobExecution.STATUS_STARTING]  = Unidata.i18n.t('model>job.statusStarting');
            result[JobExecution.STATUS_STOPPED]   = Unidata.i18n.t('model>job.statusStopped');
            result[JobExecution.STATUS_STOPPING]  = Unidata.i18n.t('model>job.statusStopping');
            result[JobExecution.STATUS_UNKNOWN]   = Unidata.i18n.t('model>job.statusUnknown');

            result[JobExecution.STATUS_WAIT]      = Unidata.i18n.t('model>job.statusWait');

            return result;
        },

        /**
         * Получение названия статуса выполнения операции (функция аналогична функции в Unidata.model.job.execution.Execution,
         * но склонения другие)
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

    /**
     * Для новых записей - отрицательные id
     */
    identifier: 'negative',

    fields: [
        // серверные поля
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'cronExpression',
            type: 'string'
        },
        {
            name: 'jobNameReference',
            type: 'string'
        },
        {
            name: 'parameters',
            type: 'auto'
        },
        {
            name: 'lastExecution',
            type: 'auto'
        },
        {
            name: 'enabled',
            type: 'boolean'
        },
        {
            name: 'error',
            type: 'boolean',
            persist: false,
            defaultValue: false
        },
        // для внутреннего использования, на сервере не хранится
        {
            name: 'status',
            type: 'string',
            persist: false,
            convert: function (value, model) {

                if (value !== model.getStatus()) {
                    return value;
                }

                if (!model.get('lastExecution')) {
                    return Unidata.model.job.execution.Execution.STATUS_UNKNOWN;
                } else {
                    return model.get('lastExecution').status;
                }

            },
            depends: ['lastExecution']
        }
    ],

    hasMany: [
        // список параметров, при создании операции может меняться в зависимости от типа операции (см. функцию setMeta)
        {
            name: 'parameters',
            model: 'job.parameter.Parameter'
        }
    ],

    hasOne: [
        {
            name: 'lastExecution',
            model: 'job.JobExecution'
        }
    ],

    validators: {
        name: {
            type: 'length',
            emptyMessage: Unidata.i18n.t('model>operationNameRequired'),
            minOnlyMessage: Unidata.i18n.t('model>operationNameTooShort'),
            min: 2
        },
        jobNameReference: {
            type: 'presence',
            message: Unidata.i18n.t('model>operationTypeRequired')
        }
    },

    proxy: 'un.job',

    /**
     *
     * @param {Unidata.model.job.JobMeta} meta
     */
    setMeta: function (meta) {

        this.meta = meta;

        if (this.phantom) {
            this.resetParameters();
        } else {
            this.fixParameters();
        }
    },

    /**
     * Сбрасывает список параметров и устанавливает дефолтные значения
     */
    resetParameters: function () {
        var newParams = [],
            meta = this.meta,
            Parameter = Unidata.model.job.parameter.Parameter;

        if (meta) {
            meta.parameters().each(function (metaParam) {

                var paramModel = new Parameter(metaParam.getData());

                paramModel.set('value', metaParam.getValue());
                paramModel.setMeta(metaParam);

                newParams.push(paramModel);

            });
        }

        this.parameters().loadRecords(newParams);
    },

    /**
     * Обновляет данные в списке параметров, сортирует их в том порядке, как они хранятся в метамодели
     */
    fixParameters: function () {

        var metaParams = this.meta.parameters(),
            params = this.parameters(),
            Parameter = Unidata.model.job.parameter.Parameter,
            data = [];

        metaParams.each(function (metaParam) {

            var parameter = params.getAt(params.findExact('name', metaParam.get('name')));

            if (!parameter) {
                parameter = new Parameter(metaParam.getData());
                parameter.set('value', metaParam.getValue());
            }

            parameter.setMeta(metaParam);

            data.push(parameter);
        });

        params.loadRecords(data);

    },

    set: function () {

        var result = this.callParent(arguments);

        if (Ext.Array.indexOf(result || [], 'status') !== -1) {
            this.callJoined('onJobStatusChange', [this.getStatus()]);
        }

        if (Ext.Array.indexOf(result || [], 'enabled') !== -1) {
            this.callJoined('onJobEnableChange', [this.getStatus()]);
        }

        if (Ext.Array.indexOf(result || [], 'parameters') !== -1) {
            this.parameters().loadData(this.get('parameters') || []);
            this.fixParameters();
        }

        return result;
    },

    /**
     * Проверяет, можно ли запускать операцию
     *
     * @returns {boolean}
     */
    startAllowed: function () {
        var me = this,
            jobExecution = Unidata.model.job.execution.Execution,
            disallowStatuses = [
                jobExecution.STATUS_STARTING,
                jobExecution.STATUS_STARTED
            ];

        return me.get('enabled') && !me.get('error') && !Ext.Array.contains(disallowStatuses, me.getStatus());
    },

    /**
     * Проверяет, можно ли останавливать операцию
     *
     * @returns {boolean}
     */
    stopAllowed: function () {
        var me = this,
            jobExecution = Unidata.model.job.execution.Execution,
            allowStatuses = [
                jobExecution.STATUS_STARTING,
                jobExecution.STATUS_STARTED
            ];

        return me.get('enabled') && !me.get('error') && Ext.Array.contains(allowStatuses, me.getStatus());
    },

    /**
     * Установка статуса операции
     *
     * @param status
     */
    setStatus: function (status) {
        if (status !== this.status) {
            this.set('status', status, {
                commit: true
            });
        }
    },

    /**
     * Получение статуса операции.
     * Статус сперва задаётся на основании поля lastExecution
     * Если операцию пытались запустить / остановить (функции start/stop), то статус
     * будет результатом выполнения этих функций
     *
     * @returns {String}
     */
    getStatus: function () {
        return this.get('status');
    },

    /**
     * Получение названия статуса выполнения операции
     *
     * @returns {String}
     */
    getStatusText: function () {
        return this.self.getStatusText(this.getStatus());
    },

    /**
     * Сохранение модели. Добавляет к данным имененные данные параметров
     *
     * @returns {Object}
     */
    save: function () {

        this.set('parameters', this.getAssociatedData().parameters);

        return this.callParent(arguments);

    }

});
