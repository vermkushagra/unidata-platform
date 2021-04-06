/**
 * Класс реализует представление карточки обратных ссылок для экрана записи
 *
 * @author Sergey Shihshigin
 * @date 2016-03-20
 */

Ext.define('Unidata.view.steward.dataviewer.card.backrel.BackRelCard', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.card.backrel.BackRelCardController',
        'Unidata.view.steward.dataviewer.card.backrel.BackRelCardModel'
    ],

    alias: 'widget.steward.dataviewer.backrelcard',

    controller: 'steward.dataviewer.backrelcard',
    viewModel: {
        type: 'steward.dataviewer.backrelcard'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'displayBackRels'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'loadAndDisplayBackRelsCard'
        }
    ],

    referenceHolder: true,

    config: {
        readOnly: null
    },

    contentContainer: null,

    items: [
        {
            xtype: 'un.toolbar',
            cls: 'x-docked-top',
            reference: 'headerBar',
            referenceHolder: true,
            layout: {
                type: 'hbox',
                align: 'top'
            },
            items: [
                {
                    xtype: 'container',
                    flex: 1
                },
                {
                    xtype: 'steward.dataviewer.card.dottedmenubtn',
                    reference: 'dottedMenuButton',
                    menuAlign: 'tr-bl?',
                    activeCardName: Unidata.view.steward.dataviewer.DataViewerConst.BACKREL_CARD
                }
            ]
        },
        {
            xtype: 'container',
            reference: 'contentContainer',
            cls: 'un-content-inner',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
    },

    initReferences: function () {
        this.contentContainer = this.lookupReference('contentContainer');
        this.headerBar = this.lookupReference('headerBar');
    },

    setRelationsDigest: function (relationsDigest) {
        var viewModel = this.getViewModel();

        viewModel.set('relationsDigest', relationsDigest);
        viewModel.notify();
    },

    getRelationsDigest: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('relationsDigest');
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
        return this.headerBar.lookupReference('dottedMenuButton');
    }
});
