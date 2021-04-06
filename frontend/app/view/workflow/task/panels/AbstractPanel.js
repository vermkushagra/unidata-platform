/**
 * @author Aleksandr Bavin
 * @date 2016-08-17
 */
Ext.define('Unidata.view.workflow.task.panels.AbstractPanel', {
    extend: 'Unidata.view.component.panel.card.PanelCard',

    requires: [
        'Unidata.view.workflow.task.panels.AbstractPanelController',
        'Unidata.view.workflow.task.panels.AbstractPanelModel'
    ],

    alias: 'widget.workflow.task.panels.abstract',

    viewModel: {
        type: 'abstract'
    },

    controller: 'abstract',

    config: {
        processId: null
    },

    ui: 'un-card',

    title: '',

    bind: {
        title: '{title}'
    },

    tools: [
        {
            xtype: 'un.fontbutton.save',
            handler: 'onAddItemClick',
            scale: 'medium',
            iconCls: 'icon-plus-circle',
            hidden: true,
            bind: {
                hidden: '{savebuttonHidden}',
                tooltip: '{addItemText}'
            }
        }
    ],

    items: [],

    updateProcessId: function (processId) {
        if (!processId) {
            return;
        }

        this.setLoading(true);

        this.initPanelItems(processId);
    },

    /**
     * Загружаем стор с айтемами для панельки
     * @param {number} processId
     */
    initPanelItems: function (processId) {
        var viewModel = this.getViewModel(),
            itemsStore = this.getViewModel().get('itemsStore'),
            proxy = itemsStore.getProxy();

        this.initExtraParams(proxy, processId);

        itemsStore.reload({
            scope: this,
            callback: function (records) {
                var itemsCount = records ? records.length : 0;

                this.removeAll();

                if (itemsCount) {
                    Ext.Array.each(records, this.addPanelItem, this);
                }

                viewModel.set('itemsCount', itemsCount);
                this.setLoading(false);
            }
        });
    },

    initExtraParams: function (proxy, processId) {
        proxy.setExtraParam('processInstanceId', processId);
        proxy.setExtraParam('sortDateAsc', false);
    },

    /**
     * @param model
     */
    addPanelItem: function () {
    }

});
