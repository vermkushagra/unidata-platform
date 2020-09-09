/**
 * Реализация политики проверки надежности пароля
 *
 * @author Ivan Marshalkin
 * @date 2017-12-15
 */

Ext.define('Unidata.uiuserexit.overridable.security.PasswordPolicy', {
    singleton: true,

    isPasswordSecuredDefault: function (password) {
        var secured = false,
            regExp = /^(?=[0-9a-z]{8,}$)(?=.*?[a-z])(?=.*?\d).*/i;

        if (regExp.test(password)) {
            secured = true;
        }

        return secured;
    },

    getUnsecuredMessageDefault: function (password) { // jscs:ignore disallowUnusedParams
        var msg = Unidata.i18n.t('uiuserexit>passwordPolicy.incorrectPassword');

        return msg;
    }
});
