/**
 * Шрифтовая кнопка открытия записи
 *
 * @author Sergey Shishigin
 * @date 2017-04-22
 *
 */

Ext.define('Unidata.view.component.button.font.SaveButton', {
    extend: 'Unidata.view.component.button.FontButton',

    xtype: 'un.fontbutton.save',

    cls: 'un-fontbutton-save',
    iconCls: 'icon-floppy-disk',
    tooltip: Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:record')}),
    scale: 'small'
});
