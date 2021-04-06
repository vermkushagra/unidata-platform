Ext.define('Unidata.view.login.password.reset.ResetPassword', {
    extend: 'Ext.container.Viewport',

    alias: 'widget.resetpassword',

    requires: [
        'Unidata.view.login.password.reset.ResetPasswordController'
    ],

    controller: 'resetpassword',

    referenceHolder: true,

    resetPasswordForm: null,                                         // ссылка на форму смена пароля

    layout: 'center',

    initComponent: function () {
        var UiUeResetPassword = Unidata.uiuserexit.overridable.authorization.ResetPassword;

        this.callParent(arguments);

        this.addCls(UiUeResetPassword.getBackgroundCls());

        this.initComponentReference();

        this.on('render', this.onComponentRender, this, {single: true});
    },

    initComponentReference: function () {
        var me = this;

        me.resetPasswordForm = me.lookupReference('resetPasswordForm');
        me.newPassword = me.lookupReference('newPassword');
        me.newPasswordConfirm = me.lookupReference('newPasswordConfirm');
    },

    onDestroy: function () {
        var me = this,
            UiUeResetPassword = Unidata.uiuserexit.overridable.authorization.ResetPassword;

        // при уничтожении viewport классы остаются у <body> поэтому удаляем ручками
        this.removeCls(UiUeResetPassword.getBackgroundCls());

        me.resetPasswordForm = null;
        me.newPassword = null;
        me.newPasswordConfirm = null;

        this.callParent(arguments);
    },

    onComponentRender: function () {
        this.newPassword.focus(true, 500);
    },

    items: [
        {
            xtype: 'form',
            cls: 'un-login-form un-animation-fadeinup',
            reference: 'resetPasswordForm',
            height: 230,
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
                                    xtype: 'button',
                                    text: Unidata.i18n.t('common:cancel'),
                                    name: 'logout',
                                    color: 'transparent',
                                    flex: 1,
                                    scale: 'medium',
                                    handler: 'onLogoutClick'
                                },
                                {
                                    formBind: true,
                                    xtype: 'button',
                                    text: Unidata.i18n.t('login>password.changeButton'),
                                    flex: 1,
                                    name: 'login',
                                    scale: 'medium',
                                    handler: 'onResetClick'
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
