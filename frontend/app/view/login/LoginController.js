Ext.define('Unidata.view.login.LoginController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.login',

    init: function () {
        this.callParent(arguments);
    },

    onLoginClick: function () {
        var view = this.getView(),
            form = this.lookupReference('loginForm'),
            password,
            login,
            promise;

        if (form.isValid()) {
            login = view.loginInput.getValue();
            password = view.passwordInput.getValue();

            promise = Unidata.util.api.Authenticate.login(login, password);

            promise
                .then(
                    function (authenticateData) {
                        var application = Unidata.getApplication();

                        application.fireEvent('authenticate', authenticateData);
                    },
                    function () {
                        if (view && view.loginButton) {
                            view.loginButton.setPressed(false);
                        }
                    })
                .done();
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
        var view = this.getView(),
            key = event.getKey();

        if (key === Ext.event.Event.TAB) {
            event.stopEvent();

            nextField.focus(true, 50);
        } else if (key === Ext.event.Event.ENTER) {
            event.stopEvent();

            view.loginButton.setPressed(true);

            // чтоб пользователь заметил изменение кнопки
            Ext.defer(function () {
                this.onLoginClick();
            }, 50, this);
        }
    }
});
