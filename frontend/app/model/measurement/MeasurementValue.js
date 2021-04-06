/**
 * Модель измеряемой величины
 *
 * @author Ivan Marshalkin
 * @date 2016-11-08
 */

Ext.define('Unidata.model.measurement.MeasurementValue', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.measurement.MeasurementUnit'
    ],

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
        }
    ],

    hasMany: [
        {
            name: 'measurementUnits',
            model: 'measurement.MeasurementUnit'
        }
    ],

    /**
     * Возвращает базовую единицу измерения для измеряемой величины (дефолтная)
     */
    getBaseMeasurementUnit: function () {
        var result = null;

        this.measurementUnits().each(function (measurementUnit) {
            if (measurementUnit.get('base')) {
                result = measurementUnit;

                return false; // остановка итерации this.measurementUnits().each
            }
        });

        return result;
    },

    /**
     * Возвращает идентификатор
     * базовой единицы измерения для измеряемой величины (дефолтная)
     */
    getBaseMeasurementUnitId: function () {
        var unit   = this.getBaseMeasurementUnit(),
            result = null;

        if (unit) {
            result = unit.get('id');
        }

        return result;
    }
});
