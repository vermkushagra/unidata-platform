/**
 * Список операций
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-13
 */

Ext.define('Unidata.view.admin.job.part.JobList', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.job.part.JobListController',
        'Unidata.view.admin.job.part.JobListModel'
    ],

    alias: 'widget.admin.job.list',

    config: {
        title: Unidata.i18n.t('admin.job>operations')
    },

    controller: 'admin.job.list',

    viewModel: {
        type: 'admin.job.list'
    },

    referenceHolder: true,

    tools: [
        {
            type: 'plus',
            handler: 'onAddJobClick',
            tooltip: Unidata.i18n.t('admin.job>addNewOperation'),
            securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
            securedEvent: 'create'
        }
    ],

    ui: 'un-result',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'container',
            cls: 'un-query-pinned-sections',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [{
                xtype: 'combo',
                reference: 'filterCombo',
                store: [
                    ['all', Unidata.i18n.t('admin.job>all')],
                    ['active', Unidata.i18n.t('admin.job>actives')],
                    ['inactive', Unidata.i18n.t('admin.job>inactives')]
                ],
                value: 'all',
                region: 'south',
                hidden: false,
                editable: false,
                listeners: {
                    select: 'onActiveFilterSelect'
                },
                ui: 'un-field-default'
            }]
        },
        {
            overflowY: 'auto',
            xtype: 'grid',
            flex: 1,
            reference: 'jobsGrid',
            listeners: {
                select: 'onSelectJob',
                deselect: 'onDeselectJob'
            },
            bind: {
                store: '{jobsStore}'
            },
            hideHeaders: true,
            cls: 'un-result-grid un-job-list-grid',
            columns: [
                {
                    flex: 1,
                    hideable: false,
                    sortable: false,
                    text: Unidata.i18n.t('glossary:designation'),
                    dataIndex: 'name',
                    disableBindUpdate: true
                },
                {
                    text: Unidata.i18n.t('glossary:status'),
                    hideable: false,
                    sortable: false,
                    dataIndex: 'status',
                    renderer: function (value, columnMeta, record) {
                        return record.getStatusText();
                    }
                },
                {
                    xtype: 'booleancolumn',
                    text: Unidata.i18n.t('admin.job>active'),
                    hideable: false,
                    sortable: false,
                    dataIndex: 'enabled',
                    trueText: Unidata.i18n.t('common:yes'),
                    falseText: Unidata.i18n.t('common:no'),
                    disableBindUpdate: true
                }
            ],
            dockedItems: [{
                xtype: 'pagingtoolbar',
                reference: 'executionStepsPaging',
                bind: {
                    store: '{jobsStore}'
                },
                dock: 'bottom',
                displayInfo: false
            }]
        }
    ],

    selectJob: function (job) {
        this.getController().selectJob(job);
    }
});
