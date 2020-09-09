/**
 *
 * Верхняя панель экрана origin view (шапка)
 *
 * @author Sergey Shishigin
 * @date 2016-03-28
 */

Ext.define('Unidata.view.steward.dataviewer.card.origin.header.HeaderBar', {
    extend: 'Unidata.view.component.toolbar.Toolbar',

    requires: [
        'Unidata.view.steward.dataviewer.card.origin.header.HeaderBarController',
        'Unidata.view.steward.dataviewer.card.origin.header.HeaderBarModel'
    ],

    alias: 'widget.steward.origincard.header',

    controller: 'steward.origincard.header',
    viewModel: {
        type: 'steward.origincard.header'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */

    layout: {
        type: 'hbox',
        align: 'top'
    },

    referenceHolder: true,

    originCard: null,             // ссылка на просмоторщик

    cls: 'x-docked-top',

    items: [
        {
            xtype: 'timeinterval',
            reference: 'timeIntervalContainer',
            readOnly: true,
            dataViewConfig: {
                autoSelectTimeInterval: false
            },
            hidden: !Unidata.Config.getTimeintervalEnabled(),
            width: 295
        },
        {
            xtype: 'container',
            flex: 1
        },
        {
            xtype: 'steward.dataviewer.card.dottedmenubtn',
            reference: 'dottedMenuButton',
            menuAlign: 'tr-bl?',
            activeCardName: Unidata.view.steward.dataviewer.DataViewerConst.ORIGIN_CARD
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
    },

    destroy: function () {
        var me = this;

        me.originCard = null;

        me.callParent(arguments);
    },

    initReferences: function () {
        this.dottedMenuButton = this.lookupReference('dottedMenuButton');
    },

    getDottedMenuButton: function () {
        return this.dottedMenuButton;
    }
});
