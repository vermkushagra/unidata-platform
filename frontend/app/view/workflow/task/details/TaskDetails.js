/**
 * Панель отображающая информацию по задаче
 *
 * @author Ivan Marshalkin
 * @date 2018-05-26
 */

Ext.define('Unidata.view.workflow.task.details.TaskDetails', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.workflow.task.details.TaskDetailsController',
        'Unidata.view.workflow.task.details.TaskDetailsModel'
    ],

    alias: 'widget.workflow.task.taskdetails',

    viewModel: {
        type: 'taskdetails'
    },

    controller: 'taskdetails',

    referenceHolder: true,

    config: {
        task: null,
        taskHistory: null,
        taskId: null
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'loadTask'
        }
    ],

    viewModelAccessors: [
        'task',
        'taskHistory'
    ],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    ui: 'un-card',
    cls: 'un-workflow-task-mainPanel',

    title: Unidata.i18n.t('workflow>task'),

    bind: {
        title: '{task.processTypeName} ({task.taskId}) {recordStateText}'
    },

    scrollable: true,

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'container',
            defaults: {
                xtype: 'workflow.task.property'
            },
            flex: 1,
            cls: 'un-workflow-task-mainPanel-column',
            items: [
                {
                    title: Unidata.i18n.t('workflow>taskName'),
                    cls: 'un-workflow-task-property-taskTitle',
                    bind: {
                        value: '{task.taskTitle}'
                    }
                },
                {
                    title: Unidata.i18n.t('workflow>taskDescription'),
                    cls: 'un-workflow-task-property-taskDescription',
                    bind: {
                        value: '{task.taskDescription}'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            defaults: {
                xtype: 'workflow.task.property'
            },
            flex: 1,
            cls: 'un-workflow-task-mainPanel-column',
            items: [
                {
                    title: Unidata.i18n.t('workflow>taskAuthor'),
                    cls: 'un-workflow-task-property-originatorName',
                    bind: {
                        value: '{task.originatorName}'
                    }
                },
                {
                    title: Unidata.i18n.t('workflow>taskCreateDate'),
                    cls: 'un-workflow-task-property-createDate',
                    bind: {
                        value: '{task.createDate:date("d.m.Y H:i")}'
                    }
                },
                {
                    title: Unidata.i18n.t('workflow>taskStatus'),
                    cls: 'un-workflow-task-property-taskFinishedText',
                    bind: {
                        value: '{taskFinishedText}'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            defaults: {
                xtype: 'workflow.task.property'
            },
            flex: 1,
            cls: 'un-workflow-task-mainPanel-column',
            items: [
                {
                    title: Unidata.i18n.t('workflow>taskAssegnee'),
                    cls: 'un-workflow-task-property-taskAssigneeName',
                    value: '',
                    bind: {
                        value: '{task.taskAssigneeName}'
                    }
                }
            ]
        }
    ],

    listeners: {
        expand: 'onPanelExpand'
    }

});
