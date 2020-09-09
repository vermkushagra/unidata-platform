/**
 * Примесь с конфигурированием класса бэкраунда форм
 *
 * @author Ivan Marshalkin
 * @date 2017-10-19
 */

// Примечание на случай необходимости разнесения настроек по разным свойствам. Предположим класс должен будет выглядеть так
//
// Ext.define('Unidata.uiuserexit.overridable.authorization.BackgroundClsMixin', {
//     /**
//      * Если несколько миксенов или определен объект на классе то необходимо закинуть дефолтную информацию, если она не указана
//      *
//      * @param targetClass
//      */
//     onClassMixedIn: function (targetClass) {
//         // актуально когда настройки разнесены по объектам
//         var mixinPrototype = this.prototype,
//             targetClassPrototype = targetClass.prototype;
//
//         Ext.Array.each(['userAppMode', 'adminAppMode', 'devAppMode'], function (item) {
//             if (targetClassPrototype[item] && !targetClassPrototype[item].backgroundCls) {
//                 targetClassPrototype[item].backgroundCls = mixinPrototype[item].backgroundCls;
//             }
//         });
//     },
//
//     userAppMode: {
//         backgroundCls: 'un-login-background'
//     },
//
//     adminAppMode: {
//         backgroundCls: 'un-login-background'
//     },
//
//     devAppMode: {
//         backgroundCls: 'un-login-background'
//     },
// });

Ext.define('Unidata.uiuserexit.overridable.authorization.BackgroundClsMixin', {
    extend: 'Ext.Mixin',

    statics: {
        userAppMode: {
            appModeCls: 'un-application-usermode'
        },

        adminAppMode: {
            appModeCls: 'un-application-adminmode'
        },

        devAppMode: {
            appModeCls: 'un-application-devmode'
        }
    },

    backgroundCls: 'un-login-background',

    getBackgroundCls: function () {
        var appMode = Unidata.Config.getAppMode(),
            BackgroundClsMixin = Unidata.uiuserexit.overridable.authorization.BackgroundClsMixin,
            backgroundCls = this.backgroundCls,
            appModeCls = BackgroundClsMixin.userAppMode.appModeCls,
            cls;

        switch (appMode) {
            case Unidata.Config.APP_MODE.USER:
                appModeCls = BackgroundClsMixin.userAppMode.appModeCls;
                break;
            case Unidata.Config.APP_MODE.ADMIN:
                appModeCls = BackgroundClsMixin.adminAppMode.appModeCls;
                break;
            case Unidata.Config.APP_MODE.DEV:
                appModeCls = BackgroundClsMixin.devAppMode.appModeCls;
                break;
        }

        cls = [backgroundCls, appModeCls].join(' ');

        return cls;
    }
});
