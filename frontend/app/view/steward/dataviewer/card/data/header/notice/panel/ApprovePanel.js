/**
 *
 *
 * Панель с уведомлениями для согласования изменений
 *
 * @author Ivan Marshalkin
 * @date 2016-03-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.panel.ApprovePanel', {
    extend: 'Ext.container.Container',

    requires: [],

    alias: 'widget.steward.datacard.header.approvepanel',

    referenceHolder: true,

    dataCard: null,        // ссылка на карточку с данными
    infoLabel: null,       // лейбл с информацией
    messageLabel: null,    // лейбл с текстом сообщения
    approveButton: null,   // ссылка на кнопку принятия
    declineButton: null,   // ссылка на кнопку отклонения

    config: {
        approvehidden: false,
        declinehidden: false
    },

    style: {
        'background-color': 'white',
        'border-top': 'solid 3px #FFC107'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'container',
                    reference: 'infoLabel',
                    html: '',
                    style: {
                        'color': '#0F5960',
                        'font-weight': 'bold',
                        'word-wrap': 'break-word'
                    }
                },
                {
                    xtype: 'container',
                    reference: 'messageLabel',
                    html: '',
                    style: {
                        'color': '#0F5960',
                        'word-wrap': 'break-word'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                pack: 'end'
            },
            items: [
                {
                    xtype: 'button',
                    reference: 'declineButton',
                    text: Unidata.i18n.t('dataviewer>decline'),
                    style: {
                        'background-color': '#D84315',
                        'border-color': '#D84315'
                    }
                },
                {
                    xtype: 'container',
                    flex: 1
                },
                {
                    xtype: 'button',
                    reference: 'approveButton',
                    text: Unidata.i18n.t('dataviewer>approve'),
                    style: {
                        'background-color': '#2D9434',
                        'border-color': '#2D9434'
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.iniComponentEvent();
    },

    destroy: function () {
        var me = this;

        me.dataCard      = null;
        me.approveButton = null;
        me.declineButton = null;
        me.infoLabel     = null;
        me.messageLabel  = null;

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.approveButton = me.lookupReference('approveButton');
        me.declineButton = me.lookupReference('declineButton');
        me.infoLabel     = me.lookupReference('infoLabel');
        me.messageLabel  = me.lookupReference('messageLabel');
    },

    iniComponentEvent: function () {
        var me       = this,
            dataCard = me.dataCard;

        me.on('show', me.onPanelShow, me);
        me.on('render', me.onComponentRender, me, {single: true});

        me.approveButton.on('click', me.onApproveButtonClick, me);
        me.declineButton.on('click', me.onDeclineButtonClick, me);

        dataCard.on('resize', me.onViewerResize, me);
    },

    onComponentRender: function () {
        this.updateVisibleButton();
    },

    onPanelShow: function () {
        this.updatePosition();
    },

    onViewerResize: function () {
        this.updatePosition();
    },

    updatePosition: function () {
        var panel    = this,
            dataCard = this.dataCard,
            x,
            y;

        x = dataCard.dataEntity.getWidth() - panel.getWidth();
        y = dataCard.headerBar.getHeight();

        if (panel.isVisible()) {
            panel.showAt(x, y);
        }
    },

    togglePanel: function () {
        var panel = this;

        if (panel.isVisible()) {
            panel.hide();
        } else {
            panel.show();
        }
    },

    updateApprovehidden: function (hidden) {
        var me = this;

        me.updateVisibleButton(me.approveButton, !hidden);
    },

    updateDeclinehidden: function (hidden) {
        var me = this;

        me.updateVisibleButton(me.declineButton, !hidden);
    },

    updateVisibleButton: function () {
        var me = this;

        me.setHiddenButton(me.approveButton, me.getApprovehidden());
        me.setHiddenButton(me.declineButton, me.getDeclinehidden());
    },

    setHiddenButton: function (button, hidden) {
        return this.setVisibleButton(button, !hidden);
    },

    setVisibleButton: function (button, visible) {
        var method = visible ? 'show' : 'hide';

        if (button) {
            button[method]();
        }
    },

    onApproveButtonClick: function () {
        this.fireEvent('approve');
    },

    onDeclineButtonClick: function () {
        this.fireEvent('decline');
    },

    /**
     *
     * @param info
     */
    setApproveInfo: function (info) {
        this.infoLabel.setHtml(info.date + ' ' + info.login);
        this.messageLabel.setHtml(info.message);
    }
});
