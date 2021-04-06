/**
 * Точка расширения для конфигурирования формы авторизации
 *
 * @author Ivan Marshalkin
 * @date 2017-10-18
 */

Ext.define('Unidata.uiuserexit.overridable.authorization.Login', {
    singleton: true,

    requires: [
        'Unidata.uiuserexit.overridable.UnidataPlatform'
    ],

    mixins: [
        'Unidata.uiuserexit.overridable.authorization.BackgroundClsMixin'
    ],

    height: 293,
    width: 280,
    maxHeight: null,
    maxWidth: null,

    buildPlatformLogoContainer: function () {
        var UiUeUnidataPlatform = Unidata.uiuserexit.overridable.UnidataPlatform,
            component;

        component = {
            xtype: 'container',
            cls: 'un-login-form-logo-container',
            height: 105,
            html: UiUeUnidataPlatform.getLogoPlatformHtml()
        };

        return component;
    },

    transformPlatformLoginForm: function (loginForm) {
        return loginForm;
    }
});
