/**
 * Для событий mousewheel, добавлена опция passive: true,
 * иначе, при постоянном скролле, браузеры ставят на паузу обработку скриптов.
 *
 * @link https://github.com/WICG/EventListenerOptions/blob/gh-pages/explainer.md#solution-the-passive-option
 *
 * @author Aleksandr Bavin
 * @date 2016-11-28
 */
Ext.define('Ext.overrides.event.publisher.Dom', {

    override: 'Ext.event.publisher.Dom',

    addDelegatedListener: function (eventName) {
        var passive = (eventName == 'mousewheel');

        this.delegatedListeners[eventName] = 1;
        this.target.addEventListener(
            eventName,
            this.onDelegatedEvent,
            {
                capture: !!this.captureEvents[eventName],
                passive: passive
            }
        );
    },

    addDirectListener: function (eventName, element, capture) {
        var passive = (eventName == 'mousewheel');

        element.dom.addEventListener(
            eventName,
            capture ? this.onDirectCaptureEvent : this.onDirectEvent,
            {
                capture: capture,
                passive: passive
            }
        );
    }

});
