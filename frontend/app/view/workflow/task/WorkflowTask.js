/**
 * @author Aleksandr Bavin
 * @date 2016-08-09
 */
Ext.define('Unidata.view.workflow.task.WorkflowTask', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.workflow.task.WorkflowTaskController',
        'Unidata.view.workflow.task.WorkflowTaskModel'
    ],

    alias: 'widget.workflow.task.workflowtask',

    viewModel: {
        type: 'workflowtask'
    },

    controller: 'workflowtask',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    eventBusHolder: true,
    bubbleBusEvents: [
        'workflowtask_close',
        'workflowtask_complete',
        'workflowtask_reload'
    ],

    referenceHolder: true,

    cls: 'un-workflow-task',

    title: Unidata.i18n.t('workflow>task'),

    items: [
        {
            // Задача (основные данные)
            xtype: 'panel',
            ui: 'un-card',
            title: Unidata.i18n.t('workflow>task'),
            cls: 'un-workflow-task-mainPanel',
            bind: {
                title: '{task.processTitle} ({task.taskId}) {recordStateText}'
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
                    xtype: 'un.dottedmenubtn',
                    scale: 'small',
                    reference: 'taskActionsButton',
                    menu: {
                        xtype: 'un.dottedmenu',
                        plain: true,
                        reference: 'taskActionsMenu',
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
                    items: [
                        {
                            title: Unidata.i18n.t('workflow>taskAssegnee'),
                            cls: 'un-workflow-task-property-taskAssigneeName',
                            value: '',
                            bind: {
                                value: '{task.taskAssigneeName}'
                            }
                        },
                        {
                            title: Unidata.i18n.t('workflow>processCreateDate'),
                            cls: 'un-workflow-task-property-wfCreateDate',
                            bind: {
                                value: '{task.variables.wfCreateDate:date("d.m.Y H:i")}'
                            }
                        },
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
            // Комментарии, Вложения, История задачи
            xtype: 'container',
            reference: 'loadableItems',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            flex: 1,
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
        } else {
            taskActionsButton.setDisabled(true);
        }
    }

});
