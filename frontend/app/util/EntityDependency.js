/**
 * Утилитный класс для работы с entityDependency
 *
 * @author Sergey Shishigin
 * @date 2016-11-18
 */
Ext.define('Unidata.util.EntityDependency', {
    singleton: true,

    /**
     * Проверяем entityDependency
     * @param entityDependency {Unidata.model.entity.EntityDependency}
     * @returns {boolean}
     */
    checkEntityDependency: function (entityDependency) {
        var sourceType,
            targetType,
            sourceKey,
            targetKey,
            result;

        if (!entityDependency) {
            return false;
        }

        sourceType = entityDependency.get('sourceType');
        targetType = entityDependency.get('targetType');
        sourceKey = entityDependency.get('sourceKey');
        targetKey = entityDependency.get('targetKey');

        result = this.checkType(sourceType) &&
                this.checkType(targetType) &&
                this.checkKey(sourceType, sourceKey) &&
                this.checkKey(targetType, targetKey);

        return result;
    },

    /**
     * Проверить тип
     * @param type {String}
     * @returns {boolean}
     */
    checkType: function (type) {
        var result,
            typesList             = this.getTypesList();

        result = Ext.Array.contains(typesList, type);

        return result;
    },

    /**
     * Проверить ключ
     *
     * @param type {String}
     * @param key {Object|*}
     * @returns {boolean}
     */
    checkKey: function (type, key) {
        var EntityDependencyType = Unidata.model.entity.EntityDependencyType,
            result;

        if (!Ext.isObject(key)) {
            return false;
        }

        switch (type) {
            case EntityDependencyType.MATCHING_RULE:
                result = key.hasOwnProperty('ruleId');
                break;
            case EntityDependencyType.ATTRIBUTE:
                result = key.hasOwnProperty('fullAttributeName') && key.hasOwnProperty('entityName');
                break;
            case EntityDependencyType.RELATION:
                result = key.hasOwnProperty('relName');
                break;
            default:
                result = false;
                break;
        }

        return result;
    },

    /**
     * Получить список типов entityDependency
     *
     * @returns {string[String]}
     */
    getTypesList: function () {
        return ['MATCHING_RULE', 'ATTRIBUTE', 'RELATION'];
    }
});
