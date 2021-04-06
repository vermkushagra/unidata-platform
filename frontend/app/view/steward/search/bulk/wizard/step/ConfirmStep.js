/**
 * @author Aleksandr Bavin
 * @date 20.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.step.ConfirmStep', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStep',

    requires: [
        'Unidata.view.steward.search.bulk.wizard.step.ConfirmStepController'
    ],

    alias: 'widget.steward.search.bulk.wizard.step.confirm',

    controller: 'confirm',

    items: {
        reference: 'content',
        padding: '30 0 0 0',
        cls: 'un-bulk-wizard-confirm',
        html: Unidata.i18n.t('search>wizard.confirmTitle')
    },

    listeners: {
        beforeactivate: 'onBeforeactivate'
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
                text: Unidata.i18n.t('common:apply'),
                xtype: 'button',
                bind: {
                    disabled: '{!canConfirm}'
                },
                listeners: {
                    click: 'onConfirmClick'
                }
            }
        );
    }

});
