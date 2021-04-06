/**
 * Шрифтовая кнопка удаления элемента
 *
 * @author Sergey Shishigin
 * @date 2017-04-22
 *
 */

Ext.define('Unidata.view.component.button.font.DeleteButton', {
    extend: 'Unidata.view.component.button.FontButton',

    xtype: 'un.fontbutton.delete',

    cls: 'un-fontbutton-delete',
    iconCls: 'icon-cross',
    tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:record')}),
    scale: 'small'
});
