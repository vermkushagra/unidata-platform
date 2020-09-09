/**
 * Plugin for Ext.tab.Panel. Prompt on tab change
 *
 * @author Sergey Shishigin
 * 2015-10-15
 */

Ext.define('Unidata.plugin.tab.DirtyTabChangePrompt', {
    extend: 'Ext.AbstractPlugin',

    alias: 'plugin.dirtytabchangeprompt',

    promptTitleText: Unidata.i18n.t('glossary:unsavedChanges'),
    leaveUnsavedTabText: Unidata.i18n.t('other>confirmLeaveUnsavedRecord'),

    panel: null,
    promptEnabled: true,

    init: function (panel) {
        this.panel = panel;

        panel.on('beforetabchange', this.OnBeforeTabChange, this);
    },

    destroy: function () {
        this.panel.un('beforetabchange', this.OnBeforeTabChange, this);

        this.panel = null;
    },

    changeTab: function (newCard) {
        this.disablePrompt();
        this.panel.setActiveTab(newCard);
        this.enablePrompt();
    },

    OnBeforeTabChange: function (tabPanel, newCard, oldCard) {
        if (this.promptEnabled && oldCard && Ext.isFunction(oldCard.getDirty) && oldCard.getDirty()) {
            this.panel.controller.showPrompt(this.promptTitleText, this.leaveUnsavedTabText, this.changeTab, this, null, [newCard]);

            return false;
        }

        return true;
    },

    isDirtyTabExists: function () {
        return Ext.Array.some(this.panel.items.getRange(), function (item) {
            var viewModel = item.getViewModel(),
                result = false;

            if (viewModel && Ext.isFunction(item.getDirty)) {
                result = item.getDirty();
            }

            return result;
        });
    },

    disablePrompt: function () {
        this.promptEnabled = false;
    },

    enablePrompt: function () {
        this.promptEnabled = true;
    }
});
