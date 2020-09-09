/**
 * Базовый класс poller'а - класс, занимающийся периодическим опросом бекенда
 * Запрашиваемая информация (в т.ч. API) определяются дочерним классом
 *
 * @author Sergey Shishigin
 * @date 2017-08-04
 */
Ext.define('Unidata.module.poller.Poller', {

    requires: [
        'Unidata.constant.Delay'
    ],

    intervalId: null,
    interval: 1 * Unidata.constant.Delay.MINUTE,
    requestActive: false,
    pollerEventBus: null,

    inheritableStatics: {
        instance: null,

        getInstance: function () {
            if (!this.instance) {
                this.instance = Ext.create(this.$className);
            }

            return this.instance;
        }
    },

    constructor: function () {
        // шаблон - синглтон
        if (this.self.instance) {
            return this.self.instance;
        }

        this.pollerEventBus = new Ext.util.Observable();

        this.callParent(arguments);

        this.self.instance = this;
    },

    start: function () {
        var me = this;

        me.stop();

        if (me.interval) {
            me.pollRequest();

            me.intervalId = setInterval(me.pollRequest.bind(this), me.interval);
        }
    },

    stop: function () {
        var intervalId = this.intervalId;

        if (intervalId) {
            clearInterval(intervalId);

            this.intervalId = null;
        }
    },

    isStarted: function () {
        return Boolean(this.intervalId);
    },

    pollRequest: function () {
        throw new Error('Method is not implemented');
    }
});
