/**
 * Хранилище моделей EtalonCluster уровня приложения
 *
 * @author Sergey Shishigin
 * @date 2016-09-01
 */
Ext.define('Unidata.module.storage.EtalonClusterStorage', {
    // Строка закомментирована для того, чтобы синглтон не создавался
    //singleton: true,

    requires: [
        'Unidata.model.Base',
        'Unidata.model.etaloncluster.EtalonCluster',
        'Unidata.model.etaloncluster.EtalonClusterRecord'
    ],

    /**
     * @private
     */
    etalonClusters: [],
    localStorage: null,

    constructor: function () {
        // build local storage
        this.localStorage = new Ext.util.LocalStorage({
            id: 'ud'
        });
        this.initFromLocalStorage();
    },

    /**
     * Начальная загрузка список записей из localStorage
     */
    initFromLocalStorage: function () {
        var localStorage = this.localStorage,
            jsonStr,
            etalonClustersObj;

        jsonStr = localStorage.getItem('etalonClusters');
        etalonClustersObj = Ext.util.JSON.decode(jsonStr);
        this.etalonClusters  = this.buildEtalonClusterModels(etalonClustersObj);
    },

    /**
     * Создать массив моделей
     * @param obj
     * @returns {*}
     */
    buildEtalonClusterModels: function (obj) {
        var reader,
            resultSet;

        if (!obj) {
            return [];
        }

        reader = Ext.create('Ext.data.JsonReader', {
            model: 'Unidata.model.etaloncluster.EtalonCluster'
        });

        resultSet = reader.readRecords(obj);

        return resultSet.getRecords();
    },

    /**
     * Добавить etalonCluster в хранилище
     * @param etalonCluster {Unidata.model.etaloncluster.EtalonCluster}
     */
    addEtalonCluster: function (etalonCluster) {
        this.etalonClusters.push(etalonCluster);
        this.persistToLocalStorage();
    },

    /**
     * Удалить списки записей
     *
     * @param etalonClusters {Unidata.model.etaloncluster.EtalonCluster[]}
     */
    removeEtalonClusters: function (etalonClusters) {
        Ext.Array.each(etalonClusters, this.removeEtalonCluster, this);
    },

    /**
     * Удалить etalonCluster из хранилища
     * @param etalonCluster {Unidata.model.etaloncluster.EtalonCluster}
     */
    removeEtalonCluster: function (etalonCluster) {
        Ext.Array.remove(this.etalonClusters, etalonCluster);
        this.persistToLocalStorage();
    },

    removeEtalonClusterRecords: function (etalonCluster, etalonClusterRecords) {
        var records = etalonCluster.records();

        if (!etalonCluster) {
            return;
        }

        records.remove(etalonClusterRecords);
        this.persistToLocalStorage();
    },

    /**
     * Получить все списки записей
     * @returns {Unidata.model.etaloncluster.EtalonCluster[]}
     */
    getEtalonClusters: function () {
        return this.etalonClusters;
    },

    getEtalonClusterByName: function () {
        //TODO: implement me
        throw new Error('Method is not implemented');
    },

    /**
     * Сохранить текущее состояние etalonClusters в локальное хранилище
     */
    persistToLocalStorage: function () {
        var etalonClustersObj;

        etalonClustersObj = Ext.Array.map(this.etalonClusters, this.mapEtalonClusterModelToObject, this);

        this.localStorage.setItem('etalonClusters', JSON.stringify(etalonClustersObj));
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
    buildEtalonClusterDefaultName: function (entityName, entityDisplayName) {
        var defaultName,
            count;

        count = this.getEtalonClustersCountByEntityName(entityName);

        defaultName = Ext.String.format('{0} {1}', entityDisplayName, count + 1);

        return defaultName;
    },

    /**
     * Получить кол-во списков записей по имени реестра
     * @param entityName {String}
     * @returns {Integer}
     */
    getEtalonClustersCountByEntityName: function (entityName) {
        return this.getEtalonClustersByEntityName(entityName).length;
    },

    /**
     * Получить списки записей по имени реестра
     * @param entityName {String}
     * @returns {Integer}
     */
    getEtalonClustersByEntityName: function (entityName) {
        return Ext.Array.filter(this.etalonClusters, function (etalonCluster) {
            return etalonCluster.get('entityName') === entityName;
        });
    }
});
