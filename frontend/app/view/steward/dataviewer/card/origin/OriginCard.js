/**
 * Класс реализует представление карточки исходных записей для экрана записи
 *
 * @author Ivan Marshalkin
 * @date 2016-03-20
 */

Ext.define('Unidata.view.steward.dataviewer.card.origin.OriginCard', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.card.origin.OriginCardController',
        'Unidata.view.steward.dataviewer.card.origin.OriginCardModel',

        'Unidata.view.steward.dataviewer.card.origin.header.HeaderBar',
        'Unidata.view.steward.dataviewer.card.origin.OriginItem'
    ],

    alias: 'widget.steward.dataviewer.origincard',

    controller: 'steward.dataviewer.origincard',
    viewModel: {
        type: 'steward.dataviewer.origincard'
    },

    cls: 'un-dataviewer-card-origin',

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'loadAndDisplayOriginCard'
        },
        {
            method: 'displayOriginCard'
        },
        {
            method: 'updateTimeIntervalDate'
        },
        {
            method: 'resetCachedData'
        },
        {
            method: 'isDataCached'
        }
    ],

    referenceHolder: true,

    headerBar: null,
    originsContainer: null,
    pagingToolbar: null,

    config: {
        readOnly: null,
        originRecords: null,
        etalonId: null,
        timeIntervalDate: null,
        originTimeIntervalDate: null,
        timeIntervalStore: null,
        metaRecord: null,
        originsClassifierNodes: null,
        drafts: false,                        // параметр для получении записи - черновика
        operationId: null
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initItems: function () {
        var originCard = this;

        this.callParent(arguments);

        this.add([
            {
                xtype: 'steward.origincard.header',
                minHeight: 50,
                reference: 'headerBar',
                originCard: originCard
            },
            {
                xtype: 'pagingtoolbar',
                reference: 'pagingToolbar',
                displayInfo: true
            },
            {
                xtype: 'container',
                reference: 'originsContainer',
                layout: {
                    type: 'hbox',
                    align: 'begin'
                },
                cls: 'un-content-inner',
                flex: 1,
                scrollable: 'vertical',
                defaults: {
                    cls: 'un-ded-layout-column'
                },
                items: [
                ]
            }
        ]);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.headerBar = me.lookupReference('headerBar');
        me.pagingToolbar = me.lookupReference('pagingToolbar');
        me.originsContainer = me.lookupReference('originsContainer');
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.headerBar = null;
        this.pagingToolbar = null;
        this.originsContainer = null;
    },

    getDottedMenuButton: function () {
        return this.headerBar.getDottedMenuButton();
    }
});
