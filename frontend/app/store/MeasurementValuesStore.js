/**
 * Хранилище величин
 *
 * @author Ivan Marshalkin
 * @date 2016-11-09
 */

Ext.define('Unidata.store.MeasurementValuesStore', {
    extend: 'Ext.data.Store',

    alias: 'store.un.measurementvalues',

    model: 'Unidata.model.measurement.MeasurementValue',

    proxy: {
        type: 'un.measurementvalues',

        limitParam: '',
        startParam: '',
        pageParam: ''
    }
});
