/**
 * Запрашивает количество нотификаций текущего пользователя
 *
 * @author Ivan Marshalkin
 * @date 2016-06-25
 *
 * UPD: Перенес в текущий класс Sergey Shishigin
 */

Ext.define('Unidata.module.poller.NotificationCountPoller', {
    extend: 'Unidata.module.poller.Poller',
    requires: [
        'Unidata.constant.Delay'
    ],

    notificationCount: null,

    pollRequest: function () {
        // запрос производим только если нет активного запроса
        if (this.requestActive) {
            return;
        }

        this.requestActive = true;

        Ext.Ajax.unidataRequest({
            url: Unidata.Config.getMainUrl() + 'internal/user/notifications/count',
            method: 'GET',
            headers: {
                'PROLONG_TTL': 'false' // сервер не должен продлевать сессию
            },
            success: this.onSuccessPollRequest,
            callback: function () {
                this.requestActive = false;
            },
            scope: this
        });
    },

    onSuccessPollRequest: function (response) {
        var count,
            responseJson;

        if (response) {
            responseJson = Ext.JSON.decode(response.responseText, true);
        }

        if (responseJson && responseJson.success) {
            count = responseJson.content;

            this.updateNotificationCount(count);
        }
    },

    updateNotificationCount: function (count) {
        var eventBus,
            oldCount;

        oldCount = this.notificationCount;
        this.notificationCount = count;

        eventBus = this.pollerEventBus;
        eventBus.fireEvent('changecount', count, oldCount);
    }
});
