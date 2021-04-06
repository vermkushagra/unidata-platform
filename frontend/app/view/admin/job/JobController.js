/**
 * Контроллер компонента для управления операциями
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.JobController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.job',

    /**
     * Список со всеми операциями
     *
     * @type {Unidata.view.admin.job.part.JobList}
     */
    jobList: null,

    /**
     * Хранилище всех операций
     *
     * @type {Ext.data.Store}
     */
    jobsStore: null,

    /**
     * Контейнер, включающий в себя контент
     *
     * @type {Ext.container.Container}
     */
    content: null,

    /**
     * Редактор операции
     *
     * @type {Unidata.view.admin.job.part.JobEditor}
     */
    editor: null,

    /**
     * Грид со списком операций
     *
     * @type {Unidata.view.admin.job.part.JobExecutions}
     */
    executions: null,

    init: function () {

        var me = this,
            view;

        me.callParent(arguments);

        me.jobList        = me.lookupReference('jobList');
        me.jobsStore      = me.getStore('jobsStore');

        me.content        = me.lookupReference('content');
        me.editor         = me.lookupReference('editor');
        me.executions     = me.lookupReference('executions');

        me.loadMeta();

    },

    /**
     * Пока метаданных нет, нет смысла вообще работать.
     */
    loadMeta: function () {

        var me = this,
            jobMetaStore = me.getStore('jobMetaStore');

        me.setLoading(true);

        jobMetaStore.load({
            callback: function (records, operation, success) {
                me.setLoading(!success);
            }
        });

    },

    setLoading: function (loading) {

        var view = this.getView();

        this.loading = loading;

        if (!view.rendered) {
            return;
        }

        view.setLoading(loading);

    },

    getLoading: function () {
        return this.loading;
    },

    /**
     * Меняет выбранную операцию в списке операций
     *
     * @param {Unidata.model.job.Job} job
     */
    changeGridSelection: function (job) {

        var jobList = this.jobList;

        if (job && !job.phantom) {
            jobList.selectJob(job);
        } else {
            jobList.selectJob(false);
        }

    },

    /**
     * @param {Unidata.model.job.Job} job
     * @param {boolean} [update]
     */
    setJob: function (job, update) {

        var me = this,
            editor = me.editor,
            currentJob = editor.getJob();

        if (currentJob === job && !update) {
            me.changeGridSelection(job);

            return me;
        }

        function confirm () {

            me.changeGridSelection(job);

            editor.setJob(job);
            me.executions.setJob(job);

            if (job) {
                me.content.setHidden(false);
            } else {
                me.content.setHidden(true);
            }
        }

        function abort () {
            me.setJob(currentJob);
        }

        if (me.isDirty()) {
            Unidata.showPrompt(
                Unidata.i18n.t('admin.job>operationNotSaved'),
                Unidata.i18n.t('admin.job>confirmChangeOperation'),
                confirm,
                me,
                null,
                [],
                abort
            );
        } else {
            confirm();
        }

        return me;
    },

    isDirty: function () {

        var me = this,
            editor = me.editor,
            currentJob = editor.getJob();

        return currentJob && currentJob.phantom;

    },

    /**
     * Событие срабатывает при выборе элемента в списке
     *
     * @param {Unidata.model.job.Job} job
     */
    onSelectJob: function (job) {
        this.setJob(job);
    },

    onDeselectJob: function () {

    },

    onAddJobClick: function () {
        var job = Ext.create('Unidata.model.job.Job');

        this.setJob(job);
    },

    onNewJob: function (job) {
        var store = this.jobsStore;

        store.add(job);

        this.setJob(job, true);

        this.reloadStore();
    },

    onUpdateJob: function () {
        this.reloadStore();
    },

    onDeleteJob: function () {
        var store = this.jobsStore,
            page  = store.currentPage;

        this.setJob(null);

        if (!store.getCount() && page > 1) {
            page--;
        }

        store.load({
            params: {
                page: page
            }
        });
    },

    onFilterJob: function (status) {
        var store = this.jobsStore;

        store.getProxy().setUrlParam('status', status);
        store.loadPage(1);
    },

    onJobStatusChanged: function () {
        this.reloadStore();
    },

    reloadStore: function () {
        var store = this.jobsStore;

        store.load({
            params: {
                page: store.currentPage
            }
        });
    }
});
