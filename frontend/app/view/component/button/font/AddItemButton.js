/**
 * Шрифтовая кнопка добавления элемента
 *
 * @author Sergey Shishigin
 * @date 2017-04-22
 *
 */

Ext.define('Unidata.view.component.button.font.AddItemButton', {
    extend: 'Unidata.view.component.button.FontButton',

    xtype: 'un.fontbutton.additem',

    cls: 'un-fontbutton-additem',
    iconCls: 'icon-plus-circle',
    tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:record')})

});
