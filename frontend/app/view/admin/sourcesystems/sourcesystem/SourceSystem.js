Ext.define('Unidata.view.admin.sourcesystems.sourcesystem.SourceSystem', {
    extend: 'Ext.panel.Panel',

    viewModel: {
        type: 'admin.sourcesystems.sourcesystem'
    },
    controller: 'admin.sourcesystems.sourcesystem',

    requires: [
        'Unidata.view.admin.sourcesystems.sourcesystem.SourceSystemController',
        'Unidata.view.admin.sourcesystems.sourcesystem.SourceSystemModel'
    ],

    alias: 'widget.admin.sourcesystems.sourcesystem',
    cls: 'unidata-dashboard unidata-admin-sourcesystems-sourcesystem',

    config: {
        draftMode: null,
        readOnly: false
    },

    methodMapper: [
        {
            method: 'updateDraftMode'
        },
        {
            method: 'updateReadOnly'
        }
    ],

    bind: {
        title: '{tabName}'
    },

    sourceSystemList: null,

    initComponent: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.setDraftMode(DraftModeNotifier.getDraftMode());

        this.callParent(arguments);
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            cls: 'right-toolbar',
            dock: 'right',
            width: 45,
            defaults: {
                xtype: 'button',
                ui: 'un-toolbar-admin',
                scale: 'medium'
            },
            items: [
                {
                    handler: 'onSaveRecord',
                    tooltip: Unidata.i18n.t('common:save'),
                    hidden: true,
                    bind: {
                        hidden: '{!saveButtonVisible}'
                    },
                    iconCls: 'icon-floppy-disk'
                },
                '->',
                {
                    handler: 'onDeleteConfirmClick',
                    tooltip: Unidata.i18n.t('common:delete'),
                    margin: '0 0 80 0',
                    hidden: true, // удаление источников данных пока считает что недопустимо, но работает
                    iconCls: 'icon-trash2'
                }
            ]
        }
    ],

    items: [
        {
            xtype: 'container',
            layout: 'hbox',
            reference: 'sourceSystemMainPanel',
            referenceHolder: true,
            items: [
                {
                    xtype: 'container',
                    layout: 'vbox',
                    width: '70%',
                    items: [
                        {
                            xtype: 'container',
                            layout: 'vbox',
                            width: '100%',
                            cls: 'properties',
                            margin: '20 0 10 20',
                            items: [
                                {
                                    xtype: 'textfieldextended',
                                    reference: 'sourceSystemName',
                                    width: '100%',
                                    bind: {
                                        value: '{currentRecord.name}',
                                        readOnly: '{!canEditId}',
                                        hideTrigger: '{!canEdit}'
                                    },
                                    fieldLabel: Unidata.i18n.t('glossary:name'),
                                    modelValidation: true,
                                    msgTarget: 'under'
                                },
                                {
                                    xtype: 'textareaextended',
                                    reference: 'sourceSystemDescription',
                                    width: '100%',
                                    bind: {
                                        value: '{currentRecord.description}',
                                        readOnly: '{!canEdit}',
                                        hideTrigger: '{!canEdit}'
                                    },
                                    fieldLabel: Unidata.i18n.t('glossary:description')
                                },
                                {
                                    xtype: 'textfieldextended',
                                    reference: 'sourceSystemWeight',
                                    width: '100%',
                                    bind: {
                                        value: '{currentRecord.weight}',
                                        readOnly: '{!canEdit}',
                                        hideTrigger: '{!canEdit}'
                                    },
                                    maskRe: /[0-9]+/,
                                    fieldLabel: Unidata.i18n.t('admin.sourcesystems>weight'),
                                    listeners: {
                                        paste: {
                                            element: 'inputEl',
                                            fn: function (event) {
                                                if (event.type === 'paste') {
                                                    event.stopPropagation();
                                                    event.preventDefault();

                                                    return false;
                                                }
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: 'keyvalue.input',
                                    fieldLabel: Unidata.i18n.t('admin.metamodel>customProperties'),
                                    name: 'customProperties',
                                    width: '100%',
                                    bind: {
                                        gridStore: '{currentRecord.customProperties}',
                                        readOnly: '{!canEdit}',
                                        hideTrigger: '{!canEdit}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
