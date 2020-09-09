/**
 * Окно визарда тестирования правил качества
 *
 * @author Ivan Marshalkin
 * @date 2018-02-20
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardWnd', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.component.wizard.Wizard',
        'Unidata.view.admin.entity.metarecord.dq.testwizard.step.SettingsStep'
    ],

    layout: 'fit',

    config: {
        metaRecord: null,
        dqRules: null
    },

    title: Unidata.i18n.t('admin.dqtest>wizardWindowTitle'),

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
        var metaRecord = this.getMetaRecord(),
            wizard;

        wizard = Ext.widget({
            xtype: 'component.wizard',
            wizardWindow: this,
            firstStep: {
                xtype: 'dqtest.wizard.settingsstep',
                metaRecord: metaRecord,
                dqRules: this.getDqRules()
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
