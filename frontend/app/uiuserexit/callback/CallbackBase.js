/**
 * Базовый класс реализации callback из ui user exit
 *
 * @author Ivan Marshalkin
 * @date 2017-07-24
 */

Ext.define('Unidata.uiuserexit.callback.CallbackBase', {
    requires: [
        'Unidata.uiuserexit.callback.CallbackTypes',
        'Unidata.uiuserexit.CustomerClassesHelper'
    ],

    inheritableStatics: {
        // включен / выключен (выключеные не учитываются)
        active: false,

        // флаг указывающий что это часть платформы
        system: false,

        // тип (место использования)
        callbackType: Unidata.uiuserexit.callback.CallbackTypes.UNKNOWN,

        // функция реализующая логику кастомера
        callback: Ext.emptyFn,

        isProvideable: function () {
            console.log(Unidata.i18n.t('other>warningCalledBaseMethod', {method: 'CallbackBase->isProvideable'}));

            return false;
        }
    },

    statics: {
        /**
         * Возвращает все классы кастомера для реализации callback
         *
         * @returns массив классов
         */
        getAllCustomerCallbackClasses: function () {
            var baseClass = Unidata.uiuserexit.callback.CallbackBase,
                result;

            result = Unidata.uiuserexit.CustomerClassesHelper.getAllCustomerClasses(baseClass);

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все активные классы кастомера для реализации callback
         *
         * @returns массив классов
         */
        getAllActiveCustomerCallbackClasses: function () {
            var baseClass = Unidata.uiuserexit.callback.CallbackBase,
                result;

            result = Unidata.uiuserexit.CustomerClassesHelper.getAllActiveCustomerClasses(baseClass);

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все классы кастомера для реализации callback по типу (месту использования)
         *
         * @returns массив классов
         */
        getAllCustomerCallbackClassesByType: function (callbackType) {
            var classes = Unidata.uiuserexit.callback.CallbackBase.getAllCustomerCallbackClasses(),
                result;

            result = Ext.Array.filter(classes, function (item) {
                return item.callbackType === callbackType;
            });

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все активные классы кастомера для реализации callback по типу (месту использования)
         *
         * @returns массив классов
         */
        getAllActiveCustomerCallbackClassesByType: function (callbackType) {
            var classes = Unidata.uiuserexit.callback.CallbackBase.getAllActiveCustomerCallbackClasses(),
                result;

            result = Ext.Array.filter(classes, function (item) {
                return item.callbackType === callbackType;
            });

            return Ext.Array.unique(result);
        }
    }
});
