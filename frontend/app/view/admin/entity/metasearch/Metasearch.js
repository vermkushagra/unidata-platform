/**
 * @author Aleksandr Bavin
 * @date 30.05.2016
 */
Ext.define('Unidata.view.admin.entity.metasearch.Metasearch', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.form.field.Text',
        'Ext.grid.Panel',
        'Ext.layout.container.VBox',
        'Ext.toolbar.Paging',
        'Unidata.view.admin.entity.metasearch.MetasearchController',
        'Unidata.view.admin.entity.metasearch.MetasearchModel'
    ],

    alias: 'widget.admin.entity.metasearch',

    viewModel: {
        type: 'metasearch'
    },

    controller: 'metasearch',

    config: {
        draftMode: false
    },

    cls: 'un-metasearch',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: true,

    padding: 10,

    header: {
        xtype: 'container',
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        items: [
            {
                xtype: 'textfield',
                reference: 'searchTextfield',

                flex: 1,
                padding: 5,
                emptyText: Unidata.i18n.t('common:search'),
                minLength: 3,
                hideLabel: true,
                validation: false,
                listeners: {
                    change: 'onTextFieldChange'
                },
                triggers: {
                    clear: {
                        cls: 'x-form-clear-trigger',
                        handler: function () {
                            this.setValue('');
                        }
                    }
                }
            },
            {
                xtype: 'combobox',
                reference: 'searchFilter',

                flex: 1,
                padding: '5 5 5 0',
                publishes: 'display',
                emptyText: Unidata.i18n.t('admin.metamodel>whereLook'),
                multiSelect: true,
                displayField: 'display',
                valueField: 'value',
                editable: false,
                store: {
                    fields: ['display', 'value'],
                    // Поля, по которым ищем
                    data: [
                        {
                            display: Unidata.i18n.t('glossary:attributes'),
                            value: [
                                'attributeName',
                                'attributeDisplayName'
                            ]
                        },
                        {
                            display: Unidata.i18n.t('admin.metamodel>properties'),
                            value: [
                                'entityName',
                                'entityDisplayName',
                                'entityDescription'
                            ]
                        },
                        {
                            display: Unidata.i18n.t('glossary:relations'),
                            value: [
                                'relationFromName',
                                'relationFromDisplayName',
                                'relationFromAttributesNames',
                                'relationFromAttributesDisplayNames'
                            ]
                        },
                        {
                            display: Unidata.i18n.t('glossary:dataQuality'),
                            value: [
                                'dqName',
                                'dqDescription'
                            ]
                        },
                        {
                            display: Unidata.i18n.t('glossary:group'),
                            value: [
                                'group',
                                'groupDisplayName'
                            ]
                        }
                    ],
                    autoLoad: true
                },
                queryMode: 'local',
                listeners: {
                    change: 'onSearchFilterChange'
                }
            }
        ]
    },

    items: [
        {
            xtype: 'grid',
            reference: 'searchResultGrid',
            flex: 1,
            columns: [
                {
                    text: Unidata.i18n.t('glossary:entityOrLookupEntity'),
                    dataIndex: 'displayValue',
                    sortable: false,
                    hideable: false,
                    flex: 1
                },
                {
                    text: Unidata.i18n.t('admin.metamodel>whereFound'),
                    dataIndex: 'searchObject',
                    renderer: 'searchObjectRenderer',
                    sortable: false,
                    hideable: false,
                    flex: 1
                },
                {
                    text: Unidata.i18n.t('admin.metamodel>value'),
                    dataIndex: 'value',
                    sortable: false,
                    hideable: false,
                    flex: 1
                }
            ],
            dockedItems: [{
                xtype: 'pagingtoolbar',
                bind: {
                    store: '{searchResultStore}'
                },
                dock: 'bottom'
            }],
            bind: {
                title: Unidata.i18n.t('admin.metamodel>totalFound'),
                store: '{searchResultStore}'
            },
            listeners: {
                rowdblclick: 'onRowClick'
            }
        }
    ],

    searchObjectMapper: {
        attributeName: Unidata.i18n.t(
            'glossary:attributeDescription',
            {description: Unidata.i18n.t('admin.metamodel>logicName')}
        ),
        attributeDisplayName: Unidata.i18n.t(
            'glossary:attribute',
            {description: Unidata.i18n.t('admin.metamodel>displayedName')}
        ),

        entityName: Unidata.i18n.t(
            'admin.metamodel>props',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>logicName')}
        ),
        entityDisplayName: Unidata.i18n.t(
            'admin.metamodel>props',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>displayedName')}
        ),
        entityDescription: Unidata.i18n.t(
            'admin.metamodel>props',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>entityDescription')}
        ),

        relationFromName: Unidata.i18n.t(
            'admin.metamodel>relations',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>relationLogicName')}
        ),
        relationFromDisplayName: Unidata.i18n.t(
            'admin.metamodel>relations',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>relationDisplayedName')}
        ),
        relationFromAttributesNames: Unidata.i18n.t(
            'admin.metamodel>relations',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>attributeRelationLogicName')}
        ),
        relationFromAttributesDisplayNames: Unidata.i18n.t(
            'admin.metamodel>relations',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>attributeRelationDisplayedName')}
        ),

        dqName: Unidata.i18n.t(
            'admin.metamodel>dataQuality',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>ruleName')}
        ),
        dqDescription: Unidata.i18n.t(
            'admin.metamodel>dataQuality',
            {context: 'description', description: Unidata.i18n.t('admin.metamodel>ruleDescription')}
        ),

        group: Unidata.i18n.t(
            'glossary:groupDescription',
            {description: Unidata.i18n.t('admin.metamodel>logicName')}
        ),
        groupDisplayName: Unidata.i18n.t(
            'glossary:group',
            {description: Unidata.i18n.t('admin.metamodel>displayedName')}
        )
    },

    onDestroy: function () {
        this.searchObjectMapper = null;

        this.callParent(arguments);
    }

});
