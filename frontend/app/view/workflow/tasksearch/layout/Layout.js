/**
 * @author Aleksandr Bavin
 * @date 06.06.2016
 */
Ext.define('Unidata.view.workflow.tasksearch.layout.Layout', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.workflow.tasksearch.resultview.ResultView',
        'Unidata.view.workflow.tasksearch.layout.LayoutController',
        'Unidata.view.workflow.tasksearch.layout.LayoutModel',
        'Unidata.view.workflow.tasksearch.resultset.Resultset',
        'Unidata.view.workflow.tasksearch.query.Query'
    ],

    alias: 'widget.workflow.tasksearch.layout',

    cls: 'un-section-task',

    viewModel: {
        type: 'layout'
    },

    controller: 'layout',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    eventBusHolder: true,
    bubbleBusEvents: [],
    referenceHolder: true,

    query: null,
    tasks: null,
    resultset: null,

    items: [
        {
            xtype: 'workflow.tasksearch.query',
            reference: 'query',
            listeners: {
                operationtypechange: 'onOperationTypeChange'
            }
        },
        {
            xtype: 'workflow.tasksearch.resultset',
            reference: 'resultset',
            listeners: {
                opentask: 'onOpenTask'
            }
        },
        {
            xtype: 'tabpanel',
            reference: 'tasks',
            ui: 'un-content',
            listeners: {
                tabchange: 'onTabchange',
                remove: 'onTabremove'
            },
            flex: 1
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.query = this.lookupReference('query');
        this.tasks = this.lookupReference('tasks');
        this.resultset = this.lookupReference('resultset');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.query = null;
        this.tasks = null;
        this.resultset = null;

        this.callParent(arguments);
    }
});
