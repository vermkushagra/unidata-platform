/**
 * Утилитный класс комплексных атрибутов, для использования в dataEntity
 *
 * @author Ivan Marshalkin
 * @date 2016-02-22
 */

Ext.define('Unidata.view.steward.dataentity.util.ComplexAttribute', {
    singleton: true,

    /**
     * Создает данные по комплексному атрибуту с учетом требуемого минимального количества заданного в метамодели
     *
     * @param metaComplexAttribute - мета информация комплексного атрибута
     * @param dataComplexAttribute - данные комплексного атрибута
     */
    constrainDataComplexAttributeByMinCount: function (metaComplexAttribute, dataComplexAttribute) {
        var minCount           = metaComplexAttribute.get('minCount'),
            dataAttributeCount = dataComplexAttribute.nestedRecords().getCount(),
            nestedRecordNew,
            i;

        // если количество меньше чем минимальное => создаем до minCount
        if (dataAttributeCount < minCount) {
            for (i = dataAttributeCount; i < minCount; i++) {
                nestedRecordNew = Ext.create('Unidata.model.data.NestedRecord');
                dataComplexAttribute.nestedRecords().add(nestedRecordNew);
            }
        }
    },

    /**
     * Создает комплексный атрибут с данными, если в dataNested не существует комлпексного атрибута
     * с именем complexAttributeName
     *
     * Пояснение:
     * комплексный атрибут может быть не создан, например, если родительские данне были созданы
     * по условию minCount/maxCount метамодели, тогда вложенных комплексных атрибутов не будет
     *
     * @param dataNested           - вложенный элемент с данными
     * @param complexAttributeName - имя компклексного атрибута для проверки
     */
    createDataComplexAttributeIfNotExist: function (dataNested, complexAttributeName) {
        var dataComplexAttributes = dataNested.complexAttributes(),
            dataComplexAttributeNew;

        if (dataComplexAttributes.findExact('name', complexAttributeName) === -1) {
            dataComplexAttributeNew = Ext.create('Unidata.model.data.ComplexAttribute', {
                name: complexAttributeName
            });

            dataComplexAttributes.add(dataComplexAttributeNew);
        }
    },

    /**
     * Возвращает комплексный атрибут с данными по его имени. Если атрибут не найден, возвращается null
     *
     * @param dataNested           - nested элемент с данными
     * @param complexAttributeName - имя комплексного атрбитута для поиска
     * @returns {*}
     */
    findDataComplexAttributeByName: function (dataNested, complexAttributeName) {
        return this.findComplexAttributeByName(dataNested, complexAttributeName);
    },

    /**
     * Возвращает комплексный атрибут с метаинформацией по его имени. Если атрибут не найден, возвращается null
     *
     * @param metaNested               - nested элемент с метоинформацией
     * @param metaComplexAttributeName - имя комплексного атрбитута для поиска
     * @returns {*}
     */
    findMetaComplexAttributeByName: function (metaNested, metaComplexAttributeName) {
        return this.findComplexAttributeByName(metaNested, metaComplexAttributeName);
    },

    /**
     * Возвращает комплексный атрибут по имени. Если атрибут не найден, возвращается null
     *
     * @param nested               - nested элемент с complexAttributes
     * @param complexAttributeName - имя комплексного атрибута для поиска
     * @returns {*}
     */
    findComplexAttributeByName: function (nested, complexAttributeName) {
        var complexAttributes     = nested.complexAttributes(),
            foundComplexAttribute = null,
            index;

        index = complexAttributes.findExact('name', complexAttributeName);

        if (index !== -1) {
            foundComplexAttribute = complexAttributes.getAt(index);
        }

        return foundComplexAttribute;
    }
});
