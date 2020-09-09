/**
 * Базовый класс оповещателя
 *
 * @author Ivan Marshalkin
 * @date 2017-10-04
 */

Ext.define('Unidata.module.notifier.Notifier', {
    inheritableStatics: {
        eventBus: null,

        subscribe: function (type, fn, scope) {
            var me = this.getMe(),
                eventName = me.getEventNameByType(type),
                subscriber;

            subscriber = me.eventBus.on(eventName, fn, scope);

            return subscriber;
        },

        unsubscribe: function (type, fn, scope) {
            var me = this.getMe(),
                eventName = me.getEventNameByType(type);

            me.eventBus.un(eventName, fn, scope);
        },

        /**
         * Уведомить о событии
         *
         * @param type - первый параметр тип уведомления
         * @param args2 ... argsn - параметры уведомления
         */
        notify: function () {
            var me = this.getMe(),
                args = Array.prototype.slice.call(arguments),
                eventName,
                type;

            type = args.shift();
            eventName = me.getEventNameByType(type);

            me.eventBus.fireEvent.apply(me.eventBus, Ext.Array.merge(eventName, args));
        },

        getEventNameByType: function () {
            throw 'method not implemented';
        },

        getMe: function () {
            return Unidata.module.notifier.Notifier;
        }
    }
});
