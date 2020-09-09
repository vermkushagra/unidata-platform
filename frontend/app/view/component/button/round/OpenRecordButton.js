/**
 *
 *
 * @author Sergey Shishigin
 * @date 2016-11-01
 */

Ext.define('Unidata.view.component.button.round.OpenRecordButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.openrecord',

    cls: 'un-roundbutton__openrecord',
    iconCls: 'icon-arrow-up',
    tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:record')})
});
