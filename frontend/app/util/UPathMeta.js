/**
 * Поддержка путей к атрибутам для метамодели вида complAttr1.complAttr2.attr3
 *
 * @author Sergey Shishigin
 * @date 2015-12-16
 */
Ext.define('Unidata.util.UPathMeta', {
    singleton: true,

    DELIMITER: '.',

    /**
     * Построение путей к атрибутам с возможностью использования фильтров значений
     * @param meta Метамодель (*Entity)
     * @param filters Фильтры для store (Object[]/Function[])
     * @param rootPathParts Массив путей верхнего уровня (для пользовательского вызова как правило не указывается)
     * @returns {*} Массив атрибутов (Unidata.model.attribute.*Attribute[])
     *
     * По поводу rootPathParts см.пример для вызова buildSimpleAttributePaths
     *
     * Примеры:
     * Получаем пути к атрибутам со свойством mainDisplayable = true
     *
     * var  filters = [{property: 'mainDisplayable', value: true}],
     *      paths = Unidata.util.UPathMeta.buildAttributePaths(meta, filters);
     */
    buildAttributePaths: function (meta, filters, rootPathParts) {
        //TODO: Реализовать поддержку текстового представления аргумента rootPathParts в виде cattr1.cattr2.attr3
        var me             = this,
            paths,
            localPathParts = [],
            complexAttributes;

        rootPathParts = rootPathParts || [];

        paths = me.buildSimpleAttributePaths(meta, filters, rootPathParts);
        paths = paths.concat(me.buildArrayAttributePaths(meta, filters, rootPathParts));

        if (typeof meta.complexAttributes === 'function') {
            complexAttributes = meta.complexAttributes();
            paths = this.buildAbstractAttributePaths(complexAttributes, filters, rootPathParts, paths, me.DELIMITER);

            complexAttributes.each(function (complexAttr) {
                meta         = complexAttr.getNestedEntity();
                localPathParts = rootPathParts.concat(meta.get('name'));

                if (meta) {
                    paths = paths.concat(me.buildAttributePaths(meta, filters, localPathParts));
                }
            });
        }

        return paths;
    },

    /**
     * Построение путей к абстрактным атрибутам с возможностью использования фильтров значений
     * В большинстве случаев вызов этого метода является внутренним
     *
     * @param attributes
     * @param filters
     * @param rootPathParts
     * @param paths
     * @param delimiter
     * @returns {*}
     */
    buildAbstractAttributePaths: function (attributes, filters, rootPathParts, paths, delimiter) {
        var oldFilters,
            localPathParts;

        if (filters) {
            attributes.setRemoteFilter(false);
            oldFilters = attributes.getFilters().items;
            attributes.setFilters(filters);
        }

        attributes.each(function (attr) {
            localPathParts = rootPathParts.concat(attr.get('name'));
            paths.push(localPathParts.join(delimiter));
        });

        if (filters) {
            // restore filters
            attributes.clearFilter();
            attributes.setFilters(oldFilters);
        }

        return paths;
    },

    /**
     * Построение путей к простым атрибутам с возможностью использования фильтров значений
     * В большинстве случаев вызов этого метода является внутренним
     * @param record Метамодель (*Entity)
     * @param filters Фильтры для store (Object[]/Function[])
     * @param rootPathParts Массив путей верхнего уровня
     * @returns {*} Массив путей (String[])
     *
     * Примеры:
     * Получаем пути к атрибутам со свойством mainDisplayable = true
     * и добавляем префикс 'complAttr1.' для полученных путей
     *
     * var  filters = [{property: 'mainDisplayable', value: true}],
     *      paths = Unidata.util.UPathMeta.buildSimpleAttributePaths(record, filters, 'complAttr1');
     */
    buildSimpleAttributePaths: function (record, filters, rootPathParts) {
        //TODO: Реализовать поддержку текстового представления аргумента rootPathParts в виде cattr1.cattr2.attr3
        var paths          = [],
            attributes     = record.simpleAttributes(),
            remoteSort = attributes.getRemoteSort(),
            aliasCodeAttributes = null,
            me             = this,
            localPathParts = [],
            codeAttr,
            tmpStore;

        attributes.setRemoteSort(false);
        attributes.sort([
            {
                property: 'order',
                direction: 'ASC'
            }
        ]);
        attributes.setRemoteSort(remoteSort);

        rootPathParts = rootPathParts || [];

        if (typeof record.aliasCodeAttributes === 'function') {
            aliasCodeAttributes = record.aliasCodeAttributes();
        }

        // if code attr is exists
        if (typeof record.getCodeAttribute === 'function') {
            tmpStore = Ext.create('Ext.data.Store', {
                model: 'Unidata.model.attribute.CodeAttribute'
            });

            codeAttr = record.getCodeAttribute();

            tmpStore.add(codeAttr);
            tmpStore.setFilters(filters);

            if (tmpStore.count()) {
                localPathParts = rootPathParts.concat(codeAttr.get('name'));
                paths.push(localPathParts.join(me.DELIMITER));
            }
        }

        paths = this.buildAbstractAttributePaths(attributes, filters, rootPathParts, paths, me.DELIMITER);

        if (aliasCodeAttributes) {
            paths = this.buildAbstractAttributePaths(aliasCodeAttributes, filters, rootPathParts, paths, me.DELIMITER);
        }

        return paths;
    },

    buildArrayAttributePaths: function (record, filters, rootPathParts) {
        var paths = [],
            attributes = record.arrayAttributes();

        rootPathParts = rootPathParts || [];

        paths = this.buildAbstractAttributePaths(attributes, filters, rootPathParts, paths, this.DELIMITER);

        return paths;
    },

    /**
     * Построение пути к атрибуту
     *
     * @param meta Метамодель (*Entity)
     * @param attribute Мета-атрибут, принадлежащий meta (CodeAttribute, SimpleAttribute, ComplexAttribute)
     * @param rootPathParts Массив путей верхнего уровня
     * @returns {*} Путь (String)
     *
     * rootPathParts в большинстве пользовательских вызовов не используется
     */
    buildAttributePath: function (meta, attribute, rootPathParts) {
        var isContains,
            path = null,
            me = this,
            localPathParts = [],
            className;

        rootPathParts = rootPathParts || [];

        if (Ext.isString(rootPathParts)) {
            rootPathParts = rootPathParts.split(me.DELIMITER);
        }

        className = Ext.getClassName(attribute);
        switch (className) {
            case 'Unidata.model.attribute.CodeAttribute':
                if (typeof meta.getCodeAttribute === 'function') {
                    if (attribute === meta.getCodeAttribute()) {
                        isContains = true;
                    }
                }
                break;
            case 'Unidata.model.attribute.AliasCodeAttribute':
                isContains = meta.aliasCodeAttributes().contains(attribute);
                break;
            case 'Unidata.model.attribute.SimpleAttribute':
                isContains = meta.simpleAttributes().contains(attribute);
                break;
            case 'Unidata.model.attribute.ComplexAttribute':
                isContains = meta.complexAttributes().contains(attribute);
                break;
            case 'Unidata.model.attribute.ArrayAttribute':
                isContains = meta.arrayAttributes().contains(attribute);
                break;
        }

        if (isContains) {
            path = rootPathParts.concat(attribute.get('name')).join(this.DELIMITER);
        } else if (typeof meta.complexAttributes === 'function') {
            meta.complexAttributes().each(function (complexAttr) {
                localPathParts = rootPathParts.concat(complexAttr.get('name'));
                path = me.buildAttributePath(complexAttr.getNestedEntity(), attribute, localPathParts);

                return !path;
            });
        }

        return path;
    },

    /**
     * Поиск комплексного атрибута по имени на верхнем уровне метамодели
     * @param entity Метамодель
     * @param name
     * @returns {*}
     */
    findComplexAttributeByName: function (entity, name) {
        var complexAttributes,
            foundIndex,
            found = null;

        if (typeof entity.complexAttributes === 'function') {
            complexAttributes = entity.complexAttributes();
            foundIndex = complexAttributes.findExact('name', name);

            if (foundIndex > -1) {
                found = complexAttributes.getAt(foundIndex);
            }
        }

        return found;
    },

    /**
     * Поиск простого атрибута по имени на верхнем уровне метамодели
     * @param entity Метамодель
     * @param name
     * @returns {*}
     */
    findSimpleAttributeByName: function (entity, name) {
        var simpleAttributes,
            foundIndex,
            found = null;

        simpleAttributes = entity.simpleAttributes();
        foundIndex = simpleAttributes.findExact('name', name);

        if (foundIndex > -1) {
            found = simpleAttributes.getAt(foundIndex);
        }

        return found;
    },

    /**
     * Поиск атрибута типа массив по имени на верхнем уровне метамодели
     * @param entity Метамодель
     * @param name
     * @returns {*}
     */
    findArrayAttributeByName: function (entity, name) {
        var attributes,
            foundIndex,
            found = null;

        attributes = entity.arrayAttributes();
        foundIndex = attributes.findExact('name', name);

        if (foundIndex > -1) {
            found = attributes.getAt(foundIndex);
        }

        return found;
    },

    /**
     * Поиск альтернативного кодового атрибута по имени на верхнем уровне метамодели
     * @param entity Метамодель
     * @param name
     * @returns {*}
     */
    findAliasCodeAttributeByName: function (entity, name) {
        var aliasCodeAttributes,
            foundIndex,
            found = null;

        if (typeof entity.aliasCodeAttributes !== 'function') {
            return null;
        }

        aliasCodeAttributes = entity.aliasCodeAttributes();
        foundIndex = aliasCodeAttributes.findExact('name', name);

        if (foundIndex > -1) {
            found = aliasCodeAttributes.getAt(foundIndex);
        }

        return found;
    },

    /**
     * Поиск кодового атрибута по имени
     * @param entity Метамодель
     * @param name
     * @returns {*}
     */
    findCodeAttributeByName: function (entity, name) {
        var found = null,
            codeAttribute;

        if (typeof entity.getCodeAttribute !== 'function') {
            return;
        }

        codeAttribute = entity.getCodeAttribute();

        if (codeAttribute.get('name') === name) {
            found = codeAttribute;
        }

        return found;
    },

    /**
     * Поиск любого атрибута по имени на верхнем уровне метамодели
     * @param entity Метамодель
     * @param name
     * @returns {*}
     */
    findAttributeByName: function (entity, name) {
        var found;

        // TODO: рефакторинг, сделать универсальный метод
        found = this.findSimpleAttributeByName(entity, name) ||
                this.findComplexAttributeByName(entity, name) ||
                this.findCodeAttributeByName(entity, name) ||
                this.findAliasCodeAttributeByName(entity, name) ||
                this.findArrayAttributeByName(entity, name);

        return found;
    },

    /**
     * Поиск одного атрибута в метамодели по его пути
     * @param meta Метамодель
     * @param path Путь
     * @returns {*} Атрибутов (Unidata.model.data.*Attribute)
     *
     * Примеры:
     * Получаем атрибуты типа Unidata.model.data.*Attribute в соответствии с путем к нему
     * var attribute = Unidata.util.UPathMeta.findAttributeByPath(meta, 'complAttr1.complAttr2.simpleAttr1');
     */
    findAttributeByPath: function (meta, path) {
        var pathParts    = null,
            nestedEntity = meta,
            result,
            me           = this,
            found        = null;

        // split path string to an array of path parts
        if (Ext.isString(path)) {
            pathParts = path.split(this.DELIMITER);
        } else if (Ext.isArray(path)) {
            pathParts = path;
        }

        if (pathParts) {
            result = pathParts.every(function (pathPart, index) {
                found = null;

                if (index < pathParts.length - 1) {
                    // iterate over complex attributes until last level
                    found = me.findComplexAttributeByName(nestedEntity, pathPart);
                    nestedEntity = found ? found.getNestedEntity() : null;
                } else {
                    // find attribute on last level
                    found = me.findAttributeByName(nestedEntity, pathPart);
                }

                return found;
            });
        }

        return result ? found : null;
    },

    findAttributesByPaths: function (meta, paths) {
        var attributes;

        attributes = Ext.Array.map(paths, function (path) {
            return this.findAttributeByPath(meta, path);
        }, this);

        return attributes;
    }

    // TODO: implement method get attributes by filter
});
