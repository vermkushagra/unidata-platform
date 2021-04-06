/**
 * @author Aleksandr Bavin
 * @date 2017-06-29
 */
Ext.define('Unidata.view.component.wizard.WizardTabPanel', {

    extend: 'Ext.tab.Panel',

    requires: [
        'Unidata.view.component.wizard.WizardTab'
    ],

    alias: 'widget.tabpanel.wizard',

    cls: 'un-wizard',

    tabBar: {
        layout: {
            type: 'hbox',
            pack: 'center'
        },
        ui: 'un-wizard'
    },

    defaults: {
        tabConfig: {
            xtype: 'tab.wizard'
        }
    },

    applyTabBar: function () {
        var tabBar = this.callParent(arguments);

        tabBar.on('add', this.updateTabIndexes, this);
        tabBar.on('remove', this.updateTabIndexes, this);

        return tabBar;
    },

    /**
     * Обновляет индексы табов
     * @param {Ext.tab.Bar} tabBar
     */
    updateTabIndexes: function (tabBar) {
        tabBar.items.each(function (tab, index) {
            if (tab instanceof Unidata.view.component.wizard.WizardTab) {
                tab.setWizardTabIndex(index + 1);
            }
        });
    }

});
