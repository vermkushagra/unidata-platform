Ext.define('Unidata.view.login.password.change.ChangePassword', {
    extend: 'Ext.container.Viewport',

    requires: [
        'Unidata.view.login.password.change.ChangePasswordController'
    ],

    alias: 'widget.changepassword',

    controller: 'changepassword',

    referenceHolder: true,

    changePasswordForm: null,                                         // ссылка на форму смена пароля
    oldPassword: null,                                                // поле ввода для текущего пароля
    newPassword: null,                                                // поле ввода для нового пароля
    newPasswordConfirm: null,                                         // поле ввода для подтверждения нового пароля

    layout: 'center',

    initComponent: function () {
        var UiUeChangePassword = Unidata.uiuserexit.overridable.authorization.ChangePassword;

        this.callParent(arguments);

        this.addCls(UiUeChangePassword.getBackgroundCls());

        this.initComponentReference();

        this.on('render', this.onComponentRender, this, {single: true});
    },

    initComponentReference: function () {
        var me = this;

        me.changePasswordForm = me.lookupReference('changePasswordForm');
        me.oldPassword = me.lookupReference('oldPassword');
        me.newPassword = me.lookupReference('newPassword');
        me.newPasswordConfirm = me.lookupReference('newPasswordConfirm');
    },

    onDestroy: function () {
        var me = this,
            UiUeChangePassword = Unidata.uiuserexit.overridable.authorization.ChangePassword;

        // при уничтожении viewport классы остаются у <body> поэтому удаляем ручками
        this.removeCls(UiUeChangePassword.getBackgroundCls());

        me.changePasswordForm = null;
        me.oldPassword = null;
        me.newPassword = null;
        me.newPasswordConfirm = null;

        this.callParent(arguments);
    },

    onComponentRender: function () {
        this.oldPassword.focus(true, 500);
    },

    items: [
        {
            xtype: 'form',
            cls: 'un-login-form un-animation-fadeinup',
            reference: 'changePasswordForm',
            height: 280,
            width: 280,
            title: Unidata.i18n.t('login>password.changeTitle'),
            items: [
                {
                    xtype: 'container',
                    cls: 'un-login-form-input-container',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    defaultType: 'textfield',
                    items: [
                        {
                            reference: 'oldPassword',
                            cls: 'un-login-form-password-input',
                            allowBlank: false,
                            name: 'old_password',
                            emptyText: Unidata.i18n.t('login>password.oldPassword'),
                            msgTarget: 'none',
                            inputType: 'password',
                            enableKeyEvents: true,
                            listeners: {
                                specialkey: 'onSpecialKeyOldPasswordInput'
                            }
                        },
                        {
                            reference: 'newPassword',
                            cls: 'un-login-form-password-input',
                            allowBlank: false,
                            name: 'new_password',
                            emptyText: Unidata.i18n.t('login>password.newPassword'),
                            msgTarget: 'none',
                            inputType: 'password',
                            enableKeyEvents: true,
                            listeners: {
                                specialkey: 'onSpecialKeyNewPasswordInput'
                            }
                        },
                        {
                            reference: 'newPasswordConfirm',
                            cls: 'un-login-form-password-input',
                            allowBlank: false,
                            name: 'new_password_confirm',
                            emptyText: Unidata.i18n.t('login>password.confirmNewPassword'),
                            msgTarget: 'none',
                            inputType: 'password',
                            enableKeyEvents: true,
                            listeners: {
                                specialkey: 'onSpecialKeyNewPasswordConfirmInput'
                            }
                        },
                        {
                            xtype: 'container',
                            layout: {
                                type: 'hbox',
                                align: 'stretch'
                            },
                            cls: 'un-login-form-button-container',
                            items: [
                                {
                                    formBind: false,
                                    xtype: 'button',
                                    color: 'transparent',
                                    text: Unidata.i18n.t('common:cancel'),
                                    scale: 'large',
                                    handler: 'onBackClick',
                                    flex: 1
                                },
                                {
                                    formBind: true,
                                    xtype: 'button',
                                    text: Unidata.i18n.t('login>password.changeButton'),
                                    name: 'login',
                                    scale: 'large',
                                    handler: 'onChangeClick',
                                    flex: 1
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]

});
