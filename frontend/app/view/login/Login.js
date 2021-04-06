Ext.define('Unidata.view.login.Login', {
    extend: 'Ext.container.Viewport',

    alias: 'widget.login',

    requires: [
        'Unidata.view.login.LoginController',
        'Unidata.view.login.LoginModel',
        'Unidata.uiuserexit.overridable.authorization.Login'
    ],

    controller: 'login',
    viewModel: {
        type: 'login'
    },

    referenceHolder: true,

    loginInput: null,                                        // ссылка на поле ввода логина
    passwordInput: null,                                     // ссылка на поле ввода пароля
    loginButton: null,                                       // ссылка на кнопку входа в систему
    loginForm: null,                                         // ссылка на форму входа

    layout: 'center',

    initComponent: function () {
        var UiUeLogin = Unidata.uiuserexit.overridable.authorization.Login;

        this.callParent(arguments);

        this.addCls(UiUeLogin.getBackgroundCls());

        this.initComponentReference();

        Unidata.uiuserexit.callback.CallbackProvider.provideActiveUiUserExit(
            Unidata.uiuserexit.callback.CallbackTypes.LOGIN_FORM_INITCOMPONENT,
            this,
            {}
        );

        this.on('render', this.onComponentRender, this, {single: true});
    },

    initComponentReference: function () {
        var me = this;

        me.loginInput = me.lookupReference('loginInput');
        me.passwordInput = me.lookupReference('passwordInput');
        me.loginButton = me.lookupReference('loginButton');
        me.loginForm = me.lookupReference('loginForm');
    },

    onDestroy: function () {
        var me = this,
            UiUeLogin = Unidata.uiuserexit.overridable.authorization.Login;

        // при уничтожении viewport классы остаются у <body> поэтому удаляем ручками
        this.removeCls(UiUeLogin.getBackgroundCls());

        me.loginInput = null;
        me.passwordInput = null;
        me.loginButton = null;
        me.loginForm = null;

        this.callParent(arguments);
    },

    onComponentRender: function () {
        Ext.defer(this.animateLoginForm, 100, this);

        this.loginInput.focus(true, 500);
    },

    initItems: function () {
        var UiUeLogin = Unidata.uiuserexit.overridable.authorization.Login,
            platformLogoContainer,
            loginForm;

        this.callParent(arguments);

        loginForm = Ext.widget({
            xtype: 'form',
            reference: 'loginForm',
            cls: 'un-login-form',
            hidden: true,
            height: UiUeLogin.height,
            width: UiUeLogin.width,
            maxHeight: UiUeLogin.maxHeight,
            maxWidth: UiUeLogin.maxWidth,
            items: [
                {
                    xtype: 'container',
                    cls: 'un-login-form-input-container',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            reference: 'loginInput',
                            itemId: 'loginInput',
                            cls: 'un-login-form-login-input',
                            emptyText: Unidata.i18n.t('login>username'),
                            msgTarget: 'none',
                            allowBlank: false,
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
                            emptyText:  Unidata.i18n.t('login>password'),
                            msgTarget: 'none',
                            allowBlank: false,
                            enableKeyEvents: true,
                            listeners: {
                                specialkey: 'onSpecialKeyPasswordInput'
                            }
                        },
                        {
                            xtype: 'button',
                            reference: 'loginButton',
                            formBind: true,
                            text: Unidata.i18n.t('login>loginButton'),
                            name: 'login',
                            scale: 'large',
                            handler: 'onLoginClick'
                        }
                    ]
                }
            ]
        });

        platformLogoContainer = UiUeLogin.buildPlatformLogoContainer();

        if (platformLogoContainer) {
            loginForm.insert(0, platformLogoContainer);
        }

        loginForm = UiUeLogin.transformPlatformLoginForm(loginForm);

        this.add(loginForm);
    },

    /**
     * Добавляет анимацию к форме авторизации
     */
    animateLoginForm: function () {
        var el;

        if (this.destroying || this.isDestroyed) {
            return;
        }

        el = this.loginForm.getEl();

        el.addCls('un-animation-fadeinup');
        this.loginForm.setHidden(false);

        // должен быть больше чем анимация показа формы
        Ext.defer(this.updateLoginFormLayout, 1500, this);
    },

    /**
     * Обновляем лейаут формы авторизации. Принудительно обновляем т.к. переодически форма отображается криво см UN-6289.
     */
    updateLoginFormLayout: function () {
        var el;

        if (this.destroying || this.isDestroyed) {
            return;
        }

        el = this.loginForm.getEl();
        el.removeCls('un-animation-fadeinup');

        this.loginForm.updateLayout();
    }
});
