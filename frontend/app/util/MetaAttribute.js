/**
 * Утилитный класс для мета информации атрибутов
 *
 * @author Ivan Marshalkin
 */

Ext.define('Unidata.util.MetaAttribute', {
    singleton: true,

    /**
     * Возвращает true если метаатрибут отмечен как скрытый, инача false
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isHidden: function (metaAttribute) {
        return Boolean(metaAttribute.get('hidden'));
    },

    /**
     * Возвращает истину, если атрибут является потомком альтернативного кодового атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isAliasCodeAttribute: function (metaAttribute) {
        return metaAttribute instanceof Unidata.model.attribute.AliasCodeAttribute;
    },

    /**
     * Возвращает истину, если атрибут является потомком кодового атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isCodeAttribute: function (metaAttribute) {
        return Ext.getClass(metaAttribute) === Unidata.model.attribute.CodeAttribute;

    },

    /**
     * Возвращает истину, если атрибут является array атрибутом
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isArrayAttribute: function (metaAttribute) {
        return metaAttribute instanceof Unidata.model.attribute.ArrayAttribute;
    },

    /**
     * Возвращает истину, если атрибут является потомком комплексного атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isComplexAttribute: function (metaAttribute) {
        return metaAttribute instanceof Unidata.model.attribute.ComplexAttribute;
    },

    /**
     * Возвращает истину, если атрибут является !!! _ПРЯМЫМ_ !!! потомком альтернативного кодового атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isAliasCodeAttributeExact: function (metaAttribute) {
        var result   = false,
            extClass = Ext.getClass(metaAttribute);

        if (extClass === Unidata.model.attribute.AliasCodeAttribute) {
            result = true;
        }

        return result;
    },

    /**
     * Возвращает истину, если атрибут является !!! _ПРЯМЫМ_ !!! потомком кодового атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isCodeAttributeExact: function (metaAttribute) {
        var result   = false,
            extClass = Ext.getClass(metaAttribute);

        if (extClass === Unidata.model.attribute.CodeAttribute) {
            result = true;
        }

        return result;
    },

    /**
     * Возвращает истину, если атрибут является !!! _ПРЯМЫМ_ !!! потомком array атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isArrayAttributeExact: function (metaAttribute) {
        var result   = false,
            extClass = Ext.getClass(metaAttribute);

        if (extClass === Unidata.model.attribute.ArrayAttribute) {
            result = true;
        }

        return result;
    },

    /**
     * Возвращает истину, если атрибут является !!! _ПРЯМЫМ_ !!! потомком комплексного атрибута
     *
     * @param metaAttribute
     * @returns {boolean}
     */
    isComplexAttributeExact: function (metaAttribute) {
        var result   = false,
            extClass = Ext.getClass(metaAttribute);

        if (extClass === Unidata.model.attribute.ComplexAttribute) {
            result = true;
        }

        return result;
    }
});
