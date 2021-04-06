/**
 *
 * EXTJS-15662 Grid drag/drop fails in some browsers
 *
 * https://www.sencha.com/forum/showthread.php?294212-Extjs-5-drag-and-drop-grid/page2
 *
 * BUG: EXTJS-15662
 *
 * Данный патч применим только к версии 5.1.0.107 и выше
 *
 * Исправлено в 5.1.2
 *
 * !!! ПРИ ИСПОЛЬЗОВАНИИ БИБЛИОТЕКИ ВЕРСИЕЙ ВЫШЕ - УДАЛИТЬ !!!
 *
 * @author Ivan Marshalkin
 * @date 2017-02-20
 */

Ext.define('Unidata.view.DragZone_EXTJS15662', {
    override: 'Ext.view.DragZone',

    compatibility: '5.1.0.107',

    init: function (id, sGroup, config) {
        var me = this,
            eventSpec = {
                itemmousedown: me.onItemMouseDown,
                scope: me
            };

        // If there may be ambiguity with touch/swipe to scroll and a drag gesture
        // *also* trigger drag start on longpress
        if (Ext.supports.touchScroll) {
            eventSpec['itemlongpress'] = me.onItemMouseDown;
        }

        me.initTarget(id, sGroup, config);
        me.view.mon(me.view, eventSpec);
    }
});
