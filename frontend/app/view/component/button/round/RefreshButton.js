/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.RefreshButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.refresh',

    cls: 'un-roundbutton__refresh',
    iconCls: 'icon-sync',
    buttonSize: 'small',
    tooltip: Unidata.i18n.t('common:refreshRecord')
});
