/**
 * Контроллер компонента для редактирования операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.part.JobEditorController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.job.editor',

    /**
     * @var {Ext.form.Panel}
     */
    jobFormPanel: null,

    /**
     * @var {Unidata.view.admin.job.part.JobParametersEditor}
     */
    parametersGrid: null,

    /**
     * @var {Ext.form.field.Checkbox}
     */
    activeCheckbox: null,

    init: function () {

        var me = this;

        me.callParent(arguments);

        me.jobFormPanel   = me.lookupReference('jobForm');
        me.parametersGrid = me.lookupReference('parametersGrid');
        me.activeCheckbox = me.lookupReference('activeCheckbox');

        me.getViewModel().bind('{job}', me.onJobChanged, me);

    },

    /**
     * @param {Unidata.model.job.Job} job
     */
    setJob: function (job) {
        var JobTriggerApi = Unidata.util.api.JobTrigger,
            me = this,
            view = this.getView(),
            jobTriggerPanel = view.jobTriggerPanel,
            viewModel = me.getViewModel(),
            currentJob = viewModel.get('job'),
            jobId;

        if (currentJob) {
            currentJob.unjoin(me);
        }

        viewModel.set('job', job);

        if (job) {

            jobId = job.get('id')

            job.join(me);

            me.onJobMetaChanged();
            jobTriggerPanel.setLoading(true);
            jobTriggerPanel.setJobId(jobId);
            // грузим список операции
            jobTriggerPanel.getStore().load(function (records, operation, success) {
                if (success) {
                    // затем грузим список триггеров
                    JobTriggerApi.loadJobTriggers(jobId)
                        .then(function (jobTriggers) {
                            var JobTrigger = Unidata.view.admin.job.part.JobTrigger,
                                successJobTrigger,
                                failureJobTrigger;

                            successJobTrigger = JobTrigger.findJobTrigger(jobTriggers, true);
                            failureJobTrigger = JobTrigger.findJobTrigger(jobTriggers, false);

                            jobTriggerPanel.resetValues(null);
                            jobTriggerPanel.setLastSuccessJobTrigger(successJobTrigger);
                            jobTriggerPanel.setLastFailureJobTrigger(failureJobTrigger);
                            jobTriggerPanel.setLoading(false);
                        }, function () {
                            jobTriggerPanel.setLoading(false);
                            Unidata.showError(Unidata.i18n.t('admin.job>jobTriggersLoadingError'));
                        });
                } else {
                    jobTriggerPanel.setLoading(false);
                    Unidata.showError(Unidata.i18n.t('admin.job>jobsLoadingError'));
                }
            });

        }

        me.updateReadOnly();
    },

    /**
     * @returns {Unidata.model.job.Job}
     */
    getJob: function () {
        return this.getViewModel().get('job');
    },

    /**
     * проверяем на наличие прав у пользователя
     *
     * @param {String} action
     *
     * @returns {boolean}
     */
    userHasRight: function (action) {
        return Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', action);
    },

    /**
     * Обновляет список всех флагов для ui, чтобы лочить возможности редактирования
     */
    updateReadOnly: function () {

        var me              = this,
            viewModel       = me.getViewModel(),
            job             = me.getJob(),
            jobCreation     = job && job.phantom,
            creationAllowed = me.userHasRight('create'),
            updateAllowed   = me.userHasRight('update') && (!job || !job.get('error')),
            deleteAllowed   = me.userHasRight('delete');

        if (!job) {
            viewModel.set('readOnly', true);
            viewModel.set('nameEditable', false);
            viewModel.set('deleteAllowed', false);
        } else {
            viewModel.set('readOnly', jobCreation ? !creationAllowed : !updateAllowed);
            viewModel.set('nameEditable', jobCreation && creationAllowed);
            viewModel.set('deleteAllowed', !jobCreation && deleteAllowed);
        }

    },

    /**
     * Вызывается моделью, если какое-либо поле в ней изменилось
     *
     * @param {Unidata.model.job.Job} job
     * @param {String[]} fieldNames
     */
    afterEdit: function (job, fieldNames) {
        if (Ext.Array.indexOf(fieldNames, 'jobNameReference') !== -1) {
            this.onJobMetaChanged();
        }

        if (Ext.Array.indexOf(fieldNames, 'enabled') !== -1) {
            this.onEnabledChange(job, job.get('enabled'));
        }
    },

    /**
     * Устанавливает все ошибки валидации параметров от сервера соответствующим атрибутам
     *
     * @param errors
     */
    setServerErrors: function (errors) {

        var i,
            error,
            fieldName,
            fieldValidationRule;

        for (i = 0; i < errors.length; i++) {
            error = errors[i];

            this.showValidationError(error.key, error.value);
        }
    },

    /**
     * Разбирает пришедшую ошибку валидации с сервера и возвращает готовые для показа пользователю данные.
     * Нужна, т.к. формат ответа от сервера весьма "оригинален"
     *
     * @param fieldName
     * @param value
     * @returns {Object}
     */
    parseErrorValidationValue: function (fieldName, value) {
        // удачного секаса (с) UN-1250
        var valueParts = value.split(':'),
            errorType = valueParts[0],
            errorTypes = errorType.split('_'),
            errorTypeLeft = errorTypes[0],
            errorTypeRight = errorTypes[1],
            errorValue = valueParts[1],
            tpl,
            params = {},
            fieldNameRegExp = /^\${3,3}/;

        switch (errorTypeRight) {
            case 'length':
                tpl = Unidata.i18n.t('admin.job>maxLengthExceeded');
                params.length = errorValue;
                break;
            case 'unique':
                tpl = Unidata.i18n.t('admin.job>fieldShouldUnique');
                break;
        }

        switch (errorTypeLeft) {
            case 'param':
                params.type = Unidata.i18n.t('admin.job>name');
                break;
            case 'value':
                params.type = Unidata.i18n.t('admin.job>value');
                break;
        }

        tpl = new Ext.Template(tpl);
        tpl.compile();

        return {
            fieldType: fieldNameRegExp.test(fieldName) ? 'field' : 'param',
            fieldName: fieldName.replace(fieldNameRegExp, ''),
            errorMessage: tpl.apply(params)
        };
    },

    /**
     * Показывает ошибку валидации
     *
     * @param fieldName
     * @param value
     */
    showValidationError: function (fieldName, value) {

        var error = this.parseErrorValidationValue(fieldName, value);

        if (error.fieldType === 'param') {
            this.parametersGrid.showValidationError(error.fieldName, error.errorMessage);
        } else {
            this.showFieldError(error.fieldName, error.errorMessage);
        }

    },

    /**
     * Показывает ошибку в форме редактирвоания операции
     *
     * @param fieldName
     * @param message
     */
    showFieldError: function (fieldName, message) {

        var field = this.jobFormPanel.getForm().findField(fieldName);

        if (!field) {
            return;
        }

        field.setActiveError(message);
    },

    /**
     * Обработчик события изменения метаинформации операции
     */
    onJobMetaChanged: function () {

        var viewModel    = this.getViewModel(),
            job          = this.getJob(),
            jobType      = job.get('jobNameReference'),
            jobMetaStore = viewModel.get('jobMetaStore'),
            meta         = jobMetaStore.findRecord('name', jobType);

        if (!meta && jobType) {
            meta = Ext.create('Unidata.model.job.JobMeta', {
                id: jobType,
                name: jobType,
                parameters: null
            });

            jobMetaStore.add(meta);
        }

        viewModel.set('jobMetaRecord', meta);

        this.updateMeta();

    },

    /**
     * Обновляет метаинформацию в операции. Нужно, т.к. сама операция не знает о существовании метаинформации
     */
    updateMeta: function () {

        var job = this.getJob(),
            jobType = job.get('jobNameReference'),
            meta = this.getViewModel().get('jobMetaStore').findRecord('name', jobType);

        job.setMeta(meta);

    },

    /**
     * Обработчик события изменения операции
     *
     * @param job
     */
    onJobChanged: function (job) {
        if (job && job.phantom) {
            this.jobFormPanel.getForm().clearInvalid();
        }
    },

    /**
     * Обработчик события клика по кнопке "удалчить"
     */
    onDeleteClick: function () {

        var me           = this,
            view         = me.getView(),
            viewModel    = me.getViewModel(),
            model        = viewModel.get('job');

        if (model.phantom) {
            return;
        }

        Unidata.showPrompt(Unidata.i18n.t('common:confirmation'), Unidata.i18n.t('admin.job>confirmRemoveOperation'), function () {
            model.erase({
                success: function () {
                    Unidata.showMessage(Unidata.i18n.t('admin.job>removeOperationSuccess'));

                    me.getView().fireEvent('deleteJob', model);
                }
            });
        });
    },

    /**
     * Сохранение операции
     *
     * @param cfg
     */
    save: function (cfg) {

        var me               = this,
            jobFormPanel     = me.jobFormPanel,
            view             = me.getView(),
            viewModel        = me.getViewModel(),
            model            = viewModel.get('job'),
            isCreation       = model.phantom,
            skipCronWarnings = false;

        cfg = cfg || {};

        skipCronWarnings = cfg.skipCronWarnings; // если true, то операция сохранится, даже если подозрительный cron

        if (!jobFormPanel.isValid()) {
            return;
        }

        model.set('skipCronWarnings', skipCronWarnings);

        model.save({
            scope: me,
            callback: function (record, operation, success) {
                var newData,
                    msg,
                    response,
                    jobTriggerPanel = view.jobTriggerPanel;

                if (!success) {
                    return;
                }

                response = operation.getResponse();

                if (!response) {
                    return;
                }

                newData = JSON.parse(response.responseText);

                // сейчас с сервера lastExecution не приходит. Возможно, в будущем будет приходить и эти строчки
                // будут не нужны
                if (!newData.lastExecution) {
                    newData.lastExecution = record.get('lastExecution');
                }

                record.set(newData);
                record.commit();

                if (isCreation) {
                    me.getView().fireEvent('newJob', record);
                    msg = Unidata.i18n.t('admin.job>createOperationSuccess');
                } else {
                    me.getView().fireEvent('updateJob', record);
                    msg = Unidata.i18n.t('admin.job>saveOperationSuccess');
                }

                Unidata.showMessage(msg);

                me.updateReadOnly();
                jobTriggerPanel.persistJobTriggers()
                    .then(function (results) {
                        var successJobTriggerJson,
                            failureJobTriggerJson,
                            successJobTrigger,
                            failureJobTrigger;

                        successJobTriggerJson = results[0];
                        failureJobTriggerJson = results[1];

                        reader = Ext.create('Ext.data.JsonReader', {
                            model: 'Unidata.model.job.JobTrigger'
                        });

                        if (successJobTriggerJson) {
                            successJobTrigger = reader.readRecords(successJobTriggerJson).records[0];

                            if (successJobTrigger) {
                                jobTriggerPanel.setLastSuccessJobTrigger(successJobTrigger);
                            }
                        }

                        if (failureJobTriggerJson) {
                            failureJobTrigger = reader.readRecords(failureJobTriggerJson).records[0];

                            if (failureJobTrigger) {
                                jobTriggerPanel.setLastFailureJobTrigger(failureJobTrigger);
                            }
                        }

                    },
                    function () {
                        Unidata.showError(Unidata.i18n.t('admin.job>jobTriggersPersistError'));
                    });
            },
            failure: function (record, operation) {
                var me = this,
                    Job = Unidata.model.job.Job,
                    response = operation.getResponse(),
                    responseData,
                    errors,
                    error,
                    i,
                    userMessage;

                if (!response) {
                    return;
                }

                responseData = JSON.parse(response.responseText);
                errors = responseData.errors;

                for (i = 0; i < errors.length; i++) {

                    error = errors[i];
                    userMessage = error.userMessage;

                    // обрабатываем ошибки
                    switch (error.errorCode) {

                        case Job.E_PARAMETER_VALIDATION:
                            me.setServerErrors(error.params);
                            break;

                        case Job.E_SAME_NAME:
                            me.showFieldError('name', Unidata.i18n.t('admin.job>operationWithNameExists'));
                            break;

                        case Job.E_CRON_EXPRESSION:
                            me.showFieldError('cronExpression', Unidata.i18n.t('admin.job>incorrectExpression'));
                            break;

                        case Job.E_SAME_PARAMETERS:
                            Unidata.showError(userMessage);
                            break;

                        case Job.E_CRON_SUSPICIOUS_SECOND:
                        case Job.E_CRON_SUSPICIOUS_MINUTE:
                        case Job.E_CRON_SUSPICIOUS_SHORT_CYCLES_DOM:
                            if (errors.length === 1) {
                                Unidata.showPrompt(Unidata.i18n.t('admin.job>warning'), userMessage, function () {
                                    me.save({
                                        skipCronWarnings: true
                                    });
                                });
                            }
                            break;
                    }
                }

            },
            // не обратываем ошибку глобально в overrides.data.Connection (не выводим toast)
            supressGlobalFailureHandling: true
        });

    },

    /**
     * Обработчик события клика по кнопке сохранения
     */
    onSaveClick: function () {
        this.save();
    },

    /**
     * Обработчик клика по галочке "включить/выключить" операцию
     *
     * @param {Unidata.model.job.Job} job
     * @param {Boolean} newValue
     */
    onEnabledChange: function (job, newValue) {
        if (job.phantom) {
            return;
        }

        job.commit();
        Unidata.util.Job.markJob(job, newValue);
    }
});
