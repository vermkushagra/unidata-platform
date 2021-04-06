/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.RestoreButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.restore',

    cls: 'un-roundbutton__restore',
    iconCls: 'icon-exit-up',
    tooltip: Unidata.i18n.t('common:restoreRecord')
});
