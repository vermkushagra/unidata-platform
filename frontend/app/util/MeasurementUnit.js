/**
 * Утилитный класс для работы с MeasurementUnits
 *
 * @author Sergey Shishigin
 * @date 2016-11-14
 */
// TODO: Можно объединить Unidata.util.MeasurementUnit и Unidata.util.Enumeration
Ext.define('Unidata.util.MeasurementUnit', {
    singleton: true,

    requires: [
        'Unidata.view.component.MeasurementUnitComboBox'
    ],

    /**
     * Фабричный метод по созданию combobox
     *
     * @param measurementValuesStore {Unidata.store.MeasurementValuesStore}
     * @param measurementValueId {Integer} Идентификатора измеряемой величины для которой должны отображаться единицы измерения
     * @param customCfg
     * @returns {Unidata.view.component.MeasurementUnitComboBox|*}
     */
    createMeasurementUnitComboBox: function (measurementValuesStore, measurementValueId, customCfg) {
        var combobox,
            store,
            measurementValue,
            measurementUnits,
            cfg,
            MeasurementValuesApi = Unidata.util.api.MeasurementValues;

        cfg = {};

        measurementValue = MeasurementValuesApi.getMeasurementValueById(measurementValueId);

        if (measurementValue) {
            measurementUnits = measurementValue.measurementUnits();

            store = Ext.create('Ext.data.ChainedStore', {
                source: measurementUnits,
                remoteSort: false,
                remoteFilter: false
            });

            cfg.store = store;
            cfg.measurementValue = measurementValue;
        }

        Ext.apply(cfg, customCfg);

        combobox = Ext.create('Unidata.view.component.MeasurementUnitComboBox', cfg);

        return combobox;
    },

    findMeasurementValue: function (measurementValuesStore, measurementValueId) {
        var measurementValue;

        measurementValue = measurementValuesStore.getById(measurementValueId);

        return measurementValue;
    }
});
