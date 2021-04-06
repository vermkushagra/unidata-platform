/**
 * @author Aleksandr Bavin
 * @date 2017-05-18
 */
Ext.define('Unidata.view.main.menu.elements.NotificationItem', {

    extend: 'Unidata.view.main.menu.MainMenuItem',

    alias: 'widget.un.list.item.mainmenu.item.notification',

    iconCls: 'un-icon-6',

    reference: 'notification',

    text: Unidata.i18n.t('glossary:notifications'),

    counterUi: 'notifications',

    initComponent: function () {
        var eventBus,
            poller;

        this.callParent(arguments);

        poller = Unidata.module.poller.NotificationCountPoller.getInstance();

        eventBus = poller.pollerEventBus;
        eventBus.on('changecount', this.onNotificationCountChange, this);
    },

    onClick: function () {
        var wnd;

        this.callParent(arguments);

        wnd = Ext.create('Unidata.view.steward.notification.NotificationWindow', {});

        wnd.show();
    },

    onNotificationCountChange: function (count) {
        this.setCounter(count);
    }

});
