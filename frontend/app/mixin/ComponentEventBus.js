/**
 * Миксин для компонентов, который реализует локальную шину событий
 * @author Aleksandr Bavin
 * @date 2016-08-02
 */
Ext.define('Unidata.mixin.ComponentEventBus', {

    extend: 'Ext.Mixin',

    mixinConfig: {
        before: {
            destroy: 'cleanupComponentEventBus'
        }
    },

    // если true, то компонент перехватывает идущие вверх события, и оповещает дочерние компоненты
    eventBusHolder: false,

    /**
     * массив эвентов, которые могут двигаться вверх по иерархии компонентов,
     * актуально только для eventBusHolder == true
     * @type {String[]}
     */
    bubbleBusEvents: [],

    privates: {
        /** @type {Ext.mixin.Observable} */
        componentEventObservable: null,

        cleanupComponentEventBus: function () {
            if (this.componentEventObservable) {
                this.componentEventObservable.destroy();
                delete this.componentEventObservable;
            }
        },

        getComponentEventObservable: function () {
            if (!this.componentEventObservable) {
                this.componentEventObservable = Ext.create('Ext.mixin.Observable');
            }

            return this.componentEventObservable;
        },

        /**
         * Проверка на существование обработчиков
         * @param eventName
         * @returns {boolean}
         */
        hasComponentListeners: function () {
            if (!this.componentEventObservable) {
                return false;
            }

            //TODO: проверка по названию эвента?

            return true;
        },

        /**
         * Рекурсивно пробегает по всем дочерним компонентам и оповещает обработчики
         * @param component
         * @param eventData
         * @param ignoreComponentId
         */
        notifyComponentListeners: function (component, eventData, ignoreComponentId) {
            var me = this,
                componentEventObservable;

            if (Ext.isFunction(component.hasComponentListeners)) {
                if (component.hasComponentListeners()) {
                    componentEventObservable = component.getComponentEventObservable();

                    componentEventObservable.fireEvent.apply(componentEventObservable, eventData);
                }
            }

            if (!component.items) {
                return;
            }

            component.items.each(function (item) {
                if (item.id != ignoreComponentId) {
                    me.notifyComponentListeners(item, eventData);
                }
            });
        },

        /**
         * Рекурсивное всплытие события
         * @param component
         * @param eventData
         */
        bubbleComponentEvent: function (component, eventData) {
            var eventBusComponent;

            // если разрешено, отправляем эвент выше
            if (component.bubbleBusEvents.indexOf(eventData[0]) !== -1) {
                eventBusComponent = this.searchEventBusComponent(component.getRefOwner());

                if (eventBusComponent) {
                    this.notifyComponentListeners(eventBusComponent, eventData, component.id);
                    this.bubbleComponentEvent(eventBusComponent, eventData);
                }
            }
        },

        /**
         * Рекурсивный поиск шины событий вверх по иерархии компонентов
         * @param {Ext.mixin.Inheritable|Unidata.mixin.ComponentEventBus} component - компонент, относительно которого искать шину событий
         * @returns {undefined|Ext.Component|Unidata.mixin.ComponentEventBus}
         * @private
         */
        searchEventBusComponent: function (component) {
            if (!component) {
                return undefined;
            }

            if (component.eventBusHolder) {
                return component;
            } else {
                return this.searchEventBusComponent(component.getRefOwner());
            }
        }
    },

    fireComponentEvent: function () {
        var eventBusComponent;

        if (this.eventBusHolder) {

            this.notifyComponentListeners(this, arguments);
            this.bubbleComponentEvent(this, arguments);

        } else {
            eventBusComponent = this.searchEventBusComponent(this);

            if (eventBusComponent) {
                eventBusComponent.fireComponentEvent.apply(eventBusComponent, arguments);
            }
        }
    },

    addComponentListener: function (eventName, handler, handlerScope) {
        var componentEventObservable = this.getComponentEventObservable();

        componentEventObservable.addListener(eventName, handler, handlerScope ? handlerScope : this);
    },

    removeComponentListener: function (eventName, handler, handlerScope) {
        var componentEventObservable = this.getComponentEventObservable();

        componentEventObservable.removeListener(eventName, handler, handlerScope ? handlerScope : this);
    }

});
