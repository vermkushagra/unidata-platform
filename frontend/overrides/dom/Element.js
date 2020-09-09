/**
 * @author Sevastyanov Cyrill
 * @date 2016-02-24
 */
Ext.define('Ext.overrides.dom.Element', {
    override: 'Ext.dom.Element',

    /**
     * В ExtJS 5 эта функция кривая, вообще никогда не выполняется.
     * Исправленный вариант
     *
     * @param text
     */
    updateText: function (text) {

        var me = this,
            dom = me.dom,
            textNode;

        if (dom) {

            textNode = dom.firstChild;

            if (!textNode || (textNode.nodeType !== 3 || textNode.nextSibling)) {
                textNode = document.createTextNode(text);
                me.empty();
                dom.appendChild(textNode);
            }

            if (text) {
                textNode.data = text;
            }
        }
    }

}, function (Element) {

    /**
     * В FF есть проблемы с mouse event для этого корректируем eventMap
     * eventMap зависим от флагов Ext.supports которые переопределены в Ext.overrides.Base
     *  Код ниже является почти полной копипастой из версии 5.1.0.107 выкинуты только части которые нет необходимости переопределять
     */

    /* **************************** START **************************** */

    var prototype = Element.prototype,
        supports = Ext.supports,
        pointerdown = 'pointerdown',
        pointermove = 'pointermove',
        pointerup = 'pointerup',
        pointercancel = 'pointercancel',
        MSPointerDown = 'MSPointerDown',
        MSPointerMove = 'MSPointerMove',
        MSPointerUp = 'MSPointerUp',
        MSPointerCancel = 'MSPointerCancel',
        mousedown = 'mousedown',
        mousemove = 'mousemove',
        mouseup = 'mouseup',
        mouseover = 'mouseover',
        mouseout = 'mouseout',
        mouseenter = 'mouseenter',
        mouseleave = 'mouseleave',
        touchstart = 'touchstart',
        touchmove = 'touchmove',
        touchend = 'touchend',
        touchcancel = 'touchcancel',
        click = 'click',
        dblclick = 'dblclick',
        tap = 'tap',
        doubletap = 'doubletap',
        eventMap = prototype.eventMap = {},
        additiveEvents = prototype.additiveEvents = {};

    if (supports.PointerEvents) {
        eventMap[mousedown] = pointerdown;
        eventMap[mousemove] = pointermove;
        eventMap[mouseup] = pointerup;
        eventMap[touchstart] = pointerdown;
        eventMap[touchmove] = pointermove;
        eventMap[touchend] = pointerup;
        eventMap[touchcancel] = pointercancel;
        eventMap[click] = tap;
        eventMap[dblclick] = doubletap;

        // On devices that support pointer events we block pointerover, pointerout,
        // pointerenter, and pointerleave when triggered by touch input (see
        // Ext.event.publisher.Dom#blockedPointerEvents).  This is because mouseover
        // behavior is typically not desired when touching the screen.  This covers the
        // use case where user code requested a pointer event, however mouseover/mouseout
        // events are not cancellable, period.
        // http://www.w3.org/TR/pointerevents/#mapping-for-devices-that-do-not-support-hover
        // To ensure mouseover/out handlers don't fire when touching the screen, we need
        // to translate them to their pointer equivalents
        eventMap[mouseover] = 'pointerover';
        eventMap[mouseout] = 'pointerout';
        eventMap[mouseenter] = 'pointerenter';
        eventMap[mouseleave] = 'pointerleave';
    } else if (supports.MSPointerEvents) {
        // IE10
        eventMap[pointerdown] = MSPointerDown;
        eventMap[pointermove] = MSPointerMove;
        eventMap[pointerup] = MSPointerUp;
        eventMap[pointercancel] = MSPointerCancel;
        eventMap[mousedown] = MSPointerDown;
        eventMap[mousemove] = MSPointerMove;
        eventMap[mouseup] = MSPointerUp;
        eventMap[touchstart] = MSPointerDown;
        eventMap[touchmove] = MSPointerMove;
        eventMap[touchend] = MSPointerUp;
        eventMap[touchcancel] = MSPointerCancel;
        eventMap[click] = tap;
        eventMap[dblclick] = doubletap;

        // translate mouseover/out so they can be prevented on touch screens.
        // (see above comment in the PointerEvents section)
        eventMap[mouseover] = 'MSPointerOver';
        eventMap[mouseout] = 'MSPointerOut';
    } else if (supports.TouchEvents) {
        eventMap[pointerdown] = touchstart;
        eventMap[pointermove] = touchmove;
        eventMap[pointerup] = touchend;
        eventMap[pointercancel] = touchcancel;
        eventMap[mousedown] = touchstart;
        eventMap[mousemove] = touchmove;
        eventMap[mouseup] = touchend;
        eventMap[click] = tap;
        eventMap[dblclick] = doubletap;

        if (Ext.isWebKit && Ext.os.is.Desktop) {
            // Touch enabled webkit browsers on windows8 fire both mouse events and touch
            // events. so we have to attach listeners for both kinds when either one is
            // requested.  There are a couple rules to keep in mind:
            // 1. When the mouse is used, only a mouse event is fired
            // 2. When interacting with the touch screen touch events are fired.
            // 3. After a touchstart/touchend sequence, if there was no touchmove in
            // between, the browser will fire a mousemove/mousedown/mousup sequence
            // immediately after.  This can cause problems because if we are listening
            // for both kinds of events, handlers may run twice.  To work around this
            // issue we filter out the duplicate emulated mouse events by checking their
            // coordinates and timing (see Ext.event.publisher.Gesture#onDelegatedEvent)
            eventMap[touchstart] = mousedown;
            eventMap[touchmove] = mousemove;
            eventMap[touchend] = mouseup;
            eventMap[touchcancel] = mouseup;

            additiveEvents[mousedown] = mousedown;
            additiveEvents[mousemove] = mousemove;
            additiveEvents[mouseup] = mouseup;
            additiveEvents[touchstart] = touchstart;
            additiveEvents[touchmove] = touchmove;
            additiveEvents[touchend] = touchend;
            additiveEvents[touchcancel] = touchcancel;

            additiveEvents[pointerdown] = mousedown;
            additiveEvents[pointermove] = mousemove;
            additiveEvents[pointerup] = mouseup;
            additiveEvents[pointercancel] = mouseup;
        }
    } else {
        // browser does not support either pointer or touch events, map all pointer and
        // touch events to their mouse equivalents
        eventMap[pointerdown] = mousedown;
        eventMap[pointermove] = mousemove;
        eventMap[pointerup] = mouseup;
        eventMap[pointercancel] = mouseup;
        eventMap[touchstart] = mousedown;
        eventMap[touchmove] = mousemove;
        eventMap[touchend] = mouseup;
        eventMap[touchcancel] = mouseup;
    }

    if (Ext.isWebKit) {
        // These properties were carried forward from touch-2.x. This translation used
        // do be done by DomPublisher.  TODO: do we still need this?
        eventMap.transitionend = Ext.browser.getVendorProperyName('transitionEnd');
        eventMap.animationstart = Ext.browser.getVendorProperyName('animationStart');
        eventMap.animationend = Ext.browser.getVendorProperyName('animationEnd');
    }

    if (!Ext.supports.MouseWheel && !Ext.isOpera) {
        eventMap.mousewheel = 'DOMMouseScroll';
    }

    /* **************************** END **************************** */
});
