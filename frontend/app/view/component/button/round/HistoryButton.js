/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.HistoryButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.history',

    cls: '',
    iconCls: 'icon-calendar-insert',
    buttonSize: 'medium',
    tooltip: Unidata.i18n.t('glossary:recordHistory')
});
