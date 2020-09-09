/**
 * Экран редактирования роли
 *
 * @author Ivan Marshalkin
 * @date 2017-02-03
 */
Ext.define('Unidata.view.admin.security.role.RoleEdit', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.security.roleedit',

    viewModel: {
        type: 'admin.security.roleedit'
    },
    controller: 'admin.security.roleedit',

    requires: [
        'Unidata.view.component.role.SecurityAttribute',
        'Unidata.view.admin.security.role.RoleEditController',
        'Unidata.view.admin.security.role.RoleEditModel',

        'Unidata.view.component.user.Properties'
    ],

    bind: {
        title: '{title} : {currentRole.displayName:htmlEncode}'
    },

    referenceHolder: true,

    config: {
        role: null
    },

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    methodMapper: [
        {
            method: 'displayRoleEditor'
        },
        {
            method: 'getCheckRightCellClass'
        },
        {
            method: 'getCheckRightCellTooltip'
        },
        {
            method: 'updateRole'
        }
    ],

    cls: 'un-role-edit',

    layout: 'fit',
    bodyBorder: false,

    treePanel: null,

    statics: {
        /**
         * Направление связи/ссылки между реестрами/справочниками
         */
        EDGE_DIRECTION: {
            INBOUND: 'INBOUND',
            OUTBOUND: 'OUTBOUND'
        }
    },

    items: [],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
    },

    initReferences: function () {
        this.treePanel = this.lookupReference('treepanel');
        this.aggregatedCounts = this.lookupReference('aggregatedCounts');
    },

    initItems: function () {
        var view = this;

        this.callParent(arguments);

        this.add([
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                flex: 1,
                scrollable: true,
                items: [
                    {
                        flex: 1,
                        region: 'bottom',
                        reference: 'rolePanel',
                        cls: 'un-role-panel',
                        hidden: true,
                        rbar: [
                            {
                                xtype: 'button',
                                ui: 'un-toolbar-admin',
                                scale: 'small',
                                iconCls: 'icon-floppy-disk',
                                reference: 'saveButton',
                                handler: 'onSaveClick',
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
                                handler: 'onDeleteClick',
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
                                xtype: 'container',
                                layout: {
                                    type: 'hbox',
                                    align: 'stretch'
                                },
                                items: [
                                    {
                                        xtype: 'form',
                                        reference: 'roleForm',
                                        ui: 'un-card',
                                        padding: 5,
                                        margin: '0 10 10 0',
                                        items: [
                                            {
                                                xtype: 'fieldcontainer',
                                                layout: 'hbox',
                                                fieldLabel: Unidata.i18n.t('admin.security>roleName'),
                                                defaultType: 'textfield',
                                                items: [
                                                    {
                                                        name: 'name',
                                                        emptyText: Unidata.i18n.t('glossary:boolean'),
                                                        flex: 2,
                                                        modelValidation: true,
                                                        bind: {
                                                            value: '{currentRole.name}',
                                                            editable: '{nameEditable}'
                                                        }
                                                    },
                                                    {
                                                        name: 'displayName',
                                                        emptyText: Unidata.i18n.t('admin.common>displayed'),
                                                        flex: 3,
                                                        margin: '0 0 0 6',
                                                        modelValidation: true,
                                                        bind: {
                                                            value: '{currentRole.displayName}',
                                                            editable: '{!readOnly}'
                                                        }
                                                    }
                                                ]
                                            }
                                        ],
                                        flex: 1
                                    },
                                    {
                                        xtype: 'container',
                                        reference: 'aggregatedCounts',
                                        padding: '8 13 8 13',
                                        width: 500,
                                        tpl: [
                                            '<tpl if="warningCount">',
                                            '<span class="un-right-msg-warning">',
                                            Unidata.i18n.t('admin.security>unapprovedResourceCounter'),
                                            '</span>',
                                            '</tpl>'
                                        ]
                                    }
                                ]
                            },
                            {
                                xtype: 'user.properties',
                                reference: 'rolePropertyPanel',
                                title: Unidata.i18n.t('admin.security>additionalRolePropertySettingsPanelTitle'),
                                margin: '20 0',
                                collapsed: true,
                                readOnly: true,
                                ui: 'un-card',
                                bind: {
                                    readOnly: '{readOnly}',
                                    hidden: '{!propertiesIsVisible}'
                                }
                            },
                            {
                                xtype: 'panel',
                                flex: 1,
                                layout: {
                                    type: 'hbox',
                                    align: 'stretch'
                                },
                                cls: 'un-role-panel-inner',
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        reference: 'treepanel',
                                        bind: {
                                            store: '{treeGroupedStore}'
                                        },
                                        flex: 1,
                                        rootVisible: false,
                                        ui: 'un-card',
                                        animate: false,
                                        listeners: {
                                            beforeitemdblclick: function () {
                                                return false;
                                            }
                                        },
                                        lbar: [
                                            {
                                                xtype: 'button',
                                                ui: 'un-toolbar-admin',
                                                scale: 'small',
                                                iconCls: 'icon-expand2',
                                                tooltip: Unidata.i18n.t('admin.security>expandAll'),
                                                handler: 'onExpandAllRightNode'
                                            },
                                            {
                                                xtype: 'button',
                                                ui: 'un-toolbar-admin',
                                                scale: 'small',
                                                iconCls: 'icon-contract2',
                                                tooltip: Unidata.i18n.t('admin.security>collapseAll'),
                                                handler: 'onCollapseAllRightNode'
                                            }
                                        ],
                                        columns: {
                                            defaults: {
                                                sortable: false,
                                                resizable: false,
                                                menuDisabled: true,
                                                flex: 1
                                            },
                                            items: [
                                                {
                                                    xtype: 'treecolumn',
                                                    text: Unidata.i18n.t('admin.security>resourceName'),
                                                    dataIndex: 'displayName',
                                                    resizable: true,
                                                    width: 350,
                                                    flex: null,
                                                    renderer: function (value, metaData, record) {
                                                        var nodeType    = record.get('nodeType'),
                                                            displayText = Ext.htmlEncode(value);

                                                        if (nodeType === 'SECURED_RESOURCE_NODE') {
                                                            if (record.get('isEntity')) {
                                                                return '<span class="fa un-tree-icon fa-book"></span> ' + displayText;
                                                            }

                                                            if (record.get('record').get('isSystemResource')) {
                                                                return '<b>' + displayText + '</b>';
                                                            }
                                                        } else if (nodeType === 'CATALOG_NODE') {
                                                            return '<i>' + displayText + '</i>';
                                                        }

                                                        return displayText;
                                                    }
                                                },
                                                {
                                                    xtype: 'checkcolumn',
                                                    header: Unidata.i18n.t('admin.security>full'),
                                                    dataIndex: 'full',
                                                    listeners: {
                                                        checkchange: 'onCheckChangeColumn',
                                                        beforecheckchange: 'onBeforeCheckChangeColumn'
                                                    },
                                                    bind: {
                                                        disabled: '{readOnly}'
                                                    },
                                                    renderer: function (value, metaData, record) {
                                                        var nodeType = record.get('nodeType'),
                                                            securedResourceName;

                                                        if (nodeType !== 'SECURED_RESOURCE_NODE') {
                                                            return '';
                                                        } else {
                                                            securedResourceName = record.get('record').get('name');
                                                        }

                                                        if (securedResourceName === 'BULK_OPERATIONS_OPERATOR') {
                                                            return '';
                                                        }

                                                        return this.defaultRenderer(value, metaData);
                                                    }
                                                },
                                                {
                                                    xtype: 'checkcolumn',
                                                    header: Unidata.i18n.t('admin.security>create'),
                                                    dataIndex: 'create',
                                                    listeners: {
                                                        checkchange: 'onCheckChangeColumn',
                                                        beforecheckchange: 'onBeforeCheckChangeColumn'
                                                    },
                                                    bind: {
                                                        disabled: '{readOnly}'
                                                    },
                                                    renderer: function (value, metaData, record) {
                                                        var nodeType = record.get('nodeType'),
                                                            securedResourceName,
                                                            tooltip;

                                                        if (nodeType !== 'SECURED_RESOURCE_NODE') {
                                                            return '';
                                                        } else {
                                                            securedResourceName = record.get('record').get('name');
                                                        }

                                                        if (securedResourceName === 'BULK_OPERATIONS_OPERATOR') {
                                                            return '';
                                                        }

                                                        tooltip = view.getCheckRightCellTooltip(record, 'create');
                                                        metaData.tdCls = view.getCheckRightCellClass(record, 'create');
                                                        metaData.tdAttr = 'data-qtip="' + tooltip + '"';

                                                        return this.defaultRenderer(value, metaData);
                                                    }
                                                },
                                                {
                                                    xtype: 'checkcolumn',
                                                    header: Unidata.i18n.t('admin.security>read'),
                                                    dataIndex: 'read',
                                                    listeners: {
                                                        checkchange: 'onCheckChangeColumn',
                                                        beforecheckchange: 'onBeforeCheckChangeColumn'
                                                    },
                                                    bind: {
                                                        disabled: '{readOnly}'
                                                    },
                                                    renderer: function (value, metaData, record) {
                                                        var nodeType = record.get('nodeType'),
                                                            tooltip;

                                                        if (nodeType !== 'SECURED_RESOURCE_NODE') {
                                                            return '';
                                                        }

                                                        tooltip = view.getCheckRightCellTooltip(record, 'read');
                                                        metaData.tdCls = view.getCheckRightCellClass(record, 'read');
                                                        metaData.tdAttr = 'data-qtip="' + tooltip + '"';

                                                        return this.defaultRenderer(value, metaData);
                                                    }
                                                },
                                                {
                                                    xtype: 'checkcolumn',
                                                    header: Unidata.i18n.t('admin.security>update'),
                                                    dataIndex: 'update',
                                                    listeners: {
                                                        checkchange: 'onCheckChangeColumn',
                                                        beforecheckchange: 'onBeforeCheckChangeColumn'
                                                    },
                                                    bind: {
                                                        disabled: '{readOnly}'
                                                    },
                                                    renderer: function (value, metaData, record) {
                                                        var nodeType = record.get('nodeType'),
                                                            securedResourceName,
                                                            tooltip;

                                                        if (nodeType !== 'SECURED_RESOURCE_NODE') {
                                                            return '';
                                                        } else {
                                                            securedResourceName = record.get('record').get('name');
                                                        }

                                                        if (securedResourceName === 'BULK_OPERATIONS_OPERATOR') {
                                                            return '';
                                                        }

                                                        tooltip = view.getCheckRightCellTooltip(record, 'update');
                                                        metaData.tdCls = view.getCheckRightCellClass(record, 'update');
                                                        metaData.tdAttr = 'data-qtip="' + tooltip + '"';

                                                        return this.defaultRenderer(value, metaData);
                                                    }
                                                },
                                                {
                                                    xtype: 'checkcolumn',
                                                    header: Unidata.i18n.t('admin.security>delete'),
                                                    dataIndex: 'delete',
                                                    listeners: {
                                                        checkchange: 'onCheckChangeColumn',
                                                        beforecheckchange: 'onBeforeCheckChangeColumn'
                                                    },
                                                    bind: {
                                                        disabled: '{readOnly}'
                                                    },
                                                    renderer: function (value, metaData, record) {
                                                        var nodeType = record.get('nodeType'),
                                                            securedResourceName,
                                                            tooltip;

                                                        if (nodeType !== 'SECURED_RESOURCE_NODE') {
                                                            return '';
                                                        } else {
                                                            securedResourceName = record.get('record').get('name');
                                                        }

                                                        if (securedResourceName === 'BULK_OPERATIONS_OPERATOR') {
                                                            return '';
                                                        }

                                                        tooltip = view.getCheckRightCellTooltip(record, 'delete');
                                                        metaData.tdCls = view.getCheckRightCellClass(record, 'delete');
                                                        metaData.tdAttr = 'data-qtip="' + tooltip + '"';

                                                        return this.defaultRenderer(value, metaData);
                                                    }
                                                }
                                            ]
                                        }
                                    },
                                    {
                                        xtype: 'panel',
                                        ui: 'un-card',
                                        width: 500,
                                        bodyPadding: '0 10 0 10',
                                        margin: '0 0 0 10',
                                        scrollable: true,
                                        title: Unidata.i18n.t('glossary:securityLabel'),
                                        reference: 'securityLabelAttributes'
                                    }
                                ]
                            }]

                    }
                ]
            }
        ]);
    },

    isDirty: function () {
        var controller = this.getController();

        return controller.isCurrentRoleDirty();
    }
});
