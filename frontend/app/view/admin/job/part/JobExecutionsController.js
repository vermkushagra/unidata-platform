/**
 * Контроллер компонента для просмотра запусков операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */
Ext.define('Unidata.view.admin.job.part.JobExecutionsController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.job.executions',

    requires: [
        'Unidata.util.Job'
    ],

    /**
     * Таблица со списком запусков операции
     *
     * @type {Ext.grid.Panel}
     */
    executionsGrid: null,

    /**
     * Таблица со списком шагов запуска операции
     *
     * @type {Ext.grid.Panel}
     */
    executionStepsGrid: null,

    /**
     * Хранилище со списком запусков операции
     *
     * @type {Ext.data.Store}
     */
    executionsStore: null,

    /**
     * Хранилище со списком шагов запуска операции
     *
     * @type {Ext.grid.Panel}
     */
    executionStepsStore: null,

    init: function () {

        this.callParent(arguments);

        this.executionsGrid      = this.lookupReference('executions');
        this.executionStepsGrid  = this.lookupReference('executionSteps');
        this.executionStepsGrid.on('disable', function (grid) {
            grid.setTitle(Unidata.i18n.t('admin.job>operationSteps'));
        });

        this.executionsStore     = this.getStore('executions');
        this.executionStepsStore = this.getStore('executionSteps');

        this.executionsPaging      = this.lookupReference('executionsPaging');
        this.executionStepsPaging  = this.lookupReference('executionStepsPaging');

        this.executionsStore.on('beforeload', this.onBeforeLoadExecutionStore, this);
        this.executionStepsStore.on('beforeload', this.onBeforeLoadExecutionStepsStore, this);
    },

    /**
     * @returns {Unidata.model.job.Job}
     */
    getJob: function () {
        return this.getViewModel().get('job');
    },

    /**
     * @param {Unidata.model.job.Job} job
     */
    setJob: function (job) {

        var me              = this,
            executionsStore = me.executionsStore,
            executionsGrid  = me.executionsGrid,
            viewModel       = me.getViewModel(),
            previousJob     = me.getJob();

        if (previousJob) {
            previousJob.unjoin(me);
        }

        if (job) {
            job.join(me);
        }

        viewModel.set('job', job);

        if (me.refreshAllowed()) {
            me.refresh();
            executionsGrid.setDisabled(false);
        } else {
            executionsStore.removeAll();
            me.executionsPaging.updateBarInfo();
            executionsGrid.setDisabled(true);
        }

        me.executionStepsStore.removeAll();
        me.executionStepsPaging.updateBarInfo();
        me.executionStepsGrid.setDisabled(true);

        me.updateButtonsStates();

    },

    /**
     * Проверяет, можно ли обновлять список операции
     *
     * @returns {boolean}
     */
    refreshAllowed: function () {
        var job = this.getJob();

        return job && !job.phantom;
    },

    /**
     * Перезагружает список выполнений операции
     */
    refresh: function () {
        this.executionsStore.load();
    },

    /**
     * Обновление состояния всех кнопок управления запуском/остановкой операции
     */
    updateButtonsStates: function () {
        var viewModel = this.getViewModel(),
            job = this.getJob(),
            execution = this.executionsGrid.getSelection()[0],
            lock = viewModel.get('lockButtons'),
            executionRestartable = false;

        if (execution) {
            executionRestartable = execution.get('restartable');
        }

        viewModel.set({
            startAllowed:          Boolean(!lock && job && job.startAllowed()),
            stopAllowed:           Boolean(!lock && job && job.stopAllowed()),
            startExecutionAllowed: Boolean(!lock && job && job.startAllowed() && execution && executionRestartable),
            refreshAllowed:        this.refreshAllowed()
        });
    },

    stepActionIsDisabled: function (view, rowIdx, colIdx, item, record) {
        return record.get('exitCode') !== Unidata.model.job.execution.Execution.STATUS_FAILED;
    },

    onSelectExecution: function () {
        var me = this;

        this.executionStepsStore.loadPage(1, {
            callback: function (records, operation, success) {
                var response;

                if (success) {
                    response = Ext.util.JSON.decode(operation.getResponse().responseText);

                    me.executionStepsGrid.setTitle(
                        Unidata.i18n.t('admin.job>operationSteps') +
                        ': ' +
                        Ext.String.format(
                            Unidata.i18n.t('admin.job>operationStepsCompleted'),
                            response.completed_count,
                            response.total_count
                        )
                    );
                }

                me.executionStepsGrid.setDisabled(false);

                me.updateButtonsStates();
            }
        });

        me.executionStepsGrid.setDisabled(true);
    },

    onDeselectExecution: function () {
        this.executionStepsStore.removeAll();
        this.executionStepsPaging.updateBarInfo();
        this.executionStepsGrid.setDisabled(true);

        this.updateButtonsStates();
    },

    setLockButtons: function (value) {
        this.getViewModel().set('lockButtons', value);
        this.updateButtonsStates();
    },

    onStartClick: function () {
        var me = this,
            view = this.getView(),
            job = this.getJob();

        me.setLockButtons(true);

        Unidata.util.Job.startJob(job, function () {
            view.fireEvent('jobstatuschanged', job);
            me.setLockButtons(false);
        });
    },

    onStopClick: function () {
        var me = this,
            view = this.getView(),
            job = this.getJob();

        me.setLockButtons(true);

        Unidata.util.Job.stopJob(job, function () {
            view.fireEvent('jobstatuschanged', job);
            me.setLockButtons(false);
        });
    },

    onRefreshClick: function () {
        this.refresh();
    },

    onJobStatusChange: function () {

        this.updateButtonsStates();

        if (this.refreshAllowed()) {
            this.refresh();
        }
    },

    onJobEnableChange: function () {
        this.updateButtonsStates();
    },

    onShowErrorMessageClick: function (grid, rowIndex) {
        var record = grid.getStore().getAt(rowIndex);

        Ext.widget({
            xtype: 'admin.job.execution.error.wnd',
            exitCode: record.get('exitCode'),
            exitDescription: record.get('exitDescription')
        });
    },

    onBeforeLoadExecutionStepsStore: function (store) {
        var proxy = store.getProxy(),
            jobExecutionId,
            record;

        record = this.executionsGrid.getSelection()[0];

        if (!record) {
            return false;
        }

        jobExecutionId = record.get('id');

        if (proxy) {
            proxy.setExtraParam('jobExecutionId', jobExecutionId);
        }
    },

    onBeforeLoadExecutionStore: function (store) {
        var proxy = store.getProxy(),
            job = this.getJob(),
            executionsGrid = this.executionsGrid,
            record,
            selectedId,
            jobId;

        record = this.executionsGrid.getSelection()[0];

        if (record) {
            selectedId = record.get('id');
            executionsGrid.setSelection(false);
        }

        if (job) {
            jobId = job.get('id');
        }

        if (proxy) {
            proxy.setExtraParam('jobId', jobId);
        }

        store.on('load', function () {
            var selectedRecord;

            if (selectedId) {
                selectedRecord = store.getById(selectedId);
            }

            if (selectedRecord) {
                executionsGrid.setSelection(selectedRecord);
            }
        }, this, {single: true});
    },

    onStartExecutionClick: function () {
        var me = this,
            record;

        record = this.executionsGrid.getSelection()[0];

        me.setLockButtons(true);

        if (record) {
            Unidata.util.Job.restartExecution(record, function (options, success) {
                if (success) {
                    me.executionsStore.load();
                }

                me.setLockButtons(false);
            });
        }
    }

});
