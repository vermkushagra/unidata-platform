Ext.define('Unidata.view.component.search.searchpanel.SearchPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.searchpanel',

    relayers: null,

    getSelectedSearchHits: function () {
        var view = this.getView(),
            resultsetPanel = view.resultsetPanel;

        return resultsetPanel.getSelectedSearchHits();
    },

    initRelayers: function () {
        var view = this.getView(),
            resultsetPanel = view.resultsetPanel;

        this.relayers = view.relayEvents(resultsetPanel, ['itemclick', 'selectionchange'], 'resultset');
    },

    onDestroy: function () {
        Ext.destroy(this.relayers);

        // здень не нужно вызывать this.callParent(arguments); т.к. его нет
    },

    updateToEntityDefaultDisplayAttributes: function (toEntityDefaultDisplayAttributes) {
        var view = this.getView(),
            queryPanel = view.queryPanel,
            resultsetPanel = view.resultsetPanel;

        if (!queryPanel) {
            return;
        }

        queryPanel.setToEntityDefaultDisplayAttributes(toEntityDefaultDisplayAttributes);
        resultsetPanel.setToEntityDefaultDisplayAttributes(toEntityDefaultDisplayAttributes);
    },

    updateSelectedEntityName: function (selectedEntityName) {
        var view = this.getView(),
            queryPanel = view.queryPanel;

        if (!queryPanel) {
            return;
        }

        queryPanel.setSelectedEntityName(selectedEntityName);
    },

    updateEntityReadOnly: function (entityReadOnly) {
        var view = this.getView(),
            queryPanel = view.queryPanel;

        if (!queryPanel) {
            return;
        }

        queryPanel.setEntityReadOnly(entityReadOnly);
    },

    onEntityChange: function (metaRecord) {
        var view                = this.getView(),
            resultsetPanel      = view.resultsetPanel;

        resultsetPanel.setMetaRecord(metaRecord);
    },

    onAllPeriodActualSearchChange: function (allPeriodSearch) {
        var view = this.getView();

        if (allPeriodSearch) {
            view.resultsetPanel.setEditMode(view.resultsetPanel.editModeType.DISABLED);
        } else {
            view.resultsetPanel.setEditMode(view.resultsetPanel.editModeType.NONE);
        }
    }
});
