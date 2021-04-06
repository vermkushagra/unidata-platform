/**
 * Экран редактирования ролей
 *
 * @author unidata team
 * @date 2015
 */

Ext.define('Unidata.view.admin.security.role.Role', {
    extend: 'Ext.Container',

    viewModel: {
        type: 'admin.security.role'
    },
    controller: 'admin.security.role',

    requires: [
        'Unidata.view.admin.security.role.RoleController',
        'Unidata.view.admin.security.role.RoleModel',

        'Unidata.view.admin.security.role.RoleAdditionalPropertySettings'
    ],

    alias: 'widget.admin.security.role',

    layout: {
        type: 'border',
        align: 'stretch'
    },

    cls: 'un-security-role',

    roleLoadingErrorText: Unidata.i18n.t('admin.security>loadRoleError'),

    referenceHolder: true,

    items: [],

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'panel',
                ui: 'un-result',
                title: Unidata.i18n.t('glossary:roles'),
                region: 'west',
                collapsible: true,
                collapseDirection: 'left',
                animCollapse: false,
                titleCollapse: true,
                split: true,
                overflowY: 'auto',
                width: 270,
                tools: [
                    {
                        type: 'plus',
                        handler: 'onAddRoleButtonClick',
                        tooltip: Unidata.i18n.t('admin.security>addNewRole'),
                        securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                        securedEvent: 'create'
                    },
                    {
                        type: 'gear',
                        handler: 'onRolePropertiesSettingsClick',
                        tooltip: Unidata.i18n.t('admin.security>additionalRolePropertySettingsEditButtonTooltip'),
                        securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                        securedEvent: 'read'
                    }
                ],
                items: [{
                    xtype: 'grid',
                    reference: 'rolesGrid',
                    cls: 'un-result-grid',
                    hideHeaders: true,
                    listeners: {
                        itemclick: 'onRolesGridItemClick'
                    },
                    disableSelection: true,
                    bind: {
                        store: '{roles}'
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
                }]
            },
            {
                xtype: 'tabpanel',
                reference: 'roleTabPanel',
                referenceHolder: true,
                region: 'center',
                maxTabWidth: 250,
                defaults: {
                    bodyPadding: 0,
                    closable: true
                },
                ui: 'un-content'
            }
        ]);
    },

    isDirty: function () {
        var controller = this.getController(),
            roleTabs = controller.getOpenedRoleTabs(),
            anyRoleDirty;

        anyRoleDirty = Ext.Array.some(roleTabs, function (roleTab) {
            return roleTab.isDirty();
        });

        return anyRoleDirty;
    }
});
