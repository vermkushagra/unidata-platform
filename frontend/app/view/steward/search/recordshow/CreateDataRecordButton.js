/**
 * Класс реализующий кнопку создания нового датарекорда
 *
 * @author Ivan Marshalkin
 * @date 2017-05-22
 */

Ext.define('Unidata.view.steward.search.recordshow.CreateDataRecordButton', {
    extend: 'Ext.button.Button',

    iconCls: 'icon-plus',

    scale: 'large',

    height: 28,
    width: 28,

    padding: 0,

    tooltip:  Unidata.i18n.t('search>recordshow.createNewRecord')

});
