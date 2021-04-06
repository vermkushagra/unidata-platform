/**
 * @author Aleksandr Bavin
 * @date 2016-06-29
 */
Ext.define('Unidata.view.workflow.tasksearch.query.Query', {
    extend: 'Ext.form.Panel',

    requires: [
        'Unidata.view.workflow.tasksearch.query.QueryController'
    ],

    alias: 'widget.workflow.tasksearch.query',

    controller: 'query',

    viewModel: {
        type: 'query'
    },

    ui: 'un-search',

    title: Unidata.i18n.t('workflow>tasksearch.title').toUpperCase(),
    collapsible: true,
    collapsed: true,
    collapseDirection: 'left',
    animCollapse: false,
    titleCollapse: true,
    width: 300,
    scrollable: true,

    referenceHolder: true,

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
        }
    ],

    items: [],

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
        }
    },

    initItems: function () {
        this.callParent(arguments);

        this.initTaskId();

        this.initEntityCombo();

        this.initInitiator();
        this.initProcessStart();
        this.initTaskStart();

        this.initCandidateOrAssignee();
        this.initHistorical();

        this.initSearchButton();
    },

    initTaskId: function () {
        this.add(
            {
                xtype: 'textfield',
                ui: 'un-field-default',
                fieldLabel: Unidata.i18n.t('workflow>tasksearch.taskNumber'),
                name: 'taskId',
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
            }
        );
    },

    /**
     * Реестр/Справочник
     */
    initEntityCombo: function () {
        this.add({
            xtype: 'un.entitycombo',
            ui: 'un-field-default',
            fieldLabel: Unidata.i18n.t('glossary:entityOrLookupEntity'),
            name: 'entityName',
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
        });
    },

    /**
     * Инициатор согласования
     */
    initInitiator: function () {
        this.add({
            xtype: 'combobox',
            ui: 'un-field-default',
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
            },
            fieldLabel: Unidata.i18n.t('workflow>tasksearch.initiator'),
            name: 'initiator'
        });
    },

    /**
     * Период создания процесса согласования
     */
    initProcessStart: function () {
        this.add([
            {
                xtype: 'label',
                hidden: true,
                bind: {
                    hidden: '{!searchComplex}'
                },
                text: Unidata.i18n.t('workflow>tasksearch.approvePeriod')
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
                        name: 'processStartAfter'
                    },
                    {
                        xtype: 'container',
                        flex: null,
                        width: 10
                    },
                    {
                        emptyText: Unidata.i18n.t('workflow>by'),
                        name: 'processStartBefore'
                    }
                ]
            }
        ]);
    },

    /**
     * Период создания задачи
     */
    initTaskStart: function () {
        this.add([
            {
                xtype: 'label',
                hidden: true,
                bind: {
                    hidden: '{!searchComplex}'
                },
                text: Unidata.i18n.t('workflow>tasksearch.assignmentPeriod')
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
            }
        ]);
    },

    initCandidateOrAssignee: function () {
        var userLogin = Unidata.Config.getUser().get('login');

        this.add(
            {
                xtype: 'textfield',
                ui: 'un-field-default',
                name: 'candidateUser',
                hidden: true,
                disabled: true,
                bind: {
                    disabled: '{!searchAvailable}'
                },
                value: userLogin
            },
            {
                xtype: 'textfield',
                ui: 'un-field-default',
                name: 'assignedUser',
                hidden: true,
                disabled: false,
                bind: {
                    disabled: '{!searchMy}'
                },
                value: userLogin
            }
        );
    },

    /**
     * Поиск по истории задач
     */
    initHistorical: function () {
        this.add({
            xtype: 'checkbox',
            ui: 'un-field-default',
            reference: 'historical',
            margin: '0 0 5 0',
            checked: false,
            hidden: true,
            bind: {
                value: '{searchHistorical}'
            },
            inputValue: true,
            fieldLabel: Unidata.i18n.t('workflow>tasksearch.searchHistory'),
            name: 'historical',
            labelWidth: 150,
            labelAlign: 'left'
        });

        this.initTaskCompletedBy();
        this.initTaskEnd();
    },

    /**
     * Выполнивший задачу
     */
    initTaskCompletedBy: function () {
        this.add({
            xtype: 'combobox',
            ui: 'un-field-default',
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
            },
            fieldLabel: Unidata.i18n.t('workflow>tasksearch.completedBy'),
            name: 'taskCompletedBy'
        });
    },

    /**
     * Период завершения задачи
     */
    initTaskEnd: function () {
        this.add([
            {
                xtype: 'label',
                hidden: true,
                bind: {
                    hidden: '{!historical.checked}'
                },
                text: Unidata.i18n.t('workflow>tasksearch.taskCompletePeriod')
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
        ]);
    },

    initSearchButton: function () {
        this.add({
            layout: 'hbox',
            margin: '15 0 0 0',
            items: [
                {
                    flex: 1
                },
                {
                    xtype: 'button',
                    scale: 'large',
                    text: Unidata.i18n.t('common:search'),
                    iconCls: 'icon-magnifier',
                    width: '100%',
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
    }
});
