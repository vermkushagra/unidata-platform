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

    items: [
        {
            xtype: 'workflow.tasksearch.query',
            reference: 'query'
        },
        {
            xtype: 'workflow.tasksearch.resultset',
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
        this.initReferences();
    },

    initReferences: function () {
        this.query = this.lookupReference('query');
    }

});
