/**
 * Утилитный класс для работы с DataRecordBundles
 *
 * @author
 * @date
 */

Ext.define('Unidata.util.DataRecordBundle', {
    singleton: true,

    findDataRecord: function (dataRecordBundles, dataRecordKey) {
        var dataRecordBundle;

        dataRecordBundle = Ext.Array.findBy(dataRecordBundles, this.findDataRecordBundleFn.bind(this, dataRecordKey), this);

        return dataRecordBundle ? dataRecordBundle.dataRecord : null;
    },

    findDataRecordBundle: function (dataRecordBundles, dataRecordKey) {
        return Ext.Array.findBy(dataRecordBundles, this.findDataRecordBundleFn.bind(this, dataRecordKey), this);
    },

    /**
     * @private
     * @param dataRecordKey
     * @param dataRecordBundle
     * @returns {boolean}
     */
    findDataRecordBundleFn: function (dataRecordKey, dataRecordBundle) {
        var etalonId,
            date,
            bundleDataRecordKey;

        if (!dataRecordKey) {
            return false;
        }

        etalonId = dataRecordKey.get('etalonId');
        date = dataRecordKey.get('date');

        bundleDataRecordKey = dataRecordBundle.dataRecordKey;

        if (!bundleDataRecordKey) {
            return false;
        }

        return dataRecordKey.get('etalonId') === bundleDataRecordKey.get('etalonId');
    },

    /**
     * Создать dataRecordBundle
     *
     * @param cfg
     * @returns {Unidata.view.steward.cluster.merge.DataRecordBundle|*}
     */
    buildDataRecordBundle: function (cfg) {
        var dataRecordBundle,
            etalonId = cfg.etalonId,
            dataRecord = cfg.dataRecord,
            dataRecordKey = cfg.dataRecordKey;

        if (!dataRecordKey) {
            cfg.dataRecordKey = Unidata.util.DataRecord.buildDataRecordKey({
                etalonId: etalonId,
                dataRecord: dataRecord
            });
        }

        dataRecordBundle = Ext.create('Unidata.view.steward.cluster.merge.DataRecordBundle');
        Ext.apply(dataRecordBundle, cfg);

        return dataRecordBundle;
    },

    retrieveEtalonId: function (dataRecordBundle) {
        var etalonId = null,
            dataRecordKey;

        if (dataRecordBundle) {
            dataRecordKey = dataRecordBundle.dataRecordKey;

            if (dataRecordKey) {
                etalonId = dataRecordKey.get('etalonId');
            }
        }

        return etalonId;
    }
});
