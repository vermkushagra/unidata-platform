/**
 * Plugin for Ext.tab.Panel. Prompt on tab close
 *
 * @author Sergey Shishigin
 * 2015-10-15
 */

Ext.define('Unidata.plugin.tab.DirtyTabClosePrompt', {
    extend: 'Ext.AbstractPlugin',

    alias: 'plugin.dirtytabcloseprompt',

    promptTitleText: Unidata.i18n.t('glossary:unsavedChanges'),
    closeUnsavedTabText: Unidata.i18n.t('other>confirmCloseUnsavedRecord'),

    panel: null,
    promptEnabled: true,

    init: function (panel) {
        this.panel = panel;

        panel.on('beforeclose', this.onBeforeClose, this);
    },

    destroy: function () {
        this.panel.un('beforeclose', this.onBeforeClose, this);

        this.panel = null;
    },

    closeTab: function () {
        this.panel.suspendEvent('beforeclose');
        this.panel.close();
    },

    onBeforeClose: function () {
        if (this.promptEnabled && Ext.isFunction(this.panel.getDirty) && this.panel.getDirty()) {
            this.panel.controller.showPrompt(this.promptTitleText, this.closeUnsavedTabText, this.closeTab, this);

            return false;
        }

        return true;
    },

    disablePrompt: function () {
        this.promptEnabled = false;
    },

    enablePrompt: function () {
        this.promptEnabled = true;
    }
});
