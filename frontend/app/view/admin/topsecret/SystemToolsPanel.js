/**
 * Панелька с сверх секретными табками
 *
 * @author Ivan Marshalkin
 * @date 2017-02-10
 */

Ext.define('Unidata.view.admin.topsecret.SystemToolsPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'Unidata.view.admin.topsecret.tabs.LogTab'
    ],

    alias: 'widget.admin.systemtoolspanel',

    viewModel: {
        type: 'admin.systemtoolspanel'
    },
    controller: 'admin.systemtoolspanel',

    title: '',

    referenceHolder: true,

    items: [
        {
            xtype: 'admin.systemtoolspanel.logtab',
            title: Unidata.i18n.t('admin.topsecrets>logs')
        },
        {
            xtype: 'admin.systemtoolspanel.logtab',
            title: Unidata.i18n.t('admin.topsecrets>system'),
            disabled: true
        }
    ],

    listeners: {}
});
