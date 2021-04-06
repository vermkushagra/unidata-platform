Ext.define('Unidata.view.login.password.reset.ResetPasswordController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.resetpassword',

    onResetClick: function () {
        var PasswordPolicy = Unidata.uiuserexit.overridable.security.PasswordPolicy,
            form = this.lookupReference('resetPasswordForm'),
            formValues = form.getValues();

        if (form.isValid()) {
            if (formValues.new_password !== '' && formValues.new_password === formValues.new_password_confirm) {
                if (!PasswordPolicy.isPasswordSecuredDefault(formValues.new_password)) {
                    Ext.MessageBox.show({
                        title: Unidata.i18n.t('login>password.attentionTitle'),
                        msg: PasswordPolicy.getUnsecuredMessageDefault(formValues.new_password),
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.WARNING
                    });

                    return false;
                }
            } else {
                Ext.MessageBox.show({
                    title: Unidata.i18n.t('login>password.attentionTitle'),
                    msg: Unidata.i18n.t('login>password.notEqualPasswords'),
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.WARNING
                });

                return false;
            }

            Ext.Ajax.request({
                url: Unidata.Config.getMainUrl() + 'internal/authentication/setpassword',
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                jsonData: Ext.util.JSON.encode({
                    userName: Unidata.Config.getUser().get('login'),
                    password: formValues.new_password
                }),
                success: function (response) {
                    if (response.status === 200) {
                        Unidata.showMessage(Unidata.i18n.t('login>password.successfullyChanged'));
                        Unidata.getApplication().showViewPort('main');
                    }
                },
                scope: this
            });

        }
    },

    onLogoutClick: function () {
        var promise;

        Unidata.util.Router
            .suspendTokenEvents()
            .removeTokens()
            .resumeTokenEvents();

        promise = Unidata.util.api.Authenticate.logout();

        promise
            .then(function () {
                var application = Unidata.getApplication();

                application.fireEvent('deauthenticate');

                application.showViewPort('login');
            })
            .otherwise(function () {
            })
            .done();
    },

    onSpecialKeyNewPasswordInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyResetPasswordInput(component, view.newPasswordConfirm, e);
    },

    onSpecialKeyNewPasswordConfirmInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyResetPasswordInput(component, view.newPassword, e);
    },

    /**
     * Должен работать переход по табу между полями логин - пароль
     *
     * @param field - поле на котором обрабатывается событие
     * @param nextField - поле на которое должен перейти фокус
     * @param event - событие
     */
    handleSpecSpecialKeyResetPasswordInput: function (field, nextField, event) {
        var key = event.getKey();

        if (key === Ext.event.Event.TAB) {
            event.stopEvent();

            nextField.focus(true, 50);
        } else if (key === Ext.event.Event.ENTER) {
            event.stopEvent();

            this.onResetClick();
        }
    }
});
