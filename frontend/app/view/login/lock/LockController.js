Ext.define('Unidata.view.login.lock.LockController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.lock',

    onLoginButtonClick: function () {
        this.doAuthorization();
    },

    doAuthorization: function () {
        var view = this.getView(),
            form = this.lookupReference('loginForm'),
            previousUser = Unidata.Config.getUser(),
            password,
            login,
            promise;

        if (form.isValid()) {
            login = view.loginInput.getValue();
            password = view.passwordInput.getValue();

            promise = Unidata.util.api.Authenticate.login(login, password);

            promise
                .then(function (authenticateData) {
                    var application = Unidata.getApplication(),
                        loginUser;

                    application.fireEvent('authenticate', authenticateData);

                    loginUser = Unidata.Config.getUser();

                    // если вошли под новым пользователем после завершения сессии, то необходим перезапуск приложения
                    // иначе остается интерфейс предыдущего пользователя с потерциально не верными правами
                    if (previousUser.get('login') !== loginUser.get('login')) {
                        window.location.reload();
                    }

                    view.hide();
                })
                .otherwise(function () {
                    view.loginInput.focus(true, 50);

                    form.reset();
                })
                .done();
        }
    },

    onShowLockScreen: function () {
        var view = this.getView();

        this.lockTabSwitch();
        Unidata.module.hotkey.GlobalHotKeyManager.disableAutoManagedHotKeys();

        // закрываем все всплывающие подсказки
        Ext.window.Toast.closeAllToasts(true);

        // регистрируем компонент в zindexmanager т.к. экран должен быть поверх
        Ext.WindowManager.register(view);
        Ext.WindowManager.bringToFront(view);

        // пользователь должен ввести данные для авторизаци самостоятельно
        view.loginInput.setValue(null);
        view.passwordInput.setValue(null);

        view.loginInput.focus(true, 500);
    },

    onHideLockScreen: function () {
        var view = this.getView();

        this.unlockTabSwitch();
        Unidata.module.hotkey.GlobalHotKeyManager.disableAutoManagedHotKeys();

        // zindexmanager больше не должен управлять этим экраном
        Ext.WindowManager.unregister(view);
    },

    lockTabSwitch: function () {
        this.bindedSwitchListener = Ext.bind(this.preventTabSwitch, this);

        $(window.document).on('focus', 'input, a, div', this.bindedSwitchListener);
    },

    unlockTabSwitch: function () {
        $(window.document).off('focus', 'input, a, div', this.bindedSwitchListener);
    },

    preventTabSwitch: function (e) {
        var view = this.getView();

        if (!view.getEl().contains(e.target)) {
            e.preventDefault();
            e.stopPropagation();

            view.loginInput.focus(true, 50);
        }
    },

    /**
     * Обработчик нажатия на спец клавиши для логина
     *
     * @param component
     * @param e
     */
    onSpecialKeyLoginInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyAuthorizationInput(component, view.passwordInput, e);
    },

    /**
     * Обработчик нажатия на спец клавиши для пароля
     *
     * @param component
     * @param e
     */
    onSpecialKeyPasswordInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyAuthorizationInput(component, view.loginInput, e);
    },

    /**
     * Должен работать переход по табу между полями логин - пароль
     *
     * @param field - поле на котором обрабатывается событие
     * @param nextField - поле на которое должен перейти фокус
     * @param event - событие
     */
    handleSpecSpecialKeyAuthorizationInput: function (field, nextField, event) {
        var key = event.getKey();

        if (key === Ext.event.Event.TAB) {
            event.stopEvent();

            nextField.focus(true, 50);
        } else if (key === Ext.event.Event.ENTER) {
            event.stopEvent();

            this.doAuthorization();
        }
    }
});
