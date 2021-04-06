/**
 * Абстрактный менеджер эвентов
 * @author Aleksandr Bavin
 * @date 10.06.2016
 */
Ext.define('Unidata.event.AbstractEventManager', {

    mixins: ['Ext.mixin.Observable'],

    /**
     * Список доступных эвентов
     * @property {String[]} events
     * @readonly
     */
    events: [],

    inheritableStatics: {
        relayEvents: function (origin, events) {
            var instance = this.create(),
                wrongEventNames = Ext.Array.difference(events, instance.events);

            if (wrongEventNames.length != 0) {
                Ext.Error.raise('You can not relay this events: ' + wrongEventNames.join(', '));
            }

            return instance.relayEvents.apply(instance, arguments);
        },

        addListener: function (ename /* private */) {
            var instance = this.create(),
                eventName,
                wrongEventNames = [];

            if (typeof ename !== 'string') {
                for (eventName in ename) {
                    if (!instance.$eventOptions[eventName]) {
                        if (instance.events.indexOf(eventName) === -1) {
                            wrongEventNames.push(eventName);
                        }
                    }
                }
            } else {
                if (instance.events.indexOf(ename) === -1) {
                    wrongEventNames.push(ename);
                }
            }

            if (wrongEventNames.length != 0) {
                Ext.Error.raise('You can not listen this events: ' + wrongEventNames.join(', '));
            }

            return instance.addListener.apply(instance, arguments);
        },

        on: function () {
            return this.addListener.apply(this, arguments);
        }
    },

    constructor: function () {
        if (this.self.instance === undefined) {
            this.self.addMembers('instance', true, true);
            this.self.instance = this;
            this.mixins.observable.constructor.call(this, arguments);
        }

        return this.self.instance;
    }

});
