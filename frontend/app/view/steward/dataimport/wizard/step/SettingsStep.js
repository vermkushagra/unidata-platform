/**
 * @author Aleksandr Bavin
 * @date 2017-07-03
 */
Ext.define('Unidata.view.steward.dataimport.wizard.step.SettingsStep', {

    extend: 'Unidata.view.steward.dataimport.wizard.DataImportStep',

    requires: [
        'Unidata.view.steward.dataimport.wizard.step.SettingsStepController',
        'Unidata.view.steward.dataimport.wizard.step.SettingsStepModel',
        'Unidata.view.steward.dataimport.wizard.step.TemplateStep'
    ],

    alias: 'widget.dataimport.wizard.settings',

    controller: 'dataimport.wizard.settings',
    viewModel: 'dataimport.wizard.settings',

    title: Unidata.i18n.t('glossary:settings'),

    nextStep: {
        xtype: 'dataimport.wizard.template'
    },

    config: {
        adminSystemName: null
    },

    initComponent: function () {
        this.callParent(arguments);
    },

    updateEntityName: function (entityName) {
        var entitycombo;

        this.callParent(arguments);

        entitycombo = this.lookupReference('entitycombo');

        if (entitycombo) {
            entitycombo.setValue(entityName);
        }
    },

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'form',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                listeners: {
                    validitychange: 'onValidityChange'
                },
                defaults: {
                    msgTarget: 'qtip'
                },
                items: [
                    {
                        xtype: 'un.entitycombo',
                        reference: 'entitycombo',
                        ui: 'un-field-default',
                        fieldLabel: Unidata.i18n.t('glossary:selectEntityOrLookupEntity'),
                        value: this.getEntityName(),
                        labelWidth: 210,
                        autoSelect: true,
                        allowBlank: false,
                        allowedUserRights: ['create', 'update'],
                        validateOnBlur: false,
                        listeners: {
                            change: 'onEntityComboChange'
                        }
                    },
                    {
                        xtype: 'combo',
                        ui: 'un-field-default',
                        fieldLabel: Unidata.i18n.t('search>wizard.loadDataBehalfSysytem'),
                        labelWidth: 210,
                        allowBlank: false,
                        editable: false,
                        bind: {
                            store: '{sourceSystems}'
                        },
                        displayField: 'name',
                        valueField: 'name',
                        validateOnBlur: false,
                        listeners: {
                            select: 'onSourceSystemSelect',
                            change: 'onSourceSystemChange'
                        }
                    },
                    {
                        xtype: 'checkbox',
                        reference: 'mergeWithPreviousVersion',
                        boxLabel: Unidata.i18n.t('search>wizard.ignoreEmptyCells'),
                        margin: '0 0 0 215',
                        labelAlign: 'right',
                        boxLabelAlign: 'after',
                        checked: true,
                        listeners: {
                            change: 'onMergeWithPreviousVersionChange'
                        }
                    }
                ]
            }
        ]);
    }

});
