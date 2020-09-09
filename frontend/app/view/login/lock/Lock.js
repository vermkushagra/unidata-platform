Ext.define('Unidata.view.login.lock.Lock', {
    extend: 'Ext.container.Container',

    alias: 'widget.lock',

    requires: [
        'Unidata.view.login.lock.LockController'
    ],

    cls: 'un-lockscreen-background',

    controller: 'lock',

    referenceHolder: true,

    loginInput: null,                                        // ссылка на поле ввода логина
    passwordInput: null,                                     // ссылка на поле ввода пароля
    loginForm: null,                                         // ссылка на форму входа

    layout: 'absolute',

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        var me = this;

        me.loginInput = me.lookupReference('loginInput');
        me.passwordInput = me.lookupReference('passwordInput');
        me.loginForm = me.lookupReference('loginForm');
    },

    initComponentEvent: function () {
        var controller = this.getController();

        this.on('show', controller.onShowLockScreen, controller);
        this.on('hide', controller.onHideLockScreen, controller);
    },

    onDestroy: function () {
        var me = this;

        me.loginInput = null;
        me.passwordInput = null;
        me.loginForm = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'form',
            cls: 'un-login-form un-animation-fadeinup un-lockscreen-form',
            reference: 'loginForm',
            height: 240,
            width: 280,
            items: [
                {
                    xtype: 'container',
                    cls: 'un-login-form-usermsg-container',
                    html: Unidata.i18n.t('login>sessionExpired')
                },
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
                            xtype: 'textfield',
                            reference: 'loginInput',
                            cls: 'un-login-form-login-input',
                            allowBlank: false,
                            emptyText: Unidata.i18n.t('login>username'),
                            msgTarget: 'none',
                            enableKeyEvents: true,
                            listeners: {
                                specialkey: 'onSpecialKeyLoginInput'
                            }
                        },
                        {
                            xtype: 'textfield',
                            reference: 'passwordInput',
                            cls: 'un-login-form-password-input',
                            inputType: 'password',
                            allowBlank: false,
                            emptyText: Unidata.i18n.t('login>password'),
                            msgTarget: 'none',
                            enableKeyEvents: true,
                            listeners: {
                                specialkey: 'onSpecialKeyPasswordInput'
                            }
                        },
                        {
                            xtype: 'button',
                            formBind: true,
                            text: Unidata.i18n.t('login>loginButton'),
                            name: 'login',
                            reference: 'login',
                            scale: 'medium',
                            handler: 'onLoginButtonClick'
                        }
                    ]
                }
            ]
        }
    ],

    listeners: {
        show: 'onShowLockScreen',
        hide: 'onHideLockScreen'
    }
});
