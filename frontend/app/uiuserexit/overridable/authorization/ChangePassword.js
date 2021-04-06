/**
 * Точка расширения для конфигурирования изменения пароля
 *
 * @author Ivan Marshalkin
 * @date 2017-10-18
 */

Ext.define('Unidata.uiuserexit.overridable.authorization.ChangePassword', {
    singleton: true,

    requires: [
        'Unidata.uiuserexit.overridable.UnidataPlatform'
    ],

    mixins: [
        'Unidata.uiuserexit.overridable.authorization.BackgroundClsMixin'
    ]
});
