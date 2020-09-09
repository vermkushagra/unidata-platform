/**
 * @author Aleksandr Bavin
 * @date 20.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.WizardStep', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.WizardStepController'
    ],

    alias: 'widget.steward.search.bulk.wizard.step.default',

    controller: 'default',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    methodMapper: [
        {
            method: 'getWizard'
        },
        {
            method: 'isStepAllowed'
        },
        {
            method: 'allowStep'
        },
        {
            method: 'disallowStep'
        },
        {
            method: 'allowNextStep'
        },
        {
            method: 'disallowNextStep'
        }
    ],

    config: {
        stepAllowed: false
    },

    wizardTabPanel: null, // панелька к которой принадлежит таб

    disabled: true,

    referenceHolder: true,

    defaults: {
        flex: 1
    },

    items: [],

    dockedItems: [
        {
            xtype: 'un.toolbar',
            reference: 'buttonItems',
            dock: 'bottom'
        }
    ],

    listeners: {
        activate: 'onActivate'
    },

    initItems: function () {
        this.callParent(arguments);
        this.initButtonItems();
    },

    initButtonItems: function () {
        this.lookupReference('buttonItems').add(
            {
                text: Unidata.i18n.t('search>wizard.prev'),
                xtype: 'button',
                color: 'transparent',
                listeners: {
                    click: 'onPrevClick'
                }
            },
            {
                xtype: 'container',
                flex: 1
            },
            {
                text: Unidata.i18n.t('search>wizard.next'),
                xtype: 'button',
                bind: {
                    disabled: '{!nextStepAllowed}'
                },
                listeners: {
                    click: 'onNextClick'
                }
            }
        );
    },

    /**
     * Можно запретить переход на шаг подтверждения
     * @returns {boolean}
     */
    isConfirmStepAllowed: function () {
        return true;
    },

    onDestroy: function () {
        delete this.wizardTabPanel;

        this.callParent(arguments);
    },

    updateStepAllowed: function () {
        if (this.isConfiguring) {
            return;
        }

        this.fireEvent('stepallowchange', this);
    }

});
