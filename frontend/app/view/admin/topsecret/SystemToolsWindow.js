/**
 * Сверх секретное окно только для администратора
 *
 * @author Ivan Marshalkin
 * @date 2017-02-10
 */

Ext.define('Unidata.view.admin.topsecret.SystemToolsWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.admin.topsecret.SystemToolsPanel'
    ],

    alias: 'widget.admin.systemtoolswindow',

    title: Unidata.i18n.t('admin.topsecrets>systemPanel'),

    layout: 'fit',

    referenceHolder: true,

    items: [
        {
            xtype: 'admin.systemtoolspanel'
        }
    ],

    listeners: {}
});
