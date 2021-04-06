/**
 * Override для data.Connection
 *
 * Author: Sergey Shishigin
 * Date: 2016-04-13
 */
Ext.define('Ext.overrides.data.Connection', {
    override: 'Ext.data.Connection',

    defaultErrorText: Unidata.i18n.t('other>unknownError'),

    inheritableStatics: {
        maintenanceMask: null,
        maintenanceTimer: null
    },

    onFailure: function (response) {
        this.handleErrors(response);
    },

    handleErrors: function (response) {
        var ErrorMessageFactory = Unidata.util.ErrorMessageFactory,
            msg,
            severity,
            userMessageDetails,
            maintenanceError,
            errorDetails,
            autoClose,
            stackTrace,
            url;

        maintenanceError = ErrorMessageFactory.getErrorFromResponseByErrorCode(response, 'EX_MAINTENANCE');

        if (maintenanceError) {
            this.handleMaintenanceError(maintenanceError);

            return;
        }

        msg  = ErrorMessageFactory.getErrorUserMessagesFromResponse(response);
        severity = ErrorMessageFactory.getMaxSeverityFromResponse(response);
        userMessageDetails = ErrorMessageFactory.getUserMessageDetailsFromResponse(response);
        userMessageDetails = userMessageDetails ? userMessageDetails.join('\n') : userMessageDetails;
        stackTrace = ErrorMessageFactory.getStackTraceFromResponse(response);
        url = response.request.options.url;

        if (stackTrace) {
            errorDetails = {
                stackTrace: stackTrace,
                severity: severity,
                url: url
            };
        }

        // выводим только для http code 200
        if (response.status === 200 && msg) {
            msg = msg || this.defaultErrorText;
            autoClose = !userMessageDetails;

            if (!errorDetails && userMessageDetails) {
                errorDetails = {
                    userMessageDetails: userMessageDetails
                };
            }

            Unidata.showError(msg, autoClose, errorDetails, severity);
        }
    },

    handleMaintenanceError: function () {
        var me = this,
            mask = me.getMaintenanceMask();

        if (mask) {
            mask.show();

            this.self.maintenanceTimer = Ext.defer(function () {
                me.getMaintenanceMask().destroy();
                me.self.maintenanceMask = null;
            }, 1000 * 70);
        } else {
            Ext.defer(me.handleMaintenanceError, 1000, me);
        }
    },

    getMaintenanceMask: function () {
        var activeView = Unidata.getApplication().getActiveView(),
            maintenanceMask = this.self.maintenanceMask;

        if (!maintenanceMask && activeView) {
            this.self.maintenanceMask = maintenanceMask = new Ext.LoadMask({
                msg: Unidata.i18n.t('application>maintenanceMode'),
                target: activeView,
                style: {
                    zIndex: 999999
                }
            });
        }

        return maintenanceMask;
    },

    /**
     * В отличии от переопределенного Ext.Ajax.request данный вид запроса вызывает
     *
     * success обработчик если:
     *
     * 1) AJAX запрос был успешно выполнен
     * 2) ответ backend соответсвует оговоренному формату
     * 3) в ответе backend содержится флаг success === true
     *
     * В остальных случаях вызывает обработчик failure
     *
     * @param options
     */
    unidataRequest: function (options) {
        var requestOptions,
            oldSuccess  = options.success  || Ext.emptyFn,
            oldFailure  = options.failure  || Ext.emptyFn,
            oldCallback = options.callback || Ext.emptyFn,
            oldScope    = options.scope    || this;

        requestOptions = Ext.apply(options, {
            success: function (response) {
                var reponseJson;

                if (response) {
                    reponseJson = Ext.JSON.decode(response.responseText, true);
                }

                /**
                 * Вызываем success только если ответ пришел в правильном виде и флаг success === true и есть content
                 * Иначе это failure
                 */
                if (!reponseJson || !Ext.isBoolean(reponseJson.success)) {
                    oldFailure.apply(oldScope, arguments);
                }
                /*
                } else if (reponseJson.success === true && !reponseJson.content) {
                    oldFailure.apply(oldScope, arguments);
                }*/
                else if (reponseJson.success === false /** && reponseJson.errors **/) {
                    oldFailure.apply(oldScope, arguments);
                } else {
                    oldSuccess.apply(oldScope, arguments);
                }
            },
            failure: function () {
                oldFailure.apply(oldScope, arguments);
            },
            callback: function () {
                oldCallback.apply(oldScope, arguments);
            }
        });

        Ext.Ajax.request(requestOptions);
    },

    /**
     * Переопределяем метод request с целью расширения callback функцией handleErrors
     *
     * @param options
     */
    request: function (options) {
        var oldCallback,
            oldCallbackScope,
            me = this;

        // если установлен флаг supressGlobalFailureHandling в operaion model options,
        // то не обратываем ошибку глобально
        if (!options.operation || options.operation.supressGlobalFailureHandling !== true) {

            if (options.callback) {
                oldCallback      = Ext.clone(options.callback);
                oldCallbackScope = options.scope || me;
            }

            // определяем новый callback (расширяем функцией handleErrors)
            options.callback = function (options, success, response) {
                var responseBody = null;

                if (oldCallback) {
                    // вызываем старый callback
                    oldCallback.apply(oldCallbackScope, arguments);
                }

                // нет смысла пытаться декодировать JSON если ответ пустой иначе постоянно останавляваемся на catch exception в dev tools
                if (response && !Ext.isEmpty(response.responseText)) {
                    responseBody = Ext.decode(response.responseText, true);
                }

                if (responseBody && Ext.isArray(responseBody.errors) && responseBody.errors.length > 0) {
                    me.handleErrors(response);
                }
            };
        }

        this.callParent(arguments);
    }
});
