/**
 * UPathElement, который хранит информацию об одном токене UPath
 *
 * @author Sergey Shishigin
 * @date 2018-02-28
 */
Ext.define('Unidata.util.upath.UPathElement', {

    /**
     * {Object|Integer|null}
     *
     * Если тип UPathElementType.SUBSCRIPT, то predicate - Integer
     * Если тип UPathElementType.EXPRESSION, то predicate - Object по аналогии с Ext.util.Filter
     *
     * Пример:
     * {
     *    property: 'surname',
     *   value: 'Иванов'
     * }
     *
     * Если тип UPathElementType.SUBSCRIPT, то predicate - null
     */
    predicate: null,

    /**
     * Имя элемента (строковый токен)
     * @param {String}
     */
    name: null,

    /**
     * Тип элемента
     */
    type: null,

    constructor: function (config) {
        this.name = config.name;
        this.name = this.name || '';

        if (!Ext.isEmpty(config.type)) {
            this.type = config.type;
        }

        if (!Ext.isEmpty(config.predicate)) {
            this.predicate = config.predicate;
        }

        if (!Ext.isEmpty(config.path)) {
            this.path = config.path;
        }

        if (!this.checkPredicate(this.type, this.predicate)) {
            throw new Error(Ext.String.format('UPathElement: Неверный предикат {0} для типа {1}', this.predicate, this.type));
        }
    },

    /**
     * Проверка предиката
     *
     * @param {String} type
     * @param {Object|Integer|null} predicate
     *
     * @return {boolean}
     */
    checkPredicate: function (type, predicate) {
        var UPathElementType = Unidata.util.upath.UPathElement.UPathElementType,
            result = false;

        switch (type) {
            case UPathElementType.SUBSCRIPT:
                result = Ext.isInteger(predicate);
                break;
            case UPathElementType.EXPRESSION:
                result = Ext.isObject(predicate) && predicate.hasOwnProperty('property') && predicate.hasOwnProperty('value');
                break;
            case UPathElementType.COLLECTING:
                result = predicate === null;
                break;
        }

        return result;
    },

    statics: {
        UPathElementType: {
            SUBSCRIPT: 'SUBSCRIPT',
            EXPRESSION: 'EXPRESSION',
            COLLECTING: 'COLLECTING'
        }
    }
});
