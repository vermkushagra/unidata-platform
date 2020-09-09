/**
 * API загрузки cleanse functions
 *
 * @author Sergey Shishigin
 */

Ext.define('Unidata.util.api.CleanseFunction', {
    singleton: true,

    /**
     * Кэш вида
     * 'draft' -> (cleanseFunctionFullName -> cleanseFunction)
     * 'original' -> (cleanseFunctionFullName -> cleanseFunction)
     *
     * {Object}
     */
    functionsCache: null,

    /**
     * Загрузка списка cleanse функций
     * @returns {Ext.promise}
     */
    loadCleanseFunctionList: function () {
        var deferred,
            store;

        store = this.createCleanseFunctionStore();
        deferred = new Ext.Deferred();
        store.on('load', function (store, records, successful) {
            var cleanseFunctions;

            if (successful) {
                cleanseFunctions = store.first();
                deferred.resolve(cleanseFunctions);
            } else {
                deferred.reject();
            }
        });

        store.load();

        return deferred.promise;
    },

    /**
     * Получить объект кэша функций
     * @return {Object|null}
     */
    getFunctionsCache: function () {
        if (!Ext.isObject(this.functionsCache)) {
            this.functionsCache = {draft: {}, original: {}};
        }

        return this.functionsCache;
    },

    /**
     * Получить имя подраздела кэша в зависимости от свойства draft
     * @param draft
     * @return {string}
     */
    getDraftMode: function (draft) {
        return draft ? 'draft' : 'original';
    },

    /**
     * Получить cleanse function из кэша по полному имени
     * @param name Полное имя cleanse функции
     * @param draft Признак draft
     * @return {*}
     */
    getFromCacheByName: function (name, draft) {
        var functionsCache = this.getFunctionsCache(),
            draftMode;

        draft = Ext.isBoolean(draft) ? draft : false;
        draftMode = this.getDraftMode(draft);

        return functionsCache[draftMode][name];
    },

    /**
     * Записать в кэш cleanse function по полному имени
     * @param name Полное имя cleanse функции
     * @param cleanseFunction {Unidata.model.cleansefunction.CleanseFunction|Unidata.model.cleansefunction.CompositeCleanseFunction}
     * @param draft Признак draft
     * @param override Признак перезаписи функции в случае ее наличия
     */
    putToCache: function (name, cleanseFunction, draft, override) {
        var functionsCache = this.getFunctionsCache(),
            draftMode;

        override = Ext.isBoolean(override) ? override : true;
        draft = Ext.isBoolean(draft) ? draft : false;
        draftMode = this.getDraftMode(draft);

        if (override || Ext.isEmpty(functionsCache[name])) {
            functionsCache[draftMode][name] = cleanseFunction;
        }
    },

    /**
     * Удалить из кеша по имени
     * @param name Полное имя cleanse функции
     * @param draft Признак draft
     */
    deleteFromCacheByName: function (name, draft) {
        var functionsCache = this.getFunctionsCache(),
            draftMode;

        draft = Ext.isBoolean(draft) ? draft : false;
        draftMode = this.getDraftMode(draft);
        delete functionsCache[draftMode][name];
    },

    /**
     * Сброс кэша
     */
    resetCache: function () {
        this.functionsCache = null;
    },

    /**
     * Загрузить cleanse functions по полному имени (пути)
     * @param fullName
     * @returns {Ext.promise}
     */
    loadCleanseFunction: function (fullName, draft) {
        var me = this,
            deferred,
            cleanseFunction;

        deferred = new Ext.Deferred();

        if (!fullName) {
            Unidata.model.cleansefunction.CleanseFunction.load(fullName, {
                success: function (cleanseFunction) {
                    cleanseFunction.set('fullName', fullName);
                    deferred.resolve(cleanseFunction);
                },
                failure: function () {
                    deferred.reject();
                }
            });

            return deferred.promise;
        }

        // пытаемся получить cleanse function из кеша
        draft = Ext.isBoolean(draft) ? draft : false;
        cleanseFunction = this.getFromCacheByName(fullName, draft);

        if (cleanseFunction) {
            deferred.resolve(cleanseFunction);
        } else {
            // если cleanse function отсутвует в кэше, то грузим с бэкенда
            Unidata.model.cleansefunction.CleanseFunction.load(fullName, {
                success: function (cleanseFunction) {
                    cleanseFunction.set('fullName', fullName);
                    me.putToCache(fullName, cleanseFunction, draft);
                    deferred.resolve(cleanseFunction);
                },
                failure: function () {
                    deferred.reject();
                }
            });
        }

        return deferred.promise;
    },

    /**
     * Создать store
     * @returns {Ext.data.Store}
     */
    createCleanseFunctionStore: function () {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'cleansefunction.Group',
            autoLoad: false,
            proxy: {
                type: 'rest',
                url: Unidata.Api.getCleanseFunctionsUrl()
            }
        });

        return store;
    },

    /**
     * Выполнить cleanse function
     * @param jsonData
     * @returns {Ext.promise}
     */
    executeCleanseFunction: function (jsonData) {
        var deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: Unidata.Api.getCleanseFunctionExecuteUrl(),
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            jsonData: Ext.util.JSON.encode(jsonData),
            success: function (response) {
                var jsonResp;

                if (response.status === 200) {
                    jsonResp = Ext.util.JSON.decode(response.responseText);

                    if (jsonResp.resultCode === 'ok') {
                        deferred.resolve(jsonResp);
                    } else {
                        deferred.reject(response);
                    }
                } else {
                    deferred.reject(response);
                }
            },
            failure: function (response) {
                deferred.reject(response);
            }
        });

        return deferred.promise;
    }
});
