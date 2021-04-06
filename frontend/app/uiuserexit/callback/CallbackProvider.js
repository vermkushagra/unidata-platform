/**
 * Провайдер
 * Вспомогательный класс для реализации callback из ui user exit
 *
 * @author Ivan Marshalkin
 * @date 2017-07-24
 */

Ext.define('Unidata.uiuserexit.callback.CallbackProvider', {
    requires: [
        'Unidata.uiuserexit.callback.CallbackBase'
    ],

    statics: {
        provideCallbackClasses: function (callbackClasses, scope, opts) {
            var results = [];

            scope = scope || Unidata.getApplication();
            opts = opts || {};

            Ext.Array.each(callbackClasses, function (callbackClass) {
                var result;

                result = callbackClass.callback.call(scope, opts);

                results.push(result);
            });

            return results;
        },

        /**
         * Возвращает массив
         *
         * @param callbackType
         * @param scope
         * @param opts
         *
         * @returns {*}
         */
        getAllProvideableCallbackClasses: function (callbackType, scope, opts) {
            var callbackClasses;

            opts = opts || {};

            // ативные callback кастомера
            callbackClasses = Unidata.uiuserexit.callback.CallbackBase.getAllActiveCustomerCallbackClassesByType(callbackType);

            // оставляем только те которые могут быть применены для данного контекста
            callbackClasses = Ext.Array.filter(callbackClasses, function (callbackClass) {
                return callbackClass.isProvideable(opts) === true;
            });

            return callbackClasses;
        },

        /**
         * Фильтрует колбеки. Возвращает новый массив колбеков системы
         *
         * @param callbackClasses - массив классов колбеков
         *
         * @returns {*}
         */
        getSystemCallbackClasses: function (callbackClasses) {
            var result;

            result = Ext.Array.filter(callbackClasses, function (callbackClass) {
                return callbackClass.system === true;
            });

            return result;
        },

        /**
         * Фильтрует колбеки. Возвращает новый массив колбеков кастомера
         *
         * @param callbackClasses - массив классов колбеков
         *
         * @returns {*}
         */
        getCustomerCallbackClasses: function (callbackClasses) {
            var result;

            result = Ext.Array.filter(callbackClasses, function (callbackClass) {
                return callbackClass.system !== true;
            });

            return result;
        },

        /**
         * Провайдит callback кастомера
         *
         * @param callbackType - тип (место использования)
         * @param scope - контекст с котором должен вызываться callback кастомера
         * @param opts - объект содержащий дополнительные параметры
         *
         * @returns {*} - массив результатов колбеков кастомера
         */
        provideActiveUiUserExit: function (callbackType, scope, opts) {
            var callbackClasses = Unidata.uiuserexit.callback.CallbackProvider.getAllProvideableCallbackClasses(callbackType, scope, opts),
                result;

            result = Unidata.uiuserexit.callback.CallbackProvider.provideCallbackClasses(callbackClasses, scope, opts);

            return result;
        },

        /**
         * Провайдит один callback кастомера
         *
         * @param callbackType - тип (место использования)
         * @param scope - контекст с котором должен вызываться callback кастомера
         * @param opts - объект содержащий дополнительные параметры
         *
         * @returns {*} - результатов колбека кастомера
         */
        provideFirstActiveUiUserExit: function (callbackType, scope, opts) {
            var callbackClasses = Unidata.uiuserexit.callback.CallbackProvider.getAllProvideableCallbackClasses(callbackType, scope, opts),
                result = null;

            if (callbackClasses.length > 1) {
                console.warn(Unidata.i18n.t('other>warningFewActiveCallbacks', {type: callbackType}), 'color: red;', callbackClasses);
            }

            if (Ext.isArray(callbackClasses) && callbackClasses.length) {
                result = Unidata.uiuserexit.callback.CallbackProvider.provideCallbackClasses([callbackClasses[0]], scope, opts);
                result = result[0];
            }

            return result;
        },

        /**
         * Провайдит callback-interceptor кастомера. Реализация подразумевает что провадится будет только единственный interceptor.
         *
         * Логика исполнения интерсепторов следующая:
         *     1) Если есть колбеки кастомера - исполняется первый из них
         *     2) Если колбеков кастомера нет, но есть системные - исполняется первый из них
         *     3) Возвращается результат исполнения колбека или null
         *
         * @param callbackType - тип (место использования)
         * @param scope - контекст с котором должен вызываться callback кастомера
         * @param opts - объект содержащий дополнительные параметры
         *
         * @returns {*} - результатов колбека кастомера или системного колбека
         */
        provideActiveUiUserExitInterceptor: function (callbackType, scope, opts) {
            var callbackClasses = Unidata.uiuserexit.callback.CallbackProvider.getAllProvideableCallbackClasses(callbackType, scope, opts),
                systemCallbackClasses = Unidata.uiuserexit.callback.CallbackProvider.getSystemCallbackClasses(callbackClasses),
                customerCallbackClasses = Unidata.uiuserexit.callback.CallbackProvider.getCustomerCallbackClasses(callbackClasses),
                classes = [],
                result = null;

            if (systemCallbackClasses.length > 1) {
                console.warn(
                  Unidata.i18n.t('other>warningFewActiveCallbackInspectors', {type: callbackType, provider: 'system'}),
                  'color: red;',
                  systemCallbackClasses
                );
            }

            if (customerCallbackClasses.length > 1) {
                console.warn(
                  Unidata.i18n.t('other>warningFewActiveCallbackInspectors', {type: callbackType, provider: 'customer'}),
                  'color: red;',
                  customerCallbackClasses
                );
            }

            if (customerCallbackClasses.length) {
                classes = customerCallbackClasses;
            } else {
                classes = systemCallbackClasses;
            }

            if (classes.length) {
                result = Unidata.uiuserexit.callback.CallbackProvider.provideCallbackClasses([classes[0]], scope, opts);
                result = result[0];
            }

            return result;
        }
    }
});
