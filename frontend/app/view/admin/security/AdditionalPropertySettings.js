/**
 * @author Aleksandr Bavin
 * @date 2016-07-08
 */
Ext.define('Unidata.view.admin.security.AdditionalPropertySettings', {
    extend: 'Ext.panel.Panel',

    required: [
        'Unidata.view.admin.security.AdditionalPropertySettingsController',
        'Unidata.view.admin.security.AdditionalPropertySettingsModel'
    ],

    alias: 'widget.admin.security.additionalpropertysettings',

    viewModel: {
        type: 'additionalpropertysettings'
    },

    controller: 'additionalpropertysettings',

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    scrollable: true,

    referenceHolder: true,

    config: {
        readOnly: false
    },

    items: [
        {
            xtype: 'grid',
            reference: 'settingsGrid',

            plugins: {
                ptype: 'cellediting',
                clicksToEdit: 1,
                listeners: {
                    beforeedit: 'onBeforeedit'
                }
            },

            columns: {
                defaults: {
                    sortable: false,
                    hideable: false,
                    flex: 1
                },
                items: [
                    {
                        xtype: 'widgetcolumn',
                        width: 40,
                        flex: null,
                        hidden: true,
                        bind: {
                            hidden: '{!showDeleteColumn}'
                        },
                        onWidgetAttach: function (column, widget, record) {
                            if (Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'delete')) {
                                return;
                            }

                            if (!record.phantom && !Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'edit')) {
                                widget.setHidden(true);
                            }

                            if (record.phantom && !Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'create')) {
                                widget.setHidden(true);
                            }
                        },
                        widget: {
                            xtype: 'button',
                            ui: 'un-toolbar-admin',
                            scale: 'small',
                            iconCls: 'icon-trash2',
                            tooltip: Unidata.i18n.t('common:delete'),
                            handler: function (button) {
                                var grid   = this.up('grid'),
                                    store  = grid.getStore(),
                                    record = button.getWidgetRecord();

                                store.remove(record);
                            }
                        }
                    },
                    {
                        text: Unidata.i18n.t('glossary:name'),
                        dataIndex: 'name',
                        editor: {
                            xtype: 'textfield',
                            emptyText: Unidata.i18n.t('glossary:name'),
                            allowBlank: false
                        }
                    },
                    {
                        text: Unidata.i18n.t('glossary:displayName'),
                        dataIndex: 'displayName',
                        editor: {
                            xtype: 'textfield',
                            emptyText: Unidata.i18n.t('glossary:displayName'),
                            allowBlank: false
                        }
                    }
                ]
            },
            bind: {
                store: '{additionalProperties}'
            }
        }
    ],

    dockedItems: [{
        xtype: 'toolbar',
        reference: 'toolbar',
        dock: 'right',
        items: [
            {
                text: '',
                tooltip: Unidata.i18n.t('common:save'),
                hidden: true,
                bind: {
                    hidden: '{readOnlyViewer}'
                },
                iconCls: 'icon-floppy-disk',
                ui: 'un-toolbar-admin',
                scale: 'medium',
                handler: 'onSaveClick'
            },
            {
                text: '',
                tooltip: Unidata.i18n.t('common:add'),
                hidden: true,
                bind: {
                    hidden: '{!canCreate}'
                },
                iconCls: 'icon-plus',
                ui: 'un-toolbar-admin',
                scale: 'medium',
                handler: 'onAddClick'
            }
        ]
    }],

    isDirty: function () {
        var additionalProperties = this.getViewModel().get('additionalProperties'),
            hasUpdated = additionalProperties.getUpdatedRecords().length != 0,
            hasNew = additionalProperties.getNewRecords().length != 0;

        return hasUpdated || hasNew;
    }

});
