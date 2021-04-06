/**
 * Компонент для просмотра запусков операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.part.JobExecutions', {

    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.admin.job.part.JobExecutionsController',
        'Unidata.view.admin.job.part.JobExecutionsModel',
        'Unidata.view.admin.job.part.JobExecutionErrorWindow',
        'Unidata.view.component.grid.column.FontAwesomeAction',
        'Ext.resizer.Splitter'
    ],

    alias: 'widget.admin.job.executions',

    viewModel: {
        type: 'admin.job.executions'
    },

    controller: 'admin.job.executions',

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initItems: function () {

        var dateFormat = Unidata.Config.getDateTimeFormat(),
            controller = this.getController(),
            getStore   = controller.getStore.bind(controller);

        this.callParent(arguments);

        this.add([
            {
                xtype: 'grid',
                flex: 1,
                overflowY: 'auto',
                cls: 'un-job-executions',
                title: Unidata.i18n.t('admin.job>startList'),
                reference: 'executions',
                listeners: {
                    select: 'onSelectExecution',
                    deselect: 'onDeselectExecution'
                },
                store: getStore('executions'),
                columns: [
                    {
                        xtype: 'datecolumn',
                        text: Unidata.i18n.t('admin.job>startTime'),
                        dataIndex: 'startTime',
                        format: dateFormat,
                        width: '30%',
                        hideable: false,
                        sortable: false
                    },
                    {
                        xtype: 'datecolumn',
                        text: Unidata.i18n.t('admin.job>endTime'),
                        dataIndex: 'endTime',
                        format: dateFormat,
                        width: '30%',
                        hideable: false,
                        sortable: false
                    },
                    {
                        text: Unidata.i18n.t('glossary:status'),
                        dataIndex: 'status',
                        width: '40%',
                        hideable: false,
                        sortable: false,
                        renderer: function (value, columnMeta, record) {
                            return record.getStatusText();
                        }
                    }
                ],
                tools: [
                    {
                        type: 'refresh',
                        tooltip: Unidata.i18n.t('common:refresh'),
                        handler: 'onRefreshClick',
                        bind: {
                            disabled: '{!refreshAllowed}'
                        }
                    },
                    {
                        type: 'play',
                        tooltip: Unidata.i18n.t('glossary:start'),
                        handler: 'onStartClick',
                        bind: {
                            disabled: '{!startAllowed}'
                        }
                    },
                    {
                        type: 'stop',
                        tooltip: Unidata.i18n.t('admin.job>stop'),
                        handler: 'onStopClick',
                        bind: {
                            disabled: '{!stopAllowed}'
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    reference: 'executionsPaging',
                    store: getStore('executions'),
                    dock: 'bottom',
                    displayInfo: true
                }]
            },
            {
                xtype: 'splitter'
            },
            {
                xtype: 'grid',
                flex: 1,
                overflowY: 'auto',
                cls: 'un-job-steps',
                title: Unidata.i18n.t('admin.job>operationSteps'),
                reference: 'executionSteps',
                store: getStore('executionSteps'),
                columns: [
                    {
                        text: Unidata.i18n.t('glossary:step'),
                        dataIndex: 'stepName',
                        width: '40%',
                        hideable: false,
                        sortable: false
                    },
                    {
                        xtype: 'datecolumn',
                        text: Unidata.i18n.t('admin.job>startTime'),
                        dataIndex: 'startTime',
                        format: dateFormat,
                        flex: 1,
                        hideable: false,
                        sortable: false
                    },
                    {
                        xtype: 'datecolumn',
                        text: Unidata.i18n.t('admin.job>endTime'),
                        dataIndex: 'endTime',
                        format: dateFormat,
                        flex: 1,
                        hideable: false,
                        sortable: false
                    },
                    {
                        text: Unidata.i18n.t('glossary:status'),
                        dataIndex: 'status',
                        flex: 1,
                        hideable: false,
                        sortable: false,
                        renderer: function (value, columnMeta, record) {
                            return record.getStatusText();
                        }
                    },
                    {
                        xtype: 'un.actioncolumn',
                        width: 30,
                        items: [{
                            faIcon: 'eye',
                            handler: 'onShowErrorMessageClick',
                            isDisabled: 'stepActionIsDisabled'
                        }]
                    }
                ],
                tools: [
                    {
                        type: 'play',
                        tooltip: Unidata.i18n.t('glossary:start'),
                        handler: 'onStartExecutionClick',
                        bind: {
                            disabled: '{!startExecutionAllowed}'
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    reference: 'executionStepsPaging',
                    store: getStore('executionSteps'),
                    dock: 'bottom',
                    displayInfo: true
                }]
            }
        ]);
    },

    setJob: function (job) {
        this.getController().setJob(job);
    }

});
