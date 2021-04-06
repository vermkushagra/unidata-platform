/**
 *
 * Кнопка для отображения меню (символ "три точки, вертикально расположенные")
 *
 * @author Sergey Shishigin
 * @date 2017-04-14
 */
Ext.define('Unidata.view.steward.dataviewer.card.CardDottedMenuButton', {
    extend: 'Unidata.view.component.button.DottedMenuButton',

    alias: 'widget.steward.dataviewer.card.dottedmenubtn',

    config: {
        activeCardName: null,
        clusterCount: 0
    },

    mainActionsSeparator: null,
    cardSectionsSeparator: null,

    menu: {
        xtype: 'un.dottedmenu',
        referenceHolder: true,
        items: [
            {
                text: Unidata.i18n.t('common:refresh'),
                reference: 'refreshMenuItem'
            },
            {
                text: Unidata.i18n.t('dataviewer>jmsPublish'),
                reference: 'jmsPublishMenuItem'
            },
            {
                'text': Unidata.i18n.t('common:merge'),
                reference: 'mergeMenuItem'
            },
            {
                'text': Unidata.i18n.t('dataviewer>manualMerge'),
                reference: 'manualMergeMenuItem'
            },
            {
                xtype: 'menuseparator',
                reference: 'mainActionsSeparator'
            },
            {
                text: Unidata.i18n.t('glossary:referenceRecord'),
                reference: 'dataCardMenuItem'
            },
            {
                text: Unidata.i18n.t('glossary:recordHistory'),
                reference: 'historyCardMenuItem'
            },
            {
                text: Unidata.i18n.t('glossary:backRels'),
                reference: 'backrelationCardMenuItem'
            },
            {
                text: Unidata.i18n.t('dataviewer>originRecord'),
                reference: 'originCardMenuItem'
            },
            {
                xtype: 'menuseparator',
                reference: 'cardSectionsSeparator'
            },
            {
                text: Unidata.i18n.t('dataviewer>info'),
                reference: 'etalonInfoMenuItem'
            }
        ]
    },

    initComponent: function () {
        var menu;

        this.setMenuItems({
            // main actions
            merge: null,
            manualMerge: null,

            // card sections
            dataCard: null,
            historyCard: null,
            backrelationCard: null,
            originCard: null,

            // additional actions
            etalonInfo: null,
            refresh: null,
            jmsPublish: null
        });

        this.setMenuSeparators({
            mainActionsSeparator: null,
            cardSectionsSeparator: null
        });

        this.callParent(arguments);

        this.initReferences();
        this.initActiveCard();
    },

    initReferences: function () {
        var menuItems = this.getMenuItems(),
            menuSeparators = this.getMenuSeparators(),
            menu = this.getMenu(),
            postfix = this.MENU_ITEM_REF_POSTFIX;

        Ext.Object.each(menuItems, function (key) {
            menuItems[key] = menu.lookupReference(key + postfix);
        });

        Ext.Object.each(menuSeparators, function (key) {
            menuSeparators[key] = menu.lookupReference(key);
        });
    },

    initActiveCard: function () {
        var DottedMenu = Unidata.view.component.menu.DottedMenu,
            cardName   = this.getActiveCardName(),
            item       = this.getMenuItem(cardName + 'Card'),
            selectedCls = DottedMenu.MENU_ITEM_SELECTED_CLS;

        if (!item) {
            return;
        }

        item.addCls(selectedCls);
    },

    updateClusterCount: function (clusterCount) {
        var CardDottedMenuButton = Unidata.view.steward.dataviewer.card.CardDottedMenuButton,
            mergeMenuItem;

        mergeMenuItem = this.getMenuItem(CardDottedMenuButton.MENU_ITEM_MERGE);

        if (!mergeMenuItem) {
            return;
        }

        mergeMenuItem.setText(Unidata.i18n.t('dataviewer>mergeClusterCount', {count: clusterCount}));
    },

    statics: {
        MENU_ITEM_MERGE: 'merge',
        MENU_ITEM_MANUAL_MERGE: 'manualMerge',

        // card sections
        MENU_ITEM_DATA_CARD: 'dataCard',
        MENU_ITEM_HISTORY_CARD: 'historyCard',
        MENU_ITEM_BACKRELATION_CARD: 'backrelationCard',
        MENU_ITEM_ORIGIN_CARD: 'originCard',

        // additional actions
        MENU_ITEM_ETALON_INFO: 'etalonInfo',
        MENU_ITEM_REFRESH: 'refresh',
        MENU_ITEM_JMS_PUBLISH: 'jmsPublish',

        MENU_MERGE_ACTIONS_SEPARATOR: 'mainActionsSeparator',
        MENU_CARD_SECTIONS_SEPARATOR: 'cardSectionsSeparator'
    }
});
