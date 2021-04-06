/**
 * Прокси для работы с величинами единиц измерения
 *
 * @author Ivan Marshalkin
 * @date 2016-11-09
 */

Ext.define('Unidata.proxy.entity.MeasurementValuesProxy', {
    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.measurementvalues',

    url: Unidata.Api.getMeasurementValuesUrl(),

    reader: {
        type: 'json',
        model: 'Unidata.model.measurement.MeasurementValue',
        rootProperty: 'content'
    }
});
