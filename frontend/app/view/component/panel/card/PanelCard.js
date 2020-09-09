/**
 * @author Aleksandr Bavin
 * @date 2016-08-11
 */
Ext.define('Unidata.view.component.panel.card.PanelCard', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.panel.card',

    ui: 'card',

    headerShadow: true,
    cardShadow: true,

    initComponent: function () {

        if (!this.cardShadow) {
            this.addCls('x-panel-card-disable-shadow');
        }

        if (!this.headerShadow) {
            this.addCls('x-panel-header-card-disable-shadow');
        }

        this.callParent(arguments);
    }

});
