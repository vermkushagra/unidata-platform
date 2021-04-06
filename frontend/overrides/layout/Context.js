/**
 * Если элементы вложены друг в друга с большой глубиной начинает срывать голову у layout.
 * Т.к. количество итераций в библиотеке ограничино не более 100.
 *
 * Глобально это проблемм не решает, но пока так. В ExtJS 6 переопределять нужно по другому.
 * Смотри код: http://docs.sencha.com/extjs/6.0/6.0.1-classic/source/Context.html#Ext-layout-Context
 *
 * Смотри подробности:
 * http://forums.ext.net/showthread.php?25289-OPEN-Trunk-quot-Layout-run-failed-quot-appears-when-displaying-large-no-of-dynamic-MenuPanels-inside-Portals-inside-GroupTabPanel
 * https://fiddle.sencha.com/#fiddle/lam
 * https://www.sencha.com/forum/showthread.php?300066-About-the-watchDog-in-runLayout&langid=14
 *
 * @author Ivan Marshalkin
 * @date 2016-03-10
 */

Ext.define('Ext.overrides.layout.Context', {
    override: 'Ext.layout.Context',

    compatibility: '5.1.0.107',

    run: function () {
        var me = this,
            flushed = false,
            watchDog = 1000; // <<< ---- все затевалось ради этого значение. default value = 100

        me.purgeInvalidates();
        me.flushInvalidates();
        me.state = 1;
        me.totalCount = me.layoutQueue.getCount();

        me.flush();

        while ((me.remainingLayouts || me.invalidQueue.length) && watchDog--) {
            if (me.invalidQueue.length) {
                me.flushInvalidates();
            }

            if (me.runCycle()) {
                flushed = false;
            } else if (!flushed) {

                me.flush();
                flushed = true;

                me.flushLayouts('completionQueue', 'completeLayout');
            } else if (!me.invalidQueue.length) {

                me.state = 2;
                break;
            }

            if (!(me.remainingLayouts || me.invalidQueue.length)) {
                me.flush();
                me.flushLayouts('completionQueue', 'completeLayout');
                me.flushLayouts('finalizeQueue', 'finalizeLayout');
            }
        }

        return me.runComplete();
    }
});
