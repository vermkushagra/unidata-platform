/**
 * Набор структур по отображению записей
 *
 * @author Sergey Shishigin
 * @date 2016-10-27
 */
Ext.define('Unidata.view.steward.cluster.merge.DataRecordBundle', {
    dataRecordKey: null,
    dataRecord: null,
    classifierNodes: null,

    setDataRecordKey: function (value) {
        this.dataRecordKey = value;
    },

    getDataRecordKey: function () {
        return this.dataRecordKey;
    },

    setDataRecord: function (value) {
        this.dataRecord = value;
    },

    getDataRecord: function () {
        return this.dataRecord;
    },

    setClassifierNodes: function (value) {
        this.classifierNodes = value;
    },

    getClassifierNodes: function () {
        return this.classifierNodes;
    },

    applyValues: function (cfg) {
        if (cfg.hasOwnProperty('dataRecordKey')) {
            this.dataRecordKey = cfg.dataRecordKey;
        }

        if (cfg.hasOwnProperty('dataRecord')) {
            this.dataRecord = cfg.dataRecord;
        }

        if (cfg.hasOwnProperty('classifierNodes')) {
            this.classifierNodes = cfg.classifierNodes;
        }
    },

    getValues: function () {
        return {
            dataRecordKey: this.dataRecordKey,
            dataRecord: this.dataRecord,
            classifierNodes: this.classifierNodes
        };
    }
});
