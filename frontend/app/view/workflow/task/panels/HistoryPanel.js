/**
 * @author Aleksandr Bavin
 * @date 2016-08-25
 */
Ext.define('Unidata.view.workflow.task.panels.HistoryPanel', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanel',

    requires: [
        'Unidata.view.workflow.task.panels.HistoryPanelController',
        'Unidata.view.workflow.task.panels.HistoryPanelModel'
    ],

    alias: 'widget.workflow.task.panels.history',

    viewModel: {
        type: 'history'
    },

    controller: 'history',

    tools: [
        {
            xtype: 'un.fontbutton.save',
            handler: 'showProcessMap',
            scale: 'medium',
            iconCls: 'icon-group-work',
            bind: {
                tooltip: '{addItemText}'
            }
        }
    ],

    initExtraParams: function (proxy) {
        this.callParent(arguments);
        proxy.setExtraParam('itemType', ['WORKFLOW']);
    },

    /**
     * @param {Unidata.model.workflow.History} history
     */
    addPanelItem: function (history) {
        var title = Ext.Date.format(history.get('startTime'), 'd.m.Y H:i'),
            value = history.get('description');

        this.add({
            xtype: 'workflow.task.property',
            title: title,
            value: value
        });
    }

});
