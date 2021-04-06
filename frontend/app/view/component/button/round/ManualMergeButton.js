/**
 * Кнопка ручной консолидации
 *
 * @author Sergey Shishigin
 * @date 2016-10-31
 */

Ext.define('Unidata.view.component.button.round.ManualMergeButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.manualmerge',

    cls: 'un-roundbutton__manualmerge',
    iconCls: 'icon-arrows-merge',
    tooltip: Unidata.i18n.t('other>manualMergeRecords')
});
