/**
 * @author Aleksandr Bavin
 * @date 2017-06-29
 */
Ext.define('Unidata.view.component.wizard.WizardTab', {

    extend: 'Ext.tab.Tab',

    alias: 'widget.tab.wizard',

    config: {
        wizardTabIndex: 1
    },

    updateWizardTabIndex: function () {
        this.updateWizardIcon();
    },

    onRender: function () {
        this.callParent(arguments);
        this.updateWizardIcon();
    },

    updateWizardIcon: function () {
        if (this.btnIconEl) {
            this.btnIconEl.dom.innerHTML = this.getWizardTabIndex();
        }
    }

});
