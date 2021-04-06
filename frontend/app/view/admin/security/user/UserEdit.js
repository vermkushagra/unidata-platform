Ext.define('Unidata.view.admin.security.user.UserEdit', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.security.useredit',

    viewModel: {
        type: 'admin.security.useredit'
    },
    controller: 'admin.security.useredit',

    requires: [
        'Unidata.view.admin.security.user.UserEditController',
        'Unidata.view.admin.security.user.UserEditModel',

        'Unidata.view.component.user.SecurityAttribute',
        'Unidata.view.component.user.Properties'
    ],

    bind: {
        title: '{title} : {currentUser.firstName:htmlEncode} {currentUser.lastName:htmlEncode}'
    },

    layout: 'fit',

    bodyBorder: false,

    referenceHolder: true,

    userGrid: null,

    items: [],

    cls: 'un-user-edit',

    rbar: [
        {
            xtype: 'button',
            ui: 'un-toolbar-admin',
            scale: 'small',
            iconCls: 'icon-floppy-disk',
            text: '',
            reference: 'saveButton',
            hidden: true,
            handler: 'onSaveClick'
        }
    ],

    listeners: {
        activate: 'onActivate'
    },

    initItems: function () {
        var currentUser;

        this.callParent(arguments);

        currentUser = this.getViewModel().get('currentUser');

        this.add([
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                flex: 1,
                cls: 'un-user-edit-container',
                scrollable: true,
                items: [
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        defaults: {
                            flex: 1
                        },
                        items: [
                            {
                                collapsible: false,
                                ui: 'un-card',
                                items: [
                                    {
                                        xtype: 'form',
                                        referenceHolder: true,
                                        reference: 'userForm',
                                        bodyPadding: 10,
                                        border: false,
                                        layout: {
                                            type: 'vbox',
                                            align: 'stretch'
                                        },
                                        fieldDefaults: {
                                            labelWidth: 200,
                                            msgTarget: 'side'
                                        },
                                        defaultType: 'textfield',
                                        items: [
                                            {
                                                xtype: 'checkbox',
                                                allowBlank: false,
                                                name: 'external',
                                                fieldLabel: Unidata.i18n.t('admin.security>external'),
                                                readOnly: true,
                                                bind: {
                                                    value: '{currentUser.external}',
                                                    readOnly: '{readOnly}'
                                                }
                                            },
                                            {
                                                xtype: 'container',
                                                layout: 'vbox',
                                                defaults: {
                                                    xtype: 'combobox',
                                                    displayField: 'description',
                                                    valueField: 'name',
                                                    queryMode: 'local',
                                                    editable: false,
                                                    disabled: true,
                                                    hidden: true,
                                                    labelAlign: 'left',
                                                    width: '100%',
                                                    bind: {
                                                        disabled: '{readOnly}',
                                                        hidden: '{!currentUser.external}',
                                                        store: '{authSources}'
                                                    },
                                                    triggers: {
                                                        reset: {
                                                            cls: 'x-form-clear-trigger',
                                                            handler: function () {
                                                                this.reset();
                                                                this.collapse();
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        fieldLabel: Unidata.i18n.t('admin.security>authorizationMethod'),
                                                        name: 'securityDataSource',
                                                        bind: {
                                                            value: '{currentUser.securityDataSource}'
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'checkbox',
                                                allowBlank: false,
                                                name: 'active',
                                                fieldLabel: Unidata.i18n.t('admin.security>active'),
                                                readOnly: true,
                                                bind: {
                                                    value: '{currentUser.active}',
                                                    readOnly: '{activeReadOnly}'
                                                }
                                            },
                                            {
                                                xtype: 'checkbox',
                                                allowBlank: false,
                                                name: 'admin',
                                                fieldLabel: Unidata.i18n.t('admin.security>admin'),
                                                readOnly: true,
                                                bind: {
                                                    value: '{currentUser.admin}',
                                                    readOnly: '{readOnly}'
                                                    //disabled: true
                                                }
                                            },
                                            {
                                                xtype: 'fieldcontainer',
                                                layout: 'hbox',
                                                fieldLabel: Unidata.i18n.t('admin.security>name') + '/' + Unidata.i18n.t('admin.security>lastName'),
                                                defaultType: 'textfield',
                                                defaults: {
                                                    allowBlank: false,
                                                    validateBlank: true
                                                },
                                                items: [
                                                    {
                                                        name: 'name',
                                                        flex: 2,
                                                        emptyText: Unidata.i18n.t('glossary:name'),
                                                        readOnly: true,
                                                        bind: {
                                                            value: '{currentUser.firstName}',
                                                            readOnly: '{readOnly}'
                                                        }
                                                    },
                                                    {
                                                        name: 'displayName',
                                                        flex: 3,
                                                        margin: '0 0 0 6',
                                                        emptyText: Unidata.i18n.t('admin.security>lastName'),
                                                        readOnly: true,
                                                        bind: {
                                                            value: '{currentUser.lastName}',
                                                            readOnly: '{readOnly}'
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                name: 'login',
                                                fieldLabel: Unidata.i18n.t('login>title'),
                                                modelValidation: true,
                                                readOnly: true,
                                                bind: {
                                                    value: '{currentUser.login}',
                                                    readOnly: '{loginReadOnly}'
                                                }
                                            },
                                            {
                                                name: 'email',
                                                vtype: 'email',
                                                fieldLabel: 'Email',
                                                readOnly: true,
                                                bind: {
                                                    value: '{currentUser.email}',
                                                    readOnly: '{readOnly}'
                                                }
                                            },
                                            {
                                                allowBlank: true,
                                                fieldLabel: Unidata.i18n.t('admin.security>newPassword'),
                                                name: 'pass',
                                                reference: 'refPass',
                                                emptyText: Unidata.i18n.t('login>password'),
                                                inputType: 'password',
                                                readOnly: true,
                                                bind: {
                                                    readOnly: '{readOnly}'
                                                },
                                                validator: function () {
                                                    var PasswordPolicy = Unidata.uiuserexit.overridable.security.PasswordPolicy,
                                                        password = this.up('form').lookupReference('refPass').getValue();

                                                    if (password !== '' && !PasswordPolicy.isPasswordSecuredDefault(password)) {
                                                        return PasswordPolicy.getUnsecuredMessageDefault(password);
                                                    }

                                                    return true;
                                                }
                                            },
                                            {
                                                allowBlank: true,
                                                fieldLabel: Unidata.i18n.t('admin.security>confirmPassword'),
                                                name: 'pass',
                                                reference: 'confirmPass',
                                                emptyText: Unidata.i18n.t('common:confirmation'),
                                                inputType: 'password',
                                                readOnly: true,
                                                bind: {
                                                    readOnly: '{readOnly}'
                                                },
                                                validator: function () {
                                                    var passValue = this.up('form').lookupReference('refPass').getValue(),
                                                        confirmValue = this.getValue();

                                                    if (confirmValue !== passValue) {
                                                        return Unidata.i18n.t('admin.security>passwordsNotMatch');
                                                    }

                                                    return true;
                                                }
                                            },
                                            {
                                                xtype: 'tagfield',
                                                fieldLabel: Unidata.i18n.t('admin.common>interface'),
                                                reference: 'userEndpoints',
                                                valueField: 'name',
                                                displayField: 'displayName',
                                                queryMode: 'local',
                                                value: currentUser.endpoints().getData().getValues('name', 'data'),
                                                readOnly: true,
                                                allowBlank: false,
                                                bind: {
                                                    store: '{endpoints}',
                                                    readOnly: '{readOnly}'
                                                },
                                                publishes: 'value',
                                                listeners: {
                                                    change: 'onEndpointsChange'
                                                }
                                            },
                                            {
                                                xtype: 'tagfield',
                                                fieldLabel: Unidata.i18n.t('glossary:roles'),
                                                reference: 'userRoles',
                                                valueField: 'name',
                                                displayField: 'displayName',
                                                queryMode: 'local',
                                                value: currentUser.get('roles'),
                                                readOnly: true,
                                                bind: {
                                                    value: '{currentUser.roles}',
                                                    store: '{roles}',
                                                    readOnly: '{readOnly}'
                                                },
                                                publishes: 'value',
                                                listeners: {
                                                    change: 'onRolesChange'
                                                }
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                xtype: 'user.properties',
                                reference: 'userPropertyPanel',
                                title: Unidata.i18n.t('admin.security>additionalUserPropertySettingsPanelTitle'),
                                collapsed: true,
                                readOnly: true,
                                bind: {
                                    readOnly: '{readOnly}',
                                    hidden: '{!propertiesIsVisible}'
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'container',
                        layout: 'hbox',
                        defaults: {
                            flex: 1
                        },
                        items: [
                            {
                                xtype: 'panel',
                                reference: 'securityLabelPanelUser',
                                title: Unidata.i18n.t('admin.security>userEdit.userSecurityLabels'),
                                ui: 'un-card',
                                collapsible: true,
                                attrLabelWidth: 150,
                                disabled: true,
                                bind: {
                                    disabled: '{readOnly}'
                                }
                            },
                            {
                                xtype: 'panel',
                                reference: 'securityLabelPanelRole',
                                title: Unidata.i18n.t('admin.security>userEdit.roleSecurityLabels'),
                                ui: 'un-card',
                                collapsible: true,
                                attrLabelWidth: 150
                            }
                        ]
                    }
                ]
            }
        ]);
    },

    isDirty: function () {
        var currentUser = this.getViewModel().get('currentUser');

        return currentUser.phantom || currentUser.dirty;
    },

    setReadOnly: function (readOnly) {
        var controller = this.getController();

        controller.setReadOnly(readOnly);
    }
});
