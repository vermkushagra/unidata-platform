/**
 * Синглтон, который управляет хранением данных на backend
 *
 * @author Aleksandr Bavin
 * @date 2017-10-19
 *
 * @alias Unidata.BackendStorage
 */
Ext.define('Unidata.module.storage.BackendStorageManager', {

    alternateClassName: [
        'Unidata.BackendStorage'
    ],

    requires: [
        'Unidata.store.BackendStorageStore',
        'Unidata.model.settings.BackendStorageItem',
        'Unidata.proxy.storage.BackendStorageProxy'
    ],

    singleton: true,

    privates: {
        store: null
    },

    constructor: function () {
        this.callParent(arguments);
    },

    destroy: function () {
        this.callParent(arguments);

        Ext.destroyMembers(
            this,
            'store'
        );
    },

    /**
     * Генерит id для модели
     *
     * @param key
     * @param user
     * @returns {string}
     */
    modelIdGenerator: function (key, user) {
        return [key, user].join('-');
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
     * @returns {Ext.data.Store}
     */
    getStore: function () {
        if (!this.store) {
            this.store = Ext.create('Unidata.store.BackendStorageStore');
        }

        return this.store;
    },

    /**
     * Загружает данные по key + user или по key или по user,
     * null - валидное значение
     *
     * @param key - если указать undefined, то будут загружены все данные по user
     * @param user - если указать undefined, то будут загружены все данные по key
     * @returns {Ext.promise.Promise}
     */
    load: function (key, user) {
        var deferred = Ext.create('Ext.Deferred'),
            store = this.getStore(),
            storeProxy = store.getProxy();

        // настраиваем прокси для загрузки
        if (key === undefined) {
            storeProxy.setUrlParam('user_name', user);
        }

        if (user === undefined) {
            storeProxy.setUrlParam('key', key);
        }

        if (key !== undefined && user !== undefined) {
            storeProxy.setExtraParams({
                key: key,
                user_name: user
            });
        }

        store.load({
            callback: function (records, operation, success) {
                if (success) {
                    deferred.resolve(records);
                } else {
                    deferred.reject();
                }
            },
            addRecords: true,
            scope: this
        });

        // очищаем доп параметры, для корректной работы sync
        storeProxy.setExtraParams({});
        storeProxy.setUrlParams({});

        return deferred.promise;
    },

    /**
     * Загружает данные по key, для всех пользователей
     *
     * @param key
     * @returns {Ext.promise.Promise}
     */
    loadByKey: function (key) {
        return this.load(key, undefined);
    },

    /**
     * Загружает данные по user, со всеми значениеями key
     *
     * @param user
     * @returns {Ext.promise.Promise}
     */
    loadByUser: function (user) {
        return this.load(undefined, user);
    },

    /**
     * Загружает данные по текущему пользователю
     *
     * @returns {Ext.promise.Promise}
     */
    loadByCurrentUser: function () {
        return this.load(undefined, this.getCurrentUserLogin());
    },

    /**
     * Сохраняет изменения
     *
     * @returns {Ext.promise.Promise}
     */
    save: function () {
        var deferred = Ext.create('Ext.Deferred');

        this.getStore().sync({
            success: function () {
                deferred.resolve();
            },
            failure: function () {
                deferred.reject();
            },
            scope: this
        });

        return deferred.promise;
    },

    /**
     * @param {string} key
     * @param {string} user
     * @returns {Unidata.model.settings.BackendStorageItem|null}
     */
    getStoreItem: function (key, user) {
        return this.getStore().getById(this.modelIdGenerator(key, user));
    },

    /**
     * @param {string} key
     * @param {string} user
     * @returns {string}
     */
    getValue: function (key, user) {
        var storeItem = this.getStoreItem(key, user);

        if (storeItem) {
            return storeItem.get('value');
        }

        return null;
    },

    /**
     * @param {string} key
     * @returns {string}
     */
    getCurrentUserValue: function (key) {
        return this.getValue(key, this.getCurrentUserLogin());
    },

    /**
     * @param {string} key
     * @param {string} user
     * @param {string} value
     */
    setValue: function (key, user, value) {
        var storeItem = this.getStoreItem(key, user);

        if (storeItem) {
            storeItem.set('value', value);
        } else {
            this.getStore().add({
                key: key,
                user: user,
                value: value
            });
        }
    },

    /**
     * @param {string} key
     * @param {string} value
     */
    setCurrentUserValue: function (key, value) {
        this.setValue(key, this.getCurrentUserLogin(), value);
    },

    /**
     * @param {string} key
     * @param {string} user
     */
    removeValue: function (key, user) {
        var store = this.getStore(),
            storeItem = this.getStoreItem(key, user);

        if (storeItem) {
            store.remove(storeItem);
        }
    },

    /**
     * @param {string} key
     */
    removeCurrentUserValue: function (key) {
        this.removeValue(key, this.getCurrentUserLogin());
    }

});
