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

    // переопределяе tools т.к. они есть в базовом классе, а здесь он не нужен
    tools: [],

    initExtraParams: function (proxy) {
        this.callParent(arguments);

        proxy.setExtraParam('itemType', ['WORKFLOW']);
    },

    /**
     * @param {Unidata.model.workflow.History} history
     */
    addPanelItem: function (history) {
        var dateFormat = Unidata.Config.getDateFormat(),
            titleDateFormat = dateFormat + ' H:i',
            title = Ext.Date.format(history.get('startTime'), titleDateFormat),
            value = history.get('description'),
            completedBy = history.get('completedBy'),
            tpl = new Ext.XTemplate('<span style="font-weight: bold;">{title}:</span> {username}'),
            html;

        html = tpl.apply({
            title: Unidata.i18n.t('workflow>completedByTitle'),
            username: Ext.String.htmlEncode(completedBy)
        });

        this.add({
            xtype: 'workflow.task.property',
            titleCls: 'un-workflow-task-property-underline',
            title: title,
            value: value,
            subItems: [
                {
                    xtype: 'component',
                    hidden: Ext.isEmpty(completedBy),
                    html: html
                }
            ]
        });
    }

});
