/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.SaveButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.save',

    iconCls: 'icon-floppy-disk',
    cls: 'un-roundbutton__save',
    buttonSize: 'big',
    tooltip: Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:record')})
});
