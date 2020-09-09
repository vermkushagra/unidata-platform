/**
 *
 *
 * @author Sergey Shishigin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.OriginButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.origin',

    cls: '',
    iconCls: 'icon-dna',
    buttonSize: 'medium',
    tooltip: Unidata.i18n.t('glossary:originRecord', {count: 1})
});
