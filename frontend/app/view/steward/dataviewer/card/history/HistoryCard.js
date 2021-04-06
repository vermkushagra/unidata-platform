/**
 * Класс реализует представление карточки истории записи для экрана записи
 *
 * @author Ivan Marshalkin
 * @date 2016-03-20
 */

Ext.define('Unidata.view.steward.dataviewer.card.history.HistoryCard', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.card.history.HistoryCardController',
        'Unidata.view.steward.dataviewer.card.history.HistoryCardModel',

        'Unidata.view.steward.dataentity.DataEntity'
    ],

    alias: 'widget.steward.dataviewer.historycard',

    controller: 'steward.dataviewer.historycard',
    viewModel: {
        type: 'steward.dataviewer.historycard'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'displayHistoryCard'
        },
        {
            method: 'clearHistoryCard'
        }
    ],

    referenceHolder: true,

    cls: 'un-dataviewer-card-history',

    config: {
        etalonId: null,
        metaRecord: null,
        dataRecord: null,
        classifierNodes: null
    },

    dateField: null,            // дата на которую смотрим запись
    lastUpdateDateField: null,  // дата последнего обновления записи
    dataEntity: null,           // экран записи
    headerBar: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'un.toolbar',
            cls: 'x-docked-top',
            height: 50,
            reference: 'headerBar',
            referenceHolder: true,
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            items: [
                {
                    xtype: 'datefield',
                    ui: 'un-field-default',
                    reference: 'dateField',
                    fieldLabel: Unidata.i18n.t('common:onDate'),
                    labelWidth: 70,
                    width: 190,
                    margin: '0 10 0 0',
                    format: Unidata.Config.getDateFormat(),
                    editable: false,
                    value: new Date(),
                    listeners: {
                        change: 'onChangeDateRange'
                    }
                },
                {
                    xtype: 'datefield',
                    ui: 'un-field-default',
                    reference: 'lastUpdateDateField',
                    fieldLabel: Unidata.i18n.t('dataviewer>loadRecordFromSource'),
                    labelWidth: 210,
                    width: 330,
                    margin: '0 10 0 0',
                    format: Unidata.Config.getDateFormat(),
                    editable: false,
                    value: new Date(),
                    listeners: {
                        change: 'onChangeDateRange'
                    }
                },
                {
                    xtype: 'button',
                    text: Unidata.i18n.t('dataviewer>view'),
                    handler: 'onLoadHistoryRecordClick',
                    scale: 'small'
                },
                {
                    xtype: 'container',
                    flex: 1,
                    height: 35
                },
                {
                    xtype: 'steward.dataviewer.card.dottedmenubtn',
                    reference: 'dottedMenuButton',
                    menuAlign: 'tr-bl?',
                    activeCardName: Unidata.view.steward.dataviewer.DataViewerConst.HISTORY_CARD
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            cls: 'un-content-inner',
            flex: 1,
            scrollable: 'vertical',
            items: [
                {
                    xtype: 'dataentity',
                    reference: 'dataEntity',
                    useCarousel: true,
                    useAttributeGroup: true,
                    hiddenAttribute: true,
                    readOnly: true      // запись на данном экране всегда в режиме "только для чтения"
                },
                {
                    // footerBar не должен перекрывать последние поля в записи
                    // эмуляция падинга в ff есть баг что нижний паддинг может пропадать :)
                    // смотри UN-2905
                    xtype: 'container',
                    height: 90
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    destroy: function () {
        var me = this;

        me.dateField           = null;
        me.lastUpdateDateField = null;
        me.dataEntity          = null;

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.headerBar           = me.lookupReference('headerBar');
        me.dateField           = me.headerBar.lookupReference('dateField');
        me.lastUpdateDateField = me.headerBar.lookupReference('lastUpdateDateField');
        me.dataEntity          = me.lookupReference('dataEntity');
    },

    getDottedMenuButton: function () {
        return this.headerBar.lookupReference('dottedMenuButton');
    }
});
