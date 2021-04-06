/**
 * Настройка удаления записей
 * @author Aleksandr Bavin
 * @date 2016-03-07
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.RemoveRecords', {
    extend: 'Unidata.view.steward.search.bulk.wizard.settings.Default',

    alias: 'widget.steward.search.bulk.wizard.settings.removerecords',

    requiredExternalData: [
        'entityName'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [],

    referenceHolder: true,

    initComponent: function () {
        this.callParent(arguments);

        this.on('added', this.updateConfirmStep, this);
    },

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'radiogroup',
                reference: 'wipeRadiogroup',
                vertical: true,
                columns: 1,
                items: [
                    {
                        boxLabel: Unidata.i18n.t('search>wizard.removeSoft'),
                        name: 'wipe',
                        inputValue: false
                    },
                    {
                        boxLabel: Unidata.i18n.t('search>wizard.removeHard'),
                        name: 'wipe',
                        inputValue: true
                    }
                ],
                listeners: {
                    change: 'onRadiogroupChange',
                    scope: this
                }
            }
        ]);
    },

    onRadiogroupChange: function () {
        var wizard = this.getWizard();

        this.updateConfirmStep();

        wizard.activateNextStep();
    },

    updateConfirmStep: function () {
        var wizard = this.getWizard(),
            step = this.getStep(),
            wipeRadiogroup = this.lookupReference('wipeRadiogroup'),
            wipe = wipeRadiogroup.getValue().wipe,
            warning;

        if (wipe) {
            warning = Ext.String.format(Unidata.i18n.t('other>confirmRemoveWarning'), '50 000');

            wizard.setConfirmText(
                '<p>' + Unidata.i18n.t('other>confirmRemoveRecords') + '</p>' +
                '<div class="un-warning-text">' + warning + '</div>'
            );
        } else {
            wizard.setConfirmText(null);
        }

        if (Ext.isEmpty(wipe)) {
            step.disallowNextStep(true);
        } else {
            step.allowNextStep();
        }
    },

    beforeGetOperationSettings: function (operationSettings) {
        var wipeRadiogroup = this.lookupReference('wipeRadiogroup'),
            wipe = wipeRadiogroup.getValue().wipe;

        operationSettings.wipe = wipe;
    }

});
