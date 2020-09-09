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
            method: 'resetCachedData'
        },
        {
            method: 'isDataCached'
        }
    ],

    referenceHolder: true,

    headerBar: null,
    carouselPanel: null,
    carouselLegend: null,

    config: {
        readOnly: null,
        originTimeIntervalDate: null,
        timeIntervalStore: null,
        metaRecord: null,
        originsClassifierNodes: null,
        drafts: false,                        // параметр для получении записи - черновика
        operationId: null,
        allowDetachOriginOperation: true
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
                xtype: 'component',
                reference: 'carouselLegend',
                tpl: '{text}',
                margin: '16 0 0 45'
            },
            {
                xtype: 'container',
                layout: 'fit',
                cls: 'un-content-inner',
                flex: 1,
                items: [
                    {
                        xtype: 'carouselpanel',
                        reference: 'carouselPanel',
                        itemsDisplayed: Unidata.Config.getOriginCarouselDisplayCount(),
                        flex: 1
                    }
                ]
            }
        ]);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        var me = this;

        me.headerBar = me.lookupReference('headerBar');
        me.carouselPanel = me.lookupReference('carouselPanel');
        me.carouselLegend = me.lookupReference('carouselLegend');
    },

    initComponentEvent: function () {
        this.carouselPanel.on('rotate', this.onCarouselRotate, this);
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.headerBar = null;
        this.carouselPanel = null;
        this.carouselLegend = null;
    },

    setOriginRecords: function (originRecords) {
        var viewModel = this.getViewModel();

        viewModel.set('originRecords', originRecords);
        viewModel.notify();
    },

    getOriginRecords: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('originRecords');
    },

    setTimeIntervalDate: function (timeIntervalDate) {
        var viewModel = this.getViewModel();

        viewModel.set('timeIntervalDate', timeIntervalDate);
        viewModel.notify();
    },

    getTimeIntervalDate: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeIntervalDate');
    },

    setTimeIntervalStore: function (timeIntervalStore) {
        var viewModel = this.getViewModel();

        viewModel.set('timeIntervalStore', timeIntervalStore);
        viewModel.notify();
    },

    getTimeIntervalStore: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeIntervalStore');
    },

    setMetaRecord: function (metaRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('metaRecord', metaRecord);
        viewModel.notify();
    },

    getMetaRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('metaRecord');
    },

    setEtalonId: function (etalonId) {
        var viewModel = this.getViewModel();

        viewModel.set('etalonId', etalonId);
        viewModel.notify();
    },

    getEtalonId: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('etalonId');
    },

    getDottedMenuButton: function () {
        return this.headerBar.getDottedMenuButton();
    },

    onCarouselRotate: function () {
        var controller = this.getController();

        controller.updateCarouselLegend();
    }
});
