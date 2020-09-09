/**
 * Компонент для управления операциями
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.Job', {
    extend: 'Ext.Container',

    viewModel: {
        type: 'admin.job'
    },

    controller: 'admin.job',

    requires: [
        'Unidata.model.job.Job',
        'Unidata.view.admin.job.JobController',
        'Unidata.view.admin.job.JobModel',
        'Unidata.view.admin.job.part.JobEditor',
        'Unidata.view.admin.job.part.JobExecutions',
        'Unidata.view.admin.job.part.JobList',
        'Ext.resizer.Splitter'
    ],

    alias: 'widget.admin.job',

    layout: {
        type: 'border',
        align: 'stretch'
    },

    referenceHolder: true,

    items: [
        // список операций (левая часть)
        {
            xtype: 'admin.job.list',
            width: 450,
            collapsible: true,
            split: true,
            region: 'west',
            reference: 'jobList',
            listeners: {
                addJobClick: 'onAddJobClick',
                selectJob: 'onSelectJob',
                deselectJob: 'onDeselectJob',
                filterJob:  'onFilterJob'
            }
        },
        // правая часть
        {
            xtype: 'container',
            flex: 5,
            region: 'center',
            hidden: true,
            overflowY: 'auto',
            reference: 'content',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                // редактор операции
                {
                    flex: 1,
                    reference: 'editor',
                    xtype: 'admin.job.editor',
                    scrollable: true,
                    listeners: {
                        newJob: 'onNewJob',
                        updateJob: 'onUpdateJob',
                        deleteJob: 'onDeleteJob'
                    }
                },
                {
                    xtype: 'splitter'
                },
                // список запусков операций
                {
                    flex: 1,
                    xtype: 'admin.job.executions',
                    reference: 'executions',
                    height: 200,
                    listeners: {
                        jobstatuschanged: 'onJobStatusChanged'
                    }
                }
            ]
        }
    ],

    isDirty: function () {
        return this.getController().isDirty();
    },

    onRender: function () {
        this.callParent(arguments);

        this.setLoading(this.getController().getLoading());
    }

});
