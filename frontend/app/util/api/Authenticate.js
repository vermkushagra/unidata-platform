/**
 * API авторизации / деавторизации
 *
 * @author Ivan Marshalkin
 * @date 2017-01-31
 */
Ext.define('Unidata.util.api.Authenticate', {
    extend: 'Unidata.util.api.AbstractApi',
    singleton: true,

    /**
     * Авторизация пользователя по логину / паролю
     *
     * @param login - логин
     * @param password - пароль
     *
     * @returns {null|Ext.promise|*|Ext.promise.Promise}
     */
    login: function (login, password) {
        var deferred = new Ext.Deferred(),
            authInfo;

        login = !Ext.isEmpty(login) ? login : '';
        password = !Ext.isEmpty(password) ? password : '';

        authInfo = {
            password: password,
            userName: login
        };

        Ext.Ajax.request({
            url: Unidata.Config.getMainUrl() + 'internal/authentication/login',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'PROLONG_TTL': 'false'
            },
            jsonData: Ext.util.JSON.encode(authInfo),
            success: function (response) {
                var jsonResp = Ext.util.JSON.decode(response.responseText, true);

                // авторизованые если:
                // * ответ с кодом 200
                // * флаг success = true
                if (response.status === 200 && jsonResp && jsonResp.success) {
                    deferred.resolve(jsonResp.content);
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    },

    /**
     * Деавторизация пользователя
     *
     * @returns {null|Ext.promise|*|Ext.promise.Promise}
     */
    logout: function () {
        var deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: Unidata.Config.getMainUrl() + 'internal/authentication/logout',
            headers: {'Content-Type': 'application/json'},
            method: 'POST',
            success: function (response) {
                if (response.status === 202) {
                    deferred.resolve();
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    },

    /**
     * В настоящее время get-current-user используется для валидации токена авторизации => имя функции  authenticate
     *
     * @returns {null|Ext.promise|*|Ext.promise.Promise}
     */
    authenticate: function () {
        var deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: Unidata.Config.getMainUrl() + 'internal/authentication/get-current-user',
            method: 'GET',
            success: function (response) {
                var jsonResp = Ext.util.JSON.decode(response.responseText, true);

                if (response.status === 200 && jsonResp && jsonResp.success !== false) {
                    deferred.resolve(jsonResp);
                } else {
                    deferred.reject();
                }
            },
            failure: function () {
                deferred.reject();
            }
        });

        return deferred.promise;
    }
});
