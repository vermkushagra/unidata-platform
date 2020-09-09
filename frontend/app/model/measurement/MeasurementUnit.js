/**
 * Модель единицы измерения
 *
 * @author Ivan Marshalkin
 * @date 2016-11-08
 */

Ext.define('Unidata.model.measurement.MeasurementUnit', {
    extend: 'Unidata.model.Base',

    requires: [],

    idProperty: 'id',

    fields: [
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'shortName',
            type: 'string'
        },
        {
            name: 'valueId',
            type: 'int'
        },
        {
            name: 'convectionFunction',
            type: 'string'
        },
        {
            name: 'base',
            type: 'bool'
        }
    ]
});
