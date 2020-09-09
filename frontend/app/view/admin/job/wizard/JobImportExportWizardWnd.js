/**
 * Окно визарда ипорта/экспорта списка операций (jobs)
 *
 * @author Ivan Marshalkin
 * @date 2018-03-19
 */

Ext.define('Unidata.view.admin.job.wizard.JobImportExportWizardWnd', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.component.wizard.Wizard',
        'Unidata.view.admin.admin.job.wizard.step.SettingsStep'
    ],

    layout: 'fit',

    config: {},

    title: Unidata.i18n.t('admin.job>wizard>importOrExportJob'),

    monitorResize: true,
    alwaysCentered: true,
    resizable: false,
    draggable: false,
    modal: true,

    wizard: null,

    initComponent: function () {
        this.callParent(arguments);

        this.initWizard();

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
    },

    initComponentEvent: function () {
        var wizard = this.wizard,
        wnd = this;

        wnd.on('beforeclose', function () {
            if (!wizard || wizard.destroying || wizard.destroyed) {
                return;
            }

            if (wizard.isBlocked()) {
                return false;
            }
        }, this);

        wizard.on('destroy', function () {
            if (!wnd.destroyed && !wnd.destroying) {
                wnd.close();
            }
        }, this);
    },

    initWizard: function () {
        var wizard;

        wizard = Ext.widget({
            xtype: 'component.wizard',
            wizardWindow: this,
            firstStep: {
                xtype: 'jobimportexport.wizard.settingsstep'
            }
        });

        this.wizard = wizard;

        this.add(this.wizard);
    },

    onDestroy: function () {
        Ext.destroyMembers(
            this,
            'wizard'
        );

        this.callParent(arguments);
    }
});
