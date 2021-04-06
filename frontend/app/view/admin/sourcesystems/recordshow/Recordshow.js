Ext.define('Unidata.view.admin.sourcesystems.recordshow.Recordshow', {
    extend: 'Ext.tab.Panel',

    requires: [
        'Unidata.view.admin.sourcesystems.recordshow.RecordshowController',
        'Unidata.view.admin.sourcesystems.recordshow.RecordshowModel',

        'Unidata.view.admin.sourcesystems.sourcesystem.SourceSystem'
    ],

    alias: 'widget.admin.sourcesystems.recordshow',

    viewModel: {
        type: 'admin.sourcesystems.recordshow'
    },
    controller: 'admin.sourcesystems.recordshow',

    referenceHolder: true,

    plugins: [
        {
            ptype: 'dirtytabchangeprompt',
            pluginId: 'dirtytabchangeprompt',
            leaveUnsavedTabText: Unidata.i18n.t('admin.sourcesystems>confirmLeaveUnsavedSourceSystem')
        }
    ],

    maxTabWidth: 250,
    defaults: {
        bodyPadding: 0,
        closable: true
    },

    listeners: {
        add: 'onTabAdd'
    },

    ui: 'un-content'
});
