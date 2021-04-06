/**
 * Базовый класс определения формул из ui user exit
 *
 * @author Ivan Marshalkin
 * @date 2017-07-06
 */

Ext.define('Unidata.uiuserexit.viewmodel.FormulaBase', {
    requires: [
        'Unidata.uiuserexit.viewmodel.FormulaTypes',
        'Unidata.uiuserexit.CustomerClassesHelper'
    ],

    inheritableStatics: {
        // включен / выключен (выключеные не учитываются)
        active: false,

        order: 0,

        // тип (место использования)
        formulaType: Unidata.uiuserexit.viewmodel.FormulaTypes.UNKNOWN,

        // строковое имя формулы (должно быть уникальным в системе)
        formulaName: null,

        // конфиг формулы
        formula: {},

        isProvideable: function () {
            console.log(Unidata.i18n.t('other>warningCalledBaseMethod', {method: 'FormulaBase->isProvideable'}));

            return false;
        }
    },

    statics: {
        /**
         * Возвращает все классы кастомера для определения формул
         *
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllCustomerFormulaClasses: function () {
            var baseClass = Unidata.uiuserexit.viewmodel.FormulaBase,
                result;

            result = Unidata.uiuserexit.CustomerClassesHelper.getAllCustomerClasses(baseClass);

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все активные классы кастомера для определения формул
         *
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllActiveCustomerFormulaClasses: function () {
            var baseClass = Unidata.uiuserexit.viewmodel.FormulaBase,
                result;

            result = Unidata.uiuserexit.CustomerClassesHelper.getAllActiveCustomerClasses(baseClass);

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все классы кастомера для определения формул по типу (месту использования) формул
         *
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllCustomerFormulaClassesByType: function (formulaType) {
            var classes = Unidata.uiuserexit.viewmodel.FormulaBase.getAllCustomerFormulaClasses(),
                result;

            result = Ext.Array.filter(classes, function (item) {
                return item.formulaType === formulaType;
            });

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все активные классы кастомера для определения формул по типу (месту использования) формул
         *
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllActiveCustomerFormulaClassesByType: function (formulaType) {
            var classes = Unidata.uiuserexit.viewmodel.FormulaBase.getAllActiveCustomerFormulaClasses(),
                result;

            result = Ext.Array.filter(classes, function (item) {
                return item.formulaType === formulaType;
            });

            return Ext.Array.unique(result);
        },

        /**
         * Строит json объект пригодны для использования в методе viewmodel.setFormulas()
         *
         * @param classes
         * @returns {{}}
         */
        getFormulaJsonFromClasses: function (classes) {
            var json = {};

            Ext.Array.each(classes, function (cls) {
                json[cls.formulaName] = cls.formula;
            });

            return json;
        }
    }
});
