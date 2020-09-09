/**
 * Компонент для редактирования job triggers
 *
 * @author Sergey Shishigin
 * @date 2017-12-15
 */

Ext.define('Unidata.view.admin.job.part.JobTrigger', {
    extend: 'Ext.panel.Panel',

    xtype: 'admin.job.editor.trigger',

    cls: 'un-job-trigger',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {
        jobs: null,
        editable: true,
        jobId: null,
        lastSuccessJobTrigger: null,
        lastFailureJobTrigger: null,
        store: null
    },

    referenceHolder: true,

    successJobCombo: null,
    failureJobCombo: null,

    initItems: function () {
        var items,
            editable;

        this.callParent(arguments);
        this.store = this.createStore();
        editable = this.getEditable();
        items = [
            this.buildJobCombo({
                fieldLabel: Unidata.i18n.t('admin.job>successTrigger'),
                reference: 'successJobCombo',
                editable: editable,
                margin: '0 10 0 0'
            }),
            this.buildJobCombo({
                fieldLabel: Unidata.i18n.t('admin.job>failureTrigger'),
                reference: 'failureJobCombo',
                editable: editable
            })
        ];

        this.add(items);
        this.initReferences();
    },

    initReferences: function () {
        this.successJobCombo = this.lookupReference('successJobCombo');
        this.failureJobCombo = this.lookupReference('failureJobCombo');
    },

    buildJobCombo: function (customCfg) {
        var cfg,
            cmp,
            store = this.getStore();

        customCfg = customCfg || {};

        cfg = {
            xtype: 'combo',
            store: store,
            displayField: 'name',
            valueField: 'id',
            labelAlign: 'top',
            labelSeparator: '',
            emptyText: Unidata.i18n.t('admin.job>nextOperation'),
            width: 450,
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.clearValue();
                    }
                }
            }
        };

        Ext.apply(cfg, customCfg);

        cmp = Ext.create(cfg);

        return cmp;
    },

    createStore: function () {
        var me = this,
            store;

        store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            model: 'Unidata.model.job.Job',
            filters: [
                function (job) {
                    return job.get('id') !== me.getJobId();
                }
            ],
            sorters: [
                {
                    property: 'name',
                    direction: 'ASC'
                }
            ],
            proxy: {
                type: 'un.jobs',
                limitParam: '',
                startParam: '',
                pageParam: '',
                urlParams: {
                    status: 'all'
                }
            }
        });

        return store;
    },

    updateEditable: function (editable) {
        if (this.successJobCombo) {
            this.successJobCombo.setReadOnly(!editable);
        }

        if (this.failureJobCombo) {
            this.failureJobCombo.setReadOnly(!editable);
        }
    },

    updateLastSuccessJobTrigger: function (jobTrigger) {
        var startJobId;

        startJobId = jobTrigger ? jobTrigger.get('startJobId') : null;

        this.successJobCombo.setValue(startJobId);
    },

    updateLastFailureJobTrigger: function (jobTrigger) {
        var startJobId;

        startJobId = jobTrigger ? jobTrigger.get('startJobId') : null;

        this.failureJobCombo.setValue(startJobId);
    },

    resetValues: function () {
        this.successJobCombo.clearValue();
        this.failureJobCombo.clearValue();
    },

    /**
     * @return {Ext.Promise}
     */
    persistJobTriggers: function () {
        var successJobCombo = this.successJobCombo,
            failureJobCombo = this.failureJobCombo,
            successJob = successJobCombo.getSelection(),
            failureJob = failureJobCombo.getSelection(),
            successJobTrigger,
            failureJobTrigger,
            promiseOne,
            promiseTwo;

        successJobTrigger = this.buildJobTrigger(successJob, this.getLastSuccessJobTrigger(), true);
        failureJobTrigger = this.buildJobTrigger(failureJob, this.getLastFailureJobTrigger(), false);

        promiseOne = this.persistJobTrigger(successJobTrigger, this.getLastSuccessJobTrigger());
        promiseTwo = this.persistJobTrigger(failureJobTrigger, this.getLastFailureJobTrigger());

        return Ext.Deferred.all([promiseOne, promiseTwo]);
    },

    buildJobTrigger: function (job, lastJobTrigger, success) {
        var jobId = this.getJobId(),
            jobTrigger,
            jobTriggerId,
            name;

        if (!job) {
            return null;
        }

        jobTriggerId = lastJobTrigger ? lastJobTrigger.get('id') : null;
        success = Ext.isBoolean(success) ? success : true;
        name = this.buildJobTriggerName(jobId, success);

        jobTrigger = Ext.create('Unidata.model.job.JobTrigger', {
            id: jobTriggerId,
            startJobId: job.get('id'),
            successRule: success,
            name: name
        });

        return jobTrigger;
    },

    buildJobTriggerName: function (jobId, success) {
        var name;

        name = jobId + '_' + (success ? 'success' : 'failure');

        return name;
    },

    /**
     * @return {Promise}
     */
    persistJobTrigger: function (jobTrigger, lastJobTrigger) {
        var jobId = this.getJobId(),
            persistCfg,
            promise = null;

        persistCfg = {
            jobId: jobId
        };

        if (lastJobTrigger) {
            if (jobTrigger) {
                promise = Unidata.util.api.JobTrigger.saveJobTrigger(Ext.apply(persistCfg, {jobTrigger: jobTrigger}));
            } else {
                promise = Unidata.util.api.JobTrigger.deleteJobTrigger(Ext.apply(persistCfg, {jobTrigger: lastJobTrigger}));
            }
        } else {
            if (jobTrigger) {
                promise = Unidata.util.api.JobTrigger.saveJobTrigger(Ext.apply(persistCfg, {jobTrigger: jobTrigger}));
            }
        }

        return promise;
    },

    statics: {
        findJobTrigger: function (jobTriggers, success) {
            var found;

            success = Ext.isBoolean(success) ? success : true;

            found = Ext.Array.findBy(jobTriggers, function (jobTrigger) {
                return jobTrigger.get('successRule') === success;
            });

            return found;
        }
    }
});
