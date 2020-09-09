/**
 * Класс содержит типовые функции сортировки
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.util.Sorter', {
    singleton: true,

    /**
     * Сортировка объектов моделей в соответствии со списком значений поля
     *
     * @param list Список
     * @param getterField Имя поля
     * @param defaultSortField
     * @param objectA Объект сортировки A
     * @param objectB Объект сортировки B
     * @returns {number}
     */
    byListSorterFn: function (list, getterField, defaultSortField, objectA, objectB) {
        var valueA,
            valueB,
            aIndex,
            bIndex;

        getterField = getterField || 'name';
        valueA = objectA.get(getterField);
        valueB = objectB.get(getterField);

        aIndex = list.indexOf(valueA);
        bIndex = list.indexOf(valueB);

        if (aIndex === -1 && bIndex === -1) {
            // если оба элемента отсутствуют в списке то сортируем по defaultSortField
            if (defaultSortField) {
                valueA = objectA.get(defaultSortField);
                valueB = objectB.get(defaultSortField);

                return valueA.localeCompare(valueB);
            } else {
                return 0;
            }
        } else if (aIndex === -1) {
            return 1;
        } else if (bIndex === -1) {
            return -1;
        }

        return aIndex - bIndex;
    }
});
