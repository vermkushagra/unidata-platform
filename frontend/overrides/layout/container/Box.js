/**
 * Боримся с багом ExtJS после отрисовки не производится восстановление позиции прокрутки.
 *
 * Реализация функций beginLayout, completeLayout перенесена с 5.1.1.451 c с минимальной модификацией:
 *      1) scrollRestore перетирался в beginLayout
 *      2) при восстановлени в completeLayout ownerContext.ownerScrollable могло не оказаться
 *
 * Смотри подробности:
 * https://www.sencha.com/forum/showthread.php?297259-layout-causes-panel-to-reset-scroll-position-when-editing-form
 * https://www.sencha.com/forum/showthread.php?296531
 * Баг EXTJS-16350
 *
 * Перенесено из версии 5.1.1.451
 *
 * @author Ivan Marshalkin
 * @date 2018-02-14
 */

Ext.define('Ext.overrides.layout.container.Box', {
    override: 'Ext.layout.container.Box',

    beginLayout: function (ownerContext) {
        var me = this,
            owner = me.owner,
            smp = owner.stretchMaxPartner,
            style = me.innerCt.dom.style,
            names = me.names,
            overflowHandler = me.overflowHandler,
            scrollable = owner.getScrollable(),
            scrollPos;

        ownerContext.boxNames = names;

        // this must happen before callParent to allow the overflow handler to do its work
        // that can effect the childItems collection...
        if (overflowHandler) {
            overflowHandler.beginLayout(ownerContext);
        }

        // get the contextItem for our stretchMax buddy:
        if (typeof smp === 'string') {
            smp = Ext.getCmp(smp) || owner.query(smp)[0];
        }

        ownerContext.stretchMaxPartner = smp && ownerContext.context.getCmp(smp);

        me.callParent(arguments);

        ownerContext.innerCtContext = ownerContext.getEl('innerCt', me);
        ownerContext.targetElContext = ownerContext.getEl('targetEl', me);

        ownerContext.ownerScrollable = scrollable = owner.getScrollable();

        if (scrollable) {
            // If we have a scrollable, save the positions regardless of whether we can scroll in that direction
            // since the scrollable may be configured with x: false, y: false, which means it can only be
            // controlled prorammatically
            if (!ownerContext.scrollRestore) {
                ownerContext.scrollRestore = scrollable.getPosition();
            }
        }

        // Don't allow sizes burned on to the innerCt to influence measurements.
        style.width = '';
        style.height = '';
    },

    completeLayout: function (ownerContext) {
        var me = this,
            names = ownerContext.boxNames,
            invalidateScrollX = ownerContext.invalidateScrollX,
            invalidateScrollY = ownerContext.invalidateScrollY,
            overflowHandler = me.overflowHandler,
            scrollRestore = ownerContext.scrollRestore,
            dom, el, overflowX, overflowY, styles, scroll, scrollable;

        if (overflowHandler) {
            overflowHandler.completeLayout(ownerContext);
        }

        if (invalidateScrollX || invalidateScrollY) {
            el = me.getTarget();
            dom = el.dom;
            styles = dom.style;

            if (invalidateScrollX) {
                // get computed style to see if we are 'auto'
                overflowX = el.getStyle('overflowX');

                if (overflowX === 'auto') {
                    // capture the inline style (if any) so we can restore it later:
                    overflowX = styles.overflowX;
                    styles.overflowX = 'scroll'; // force the scrollbar to appear
                } else {
                    invalidateScrollX = false; // no work really since not 'auto'
                }
            }

            if (invalidateScrollY) {
                // get computed style to see if we are 'auto'
                overflowY = el.getStyle('overflowY');

                if (overflowY === 'auto') {
                    // capture the inline style (if any) so we can restore it later:
                    overflowY = styles.overflowY;
                    styles.overflowY = 'scroll'; // force the scrollbar to appear
                } else {
                    invalidateScrollY = false; // no work really since not 'auto'
                }
            }

            if (invalidateScrollX || invalidateScrollY) { // if (some form of 'auto' in play)
                // force a reflow...
                dom.scrollWidth; // jshint ignore:line

                if (invalidateScrollX) {
                    styles.overflowX = overflowX; // restore inline style
                }

                if (invalidateScrollY) {
                    styles.overflowY = overflowY; // restore inline style
                }
            }
        }

        if (scrollRestore && ownerContext.ownerScrollable) {
            ownerContext.ownerScrollable.scrollTo(scrollRestore.x, scrollRestore.y);
            ownerContext.ownerScrollable = undefined;
        }
    }
});
