/**
 *
 * EXTJS-16166 - Unable to use home/end/arrow navigation keys inside data views & grid editors.
 *
 * https://www.sencha.com/forum/showthread.php?295892-Ext-JS-5.1-Post-GA-Patches&langid=14
 *
 * BUG: EXTJS-16166
 *
 * Данный патч применим только к версии 5.1.0.107
 *
 * Исправлено в 5.1.1.451
 *
 * !!! ПРИ ИСПОЛЬЗОВАНИИ БИБЛИОТЕКИ ВЕРСИЕЙ ВЫШЕ - УДАЛИТЬ !!!
 *
 * @author Ivan Marshalkin
 * @date 2016-05-11
 */

Ext.define('Ext.overrides.view.View_EXTJS16166', {
    override: 'Ext.view.View',

    compatibility: '5.1.0.107',

    handleEvent: function (e) {
        var me = this,
            isKeyEvent = me.keyEventRe.test(e.type),
            nm = me.getNavigationModel();

        e.view = me;

        if (isKeyEvent) {
            e.item = nm.getItem();
            e.record = nm.getRecord();
        }

        // If the key event was fired programatically, it will not have triggered the focus
        // so the NavigationModel will not have this information.
        if (!e.item) {
            e.item = e.getTarget(me.itemSelector);
        }

        if (e.item && !e.record) {
            e.record = me.getRecord(e.item);
        }

        if (me.processUIEvent(e) !== false) {
            me.processSpecialEvent(e);
        }

        // We need to prevent default action on navigation keys
        // that can cause View element scroll unless the event is from an input field.
        // We MUST prevent browser's default action on SPACE which is to focus the event's target element.
        // Focusing causes the browser to attempt to scroll the element into view.

        if (isKeyEvent && !Ext.fly(e.target).isInputField()) {
            if (e.getKey() === e.SPACE || e.isNavKeyPress(true)) {
                e.preventDefault();
            }
        }
    }
});
