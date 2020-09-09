Ext.define('Unidata.view.admin.security.label.Label', {
    extend: 'Ext.Container',

    viewModel: {
        type: 'admin.security.label'
    },
    controller: 'admin.security.label',

    requires: [
        'Unidata.view.admin.security.label.LabelController',
        'Unidata.view.admin.security.label.LabelModel'
    ],

    alias: 'widget.admin.security.label',

    layout: {
        type: 'border',
        align: 'stretch'
    },

    referenceHolder: true,

    items: [
        {
            xtype: 'panel',
            ui: 'un-result',
            width: 270,
            collapsible: true,
            split: true,
            region: 'west',
            overflowY: 'auto',
            title: Unidata.i18n.t('glossary:securityLabel'),
            tools: [
                {
                    type: 'plus',
                    handler: 'onAddSecurityLabelClick',
                    tooltip: Unidata.i18n.t('admin.security>addNewLabel'),
                    securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                    securedEvent: 'create'
                }
            ],
            items: [
                {
                    xtype: 'grid',
                    reference: 'securityLabelsGrid',
                    cls: 'un-result-grid',
                    hideHeaders: true,
                    listeners: {
                        select: 'onSelectSecurityLabel',
                        deselect: 'onDeselectSecurityLabel'
                    },
                    bind: {
                        store: '{securityLabels}'
                    },
                    columns: [
                        {
                            flex: 1,
                            text: Unidata.i18n.t('glossary:name'),
                            sortable: true,
                            resizable: true,
                            hideable: false,
                            menuDisabled: true,
                            dataIndex: 'displayName',
                            disableBindUpdate: true
                        }
                    ]
                }
            ]
        },
        {
            flex: 5,
            region: 'center',
            reference: 'securityLabelPanel',
            ui: 'un-content',
            hidden: true,
            rbar: [
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-floppy-disk',
                    reference: 'saveButton',
                    listeners: {
                        click: 'onSaveClick'
                    },
                    bind: {
                        hidden: '{readOnly}'
                    }
                },
                '->',
                {
                    xtype: 'button',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-trash2',
                    tooltip: Unidata.i18n.t('common:delete'),
                    margin: '0 0 80 0',
                    listeners: {
                        click: 'onDeleteClick'
                    },
                    securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                    securedEvent: 'delete'
                }
            ],
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            bodyPadding: 10,
            items: [
                {
                    xtype: 'form',
                    flex: 1,
                    reference: 'labelForm',
                    ui: 'un-card',
                    maxWidth: 700,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    defaults: {
                        labelWidth: 150
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            reference: 'labelName',
                            fieldLabel: Unidata.i18n.t('admin.security>labelName'),
                            emptyText: Unidata.i18n.t('admin.security>labelName'),
                            bind: {
                                value: '{currentSecurityLabel.name}',
                                editable: '{!nameReadOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            flex: 0
                        },
                        {
                            xtype: 'textfield',
                            reference: 'labelDisplayName',
                            fieldLabel: Unidata.i18n.t('glossary:displayName'),
                            emptyText: Unidata.i18n.t('admin.security>displayLabelName'),
                            bind: {
                                value: '{currentSecurityLabel.displayName}',
                                editable: '{!readOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            allowBlank: false,
                            validateBlank: true,
                            flex: 0
                        },
                        {
                            xtype: 'textarea',
                            reference: 'labelDescription',
                            fieldLabel: Unidata.i18n.t('glossary:description'),
                            emptyText: Unidata.i18n.t('glossary:description'),
                            bind: {
                                value: '{currentSecurityLabel.description}',
                                editable: '{!readOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            allowBlank: true,
                            flex: 0
                        },
                        {
                            xtype: 'un.entitycombo',
                            fieldLabel: Unidata.i18n.t('glossary:selectEntityOrLookupEntity'),
                            reference: 'toEntity',
                            autoSelect: true,
                            allowBlank: false,
                            validateBlank: true,
                            matchFieldWidth: true,
                            emptyText: Unidata.i18n.t('glossary:selectEntityOrLookupEntity'),
                            bind: {
                                editable: '{!readOnly}'
                            },
                            modelValidation: true,
                            flex: 0,
                            listeners: {
                                select: 'onSelectRecord'
                            }
                        },
                        {
                            xtype: 'panel',
                            flex: 1,
                            margin: '10 0 0 0',

                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            tbar: [
                                {
                                    xtype: 'button',
                                    ui: 'un-toolbar-admin',
                                    scale: 'small',
                                    iconCls: 'icon-plus',
                                    listeners: {
                                        click: 'onAttributeAddClick'
                                    },
                                    tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:attribute')}),
                                    bind: {
                                        disabled: '{readOnly}'
                                    }
                                }
                            ],
                            items: [
                                {
                                    flex: 1,
                                    scrollable: 'vertical',
                                    xtype: 'container',
                                    reference: 'attributeContainer',
                                    layout: {
                                        type: 'vbox',
                                        align: 'stretch'
                                    },
                                    items: [
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
