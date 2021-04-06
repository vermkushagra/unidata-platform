/**
 * Вспомогательный класс для работы с классами ui user exit кастомера
 *
 * @author Ivan Marshalkin
 * @date 2017-07-06
 */

Ext.define('Unidata.uiuserexit.CustomerClassesHelper', {
    singleton: true,

    /**
     * Возвращает массив классов кастомера наследованных от базового класса
     *
     * @param baseClass - базовый класс
     *
     * @returns {*|{displayName, editor, renderer}}
     */
    getAllCustomerClasses: function (baseClass) {
        var allClasses = Ext.ClassManager.classes,
            result,
            classes;

        classes = Ext.Object.getValues(allClasses);

        result = Ext.Array.filter(classes, function (cls) {
            return (cls && cls.prototype instanceof baseClass && cls !== baseClass);
        });

        return Ext.Array.unique(result);
    },

    /**
     * Возвращает массив активных классов кастомера наследованных от базового класса
     *
     * @param baseClass - базовый класс
     *
     * @returns {*|{displayName, editor, renderer}}
     */
    getAllActiveCustomerClasses: function (baseClass) {
        var classes = Unidata.uiuserexit.CustomerClassesHelper.getAllCustomerClasses(baseClass),
            result;

        result = Ext.Array.filter(classes, function (item) {
            return item.active === true;
        });

        return Ext.Array.unique(result);
    },

    /**
     * Возвращает массив неактивных классов кастомера наследованных от базового класса
     *
     * @param baseClass - базовый класс
     *
     * @returns {*|{displayName, editor, renderer}}
     */
    getAllInactiveCustomerClasses: function (baseClass) {
        var classes = Unidata.uiuserexit.CustomerClassesHelper.getAllCustomerClasses(baseClass),
            result;

        result = Ext.Array.filter(classes, function (item) {
            return item.active !== true;
        });

        return Ext.Array.unique(result);
    }
});
