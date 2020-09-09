Ext.define('Unidata.view.admin.entity.recordshow.Recordshow', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.admin.entity.recordshow',

    viewModel: {
        type: 'admin.entity.recordshow'
    },
    controller: 'admin.entity.recordshow',

    requires: [
        'Unidata.view.admin.entity.recordshow.RecordshowController',
        'Unidata.view.admin.entity.recordshow.RecordshowModel',

        'Unidata.view.admin.entity.metarecord.MetaRecord'
    ],

    plugins: [
        {
            ptype: 'dirtytabchangeprompt',
            pluginId: 'dirtytabchangeprompt',
            leaveUnsavedTabText: Unidata.i18n.t('admin.metamodel>confirmLeaveUnsavedModel')
        }
    ],

    listeners: {
        beforedeactivate: 'onBeforeDeactivate',
        add: 'onTabAdd'
    },

    maxTabWidth: 250,
    reference: 'recordshowTabPanel',
    referenceHolder: true,
    defaults: {
        bodyPadding: 0,
        closable: true
    },

    hasOpenedTabs: function () {
        if (this.getOpenedTabCount() > 0) {
            return true;
        }

        return false;
    },

    getOpenedTabCount: function () {
        var tabs = this.getOpenedTabs();

        return tabs.length;
    },

    getOpenedTabs: function () {
        var tabs = [];

        if (this.items) {
            tabs = this.items.getRange();
        }

        return tabs;
    },

    closeTabsSilent: function () {
        var items = this.items;

        items.each(function (tab) {
            if (Ext.isFunction(tab.closeTabSilent)) {
                tab.closeTabSilent();
            }
        });
    }
});
