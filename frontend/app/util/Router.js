/**
 * Роутер для управления отображением компонентов
 * Можно подписываться на события роутера по tokenName,
 * которые срабатываеют при изменении значений токена
 *
 * http://localhost:1841/#main?section=tasks|task?taskId=122523
 * Токены разделены по tokenSplitter в примере выше - два токена "main?section=tasks" и "task?taskId=122523"
 * Токен разделяется по valueSplitter, первый элемент - это tokenName, остальное - значения
 *
 * @author Aleksandr Bavin
 * @date 2016-09-01
 */
Ext.define('Unidata.util.Router', {

    singleton: true,

    requires: [
        'Ext.util.MixedCollection',
        'Ext.util.History'
    ],

    mixins: {
        observable: 'Ext.util.Observable'
    },

    tokenSplitter: '|',
    valueSplitter: '?',

    /**
     * @typedef {Object} Token
     * @property {string} name - название токена
     * @property {Object} values - значения для токена
     * @property {string} hash
     */

    /**
     * @type {Token[]|Ext.util.MixedCollection}
     */
    tokens: null,
    oldTokens: null,

    lastRedirectHash: '',

    silentHashTimer: null,
    updateHashTimer: null,

    tokenEventsSuspended: false,

    constructor: function () {
        this.tokens = new Ext.util.MixedCollection();
        this.oldTokens = new Ext.util.MixedCollection();
        this.mixins.observable.constructor.call(this);

        Ext.util.History.on('ready', this.onHistoryReady, this);
        Ext.util.History.on('change', this.onHistoryChange, this);
    },

    /**
     * Устанавливает токен
     * @param {string} tokenName
     * @param {Object} [tokenValues]
     * @returns {Unidata.util.Router}
     */
    setToken: function (tokenName, tokenValues) {
        var newToken,
            oldToken,
            oldTokenHash;

        if (Ext.Object.isEmpty(tokenValues || {})) {
            return;
        }

        oldToken = this.getToken(tokenName);

        this.oldTokens.replace(tokenName, oldToken);

        if (oldToken) {
            oldTokenHash = oldToken.hash;
        }
        newToken = {
            name: tokenName,
            values: tokenValues
        };

        this.updateTokenHash(newToken);

        this.tokens.replace(tokenName, newToken);

        if (oldTokenHash !== newToken.hash) {
            this.onTokenChange(tokenName);
        }

        return this;
    },

    /**
     * Ставит значение для токена
     * @param {string} tokenName
     * @param {string} valueName
     * @param {*} value
     * @returns {Unidata.util.Router}
     */
    setTokenValue: function (tokenName, valueName, value) {
        var token = this.getToken(tokenName),
            tokenValues = token ? token.values : {},
            tokenOldHash;

        this.oldTokens.replace(tokenName, Ext.Object.merge({}, token));

        if (value) {
            tokenValues[valueName] = value;
        } else if (tokenValues[valueName]) {
            delete tokenValues[valueName];
        }

        if (!token) {
            this.setToken(tokenName, tokenValues);
        } else {
            tokenOldHash = token.hash;

            this.updateTokenHash(token);

            if (tokenOldHash !== token.hash) {
                this.onTokenChange(tokenName);
            }
        }

        return this;
    },

    removeTokenValue: function (tokenName, valueName) {
        this.setTokenValue(tokenName, valueName);

        return this;
    },

    getTokenValues: function (tokenName) {
        var token = this.getToken(tokenName);

        if (token) {
            return token.values;
        }

        return {};
    },

    getTokenValue: function (tokenName, valueName) {
        var token = this.getToken(tokenName);

        if (token && token.values[valueName]) {
            return token.values[valueName];
        }

        return undefined;
    },

    /**
     * Удаляет токен
     * @param tokenName
     * @returns {Unidata.util.Router}
     */
    removeToken: function (tokenName) {

        this.oldTokens.replace(tokenName, Ext.Object.merge({}, this.getToken(tokenName) || {}));

        if (this.tokens.removeAtKey(tokenName)) {
            this.onTokenChange(tokenName);
        }

        return this;
    },

    /**
     * Удаляет все токены
     * @returns {Unidata.util.Router}
     */
    removeTokens: function () {
        var tokenNames = [];

        this.tokens.each(function (token) {
            tokenNames.push(token.name);
            this.oldTokens.replace(token.name, Ext.Object.merge({}, token));
        }, this);

        this.tokens.removeAll();

        Ext.Array.each(tokenNames, function (tokenName) {
            this.onTokenChange(tokenName);
        }, this);

        return this;
    },

    suspendTokenEvents: function () {
        this.tokenEventsSuspended = true;

        return this;
    },

    resumeTokenEvents: function () {
        this.tokenEventsSuspended = false;

        return this;
    },

    redirectTo: function (hash) {
        this.updateTokensFromHash(hash);
    },

    /**
     * Генерация хэша из токенов
     * @param {Token[]} tokens
     */
    buildHash: function (tokens) {
        var hashTokens = [];

        Ext.Array.each(tokens, function (token) {
            this.updateTokenHash(token);
            hashTokens.push(token.hash);
        }, this);

        return hashTokens.join(this.tokenSplitter);
    },

    privates: {
        /**
         * @private
         */
        getToken: function (tokenName) {
            return this.tokens.getByKey(tokenName);
        },

        /**
         * @private
         */
        updateTokenHash: function (token) {
            token.hash = [token.name, Ext.Object.toQueryString(token.values)].join(this.valueSplitter);
        },

        /**
         * @private
         */
        onHistoryReady: function () {
            var hash = this.getHash();

            this.lastRedirectHash = hash;
            this.updateTokensFromHash(hash);
        },

        /**
         * @private
         */
        onHistoryChange: function () {
            this.updateTokensFromHash(this.getHash());
        },

        /**
         * @returns {*}
         * @private
         */
        getHash: function () {
            return Ext.util.History.getToken();
        },

        /**
         * @param hash
         * @param silent
         * @private
         */
        setHash: function (hash, silent) {
            var history = Ext.util.History;

            if (this.silentHashTimer) {
                clearTimeout(this.silentHashTimer);
                this.silentHashTimer = null;
                history.resumeEvent('change');
            }

            if (silent) {
                history.suspendEvent('change');
                history.add(hash);
                this.silentHashTimer = Ext.defer(function () {
                    history.resumeEvent('change');
                }, 50, this);
            } else {
                history.add(hash);
            }
        },

        /**
         * Вызывается при любом изменении токена
         * @private
         */
        onTokenChange: function (tokenName) {
            var token = this.getToken(tokenName),
                oldToken = this.oldTokens.getByKey(tokenName),
                tokenValues = token ? token.values : {},
                oldTokenValues = oldToken ? oldToken.values : {};

            if (!this.tokenEventsSuspended) {
                this.fireEvent(
                    tokenName,
                    Ext.Object.merge({}, tokenValues),
                    Ext.Object.merge({}, oldTokenValues)
                );
                this.updateHashFromTokensDelayed();
            } else {
                this.updateHashFromTokens(true);
            }
        },

        /**
         * Обновляет хэш из токенов
         * @private
         */
        updateHashFromTokensDelayed: function () {
            clearTimeout(this.updateHashTimer);
            this.updateHashTimer = Ext.defer(this.updateHashFromTokens, 10, this);
        },

        /**
         * Обновляет хэш из токенов
         * @param silent
         * @private
         */
        updateHashFromTokens: function (silent) {
            var newHash = this.getHashFromTokens();

            this.setHash(newHash, silent);
        },

        /**
         * Обновляет коллекцию токенов из хэша
         * @private
         */
        updateTokensFromHash: function (hash) {
            var tokens = this.getTokensFromHash(hash),
                newTokenNames = [],
                removeTokenNames = [];

            // обновляем новыми
            Ext.Array.each(tokens, function (token) {
                this.setToken(token.name, token.values);
                newTokenNames.push(token.name);
            }, this);

            // удаляем те, которых нет в списке новых
            this.tokens.each(function (token) {
                if (newTokenNames.indexOf(token.name) === -1) {
                    removeTokenNames.push(token.name);
                }
            });

            Ext.Array.each(removeTokenNames, function (tokenName) {
                this.removeToken(tokenName);
            }, this);
        },

        /**
         * Генерит массив токенов из хэша
         * @param hash
         * @returns {Array}
         * @private
         */
        getTokensFromHash: function (hash) {
            var hashTokens = hash.split(this.tokenSplitter),
                rawTokens = [];

            Ext.Array.each(hashTokens, function (hashToken) {
                var tokenData = hashToken.split(this.valueSplitter),
                    tokenName = tokenData[0],
                    tokenValues = Ext.Object.fromQueryString(tokenData[1] || '', true),
                    token;

                if (!tokenName) {
                    return true;
                }

                token = {
                    name: tokenName,
                    values: tokenValues
                };

                this.updateTokenHash(token);

                rawTokens.push(token);

            }, this);

            return rawTokens;
        },

        /**
         * Собирает из токенов строку
         * @returns {string}
         * @private
         */
        getHashFromTokens: function () {
            var hashTokens = [];

            this.tokens.eachKey(function (key, item) {
                hashTokens.push(item.hash);
            }, this);

            return hashTokens.join(this.tokenSplitter);
        }
    }

});
