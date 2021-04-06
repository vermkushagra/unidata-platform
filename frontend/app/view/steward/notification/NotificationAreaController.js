/**
 * @author Ivan Marshalkin
 * @date 2016-06-22
 */

Ext.define('Unidata.view.steward.notification.NotificationAreaController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.notificationarea',

    init: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('notifications');

        store.on('load', function (store, records) {
            var NotificationCountPoller = Unidata.module.poller.NotificationCountPoller,
                hasNotifications = false,
                notificationCountPoller;

            if (records && records.length != 0) {
                hasNotifications = true;
            }

            // просим обновить количество нотификаций
            notificationCountPoller = NotificationCountPoller.getInstance();
            notificationCountPoller.pollRequest();

            viewModel.set('hasNotifications', hasNotifications);
        });
    },

    notificationRenderer: function (value, metadata, record) {
        var tpl,
            str,
            date,
            content = record.get('content');

        date = Ext.Date.format(record.get('createDate'), Unidata.Config.getDateTimeFormat());

        tpl = '{0}<br>{1}';
        content = content.replace(/(\r\n|\n|\r)/g, '<br />');
        str = Ext.String.format(tpl, date, content);

        if (record.get('binaryDataId') || record.get('characterDataId')) {
            str += '<br><a href="#" style="color: white;">' + Unidata.i18n.t('other>downloadResult') + '</a>';
        }

        return str;
    },

    deleteAllNotificationsConfirm: function (button) {
        var view = this.getView(),
            title = Unidata.i18n.t('application>removeNotification'),
            msg = Unidata.i18n.t('application>confirmRemoveNotification');

        view.setPreventClose(true);

        Unidata.showPrompt(title, msg, this.deleteAllNotifications, this, button || undefined, [], this.disablePreventClose);
    },

    disablePreventClose: function () {
        var view = this.getView();

        setTimeout(function () {
            view.setPreventClose(false);
        }, 0);
    },

    deleteAllNotifications: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('notifications'),
            url = Unidata.Config.getMainUrl() + 'internal/user/notifications';

        this.disablePreventClose();

        Ext.Ajax.unidataRequest({
            method: 'DELETE',
            url: url,
            success: function (response) {
                var responseJson;

                if (response) {
                    responseJson = Ext.JSON.decode(response.responseText, true);
                }

                if (responseJson && responseJson.success) {
                    store.load();
                }
            },
            callback: function () {
            },
            scope: this
        });
    },

    onRemoveNotificationClick: function (cmp, a, b, eOpts, c, record) {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('notifications'),
            notificationGrid = this.lookupReference('notificationGrid'),
            notificationId = record.get('id'),
            url;

        if (!notificationId) {
            return;
        }

        url = Unidata.Config.getMainUrl() + 'internal/user/notifications/' + notificationId;

        notificationGrid.setDisabled(true);

        Ext.Ajax.request({
            url: url,
            method: 'DELETE',
            callback: function () {
                notificationGrid.setDisabled(false);

                store.load();
            }
        });
    },

    onGridCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e) {
        var url,
            fileId,
            type,
            downloadCfg;

        if (e.within(Ext.get(td).down('a'))) {
            fileId = record.get('binaryDataId');
            type   = 'blob';

            if (record.get('characterDataId')) {
                fileId = record.get('characterDataId');
                type   = 'clob';
            }

            url = Ext.String.format('{0}internal/data/entities/{1}/{2}', Unidata.Config.getMainUrl(), type, fileId);

            downloadCfg = {
                method: 'GET',
                url: url,
                params: {
                    token: Unidata.Config.getToken()
                }
            };

            Unidata.util.DownloadFile.downloadFile(downloadCfg);
        }
    },

    onNotificationStoreLoad: function () {
        this.updateVisiblePanel();
    },

    updateVisiblePanel: function () {
        var viewModel = this.getViewModel(),
            store = viewModel.getStore('notifications'),
            notificationGrid = this.lookupReference('notificationGrid'),
            emptyNotificationContainer = this.lookupReference('emptyNotificationContainer');

        notificationGrid.hide();
        emptyNotificationContainer.hide();

        if (store && store.getCount() > 0) {
            notificationGrid.show();
        } else {
            emptyNotificationContainer.show();
        }
    }
});
