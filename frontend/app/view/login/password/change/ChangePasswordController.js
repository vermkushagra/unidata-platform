Ext.define('Unidata.view.login.password.change.ChangePasswordController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.changepassword',

    onChangeClick: function () {
        var PasswordPolicy = Unidata.uiuserexit.overridable.security.PasswordPolicy,
            form = this.lookupReference('changePasswordForm'),
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
                } else if (formValues.new_password === formValues.old_password) {
                    Ext.MessageBox.show({
                        title: Unidata.i18n.t('login>password.attentionTitle'),
                        msg: Unidata.i18n.t('login>password.equalPasswords'),
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.WARNING
                    });

                    return false;
                }
            } else {
                Ext.MessageBox.show({
                    title: Unidata.i18n.t('login>password.attentionTitle'),
                    msg: Unidata.i18n.t('login>password.passwordRequired'),
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
                    oldPassword: formValues.old_password,
                    password: formValues.new_password
                }),
                success: function (response) {
                    var jsonResp;

                    if (response.status === 200) {
                        jsonResp = Ext.util.JSON.decode(response.responseText);

                        if (jsonResp.content.success === true) {
                            Unidata.showMessage(Unidata.i18n.t('login>password.successfullyChanged'));
                            Unidata.getApplication().showViewPort('main');
                        } else {
                            Ext.MessageBox.show({
                                title: Unidata.i18n.t('login>password.attentionTitle'),
                                msg: Unidata.i18n.t('login>password.invalidOldPassword'),
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.WARNING
                            });
                        }
                    }
                },
                scope: this
            });
        }
    },

    onBackClick: function () {
        Unidata.getApplication().showViewPort('main');
    },

    onSpecialKeyOldPasswordInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyChangePasswordInput(component, view.newPassword, e);
    },

    onSpecialKeyNewPasswordInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyChangePasswordInput(component, view.newPasswordConfirm, e);
    },

    onSpecialKeyNewPasswordConfirmInput: function (component, e) {
        var view = this.getView();

        this.handleSpecSpecialKeyChangePasswordInput(component, view.oldPassword, e);
    },

    /**
     * Должен работать переход по табу между полями логин - пароль
     *
     * @param field - поле на котором обрабатывается событие
     * @param nextField - поле на которое должен перейти фокус
     * @param event - событие
     */
    handleSpecSpecialKeyChangePasswordInput: function (field, nextField, event) {
        var key = event.getKey();

        if (key === Ext.event.Event.TAB) {
            event.stopEvent();

            nextField.focus(true, 50);
        } else if (key === Ext.event.Event.ENTER) {
            event.stopEvent();

            this.onChangeClick();
        }
    }

});
