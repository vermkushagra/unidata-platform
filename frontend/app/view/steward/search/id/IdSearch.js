/**
 * Форма поиска по etalonId / externalId / originId
 *
 * @author Aleksandr Bavin
 * @date 2016-12-21
 */
Ext.define('Unidata.view.steward.search.id.IdSearch', {
    extend: 'Unidata.view.component.form.WindowForm',

    requires: [
        'Unidata.view.steward.search.id.IdSearchController',
        'Unidata.view.steward.search.id.IdSearchModel'
    ],

    alias: 'widget.form.window.idsearch',

    viewModel: {
        type: 'idsearch'
    },

    controller: 'idsearch',

    title: Unidata.i18n.t('search>query.idSearch'),

    modal: false,
    draggable: true,

    formParams: {
        defaults: {
            validateOnBlur: false,
            margin: '0 0 10 0'
        },
        items: [
            {
                xtype: 'radiogroup',
                fieldLabel: Unidata.i18n.t('search>query.searchBy'),
                labelAlign: 'left',
                labelWidth: 65,
                columns: [75, 85, 75],
                cls: 'x-check-group-alt',
                defaults: {
                    name: 'searchBy',
                    publishes: ['value']
                },
                items: [
                    {
                        reference: 'serchByEtalonId',
                        boxLabel: 'etalonId',
                        inputValue: 'etalonId',
                        checked: true
                    },
                    {
                        reference: 'serchByExternalId',
                        boxLabel: 'externalId',
                        inputValue: 'externalId'
                    },
                    {
                        reference: 'serchByOriginId',
                        boxLabel: 'originId',
                        inputValue: 'originId'
                    }
                ]
            },
            {
                xtype: 'textfield',
                name: 'etalonId',
                emptyText: 'etalonId',
                allowBlank: false,
                msgTarget: 'under',
                listeners: {
                    afterrender: function (textfield) {
                        textfield.focus();
                    }
                },
                bind: {
                    disabled: '{!serchByEtalonId.value}',
                    hidden: '{!serchByEtalonId.value}'
                }
            },
            {
                xtype: 'textfield',
                name: 'externalId',
                emptyText: 'externalId',
                allowBlank: false,
                msgTarget: 'under',
                hidden: true,
                disabled: true,
                bind: {
                    disabled: '{!serchByExternalId.value}',
                    hidden: '{!serchByExternalId.value}'
                }
            },
            {
                xtype: 'textfield',
                name: 'originId',
                emptyText: 'originId',
                allowBlank: false,
                msgTarget: 'under',
                hidden: true,
                disabled: true,
                bind: {
                    disabled: '{!serchByOriginId.value}',
                    hidden: '{!serchByOriginId.value}'
                }
            },
            {
                xtype: 'container',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                hidden: true,
                disabled: true,
                bind: {
                    disabled: '{!serchByExternalId.value}',
                    hidden: '{!serchByExternalId.value}'
                },
                defaults: {
                    flex: 1
                },
                items: [
                    {
                        xtype: 'un.entitycombo',
                        reference: 'entityCombo',
                        name: 'name',
                        cls: 'entity',
                        hideLabel: true,
                        labelAlign: 'top',
                        autoSelect: true,
                        width: '100%',
                        allowBlank: false,
                        forceSelection: true,
                        msgTarget: false,
                        emptyText: '- ' + Unidata.i18n.t('glossary:entityOrLookupEntity').toLowerCase() + ' -'
                    },
                    {
                        xtype: 'combo',
                        reference: 'importSourceCombo',
                        name: 'sourceSystem',
                        emptyText: Unidata.i18n.t('other>selectSourceSystem'),
                        allowBlank: false,
                        editable: false,
                        bind: {
                            store: '{sourceSystems}'
                        },
                        displayField: 'name',
                        valueField: 'name'
                    }
                ]
            }
        ],
        baseParams: {
        }
    }

});
