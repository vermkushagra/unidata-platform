/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.DataButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.data',

    cls: '',
    iconCls: 'icon-star',
    buttonSize: 'medium',
    tooltip: Unidata.i18n.t('glossary:referenceRecord')
});
