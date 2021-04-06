/**
 * Кнопка исключения
 *
 * @author Sergey Shishigin
 * @date 2016-10-28
 */

Ext.define('Unidata.view.component.button.round.ExcludeButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.exclude',

    cls: 'un-roundbutton__exclude',
    iconCls: 'icon-prohibited',
    tooltip: Unidata.i18n.t('glossary:excludeRecord')
});
