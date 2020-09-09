/**
 * Контроллер списка операций
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-13
 */

Ext.define('Unidata.view.admin.job.part.JobListController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.job.list',

    /**
     * Список со всеми операциями
     *
     * @type {Ext.grid.Panel}
     */
    jobsGrid: null,

    /**
     * Хранилище всех операций
     *
     * @type {Ext.data.Store}
     */
    jobsStore: null,

    /**
     * @type {Unidata.proxy.job.JobProxy}
     */
    jobsProxy: null,

    /**
     * Комбобокс для фильтрации
     *
     * @type {Ext.form.field.ComboBox}
     */
    filterCombo: null,

    lastSelectedJob: null,

    init: function () {
        var me = this;

        me.callParent(arguments);

        me.filterCombo = me.lookupReference('filterCombo');

        me.jobsGrid    = me.lookupReference('jobsGrid');
        me.jobsStore   = me.getViewModel().get('jobsStore');

        me.jobsProxy   = me.jobsStore.getProxy();

        me.jobsGrid.getView().getRowClass = this.getJobRowClass.bind(this);

        me.startAutoReloadState(true);
    },

    startAutoReloadState: function () {
        var me = this,
            view = this.getView(),
            viewEl;

        me.autoReloadStateTimeout = setTimeout(function () {
            viewEl = view.getEl();

            // не обновляем, пока скрыт
            if (viewEl && viewEl.isVisible(true)) {
                me.reloadState(me.startAutoReloadState.bind(me));
            } else {
                me.startAutoReloadState();
            }
        }, 5000);
    },

    destroy: function () {
        this.stopAutoReloadState();
        this.callParent(arguments);
    },

    reloadState: function (callBack) {
        var store = this.jobsStore,
            proxy = this.jobsProxy,
            lastSelectedJob = this.lastSelectedJob;

        proxy.headers = proxy.headers || {};

        proxy.headers['PROLONG_TTL'] = 'false'; // сервер не должен продлевать сессию

        callBack = callBack || Ext.emptyFn;

        proxy.read(
            proxy.createOperation('read', {
                callback: function (records, operation, success) {

                    var i,
                        record,
                        existsRecord;

                    if (success) {
                        for (i = 0; i < records.length; i++) {
                            record = records[i];

                            existsRecord = store.getById(record.id);

                            if (existsRecord) {
                                existsRecord.set('lastExecution', record.get('lastExecution'));
                                // проставляем lastExecution для открытой операции
                                if (lastSelectedJob &&
                                    lastSelectedJob.get('name') === record.get('name') &&
                                    lastSelectedJob !== existsRecord) {

                                    lastSelectedJob.set('lastExecution', record.get('lastExecution'));
                                }
                            }
                        }
                    }

                    if (success || operation.getError().status !== 403) {
                        callBack();
                    }

                }
            })
        );

        delete proxy.headers['PROLONG_TTL'];
    },

    stopAutoReloadState: function () {
        clearTimeout(this.autoReloadStateTimeout);
    },

    selectJob: function (job) {
        this.jobsGrid.setSelection(job);
    },

    onAddJobClick: function () {
        this.getView().fireEvent('addJobClick');
    },

    /**
     * Событие срабатывает при выборе элемента в списке
     *
     * @param {Ext.grid.Panel} grid
     * @param {Unidata.model.job.Job} job
     */
    onSelectJob: function (grid, job) {
        this.lastSelectedJob = job;
        this.getView().fireEvent('selectJob', job);
    },

    onDeselectJob: function () {
        this.lastSelectedJob = null;
        this.getView().fireEvent('deselectJob');
    },

    onActiveFilterSelect: function () {
        var me = this,
            filterCombo = me.filterCombo,
            filterType = filterCombo.getValue();

        this.getView().fireEvent('filterJob', filterType);
    },

    getJobRowClass: function (record) {
        var cls = '';

        if (record && record.get('error')) {
            cls = 'errorjob';
        }

        return cls;
    }

});
