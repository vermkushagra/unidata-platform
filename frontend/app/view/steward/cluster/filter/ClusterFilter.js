/**
 * Панель фильтрации для экрана кластеров
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.filter.ClusterFilter', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.cluster.filter.ClusterFilterController',
        'Unidata.view.steward.cluster.filter.ClusterFilterModel'
    ],

    alias: 'widget.steward.cluster.filterview',

    viewModel: {
        type: 'steward.cluster.filterview'
    },

    controller: 'steward.cluster.filterview',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-search',

    referenceHolder: true,

    searchBy: null,
    entityCombo: null,                                            // пикер реестров / справочинков
    duplicateRuleCombo: null,                                     // пикер списка правил поиска дубликатов
    duplicateGroupCombo: null,                                    // пикер списка групп правил поиска дубликатов
    dataRecordCombo: null,                                        // пикер выбора записиы
    preprocessingCheckbox: null,                                  // чекбокс для отправки флага preprocessing
    title: Unidata.i18n.t('glossary:duplicates'),

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'getCurrentEntityName'
        },
        {
            method: 'getCurrentEntityType'
        },
        {
            method: 'setRouteToClusterData'
        },
        {
            method: 'doSearch'
        }
    ],

    config: {},

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.searchBy            = this.lookupReference('searchBy');
        me.entityCombo         = this.lookupReference('entityCombo');
        me.duplicateRuleCombo  = this.lookupReference('duplicateRuleCombo');
        me.duplicateGroupCombo = this.lookupReference('duplicateGroupCombo');
        me.dataRecordCombo     = this.lookupReference('dataRecordCombo');
        me.preprocessingCheckbox = this.lookupReference('preprocessingCheckbox');
    },

    onDestroy: function () {
        var me = this;

        me.searchBy            = null;
        me.entityCombo         = null;
        me.duplicateRuleCombo  = null;
        me.duplicateGroupCombo = null;
        me.dataRecordCombo     = null;
        me.preprocessingCheckbox = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'un.entitycombo',
            reference: 'entityCombo',
            ui: 'un-field-default',
            cls: 'entity',
            hideLabel: true,
            labelAlign: 'top',
            autoSelect: true,
            allowBlank: true,
            forceSelection: true,
            msgTarget: false,
            emptyText: Unidata.i18n.t('cluster>selectEntityOrLookupEntity'),
            publishes: ['value'],
            showLookupEntities: true,
            listeners: {
                change: 'onEntityChange'
            }
        },
        {
            xtype: 'radiogroup',
            fieldLabel: Unidata.i18n.t('cluster>searchBy'),
            reference: 'searchBy',
            labelWidth: 70,
            defaultType: 'radiofield',
            defaults: {
                width: 70,
                margin: 0,
                publishes: ['value']
            },
            labelStyle: 'color: white',
            style: {
                color: 'white'
            },
            layout: 'hbox',
            listeners: {
                change: 'onSearchByChange',
                render: function () {
                    // корректировка отступов после рендера...
                    setTimeout(this.updateLayout.bind(this), 100);
                }
            },
            items: [
                {
                    boxLabel: Unidata.i18n.t('cluster>records'),
                    name: 'searchBy',
                    inputValue: 'record',
                    checked: true
                },
                {
                    boxLabel: Unidata.i18n.t('cluster>byRule'),
                    name: 'searchBy',
                    inputValue: 'rule'
                }
            ]
        },
        {
            xtype: 'dropdownpickerfield',
            reference: 'dataRecordCombo',
            ui: 'un-field-default',
            entityType: 'entity',
            emptyText: Unidata.i18n.t(
                'common:defaultSelect',
                {entity: Unidata.i18n.t('glossary:record').toLowerCase()}
            ),
            fieldLabel: '',
            disabled: true,
            autoEnable: false,
            bind: {
                disabled: '{!entityCombo.value}'
            },
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue('');
                    }
                }
            },
            listeners: {
                change: 'onDataRecordComboChange'
            }
        },
        {
            xtype: 'un.duplicaterulecombo',
            reference: 'duplicateRuleCombo',
            ui: 'un-field-default',
            emptyText: Unidata.i18n.t('cluster>searchDuplicateRules'),
            publishes: 'dataLoading',
            hidden: true,
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.setValue('');
                    }
                }
            }
        },
        // выбор по группам скрыт, на всякий случай оставляю, что бы можн было оперативно вернуть
        // {
        //     xtype: 'un.duplicategroupcombo',
        //     reference: 'duplicateGroupCombo',
        //     ui: 'un-field-default',
        //     emptyText: Unidata.i18n.t('cluster>searchDuplicateGroups'),
        //     publishes: 'dataLoading',
        //     hidden: true,
        //     triggers: {
        //         clear: {
        //             cls: 'x-form-clear-trigger',
        //             handler: function () {
        //                 this.setValue('');
        //             }
        //         }
        //     }
        // },
        {
            xtype: 'checkbox',
            reference: 'preprocessingCheckbox',
            boxLabel: Unidata.i18n.t('cluster>preprocessing')
        },
        {
            xtype: 'button',
            text: Unidata.i18n.t('common:search'),
            listeners: {
                click: 'onSearchButtonClick'
            },
            scale: 'large',
            disabled: true,
            bind: {
                disabled: '{!canSearch}'
            }
        }
    ]
});
