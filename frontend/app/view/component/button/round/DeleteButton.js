/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.DeleteButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.delete',

    cls: 'un-roundbutton__delete',
    iconCls: 'icon-trash2',
    tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:record')})
});
