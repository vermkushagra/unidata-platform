/**
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.view.workflow.process.assignment.WorkflowProcessAssignment', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.workflow.process.assignment.WorkflowProcessAssignmentController',
        'Unidata.view.workflow.process.assignment.WorkflowProcessAssignmentModel'
    ],

    alias: 'widget.workflow.process.assignment',

    viewModel: {
        type: 'assignment'
    },

    controller: 'assignment',

    layout: 'fit',

    referenceHolder: true,

    cls: 'un-workflow-process-assignment',

    buttons: null, // контейнер с кнопками
    grid: null,    // грид с данными

    items: [],

    initItems: function () {
        var controller = this.getController(),
            processes = controller.getStore('processes'),
            triggerType = controller.getStore('triggerType');

        this.callParent(arguments);

        this.grid = Ext.create({
            xtype: 'grid',
            reference: 'grid',
            features: [
                {
                    ftype: 'grouping',
                    groupHeaderTpl: [
                        '{name:this.formatName}',
                        {
                            formatName: function (value) {
                                return Ext.String.htmlEncode(value);
                            }
                        }
                    ],
                    collapsible: false,
                    hideGroupedHeader: true,
                    enableGroupingMenu: false
                }
            ],
            rbar: [
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-floppy-disk',
                    reference: 'saveWorkflowButton',
                    securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                    securedEvent: 'update',
                    listeners: {
                        click: 'onSaveClick'
                    }
                }
            ],
            sortableColumns: false, // UN-4871 сортировка на экране бизнес процессов ломает экран. видимо связано с группировкой
            columns: {
                defaults: {
                    flex: 1
                },
                items: [
                    {
                        text: Unidata.i18n.t('glossary:entityOrLookupEntity'),
                        dataIndex: 'displayName',
                        renderer: function () {
                            return '';
                        },
                        flex: 2
                    },
                    {
                        text: Unidata.i18n.t('workflow>processType'),
                        dataIndex: 'processType',
                        renderer: 'processTypeRenderer',
                        flex: 2
                    },
                    {
                        text: Unidata.i18n.t('workflow>process'),
                        dataIndex: 'processDefinitionId',
                        xtype: 'widgetcolumn',
                        flex: 6,
                        widget: {
                            xtype: 'combobox',
                            editable: false,
                            displayField: 'name',
                            valueField: 'id',
                            queryMode: 'local',
                            store: processes,
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
                        onWidgetAttach: function (column, widget, record) {
                            var assignmentRecord = record;

                            widget.setStore(
                                Ext.create('Ext.data.ChainedStore', {
                                    source: widget.getStore(),
                                    filters: [
                                        {
                                            property: 'type',
                                            value: record.get('processType')
                                        }
                                    ]
                                })
                            );

                            widget.setValue(assignmentRecord.get('processDefinitionId'));

                            widget.on('change', function (view, value) {
                                if (view.selection) {
                                    if (view.selection.phantom) {
                                        assignmentRecord.set('processDefinitionId', '');
                                    } else {
                                        assignmentRecord.set('processDefinitionId', value);
                                    }
                                } else {
                                    assignmentRecord.set('processDefinitionId', '');
                                }
                            });
                        }
                    },
                    {
                        xtype: 'widgetcolumn',
                        text: Unidata.i18n.t('workflow>triggerCondition'),
                        dataIndex: 'triggerType',
                        flex: 2,
                        widget: {
                            xtype: 'combobox',
                            editable: false,
                            displayField: 'displayName',
                            valueField: 'name',
                            queryMode: 'local',
                            store: triggerType,
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
                        onWidgetAttach: function (column, widget, record) {
                            var widgetVisible = record.get('processType') === 'RECORD_EDIT',
                                assignmentRecord = record;

                            widget.setVisible(widgetVisible);

                            if (!widgetVisible) {
                                return;
                            }

                            widget.setValue(assignmentRecord.get('triggerType'));

                            widget.on('change', function (view, value) {
                                if (view.selection) {
                                    if (view.selection.phantom) {
                                        assignmentRecord.set('triggerType', '');
                                    } else {
                                        assignmentRecord.set('triggerType', value);
                                    }
                                } else {
                                    assignmentRecord.set('triggerType', '');
                                }
                            });
                        }
                    }
                ]
            },
            bind: {
                store: '{assignments}'
            }
        });

        this.add(this.grid);
        this.buttons = this.lookupReference('saveWorkflowButton');
    },

    onRender: function () {
        this.callParent(arguments);
        this.controller.showProgress(true);
    },

    onRemoved: function () {
        this.callParent(arguments);
        this.buttons.hide();
    },

    onDestroy: function () {
        this.buttons.destroy();

        this.callParent(arguments);

        delete this.grid;
        delete this.buttons;
    }

});
