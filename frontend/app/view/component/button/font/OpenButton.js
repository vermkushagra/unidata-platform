/**
 * Шрифтовая кнопка открытия записи
 *
 * @author Sergey Shishigin
 * @date 2017-04-22
 *
 */

Ext.define('Unidata.view.component.button.font.OpenButton', {
    extend: 'Unidata.view.component.button.FontButton',

    xtype: 'un.fontbutton.open',

    cls: 'un-fontbutton-open',
    iconCls: 'icon-launch',
    tooltip: Unidata.i18n.t('common:openSomething', {name: Unidata.i18n.t('glossary:record')}),
    scale: 'small'
});
