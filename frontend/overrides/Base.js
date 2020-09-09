/**
 *
 * @author Ivan Marshalkin
 * @date 2018-04-28
 */

Ext.define('Ext.overrides.Base', {
    requires: [
        'Ext.Base'
    ]
}, function () {
    // удаляем override т.к. это фейковы класс
    delete Ext.overrides.Base;

    // выключаем поддержку touch devices т.к. на win 10 появляются проблемы со скроллом
    Ext.supports.Touch = false;
    Ext.supports.TouchEvents = false;
    Ext.supports.touchScroll = 0;
});
