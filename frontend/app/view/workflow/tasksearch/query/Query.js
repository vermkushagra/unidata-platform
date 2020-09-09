/**
 * @author Aleksandr Bavin
 * @date 2016-06-29
 */
Ext.define('Unidata.view.workflow.tasksearch.query.Query', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.workflow.tasksearch.query.QueryController'
    ],

    alias: 'widget.workflow.tasksearch.query',

    controller: 'query',

    viewModel: {
        type: 'query'
    },

    referenceHolder: true,

    radioFieldContainer: null,
    taskForm: null,
    processForm: null,
    radioTypeSearchTask: null,
    radioTypeSearchProcess: null,

    taskCandidateUserField: null,
    taskAssignedUserField: null,

    ui: 'un-search',
    title: Unidata.i18n.t('workflow>tasksearch.title').toUpperCase(),
    collapsible: true,
    collapsed: true,
    collapseDirection: 'left',
    animCollapse: false,
    titleCollapse: true,
    width: 300,
    scrollable: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    // Список элементов формы, которые надо передать как variables
    variablesList: [
        'entityName'
    ],

    defaults: {
        labelAlign: 'top'
    },

    methodMapper: [
        {
            method: 'search'
        },
        {
            method: 'isTaskSearch'
        },
        {
            method: 'isProcessSearch'
        },
        {
            method: 'getSelectedSearchOperationType'
        }
    ],

    statics: {
        SEARCH_AVAILABLE: 0,
        SEARCH_MY: 1,
        SEARCH_COMPLEX: 2,
        SEARCH_HISTORICAL: 3,
        defaultSearchTypeNames: {
            0: Unidata.i18n.t('workflow>tasksearch.commonTasks'),
            1: Unidata.i18n.t('workflow>tasksearch.inProcess'),
            2: Unidata.i18n.t('workflow>tasksearch.title'),
            3: Unidata.i18n.t('workflow>tasksearch.history')
        },
        searchOperationTypes: {
            TASK: 'TASK',
            PROCESS: 'PROCESS'
        }
    },

    initComponent: function () {
        var userLogin = Unidata.Config.getUser().get('login');

        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();

        this.radioTypeSearchTask.setValue(true);
        this.taskCandidateUserField.setValue(userLogin);
        this.taskAssignedUserField.setValue(userLogin);
    },

    initComponentReference: function () {
        this.radioFieldContainer = this.lookupReference('radioFieldContainer');

        this.taskForm = this.lookupReference('taskForm');
        this.processForm = this.lookupReference('processForm');

        this.radioTypeSearchTask = this.lookupReference('radioTypeSearchTask');
        this.radioTypeSearchProcess = this.lookupReference('radioTypeSearchProcess');

        this.taskCandidateUserField = this.lookupReference('taskCandidateUserField');
        this.taskAssignedUserField = this.lookupReference('taskAssignedUserField');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.radioFieldContainer = null;

        this.taskForm = null;
        this.processForm = null;

        this.radioTypeSearchTask = null;
        this.radioTypeSearchProcess = null;

        this.taskCandidateUserField = null;
        this.taskAssignedUserField = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch',
                pack: 'center'
            },
            hidden: true,
            bind: {
                hidden: '{!searchComplex}'
            },
            items: [
                {
                    xtype: 'radiofield',
                    reference: 'radioTypeSearchTask',
                    name: 'searchOperationType',
                    inputValue: 'TASK',
                    boxLabel: Unidata.i18n.t('workflow>tasksearch.type.task'),
                    margin: '10 15 10 0',
                    listeners: {
                        change: 'onSearchOperationTypeChange'
                    }
                },
                {
                    xtype: 'radiofield',
                    reference: 'radioTypeSearchProcess',
                    name: 'searchOperationType',
                    inputValue: 'PROCESS',
                    boxLabel: Unidata.i18n.t('workflow>tasksearch.type.process'),
                    margin: '10 0 10 15',
                    listeners: {
                        change: 'onSearchOperationTypeChange'
                    }
                }
            ]
        },
        {
            xtype: 'form',
            reference: 'taskForm',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'textfield',
                    name: 'taskId',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('workflow>tasksearch.taskNumber'),
                    hidden: true,
                    disabled: true,
                    bind: {
                        hidden: '{!searchComplex}',
                        disabled: '{!searchComplex}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                            }
                        }
                    }
                },
                {
                    xtype: 'un.entitycombo',
                    name: 'entityName',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('glossary:entityOrLookupEntity'),
                    autoSelect: true,
                    forceSelection: true,
                    disabled: true,
                    hidden: true,
                    bind: {
                        disabled: '{!searchComplex}',
                        hidden: '{!searchComplex}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'initiator',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('workflow>tasksearch.initiator'),
                    displayField: 'fullName',
                    valueField: 'login',
                    queryMode: 'local',
                    queryDelay: 100,
                    disabled: true,
                    hidden: true,
                    bind: {
                        disabled: '{!searchComplex}',
                        hidden: '{!searchComplex}',
                        store: '{userStore}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                },
                {
                    xtype: 'label',
                    text: Unidata.i18n.t('workflow>tasksearch.approvePeriod'),
                    margin: '0 0 5 0',
                    hidden: true,
                    bind: {
                        hidden: '{!searchComplex}'
                    }
                },
                {
                    layout: 'hbox',
                    defaultType: 'datefield',
                    defaults: {
                        ui: 'un-field-default',
                        submitFormat: 'Y-m-d',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        disabled: true,
                        hidden: true,
                        bind: {
                            disabled: '{!searchComplex}',
                            hidden: '{!searchComplex}'
                        },
                        hideLabel: true,
                        width: '100%'
                    },
                    items: [
                        {
                            emptyText: Unidata.i18n.t('workflow>after'),
                            name: 'processStartAfter',
                            flex: 1
                        },
                        {
                            xtype: 'container',
                            width: 10
                        },
                        {
                            emptyText: Unidata.i18n.t('workflow>by'),
                            name: 'processStartBefore',
                            flex: 1
                        }
                    ]
                },
                {
                    xtype: 'label',
                    text: Unidata.i18n.t('workflow>tasksearch.assignmentPeriod'),
                    margin: '0 0 5 0',
                    hidden: true,
                    bind: {
                        hidden: '{!searchComplex}'
                    }
                },
                {
                    layout: 'hbox',
                    defaultType: 'datefield',
                    defaults: {
                        ui: 'un-field-default',
                        submitFormat: 'Y-m-d',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        disabled: true,
                        hidden: true,
                        bind: {
                            disabled: '{!searchComplex}',
                            hidden: '{!searchComplex}'
                        },
                        flex: 1,
                        hideLabel: true,
                        width: '100%'
                    },
                    items: [
                        {
                            emptyText: Unidata.i18n.t('workflow>after'),
                            name: 'taskStartAfter'
                        },
                        {
                            xtype: 'container',
                            flex: null,
                            width: 10
                        },
                        {
                            emptyText: Unidata.i18n.t('workflow>by'),
                            name: 'taskStartBefore'
                        }
                    ]
                },
                {
                    xtype: 'textfield',
                    reference: 'taskCandidateUserField',
                    name: 'candidateUser',
                    ui: 'un-field-default',
                    hidden: true,
                    disabled: true,
                    bind: {
                        disabled: '{!searchAvailable}'
                    }
                },
                {
                    xtype: 'textfield',
                    reference: 'taskAssignedUserField',
                    name: 'assignedUser',
                    ui: 'un-field-default',
                    hidden: true,
                    disabled: false,
                    bind: {
                        disabled: '{!searchMy}'
                    }
                },
                {
                    xtype: 'checkbox',
                    reference: 'historical',
                    name: 'historical',
                    ui: 'un-field-default',
                    margin: '0 0 5 0',
                    checked: false,
                    hidden: true,
                    bind: {
                        value: '{searchHistorical}'
                    },
                    inputValue: true,
                    fieldLabel: Unidata.i18n.t('workflow>tasksearch.searchHistory'),
                    labelWidth: 150,
                    labelAlign: 'left'
                },
                {
                    xtype: 'combobox',
                    name: 'taskCompletedBy',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('workflow>tasksearch.completedBy'),
                    displayField: 'fullName',
                    valueField: 'login',
                    queryMode: 'local',
                    disabled: true,
                    hidden: true,
                    bind: {
                        disabled: '{!historical.checked}',
                        hidden: '{!historical.checked}',
                        store: '{userStore}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                },
                {
                    xtype: 'label',
                    text: Unidata.i18n.t('workflow>tasksearch.taskCompletePeriod'),
                    margin: '0 0 5 0',
                    hidden: true,
                    bind: {
                        hidden: '{!historical.checked}'
                    }
                },
                {
                    layout: 'hbox',
                    defaultType: 'datefield',
                    defaults: {
                        ui: 'un-field-default',
                        submitFormat: 'Y-m-d',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        disabled: true,
                        hidden: true,
                        bind: {
                            disabled: '{!historical.checked}',
                            hidden: '{!historical.checked}'
                        },
                        flex: 1,
                        hideLabel: true,
                        width: '100%'
                    },
                    items: [
                        {
                            emptyText: Unidata.i18n.t('workflow>after'),
                            name: 'taskEndAfter'
                        },
                        {
                            xtype: 'container',
                            flex: null,
                            width: 10
                        },
                        {
                            emptyText: Unidata.i18n.t('workflow>by'),
                            name: 'taskEndBefore'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'form',
            reference: 'processForm',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'un.entitycombo',
                    name: 'entityName',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('glossary:entityOrLookupEntity'),
                    autoSelect: true,
                    forceSelection: true,
                    disabled: true,
                    hidden: true,
                    bind: {
                        disabled: '{!searchComplex}',
                        hidden: '{!searchComplex}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'initiator',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('workflow>tasksearch.initiator'),
                    displayField: 'fullName',
                    valueField: 'login',
                    queryMode: 'local',
                    queryDelay: 100,
                    disabled: true,
                    hidden: true,
                    bind: {
                        disabled: '{!searchComplex}',
                        hidden: '{!searchComplex}',
                        store: '{userStore}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    name: 'involved',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('workflow>tasksearch.involved'),
                    displayField: 'fullName',
                    valueField: 'login',
                    queryMode: 'local',
                    queryDelay: 100,
                    disabled: true,
                    hidden: true,
                    bind: {
                        disabled: '{!searchComplex}',
                        hidden: '{!searchComplex}',
                        store: '{userStore}'
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                },
                {
                    xtype: 'label',
                    text: Unidata.i18n.t('workflow>tasksearch.approvePeriod'),
                    margin: '0 0 5 0',
                    hidden: true,
                    bind: {
                        hidden: '{!searchComplex}'
                    }
                },
                {
                    layout: 'hbox',
                    defaultType: 'datefield',
                    defaults: {
                        ui: 'un-field-default',
                        submitFormat: 'Y-m-d',
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.reset();
                                }
                            }
                        },
                        disabled: true,
                        hidden: true,
                        bind: {
                            disabled: '{!searchComplex}',
                            hidden: '{!searchComplex}'
                        },
                        hideLabel: true,
                        width: '100%'
                    },
                    items: [
                        {
                            emptyText: Unidata.i18n.t('workflow>after'),
                            name: 'processStartAfter',
                            flex: 1
                        },
                        {
                            xtype: 'container',
                            width: 10
                        },
                        {
                            emptyText: Unidata.i18n.t('workflow>by'),
                            name: 'processStartBefore',
                            flex: 1
                        }
                    ]
                },
                {
                    xtype: 'combobox',
                    name: 'status',
                    ui: 'un-field-default',
                    labelAlign: 'top',
                    fieldLabel: Unidata.i18n.t('workflow>processsearch.process.status'),
                    displayField: 'displayName',
                    valueField: 'name',
                    queryMode: 'local',
                    queryDelay: 100,
                    editable: false,
                    store: {
                        fields: [
                            'name',
                            'value'
                        ],
                        data: [
                            {
                                displayName: Unidata.i18n.t('workflow>processsearch.process.statuses>completed'),
                                name: 'COMPLETED'
                            },
                            {
                                displayName: Unidata.i18n.t('workflow>processsearch.process.statuses>declined'),
                                name: 'DECLINED'
                            },
                            {
                                displayName: Unidata.i18n.t('workflow>processsearch.process.statuses>running'),
                                name: 'RUNNING'
                            }
                        ]
                    },
                    triggers: {
                        reset: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.reset();
                                this.collapse();
                            }
                        }
                    }
                }
            ]
        },
        {
            xtype: 'button',
            scale: 'large',
            iconCls: 'icon-magnifier',
            text: Unidata.i18n.t('common:search'),
            margin: '15 0 0 0',
            hidden: true,
            bind: {
                hidden: '{!searchComplex}'
            },
            listeners: {
                click: 'onSearchClick'
            }
        }
    ]
});
