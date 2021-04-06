/**
 * Панель отображающая уведомления пользователя
 *
 * @author Ivan Marshalkin
 * @date 2016-06-22
 */

Ext.define('Unidata.view.steward.notification.NotificationArea', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.notification.NotificationAreaController',
        'Unidata.view.steward.notification.NotificationAreaModel',
        'Unidata.module.poller.NotificationCountPoller'
    ],

    alias: 'widget.steward.notificationarea',

    viewModel: {
        type: 'steward.notificationarea'
    },
    controller: 'steward.notificationarea',

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-notificationarea',

    config: {
        preventClose: false // предотвращает закрытие NotificationArea
    },

    items: [
        {
            xtype: 'grid',
            reference: 'notificationGrid',
            ui: 'un-notification',
            hideHeaders: true,
            columns: [
                {
                    text: Unidata.i18n.t('glossary:notifications'),
                    flex: 1,
                    renderer: 'notificationRenderer',
                    menuDisabled: true,
                    sortable: false
                },
                {
                    xtype: 'un.actioncolumn',
                    width: 30,
                    items: [
                        {
                            faIcon: 'remove',
                            handler: 'onRemoveNotificationClick'
                        }
                    ]
                }
            ],
            listeners: {
                cellclick: 'onGridCellClick'
            },
            bind: {
                store: '{notifications}'
            },
            hidden: true,
            flex: 1
        },
        {
            xtype: 'container',
            reference: 'emptyNotificationContainer',
            cls: 'un-notificationclearlist',
            html: Unidata.i18n.t('application>noNotifications'),
            style: {
                color: 'white'
            },
            hidden: true,
            padding: 20,
            flex: 1
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: 0,
            hidden: true,
            bind: {
                hidden: '{!hasNotifications}'
            },
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            defaults: {
                margin: 10,
                buttonSize: 'medium'
            },
            items: [
                {
                    xtype: 'button',
                    scale: 'large',
                    color: 'transparent',
                    text: Unidata.i18n.t('common:clear'),
                    reference: 'deleteAllButton',
                    listeners: {
                        click: 'deleteAllNotificationsConfirm'
                    }
                }
            ]
        }
    ]

});
