/**
 * @author Ivan Marshalkin
 * @date 2016-06-22
 */

Ext.define('Unidata.view.steward.notification.NotificationAreaModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.notificationarea',

    data: {
        hasNotifications: false
    },

    stores: {
        notifications: {
            autoLoad: true,
            model: 'Unidata.model.notification.Notification',
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/user/notifications',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            },
            listeners: {
                load: 'onNotificationStoreLoad'
            }
        }
    }
});
