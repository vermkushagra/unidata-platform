/**
 * Кнопку отмены
 *
 * @author Sergey Shishigin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.component.button.round.CancelButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.cancel',

    cls: 'un-roundbutton__cancel',
    iconCls: 'icon-undo',
    tooltip: Unidata.i18n.t('common:cancel')
});
