/**
 * Синглтон, который управляет хранением данных в localStorage.
 * Данные могут быть как общие, так и для конкретного пользователя.
 * Данные имеют срок хранения.
 *
 * @author Aleksandr Bavin
 * @date 2017-08-01
 *
 * @alias Unidata.LocalStorage
 */
Ext.define('Unidata.module.storage.LocalStorageManager', {

    alternateClassName: [
        'Unidata.LocalStorage'
    ],

    requires: [
        'Ext.util.HashMap'
    ],

    singleton: true,

    lifetime: 1000 * 60 * 60 * 24 * 30, // время жизни элемента данных в localStorage (ms)

    localStorageName: 'ud-data',

    /**
     * Объект для хранения данных:
        {
            user1: {
                namespace1: {
                    key1: {
                        value: 'someValue',
                        expires: 123
                    },
                    key2: ...
                },
                namespace2: ...
            },
            user2: ...
        }
     */
    localStorageData: null,

    constructor: function () {
        this.initLocalStorageData();
        this.callParent(arguments);
    },

    destroy: function () {
        this.callParent(arguments);

        this.localStorageData = null;
    },

    /**
     * Инициализация данных из localStorage
     * @protected
     */
    initLocalStorageData: function () {
        this.localStorageData = Ext.util.JSON.decode(localStorage.getItem(this.localStorageName)) || {};

        this.updateLocalStorage();
    },

    /**
     * Обновляет данные в localStorage из объекта localStorageData
     */
    updateLocalStorage: function () {
        var now = Date.now();

        // удаляем просроченные
        this.each(function (user, namespace, key, value, expires) {
            if (expires < now) {
                delete this.localStorageData[user][namespace][key];
            }
        }, this);

        this.cleanEmpty();

        localStorage.setItem(this.localStorageName, Ext.util.JSON.encode(this.localStorageData));
    },

    updateLocalStorageDelayed: function () {
        clearTimeout(this.localStorageUpdateTimer);

        this.localStorageUpdateTimer = Ext.defer(this.updateLocalStorage, 300, this);
    },

    /**
     * @function eachFn
     * @param {string} user
     * @param {string} namespace
     * @param {string} key
     * @param {*} value
     * @param {number} expires
     */

    /**
     * Перебирает все значения localStorageData
     *
     * @param {eachFn} fn
     * @param scope
     */
    each: function (fn, scope) {
        Ext.Object.each(this.localStorageData, function (user, namespaces) {
            Ext.Object.each(namespaces, function (namespace, keys) {
                Ext.Object.each(keys, function (key, data) {
                    fn.apply(scope || this, [user, namespace, key, data['value'], data['expires']]);
                }, this);
            }, this);
        }, this);
    },

    /**
     * Удаляет пустые объекты
     */
    cleanEmpty: function () {
        Ext.Object.each(this.localStorageData, function (user, namespaces, obj) {
            Ext.Object.each(namespaces, function (namespace, keys, obj) {
                if (Ext.Object.isEmpty(keys)) {
                    delete obj[namespace];
                }
            }, this);

            if (Ext.Object.isEmpty(namespaces)) {
                delete obj[user];
            }
        }, this);
    },

    /**
     * Возвращает значение объекта по пути path
     *
     * @param {Object} obj
     * @param {string|Array} path - строка вида X.Y.Z или массив
     * @returns {undefined|*}
     * @private
     */
    getObjectValue: function (obj, path) {
        var pathParts = Ext.isString(path) ? path.split('.') : path,
            currentObj = obj,
            objectHasValue = true;

        Ext.Array.each(pathParts, function (pathPart) {
            if (currentObj[pathPart]) {
                currentObj = currentObj[pathPart];
            } else {
                objectHasValue = false;

                return false;
            }
        });

        return objectHasValue ? currentObj : undefined;
    },

    /**
     * Гарантированно устанавливает значение для объекта по пути path
     *
     * @param {Object} obj
     * @param {string|Array} path - строка вида X.Y.Z или массив
     * @param {*} value
     * @private
     */
    setObjectValue: function (obj, path, value) {
        var pathParts = Ext.isString(path) ? path.split('.') : path,
            currentObj = obj;

        Ext.Array.each(pathParts, function (pathPart, index, array) {
            var isLast = (index === (array.length - 1));

            if (isLast) {
                currentObj[pathPart] = value;
            } else {
                if (currentObj[pathPart]) {
                    currentObj = currentObj[pathPart];
                } else {
                    currentObj = currentObj[pathPart] = {};
                }
            }
        });
    },

    /**
     * Устанавливает значение в localStorage
     *
     * @param {string} namespace
     * @param {string} key
     * @param {*} value
     * @param {string} [user]
     */
    setValue: function (namespace, key, value, user) {
        var expires = Date.now() + this.lifetime;

        user = Ext.isEmpty(user) ? undefined : user;

        this.setObjectValue(this.localStorageData, [user, namespace, key], {
            value: Ext.clone(value),
            expires: expires
        });

        this.updateLocalStorageDelayed();
    },

    /**
     * Возвращает значение из localStorage
     *
     * @param {string} namespace
     * @param {string} key
     * @param {string} [user]
     * @returns {*}
     */
    getValue: function (namespace, key, user) {
        var expires = Date.now() + this.lifetime,
            dataValue;

        user = Ext.isEmpty(user) ? undefined : user;

        dataValue = this.getObjectValue(this.localStorageData, [user, namespace, key]);

        if (!dataValue) {
            return undefined;
        }

        dataValue['expires'] = expires;
        this.updateLocalStorageDelayed();

        return dataValue['value'];
    },

    /**
     * Удаляет значение из localStorage
     *
     * @param {string} namespace
     * @param {string} key
     * @param {string} user
     * @returns {*}
     */
    removeValue: function (namespace, key, user) {
        user = Ext.isEmpty(user) ? undefined : user;

        if (this.getObjectValue(this.localStorageData, [user, namespace, key])) {
            delete this.localStorageData[user][namespace][key];
        }

        this.updateLocalStorageDelayed();
    },

    /**
     * Удаляет все значения из localStorage для данного namespace
     *
     * @param {string} namespace
     * @param {string} user
     * @returns {*}
     */
    removeNamespace: function (namespace, user) {
        user = Ext.isEmpty(user) ? undefined : user;

        if (this.getObjectValue(this.localStorageData, [user, namespace])) {
            delete this.localStorageData[user][namespace];
        }

        this.updateLocalStorageDelayed();
    },

    /**
     * Возвращает логин текущего пользователя
     *
     * @returns {string}
     */
    getCurrentUserLogin: function () {
        return Unidata.Config.getUser().get('login');
    },

    /**
     * Устанавливает значение в localStorage для текущего пользователя
     *
     * @param {string} namespace
     * @param {string} key
     * @param {*} value
     */
    setCurrentUserValue: function (namespace, key, value) {
        this.setValue(namespace, key, value, this.getCurrentUserLogin());
    },

    /**
     * Возвращает значение из localStorage для текущего пользователя
     *
     * @param {string} namespace
     * @param {string} key
     * @returns {*}
     */
    getCurrentUserValue: function (namespace, key) {
        return this.getValue(namespace, key, this.getCurrentUserLogin());
    },

    /**
     * Удаляет значение из localStorage для текущего пользователя
     *
     * @param {string} namespace
     * @param {string} key
     * @returns {*}
     */
    removeCurrentUserValue: function (namespace, key) {
        return this.removeValue(namespace, key, this.getCurrentUserLogin());
    },

    /**
     * Удаляет все значения из localStorage для данного namespace и текущего пользователя
     *
     * @param {string} namespace
     * @returns {*}
     */
    removeCurrentUserNamespace: function (namespace) {
        this.removeNamespace(namespace, this.getCurrentUserLogin());
    }

});
