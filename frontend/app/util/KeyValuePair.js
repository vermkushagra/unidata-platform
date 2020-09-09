/**
 * Сервисный класс для работы с key/value pair
 *
 * @author Sergey Shishigin
 * @date 2016-03-11
 */

Ext.define('Unidata.util.KeyValuePair', {
    singleton: true,
    /**
     * Преобразует коллекцию keyValuePair моделей/ в объект
     * @param collection {Ext.util.Collection|Ext.data.Store} Коллекция
     * @param keyFieldName {String} Название поля ключа
     * @param valueFieldName {String} Название поля значения
     * @returns {Object} Объект
     */
    mapToObject: function (collection, keyFieldName, valueFieldName) {
        var obj = {};

        keyFieldName = keyFieldName || 'name';
        valueFieldName = valueFieldName || 'value';

        collection.each(function (item) {
            obj[item.get(keyFieldName)] = item.get(valueFieldName);
        });

        return obj;
    }
});
