Ext.define('Unidata.view.steward.relation.contains.DqBar', {
    extend: 'Unidata.view.steward.dataviewer.card.data.header.notice.bar.DqBar',

    alias: 'widget.steward.relation.contains.dqbar',

    config: {
        dataRecord: null
    },

    /**
     * @returns {Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel}
     */
    getDqPanel: function () {
        if (this.dqPanel === undefined) {
            this.dqPanel = Ext.create('Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel', {
                showByComponent: this,
                floating: true,
                width: 300,
                hidden: true
            });

            this.relayEvents(this.dqPanel, ['changedq']);
        }

        return this.dqPanel;
    },

    updateDataRecord: function (dataRecord) {
        if (dataRecord) {
            this.updateDqErrorCount(dataRecord.dqErrors().getCount());
            this.getDqPanel().setDataRecord(dataRecord);
        }
    },

    onComponentClick: function () {
        this.getDqPanel().togglePanel();
        this.callParent(arguments);
    }

});
