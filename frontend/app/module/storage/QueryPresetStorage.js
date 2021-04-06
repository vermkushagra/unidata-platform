/**
 * Хранилище моделей QueryPresetStorage
 *
 * @author Sergey Shishigin
 * @date 2016-09-01
 */
Ext.define('Unidata.module.storage.QueryPresetStorage', {
    singleton: true,

    requires: [
        'Unidata.model.Base',
        'Unidata.model.search.QueryPreset'
    ],

    queryPresetStore: null,
    localStorage: null,

    constructor: function () {
        this.localStorage = Ext.util.LocalStorage.get('ud');

        if (!this.localStorage) {
            // build local storage with id: 'ud'
            this.localStorage = new Ext.util.LocalStorage({
                id: 'ud'
            });
        }

        this.initFromLocalStorage();
    },

    /**
     * Начальная загрузка список записей из localStorage
     */
    initFromLocalStorage: function () {
        var localStorage = this.localStorage,
            jsonStr,
            queryPresetsObj;

        jsonStr = localStorage.getItem('queryPresets');
        queryPresetsObj = Ext.util.JSON.decode(jsonStr);
        this.queryPresetStore  = this.buildQueryPresetStore(queryPresetsObj);
    },

    /**
     * Создать store
     * @param obj
     * @returns {*}
     */
    buildQueryPresetStore: function (obj) {
        var reader,
            resultSet,
            range,
            store = this.queryPresetStore;

        if (!store) {
            store = Ext.create('Ext.data.Store', {
                model: 'Unidata.model.search.QueryPreset',
                proxy: {
                    //enablePaging: true,
                    type: 'memory'
                }
            });
        }

        if (obj) {
            reader = Ext.create('Ext.data.JsonReader', {
                model: 'Unidata.model.search.QueryPreset'
            });

            resultSet = reader.readRecords(obj);

            range = resultSet.getRecords();
            store.getProxy().setData(range);
            store.load();
        }

        return store;
    },

    addQueryPreset: function (queryPreset) {
        this.queryPresetStore.add(queryPreset);
        this.persistToLocalStorage();
    },

    removeQueryPresets: function (queryPresets) {
        this.queryPresetStore.remove(queryPresets);
        this.persistToLocalStorage();
    },

    removeQueryPreset: function (queryPreset) {
        this.queryPresetStore.remove(queryPreset);
        this.persistToLocalStorage();
    },

    /**
     * Сохранить текущее состояние queryPresets в локальное хранилище
     */
    persistToLocalStorage: function () {
        var queryPresetsObj,
            queryPresets;

        queryPresets = this.queryPresetStore.getRange();
        queryPresetsObj = Ext.Array.map(queryPresets, this.mapEtalonClusterModelToObject, this);

        this.localStorage.setItem('queryPresets', JSON.stringify(queryPresetsObj));
    },

    /**
     * @private
     * @param model
     * @returns {*|string}
     */
    mapEtalonClusterModelToObject: function (model) {
        return model.getData(true);
    },

    /**
     * Построить имя набора записей по умолчанию (Имя реестра N)
     * @param entityName {String}
     * @param entityDisplayName {String}
     * @returns {String}
     */
    buildQueryPresetDefaultName: function (entityName) {
        var defaultName,
            count;

        count = this.getQueryPresetsCountByEntityName(entityName);

        defaultName = Unidata.i18n.t('glossary:searchQuery') + ' ' + (count + 1);

        return defaultName;
    },

    getQueryPresetsCountByEntityName: function (entityName) {
        return this.getQueryPresetsByEntityName(entityName).length;
    },

    getQueryPresetsByEntityName: function (entityName) {
        var queryPresets = this.queryPresetStore.getRange();

        return Ext.Array.filter(queryPresets, function (queryPreset) {
            return queryPreset.get('entityName') === entityName;
        });
    },

    validateQueryPresetName: function (name, entityName) {
        var index;

        if (!name) {
            return Unidata.i18n.t('validation:somethingCantBeEmpty', {name:  Unidata.i18n.t('glossary:searchQueryName')});
        }

        index = this.queryPresetStore.findBy(function (item) {
            return item.get('name') === name && item.get('entityName') === entityName;
        });

        if (index > -1) {
            return Unidata.i18n.t('search>preset.queryNameUnique');
        }

        return true;
    }
});
