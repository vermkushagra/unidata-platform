/**
 * UPath объект, содержащий структурированную информацию о пути
 *
 * @author Sergey Shishigin
 * @date 2018-02-28
 */
Ext.define('Unidata.util.upath.UPath', {

    requires: ['Unidata.util.upath.UPathElement'],

    /**
     * Пока не используется
     *
     * @param {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
     */
    entity: null,

    /**
     * @param {UPathElement []} Элемент, описывающий токен upath
     */
    elements: null,

    /**
     * @param {String}
     */
    path: null,

    /**
     * Валидировать path по pathMap, если имеется entity
     */
    validatePath: true,

    /**
     * Map: path -> Unidata.model.attribute.AbstractAttribute
     *
     * Вычисляется, если имеется entity и установлен признак validatePath
     */
    pathMap: null,

    /**
     * Разделитель пути
     */
    PATH_DELIMITER: '.',

    /**
     * Символы для замены
     */
    specialSymbols: {
        '\\.': '&dot;',
        '\\]': '&rsqbracket;',
        '\\[': '&lsqbracket;',
        '\\}': '&rfigbracket;',
        '\\{': '&lfigbracket;',
        '\\:': '&colon;'
    },

    constructor: function (config) {
        var UPathMetaUtil = Unidata.util.UPathMeta;

        config = config || {};

        if (config.entity) {
            this.entity = config.entity;
        }

        if (config.validatePath) {
            this.validatePath = config.validatePath;
        }

        if (this.validatePath && this.entity) {
            this.pathMap = UPathMetaUtil.buildAttributePathMap(this.entity);
        }
    },

    /**
     * Сформировать канонический путь (без фильтров)
     *
     * напр: person.address.houseNumber
     *
     * @return {String|null}
     */
    toCanonicalPath: function () {
        var path,
            pathTokens;

        pathTokens = this.toPathTokens();

        if (!pathTokens) {
            return null;
        }

        path = pathTokens.join(this.PATH_DELIMITER);

        return path;
    },

    /**
     * Сформировать массив токенов пути
     *
     * @return {String[]|null}
     */
    toPathTokens: function () {
        var elements = this.elements,
            pathTokens;

        if (!Ext.isArray(elements)) {
            return null;
        }

        pathTokens = Ext.Array.map(elements, function (pathToken) {
            return pathToken.name;
        });

        return pathTokens;
    },

    /**
     * Сформировать UPath на основании массива элементов UPath
     *
     * @return {String|null}
     */
    toUPath: function () {
        var elements = this.elements,
            pathTokens,
            path;

        if (!Ext.isArray(elements)) {
            return null;
        }

        pathTokens = Ext.Array.map(elements, function (element) {
            var UPathElementTypeUtil = Unidata.util.upath.UPathElement.UPathElementType,
                pathToken;

            switch (element.type) {
                case UPathElementTypeUtil.SUBSCRIPT:
                    pathToken = Ext.String.format('{0}[{1}]', element.name, element.predicate);
                    break;
                case UPathElementTypeUtil.EXPRESSION:
                    pathToken = Ext.String.format('{0}\{{1}:{2}\}', element.name, element.predicate.property, element.predicate.value);
                    break;
                case UPathElementTypeUtil.COLLECTING:
                    pathToken = element.name;
                    break;
            }

            return pathToken;
        });

        path = pathTokens.join(this.PATH_DELIMITER);

        return path;
    },

    /**
     * Проверить, что токен является элементом типа SUBSCRIPT
     * Если это так, то вернуть UPathElement
     *
     * @param {String} pathToken
     * @return {UPathElement|null}
     */
    checkSubscriptFilter: function (pathToken) {
        var UPathElementType = Unidata.util.upath.UPathElement.UPathElementType,
            re = /^(.+)\[(\d+)\]$/,
            index,
            result,
            UPathResult = null,
            name;

        result = pathToken.match(re);

        if (!result) {
            return null;
        }

        name = result[1];
        index = result[2];

        if (Ext.isString(name) && Ext.isNumeric(index)) {
            name = this.decodeSpecialSymbols(name);
            UPathResult = Ext.create('Unidata.util.upath.UPathElement', {
                name: name,
                predicate: parseInt(index),
                type: UPathElementType.SUBSCRIPT
            });
        }

        return UPathResult;
    },

    /**
     * Проверить, что токен является элементом типа EXPRESSION
     * Если это так, то вернуть UPathElement
     *
     * @param {String} pathToken
     * @return {UPathElement|null}
     */
    checkExpressionFilter: function (pathToken) {
        var UPathElementType = Unidata.util.upath.UPathElement.UPathElementType,
            re = /^(.*)\{(.+)\}$/,
            splitRe = /([^:]*):(.*)/,
            filterTokens,
            filterStr,
            result,
            UPathResult = null,
            name,
            predicateProperty,
            predicateValue;

        result = pathToken.match(re);

        if (!result) {
            return null;
        }

        name = result[1];
        filterStr = result[2];
        filterTokens = filterStr.match(splitRe);

        if (Ext.isString(name) && filterTokens.length === 3) {
            predicateProperty = filterTokens[1];
            predicateValue = filterTokens[2];
            predicateProperty = this.decodeSpecialSymbols(predicateProperty);
            predicateValue = this.decodeSpecialSymbols(predicateValue);
            name = this.decodeSpecialSymbols(name);
            name = name || Unidata.util.upath.UPath.fullRecordPath;
            UPathResult = Ext.create('Unidata.util.upath.UPathElement', {
                name: name,
                type: UPathElementType.EXPRESSION,
                predicate: {
                    property: predicateProperty,
                    value: predicateValue
                }
            });
        }

        return UPathResult;
    },

    /**
     * Парсит upath и формирует массив UPathElement
     *
     * @param {String} upath
     *
     * @return {Unidata.util.upath.UPathElement[]|null}
     */
    fromUPath: function (upath) {
        var UPathElementType = Unidata.util.upath.UPathElement.UPathElementType,
            pathTokens,
            elements,
            result,
            previousCanonicalPathTokens = [];

        this.elements = [];
        this.path = upath;

        elements = this.getElements();

        if (!Ext.isString(upath) || upath.length < 1) {
            return null;
        }

        upath = this.encodeSpecialSymbols(upath);
        pathTokens = upath.split(this.PATH_DELIMITER);

        if (pathTokens.length < 1) {
            return null;
        }

        result = Ext.Array.every(pathTokens, function (pathToken) {
            var UPath = Unidata.util.upath.UPath,
                MetaAttributeUtil = Unidata.util.MetaAttribute,
                UPathElementUtil = Unidata.util.upath.UPathElement,
                UPathElement,
                re = /^[a-z][a-z0-9_-]*$/i,
                elementCanonicalPath,
                expressionPath,
                name;

            UPathElement = this.checkSubscriptFilter(pathToken);

            if (!UPathElement) {
                UPathElement = this.checkExpressionFilter(pathToken);
            }

            if (!UPathElement) {
                name = pathToken;
                name = this.decodeSpecialSymbols(name);
                UPathElement = Ext.create('Unidata.util.upath.UPathElement', {
                    name: name,
                    type: UPathElementType.COLLECTING
                });
            }

            previousCanonicalPathTokens.push(UPathElement.name);
            elementCanonicalPath = previousCanonicalPathTokens.join(this.PATH_DELIMITER);

            // если установлен признак необходимости валидации
            if (this.isValidate()) {
                // валидируем путь - проверяем, что он адресует существующие мета-атрибуты
                if (!this.doValidatePath(elementCanonicalPath)) {
                    return false;
                }

                if (UPathElement.type === UPathElementUtil.UPathElementType.EXPRESSION) {
                    // если тип токена: EXPRESSION
                    // то проверяем наличие атрибута по пути предиката

                    // спец кейс единственный токен вида {} для целой записи
                    if (previousCanonicalPathTokens.length === 1 && elementCanonicalPath === UPath.fullRecordPath) {
                        expressionPath = UPathElement.predicate.property;
                    } else {
                        expressionPath = previousCanonicalPathTokens.concat(UPathElement.predicate.property).join(this.PATH_DELIMITER);
                    }

                    if (!this.doValidatePath(expressionPath)) {
                        return false;
                    }
                } else if (UPathElement.type === UPathElementUtil.UPathElementType.SUBSCRIPT) {
                    // если тип токена: SUBSCRIPT
                    // то проверяем является ли комплесным атрибут, соответствующий пути
                    if (!MetaAttributeUtil.isComplexAttribute(this.pathMap[elementCanonicalPath])) {
                        return false;
                    }
                }
            }

            // пишем path ждя
            UPathElement.path = elementCanonicalPath;

            if (!re.test(UPathElement.name) && UPathElement.name !== Unidata.util.upath.UPath.fullRecordPath) {
                return false;
            }

            elements.push(UPathElement);

            return true;
        }, this);

        if (!result) {
            return null;
        }

        return elements;
    },

    /**
     * Заменить специальные символы на их кодовые эквиваленты
     * @param str {String} Строка
     * @return {String} Результат после замены
     */
    encodeSpecialSymbols: function (str) {
        var specialSymbols = this.specialSymbols;

        Ext.Object.each(specialSymbols, function (key, value) {
            str = Ext.String.replaceAll(str, key, value);
        });

        return str;
    },

    /**
     * Заменить кодовые эквиваленты обратно на специальные символы
     * @param str {String} Строка
     * @return {String} Результат после замены
     */
    decodeSpecialSymbols: function (str) {
        var specialSymbols = this.specialSymbols;

        Ext.Object.each(specialSymbols, function (key, value) {
            str = Ext.String.replaceAll(str, value, key);
        });

        return str;
    },

    isValidate: function () {
        return this.validatePath && this.pathMap;
    },

    /**
     * Провалидировать по pathMap, что для path существует рекорд
     * @param path
     * @return {*}
     */
    doValidatePath: function (path) {
        var pathMap = this.pathMap;

        if (!pathMap) {
            return false;
        }

        return path === Unidata.util.upath.UPath.fullRecordPath || this.pathMap[path];
    },

    getElements: function () {
        if (!this.elements) {
            this.elements = [];
        }

        return this.elements;
    },

    statics: {
        fullRecordPath: '{}'
    }
});
