/**
 * @author Aleksandr Bavin
 * @date 2016-08-09
 */
Ext.define('Unidata.view.workflow.task.WorkflowTask', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.workflow.task.WorkflowTaskController',
        'Unidata.view.workflow.task.WorkflowTaskModel',

        'Unidata.view.workflow.task.details.TaskDetails'
    ],

    alias: 'widget.workflow.task.workflowtask',

    viewModel: {
        type: 'workflowtask'
    },

    controller: 'workflowtask',

    referenceHolder: true,

    processDetailPanel: null,
    tasksDetailPanel: null,

    config: {
        expandTask: false,
        expandProcess: false
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-workflow-task',

    scrollable: true,

    eventBusHolder: true,
    bubbleBusEvents: [
        'workflowtask_close',
        'workflowtask_complete',
        'workflowtask_reload'
    ],

    title: Unidata.i18n.t('workflow>task'),

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();

        if (this.getExpandTask()) {
            this.tasksDetailPanel.expand();
        }

        if (this.getExpandProcess()) {
            this.processDetailPanel.expand();
        }
    },

    initComponentReference: function () {
        this.tasksDetailPanel = this.lookupReference('tasksDetailPanel');
        this.processDetailPanel = this.lookupReference('processDetailPanel');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.tasksDetailPanel = null;
        this.processDetailPanel = null;

        this.callParent(arguments);
    },

    items: [
        {
            // Задача (основные данные)
            xtype: 'panel',
            reference: 'processDetailPanel',
            ui: 'un-card',
            title: Unidata.i18n.t('workflow>task'),
            cls: 'un-workflow-task-mainPanel',
            collapsible: true,
            animCollapse: false,
            titleCollapse: true,
            collapsed: true,
            bind: {
                title: '{task.processTitle}'
            },
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            defaults: {
                flex: 1,
                cls: 'un-workflow-task-mainPanel-column'
            },
            tools: [
                {
                    xtype: 'un.fontbutton.save',
                    handler: 'showProcessMap',
                    scale: 'medium',
                    iconCls: 'icon-group-work',
                    tooltip: Unidata.i18n.t('workflow>processState')
                },
                {
                    xtype: 'un.dottedmenubtn',
                    reference: 'taskActionsButton',
                    scale: 'small',
                    menu: {
                        xtype: 'un.dottedmenu',
                        reference: 'taskActionsMenu',
                        plain: true,
                        defaults: {
                            listeners: {
                                click: 'onTaskActionClick'
                            }
                        },
                        items: []
                    }
                }
            ],
            items: [
                {
                    xtype: 'container',
                    defaults: {
                        xtype: 'workflow.task.property'
                    },
                    items: [
                        {
                            title: Unidata.i18n.t('workflow>processName'),
                            cls: 'un-workflow-task-property-taskTitle',
                            bind: {
                                value: '{task.processTitle}'
                            }
                        },
                        {
                            title: Unidata.i18n.t('workflow>processTypeName'),
                            cls: 'un-workflow-task-property-taskDescription',
                            bind: {
                                value: '{task.processTypeName}'
                            }
                        }
                    ]
                },
                {
                    xtype: 'container',
                    defaults: {
                        xtype: 'workflow.task.property'
                    },
                    items: [
                        {
                            title: Unidata.i18n.t('workflow>processCreateDate'),
                            cls: 'un-workflow-task-property-wfCreateDate',
                            bind: {
                                value: '{task.variables.wfCreateDate:date("d.m.Y H:i")}'
                            }
                        },
                        {
                            title: Unidata.i18n.t('workflow>processAuthor'),
                            cls: 'un-workflow-task-property-originatorName',
                            bind: {
                                value: '{task.originatorName}'
                            }
                        }
                    ]
                },
                {
                    xtype: 'container',
                    defaults: {
                        xtype: 'workflow.task.property'
                    },
                    items: [
                        {
                            title: Unidata.i18n.t('workflow>processState'),
                            cls: 'un-workflow-task-property-processFinishedText',
                            bind: {
                                value: '{processFinishedText}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            reference: 'tasksDetailPanel',
            ui: 'un-card',
            collapsible: true,
            animCollapse: false,
            titleCollapse: true,
            collapsed: true,
            bind: {
                title: Unidata.i18n.t('workflow>activeTaskText') + ' ({activeTaskCount})'
            }
        },
        {
            // Комментарии, Вложения, История задачи
            xtype: 'container',
            reference: 'loadableItems',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            defaults: {
                scrollable: true,
                flex: 1
            },
            items: [
                {
                    // Комментарии
                    xtype: 'workflow.task.panels.comments',
                    scrollable: true,
                    bind: {
                        processId: '{task.processId}'
                    }
                },
                {
                    // Вложения
                    xtype: 'workflow.task.panels.attachments',
                    bind: {
                        processId: '{task.processId}'
                    }
                },
                {
                    // История задачи
                    xtype: 'workflow.task.panels.history',
                    bind: {
                        processId: '{task.processId}'
                    },
                    listeners: {
                        reload: 'onHistoryPanelReload'
                    }
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'un.toolbar',
            dock: 'bottom',
            autoHide: true,
            items: [
                {
                    xtype: 'button',
                    reference: 'unassignButton',
                    scale: 'large',
                    color: 'transparent',
                    text: Unidata.i18n.t('workflow>toCommonTasks'),
                    hidden: true,
                    bind: {
                        hidden: '{!task.unassignableByCurrentUser}'
                    },
                    listeners: {
                        click: 'onUnassignButtonClick'
                    }
                },
                {
                    xtype: 'container',
                    flex: 1
                },
                {
                    xtype: 'button',
                    reference: 'assignButton',
                    scale: 'large',
                    text: Unidata.i18n.t('workflow>takeTask'),
                    hidden: true,
                    bind: {
                        hidden: '{!task.assignableToCurrentUser}'
                    },
                    listeners: {
                        click: 'onAssignButtonClick'
                    }
                }
            ]
        }
    ],

    initItems: function () {
        this.callParent(arguments);

        this.getViewModel().bind('{task}', this.initTask, this);
    },

    /**
     * Инициализация задачи и всего, что с ней связано
     * @param {Unidata.model.workflow.Task} task
     */
    initTask: function (task) {
        if (task) {
            this.initTaskActions(task.actions());
        }
    },

    /**
     * Инициализация меню действий
     * @param {Unidata.model.workflow.TaskAction[]} actions
     */
    initTaskActions: function (actions) {
        var taskActionsButton = this.lookupReference('taskActionsButton'),
            taskActionsMenu = this.lookupReference('taskActionsMenu');

        taskActionsMenu.removeAll();

        actions.each(function (action) {
            taskActionsMenu.add({
                text: action.get('name'),
                cls: 'un-workflow-task-action-' + action.get('code'),
                /**
                 * используем при клике на элемент меню
                 * @see Unidata.view.workflow.task.WorkflowTaskController.onTaskActionClick
                 */
                action: action
            });
        });

        if (actions.getCount()) {
            taskActionsButton.show();
            taskActionsButton.setDisabled(false);
            taskActionsButton.setHidden(false);
        } else {
            taskActionsButton.setDisabled(true);
            taskActionsButton.setHidden(true);
        }
    }

});
