/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.AddItemButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.add',

    cls: 'un-roundbutton__additem',
    iconCls: 'icon-plus-circle',
    tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:record')})
});
