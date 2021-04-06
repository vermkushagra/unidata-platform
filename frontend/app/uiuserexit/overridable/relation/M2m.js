/**
 * Точка расширения для конфигурирования связей типа многие ко многим
 *
 * @author Ivan Marshalkin
 * @date 2017-10-24
 */

// пример использования
//
// Ext.define('CUX.overrides.DataCard', {
//     override: 'Unidata.uiuserexit.overridable.relation.M2m',
//
//     singleton: true,
//
//     getM2mRelationPageSize: function (metaRecord, dataRecord, metaRelation, dataRelation) {
//         var pageSize = 20;
//
//         if (metaRelation.get('name') === 'm2m1') {
//             return 2;
//         } else if (metaRelation.get('name') === 'm2m2') {
//             return 1;
//         }
//
//         return pageSize;
//     }
// });

Ext.define('Unidata.uiuserexit.overridable.relation.M2m', {
    singleton: true,

    /**
     * Метод возвращает количество записей на одной странице для списка записей связи с типом многие ко многим
     *
     * @param metaRecord
     * @param dataRecord
     * @param metaRelation
     * @param dataRelation
     * @returns {number}
     */
    getM2mRelationPageSize: function (metaRecord, dataRecord, metaRelation, dataRelation) { // jscs:ignore disallowUnusedParams
        var pageSize = 20;

        return pageSize;
    }
});
