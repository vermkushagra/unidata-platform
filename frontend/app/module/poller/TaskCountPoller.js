/**
 * Модуль опроса счетчика задач
 *
 * @author Sergey Shishigin
 * @date 2017-08-04
 */
Ext.define('Unidata.module.poller.TaskCountPoller', {
    extend: 'Unidata.module.poller.Poller',

    interval: Unidata.Config.getTasksPollInterval(),
    lastRequestTime: null,  // время выполнения последнего запроса (необходимо для получения числа новых задач с этого момента)
    availableTaskCount: null,
    myTaskCount: null,
    newCount: null,

    pollRequest: function () {
        var me = this,
            cfg,
            lastRequestTime = this.lastRequestTime;

        lastRequestTime = lastRequestTime || new Date();

        cfg = {
            fromDate: lastRequestTime
        };

        this.lastRequestTime = new Date();
        Unidata.util.api.Task.getTaskStat(cfg).then(function (values) {
            var oldValues,
                newCount;

            oldValues = {
                available_count: me.availableTaskCount,
                total_user_count: me.myTaskCount,
                new_count_from_date: me.newCount
            };

            me.availableTaskCount = values.available_count;
            me.myTaskCount = values.total_user_count;
            me.newCount = values.new_count_from_date;

            newCount = values.new_count_from_date;

            // отправляем событие только если счетчик изменился (временно disabled)
            //if (oldValues.available_count !== me.availableTaskCount) {
            me.updateAvailableCount(values, oldValues);
            //}

            // отправляем событие только если счетчик изменился (временно disabled)
            //if (oldValues.total_user_count !== this.myTaskCount) {
            me.updateMyCount(values, oldValues);
            //}

            if (newCount > 0) {
                me.updateNewCount(values, oldValues);
            }
        }, function () {
        }).done();
    },

    updateAvailableCount: function (values, oldValues) {
        this.updateCount(values, oldValues, 'available');
    },

    updateMyCount: function (values, oldValues) {
        this.updateCount(values, oldValues, 'my');
    },

    updateNewCount: function (values, oldValues) {
        this.updateCount(values, oldValues, 'new');
    },

    updateCount: function (values, oldValues, type) {
        var eventName;

        eventName = Ext.String.format('change{0}count', type);
        this.pollerEventBus.fireEvent(eventName, values, oldValues);
    }
});
