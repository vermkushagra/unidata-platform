/**
 *
 * Верхняя панель экрана записи (шапка)
 *
 * @author Ivan Marshalkin
 * @date 2016-03-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.HeaderBar', {
    extend: 'Unidata.view.component.toolbar.Toolbar',

    requires: [
        'Unidata.view.steward.dataviewer.card.data.header.HeaderBarController',
        'Unidata.view.steward.dataviewer.card.data.header.HeaderBarModel',

        'Unidata.view.steward.dataviewer.card.data.header.notice.bar.DqBar',
        'Unidata.view.steward.dataviewer.card.data.header.notice.bar.ApproveBar',

        'Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel',
        'Unidata.view.steward.dataviewer.card.data.header.notice.panel.ApprovePanel',
        'Unidata.view.steward.dataviewer.card.data.header.notice.bar.StateBar',

        'Unidata.view.steward.dataviewer.card.data.EtalonInfoWindow',
        'Unidata.view.component.button.DottedMenuButton',
        'Unidata.view.component.menu.DottedMenu',

        'Unidata.view.component.form.field.ToggleCheckbox'
    ],

    alias: 'widget.steward.datacard.header',

    controller: 'steward.datacard.header',
    viewModel: {
        type: 'steward.datacard.header'
    },

    cls: 'x-docked-top',

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'showDqPanel'
        },
        {
            method: 'showApprovePanel'
        },
        {
            method: 'hideApprovePanel'
        },
        {
            method: 'showApproveBar'
        },
        {
            method: 'hideApproveBar'
        },
        {
            method: 'hideDqPanel'
        },
        {
            method: 'hideAllPanel'
        },
        {
            method: 'onDqBarClick'
        },
        {
            method: 'onApproveBarClick'
        },
        {
            method: 'updateApprovebarhidden'
        },
        {
            method: 'updateDqbarhidden'
        },
        {
            method: 'updateDottedmenubuttonhidden'
        },
        {
            method: 'onRefreshButtonClick'
        },
        {
            method: 'updateDqErrorCount'
        },
        {
            method: 'setApproveInfo'
        },
        {
            method: 'updateToggleHiddenButtonState'
        },
        {
            method: 'updateDataCardStateInfo'
        },
        {
            method: 'updateClusterCount'
        }
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    referenceHolder: true,

    config: {
        // approvebarhidden: false,
        dqbarhidden: false,
        dottedmenubuttonhidden: false,
        dqErrorCount: null,
        clusterCount: 0,
        dataRecord: null
    },

    dataCard: null,                         // ссылка на просмоторщик
    dqBar: null,                            // панелька с областью нотификаций правил качества данных
    dqPanel: null,                          // панель с ошибками правил качества
    // approveBar: null,                    // панелька с областью нотификаций согласования изменений
    approvePanel: null,                     // панелька с областью нотификаций согласования изменений
    refreshButton: null,                    // кнопка обновления
    toggleHiddenAttributeButton: null,      // кнопка отображения / скрытия скрытых атрибутов
    dataCardStatusContainer: null,          // контейнер отображающий статус дата карточки
    dottedMenuButton: null,                  // кнопка с выпадающим меню
    timeIntervalContainer: null,

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'top'
            },
            items: [
                // Поиск по карточке записи временно отключен
                //{
                //    xtype: 'textfield',
                //    reference: 'searchTextfield',
                //    emptyText: 'найти...',
                //    enableKeyEvents: true,
                //    hidden: true
                //},
                {
                    xtype: 'steward.datacard.header.statebar',
                    reference: 'dataCardStatusContainer',
                    margin: '0 10 0 0',
                    hidden: true,
                    html: ''
                },
                {
                    xtype: 'timeinterval',
                    reference: 'timeIntervalContainer',
                    referenceHolder: true,
                    bind: {
                        undoHidden: '{etalonPhantom}'
                    },
                    tools: [
                        {
                            type: 'plus',
                            cls: 'un-timeinterval-add',
                            tooltip: Unidata.i18n.t('dataviewer>makeTimeInterval'),
                            reference: 'addTimeIntervalButton'
                        }
                    ],
                    dataViewConfig: {
                        autoSelectTimeInterval: false
                    },
                    width: 295,
                    hidden: !Unidata.Config.getTimeintervalEnabled()
                },
                {
                    xtype: 'container',
                    flex: 1
                },
                {
                    xtype: 'steward.datacard.header.clusterbar',
                    margin: '0 15 0 0',
                    reference: 'clusterBar',
                    color: 'orange'
                },
                {
                    xtype: 'steward.datacard.header.dqbar',
                    margin: '0 15 0 0',
                    reference: 'dqBar'
                },
                {
                    xtype: 'togglecheckbox',
                    fieldLabel: Unidata.i18n.t('dataviewer>hiddenAttributes'),
                    reference: 'toggleHiddenAttributeButton',
                    labelWidth: 126,
                    listeners: {
                        change: 'onToggleCheckboxChange'
                    }
                },
                {
                    xtype: 'steward.dataviewer.card.dottedmenubtn',
                    reference: 'dottedMenuButton',
                    menuAlign: 'tr-bl?',
                    activeCardName: Unidata.view.steward.dataviewer.DataViewerConst.DATA_CARD
                }
            ]
        },
        {
            xtype: 'container',
            reference: 'operationIdPanel',
            hidden: true
        }
    ],

    initComponent: function () {
        var controller;

        this.callParent(arguments);

        this.initComponentReference();
        this.initChildComponentState();

        controller = this.getController();
        controller.initPanels();
    },

    /**
     * Обрабатываем уничтожение объекта
     */
    onDestroy: function () {
        var me = this,
            dqPanel = me.dqPanel,
            approvePanel = me.approvePanel;

        me.dataCard = null;

        me.dqBar                       = null;
        me.dqPanel                     = null;
        // me.approveBar                  = null;
        me.approvePanel                = null;
        me.refreshButton               = null;
        me.toggleHiddenAttributeButton = null;
        me.dataCardStatusContainer     = null;
        me.timeIntervalContainer = null;

        // эти панели рендерятся внутри компонента ручками поэтому их нужно дестроить руками и заранее
        dqPanel.destroy();
        approvePanel.destroy();

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.dqBar                       = me.lookupReference('dqBar');
        me.clusterBar                  = me.lookupReference('clusterBar');
        // me.approveBar                  = me.lookupReference('approveBar');
        me.refreshButton               = me.lookupReference('refreshButton');
        me.toggleHiddenAttributeButton = me.lookupReference('toggleHiddenAttributeButton');
        me.dataCardStatusContainer     = me.lookupReference('dataCardStatusContainer');
        me.dottedMenuButton            = me.lookupReference('dottedMenuButton');
        me.timeIntervalContainer = me.lookupReference('timeIntervalContainer');
    },

    initChildComponentState: function () {
    },

    getDottedMenuButton: function () {
        return this.dottedMenuButton;
    },

    updateDataRecord: function (dataRecord) {
        this.timeIntervalContainer.setDataRecord(dataRecord);
    }
});
