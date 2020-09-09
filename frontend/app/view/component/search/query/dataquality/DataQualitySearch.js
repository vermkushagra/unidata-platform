/**
 * @author Aleksandr Bavin
 * @date 2017-03-28
 */
Ext.define('Unidata.view.component.search.query.dataquality.DataQualitySearch', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.dataquality.DataQualitySearchController',
        'Unidata.view.component.search.query.dataquality.DataQualitySearchModel'
    ],

    alias: 'widget.component.search.query.dataquality.dataqualitysearch',

    controller: 'component.search.query.dataquality.dataqualitysearch',
    viewModel: 'component.search.query.dataquality.dataqualitysearch',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        searchQuery: null
    },

    ui: 'un-search-filter',

    cls: 'un-dataquality-search',

    title: Unidata.i18n.t('glossary:dataQuality'),

    referenceHolder: true,
    collapsible: true,
    titleCollapse: true,
    collapsed: true,
    disabled: true,

    methodMapper: [
        {
            method: 'initRouter'
        },
        {
            method: 'updateSearchQuery'
        },
        {
            method: 'isEmptyFilter'
        },
        {
            method: 'getFilter'
        }
    ],

    items: [
        {
            xtype: 'checkbox',
            boxLabel: Unidata.i18n.t('search>query.errorsOnly'),
            margin: '0 0 15 85',
            labelAlign: 'right',
            boxLabelAlign: 'after',
            reference: 'errorsOnlyCheckbox',
            disabled: false,
            publishes: ['value'],
            bind: {
                value: '{dqSearchQuery.term.facet.errors_only.termIsActive}',
                hidden: '{!searchSectionVisible.errorsOnly}'
            }
        },
        {
            xtype: 'container',
            reference: 'searchItemsContainer',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            defaults: {
                ui: 'un-field-default',
                labelWidth: 80,
                triggers: {
                    clear: {
                        cls: 'x-form-clear-trigger',
                        handler: function () {
                            this.setValue(null);
                        }
                    }
                },
                listeners: {
                    disable: 'fireChangeEvent',
                    enable: 'fireChangeEvent',
                    change: 'onInputChange'
                }
            },
            items: [
                {
                    xtype: 'combo',
                    name: '$dq_errors.ruleName',
                    reference: 'ruleName',
                    plugins: [
                        {
                            ptype: 'form.field.router',
                            routerTokenName: 'dataSearch',
                            routerValueName: '$dq_errors.ruleName'
                        }
                    ],
                    displayField: 'value',
                    valueField: 'value',
                    fieldLabel: Unidata.i18n.t('glossary:name'),
                    allowBlank: true,
                    // forceSelection: true,
                    autoSelect: true,
                    queryMode: 'local',
                    publishes: ['value'],
                    disabled: true,
                    store: {
                        fields: ['value'],
                        proxy: 'dataquality.info.names'
                    },
                    bind: {
                        value: '{dqSearchQuery.term.dq.ruleName.value}',
                        disabled: '{ruleNameDisabled}'
                    }
                },
                {
                    xtype: 'combo',
                    name: '$dq_errors.severity',
                    reference: 'severity',
                    plugins: [
                        {
                            ptype: 'form.field.router',
                            routerTokenName: 'dataSearch',
                            routerValueName: '$dq_errors.severity'
                        }
                    ],
                    fieldLabel: Unidata.i18n.t('glossary:criticalness'),
                    allowBlank: true,
                    // forceSelection: true,
                    editable: false,
                    valueField: 'value',
                    autoSelect: true,
                    publishes: ['value'],
                    disabled: true,
                    bind: {
                        value: '{dqSearchQuery.term.dq.severity.value}',
                        disabled: '{otherFieldsDisabled}'
                    },
                    store: {
                        fields: ['text', 'value'],
                        autoLoad: true,
                        data: Unidata.model.dataquality.DqRaise.getSeverityList()
                    }
                },
                {
                    xtype: 'combo',
                    name: '$dq_errors.category',
                    reference: 'category',
                    plugins: [
                        {
                            ptype: 'form.field.router',
                            routerTokenName: 'dataSearch',
                            routerValueName: '$dq_errors.category'
                        }
                    ],
                    displayField: 'value',
                    valueField: 'value',
                    fieldLabel: Unidata.i18n.t('glossary:category'),
                    allowBlank: true,
                    // forceSelection: true,
                    autoSelect: true,
                    queryMode: 'local',
                    publishes: ['value'],
                    disabled: true,
                    store: {
                        fields: ['value'],
                        proxy: 'dataquality.info.categories'
                    },
                    bind: {
                        value: '{dqSearchQuery.term.dq.category.value}',
                        disabled: '{otherFieldsDisabled}'
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    },

    setDisabled: function (disabled) {
        this.getViewModel().set('disabledAll', Boolean(disabled));

        return this.callParent([false]);
    }

});
