Ext.define('Unidata.view.admin.entity.metarecord.consolidation.Consolidation', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.admin.entity.metarecord.consolidation.ConsolidationController',
        'Unidata.view.admin.entity.metarecord.consolidation.ConsolidationModel'
    ],

    alias: 'widget.admin.entity.metarecord.consolidation',

    controller: 'admin.entity.metarecord.consolidation',
    viewModel: {
        type: 'admin.entity.metarecord.consolidation'
    },

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    draftMode: null,                                  // режим работы с черновиком

    plugins: [
        {
            ptype: 'managedstoreloader',
            pluginId: 'managedstoreloader',
            storeNames: ['sourceSystems'],
            callback: function () {
                this.getController().onNecessaryStoresLoad();
            }
        }
    ],

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'disablefieldset',
                    reference: 'bvrFieldSet',
                    title: Unidata.i18n.t('admin.metamodel>byDataSources'),
                    checkboxToggle: true,
                    disableItems: false,
                    width: 250,
                    scrollable: 'vertical',
                    bind: {
                        disabled: '{metaRecordViewReadOnly}'
                    },
                    items: [
                        {
                            xtype: 'grid',
                            reference: 'bvrGridWeight',
                            plugins: {
                                ptype: 'cellediting',
                                clicksToEdit: 2,
                                listeners: {
                                    beforeedit: 'onSourceSystemWeightBeforeCellEdit',
                                    validateedit: 'onSourceSystemWeightCellValidateEdit',
                                    edit: 'onSourceSystemWeightCellEdit'
                                }
                            },
                            scrollable: true,
                            columns: [
                                {
                                    header: Unidata.i18n.t('glossary:dataSource'),
                                    dataIndex: 'name',
                                    sortable: true,
                                    resizable: false,
                                    hideable: false,
                                    menuDisabled: true,
                                    flex: 1
                                },
                                {
                                    header: Unidata.i18n.t('admin.metamodel>weight'),
                                    sortable: false,
                                    resizable: false,
                                    hideable: false,
                                    menuDisabled: true,
                                    editor: {
                                        xtype: 'numberfield',
                                        hideTrigger: true,
                                        allowDecimals: false,
                                        minValue: 0,
                                        maxValue: 99,
                                        validateOnChange: true,
                                        selectOnFocus: true,
                                        listeners: {
                                            // запрещаем вставлять из буфера
                                            paste: {
                                                element: 'inputEl',
                                                fn: function (event) {
                                                    event.preventDefault();
                                                }
                                            },
                                            change: function (field, newValue, oldValue) {
                                                if (!field.isValid()) {
                                                    field.setValue(oldValue);
                                                }
                                            },
                                            render: function () {
                                                var checkWeightUnique = Unidata.Config.getCheckSourceSystemWeightUnique();

                                                if (!checkWeightUnique) {
                                                    this.setMaxValue(100);
                                                }
                                            }
                                        }
                                    },
                                    renderer: function (value, meta) {
                                        var record = meta.record,
                                            result = record.get('weight');

                                        if (result === -1) {
                                            result = Unidata.i18n.t('glossary:notSet');
                                        }

                                        return result;
                                    },
                                    width: 100
                                }
                            ],
                            bind: {
                                store: '{sourceSystemWeight}'
                            },
                            listeners: {
                                render: 'onRenderBvrGridWeight'
                            }
                        }
                    ],
                    listeners: {
                        change: 'onSourceSystemWeightToggle'
                    }
                },
                {
                    xtype: 'disablefieldset',
                    reference: 'bvtFieldSet',
                    title: Unidata.i18n.t('admin.metamodel>byAttributes'),
                    checkboxToggle: true,
                    disableItems: false,
                    margin: '0 0 0 10',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    flex: 1,
                    bind: {
                        disabled: '{metaRecordViewReadOnly}'
                    },
                    items: [
                        {
                            xtype: 'combobox',
                            reference: 'unselectedBvtAttributeCombo',
                            fieldLabel: '',
                            emptyText: Unidata.i18n.t(
                                'admin.common>defaultSelect',
                                {entity: Unidata.i18n.t('glossary:attribute').toLowerCase()}
                            ),
                            editable: false,
                            displayField: 'displayName',
                            valueField: 'name',
                            queryMode: 'local',
                            bind: {
                                readOnly: '{metaRecordViewReadOnly}',
                                disabled: '{metaRecordViewReadOnly}'
                            },
                            store: {
                                fields: [
                                    'name',
                                    'displayName'
                                ],
                                data: []
                            },
                            listeners: {
                                select: 'onUnselectedAttributeComboSelect'
                            }
                        },
                        {
                            xtype: 'grid',
                            reference: 'bvtGridWeight',
                            plugins: {
                                ptype: 'cellediting',
                                clicksToEdit: 2,
                                listeners: {
                                    beforeedit: 'onAttributeWeightBeforeCellEdit',
                                    validateedit: 'onAttributeWeightCellValidateEdit',
                                    edit: 'onAttributeWeightCellEdit'
                                }
                            },
                            margin: '10 0 0 0',
                            scrollable: true,
                            columns: [],
                            flex: 1,
                            bind: {
                                store: '{attributeWeight}'
                            }
                        }
                    ],
                    listeners: {
                        change: 'onAttributeWeightToggle'
                    }
                }
            ]
        }
    ],

    listeners: {
        afterrender: 'onComponentAfterRender'
    }
});
