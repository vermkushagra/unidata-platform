/**
 * Кнопка "Показать доп.информацию"
 *
 * @author Sergey Shishigin
 * @date 2016-04-10
 */

Ext.define('Unidata.view.component.button.round.InfoButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.info',

    cls: 'un-roundbutton__info',
    iconCls: 'icon-question-circle',
    buttonSize: 'extrasmall'
});
