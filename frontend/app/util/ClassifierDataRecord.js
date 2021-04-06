/**
 * Утилитный класс для работы с классификаторами для записи
 *
 * @author Ivan Marshalkin
 * @date 2016-08-15
 */

Ext.define('Unidata.util.ClassifierDataRecord', {
    singleton: true,

    /**
     * Производит поиск ноды по имени классификатора в датарекорде. Возвращает первый найденный элемнет
     *
     * @param dataRecord
     * @param classifierName
     * @returns {*}
     */
    getFirstClassifierNodeByClassifierName: function (dataRecord, classifierName) {
        var result = null;

        dataRecord.classifiers().each(function (classifierNodeInfo) {
            if (!result && classifierNodeInfo.get('classifierName') === classifierName) {
                result = classifierNodeInfo;
            }
        });

        return result;
    },

    /**
     * Производит поиск нод по имени классификатора в датарекорде. Возвращает массив найденых элементов
     *
     * @param dataRecord
     * @param classifierName
     * @returns {Array}
     */
    getClassifierNodesByClassifierName: function (dataRecord, classifierName) {
        var result = [];

        dataRecord.classifiers().each(function (classifierNodeInfo) {
            if (classifierNodeInfo.get('classifierName') === classifierName) {
                result.push(classifierNodeInfo);
            }
        });

        return result;
    },

    /**
     * Возвращает атрибут из ноды с данными dataRecord по имени атрибута
     *
     * @param dataClassifierNode
     * @param attributeName
     * @returns {*}
     */
    getClassifierNodeAttributeByAttributeName: function (dataClassifierNode, attributeName) {
        var attribute = null,
            simpleAttributes,
            index;

        simpleAttributes = dataClassifierNode.simpleAttributes();

        index = simpleAttributes.findExact('name', attributeName);

        if (index !== -1) {
            attribute = simpleAttributes.getAt(index);
        }

        return attribute;
    },

    /**
     * Удаляет элемент из дата рекорда по имени классификтора
     *
     * @param dataRecord
     * @param classifierName
     */
    removeFirstClassifierNodeByClassifierName: function (dataRecord, classifierName) {
        var item = this.getFirstClassifierNodeByClassifierName(dataRecord, classifierName);

        if (item) {
            dataRecord.classifiers().remove(item);
        }
    },

    /**
     * Удаляет все элементы из дата рекорда по имени классификтора
     *
     * @param dataRecord
     * @param classifierName
     */
    removeClassifierNodesByClassifierName: function (dataRecord, classifierName) {
        var items = this.getClassifierNodesByClassifierName(dataRecord, classifierName);

        if (items) {
            dataRecord.classifiers().remove(items);
        }
    },

    /**
     * Удаляет указаные элементы из датарекорда
     *
     * @param dataRecord
     * @param items
     */
    removeClassifierNodes: function (dataRecord, items) {
        dataRecord.classifiers().remove(items);
    }
});
