/**
 * Поддержка путей к атрибутам для записей вида complAttr1.complAttr2.attr3
 * @author Sergey Shishigin
 * @date 2015-12-16
 */
Ext.define('Unidata.util.UPathData', {
    singleton: true,

    DELIMITER: '.',

    buildAttributePath: function () {
        //TODO: implement me
    },

    /**
     * Построение путей к атрибутам
     * @param record Датарекорд
     * @param rootPathParts Массив путей верхнего уровня (для пользовательского вызова как правило не указывается)
     * @returns {*} Массив атрибутов (Unidata.model.data.*Attribute[])
     *
     * По поводу rootPathParts см.пример для вызова buildSimpleAttributePaths
     */
    buildAttributePaths: function (record, rootPathParts) {
        var me             = this,
            paths,
            localPathParts = [];

        rootPathParts = rootPathParts || [];

        paths = me.buildSimpleAttributePaths(record, rootPathParts);

        if (typeof record.complexAttributes === 'function') {
            record.complexAttributes().each(function (complexAttr) {
                complexAttr.nestedRecords().each(function (nestedRecord) {
                    localPathParts = rootPathParts.concat(complexAttr.get('name'));

                    if (nestedRecord) {
                        paths = paths.concat(me.buildAttributePaths(nestedRecord, localPathParts));
                    }
                });
            });
        }

        return paths;
    },

    /**
     * Построение путей к простым атрибутам
     * В большинстве случаев вызов этого метода является внутренним
     * @param record Датарекорд
     * @param rootPathParts Массив путей верхнего уровня
     * @returns {*} Массив путей (String[])
     */
    buildSimpleAttributePaths: function (record, rootPathParts) {
        var paths          = [],
            attributes     = record.simpleAttributes(),
            me             = this,
            localPathParts = [];

        rootPathParts = rootPathParts || [];

        attributes.each(function (attr) {
            localPathParts = rootPathParts.concat(attr.get('name'));
            paths.push(localPathParts.join(me.DELIMITER));
        });

        return paths;
    },

    /**
     * Поиск атрибутов в записи по массиву путей
     * @param record Запись
     * @param paths Массив путей
     * @returns {*} Массив атрибутов (Unidata.model.data.*Attribute[])
     *
     * Примеры:
     * Получаем атрибуты типа Unidata.model.data.*Attribute в соответствии со списком путей к ним
     * var  paths = ['simpleAttr1', 'complAttr1.complAttr2.simpleAttr2'],
     *      attributes = Unidata.util.UPathData.findAttributeByPaths(record, paths);
     */
    findAttributesByPaths: function (record, paths) {
        var attributes = [],
            attribute,
            me         = this,
            result;

        if (!paths) {
            return;
        }

        result = paths.every(function (path) {
            attribute = me.findFirstAttributeByPath(record, path);

            if (attribute) {
                attributes.push(attribute);
            }

            return Boolean(attribute);
        });

        return result ? attributes : null;
    },

    /**
     * Поиск одного атрибута в записи по его пути
     * @param record Запись
     * @param path Путь
     * @returns {*} Атрибутов (Unidata.model.data.*Attribute)
     *
     * Примеры:
     * Получаем атрибуты типа Unidata.model.data.*Attribute в соответствии с путем к нему
     * var attribute = Unidata.util.UPathData.findFirstAttributeByPath(record, 'complAttr1.complAttr2.simpleAttr1');
     */
    findFirstAttributeByPath: function (record, path) {
        var pathParts      = null,
            foundIndex,
            nestedRecord   = record,
            result,
            foundAttribute = null;

        if (Ext.isString(path)) {
            pathParts = path.split(this.DELIMITER);
        } else if (Ext.isArray(path)) {
            pathParts = path;
        }

        function findComplexAttributeByPath (pathPart) {

            if (!Ext.isFunction(nestedRecord.complexAttributes)) {
                return -1;
            }

            foundIndex = nestedRecord.complexAttributes().findBy(function (complAttr) {
                return complAttr.get('name') === pathPart;
            });

            return foundIndex;
        }

        if (pathParts) {
            result = pathParts.every(function (pathPart, index) {
                if (index < pathParts.length - 1) {
                    foundIndex = findComplexAttributeByPath(pathPart);

                    if (foundIndex > -1) {
                        // TODO: реализовать для случая многих комплексных атрибутов
                        nestedRecord = nestedRecord.complexAttributes().getAt(foundIndex).nestedRecords().first();
                    }
                } else {
                    foundIndex = nestedRecord.simpleAttributes().findExact('name', pathPart);

                    if (foundIndex > -1) {
                        foundAttribute = nestedRecord.simpleAttributes().getAt(foundIndex);
                    } else {
                        if (nestedRecord.arrayAttributes) {
                            foundIndex = nestedRecord.arrayAttributes().findExact('name', pathPart);
                        }

                        if (foundIndex > -1) {
                            foundAttribute = nestedRecord.arrayAttributes().getAt(foundIndex);

                            return true;
                        }

                        if (typeof nestedRecord.codeAttributes === 'function') {
                            foundIndex = nestedRecord.codeAttributes().findExact('name', pathPart);
                        }

                        if (foundIndex > -1) {
                            foundAttribute = nestedRecord.codeAttributes().getAt(foundIndex);
                        } else {
                            foundIndex = findComplexAttributeByPath(pathPart);

                            if (foundIndex > -1) {
                                foundAttribute = nestedRecord.complexAttributes().getAt(foundIndex);
                            }
                        }
                    }
                }

                return nestedRecord && (foundAttribute || foundIndex > -1);
            });
        }

        return result ? foundAttribute : null;
    }
});
