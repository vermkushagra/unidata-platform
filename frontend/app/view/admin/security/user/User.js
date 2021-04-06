Ext.define('Unidata.view.admin.security.user.User', {
    extend: 'Ext.Container',

    alias: 'widget.admin.security.user',

    viewModel: {
        type: 'admin.security.user'
    },
    controller: 'admin.security.user',

    requires: [
        'Unidata.view.admin.security.user.UserController',
        'Unidata.view.admin.security.user.UserModel',

        'Unidata.view.component.user.Properties',
        'Unidata.view.admin.security.user.UserEdit',

        'Unidata.view.admin.security.user.UserAdditionalPropertySettings'
    ],

    layout: {
        type: 'border',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'panel',
            region: 'west',
            collapsible: true,
            collapseDirection: 'left',
            animCollapse: false,
            titleCollapse: true,
            title: Unidata.i18n.t('glossary:users'),
            split: true,
            ui: 'un-result',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            tools: [
                {
                    type: 'plus',
                    handler: 'onAddUserButtonClick',
                    tooltip: Unidata.i18n.t('admin.security>addUser'),
                    securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                    securedEvent: 'create'
                },
                {
                    type: 'gear',
                    handler: 'onUserPropertiesSettingsClick',
                    tooltip: Unidata.i18n.t('admin.security>additionalUserPropertySettingsEditButtonTooltip'),
                    securedResource: 'ADMIN_SYSTEM_MANAGEMENT',
                    securedEvent: 'read'
                }
            ],
            items: [
                {
                    xtype: 'container',
                    cls: 'un-query-pinned-sections',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [{
                        xtype: 'combo',
                        reference: 'comboActiveFilter',
                        store: [
                          ['all', Unidata.i18n.t('admin.job>all')],
                          ['active', Unidata.i18n.t('admin.job>actives')],
                          ['inactive', Unidata.i18n.t('admin.job>inactives')]
                        ],
                        value: 'all',
                        region: 'south',
                        editable: false,
                        listeners: {
                            select: 'onActiveFilterSelect'
                        },
                        ui: 'un-field-default'
                    }]
                },
                {
                    xtype: 'grid',
                    reference: 'userGrid',
                    width: 270,
                    overflowY: 'auto',
                    flex: 1,
                    cls: 'un-result-grid',
                    listeners: {
                        select: 'onSelectUser'
                    },
                    bind: {
                        store: '{users}'
                    },
                    viewConfig: {
                        getRowClass: function (record) {
                            var cls = '';

                            if (!record.get('active')) {
                                cls = 'opacity_5';
                            }

                            return cls;
                        }
                    },
                    columns: [
                        {
                            flex: 1,
                            text: Unidata.i18n.t('glossary:name'),
                            sortable: false,
                            resizable: true,
                            hideable: false,
                            menuDisabled: true,
                            dataIndex: 'firstName',
                            bind: '{theUser.firstName}',
                            disableBindUpdate: true
                        },
                        {
                            flex: 1,
                            sortable: true,
                            resizable: true,
                            hideable: false,
                            menuDisabled: true,
                            text: Unidata.i18n.t('admin.security>lastName'),
                            dataIndex: 'lastName',
                            disableBindUpdate: true
                        },
                        {
                            flex: 1,
                            sortable: true,
                            resizable: true,
                            hideable: false,
                            menuDisabled: true,
                            text: Unidata.i18n.t('login>title'),
                            dataIndex: 'login',
                            disableBindUpdate: true
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'tabpanel',
            referenceHolder: true,
            reference: 'userTabPanel',
            region: 'center',
            maxTabWidth: 250,
            ui: 'un-content',
            defaults: {
                bodyPadding: 0,
                closable: true
            }
        }
    ],

    isDirty: function () {
        var userTabPanel = this.lookupReference('userTabPanel'),
            isDirty = false;

        userTabPanel.items.each(function (item) {
            if (item.isDirty && item.isDirty()) {
                isDirty = true;

                return false;
            }
        });

        return isDirty;
    }

});
