/**
 * Базовый класс определения расширения menu button из ui user exit
 *
 * @author Ivan Marshalkin
 * @date 2017-07-06
 */

Ext.define('Unidata.uiuserexit.dataviewer.MenuButtonBase', {
    requires: [
        'Unidata.uiuserexit.dataviewer.MenuButtonTypes',
        'Unidata.uiuserexit.CustomerClassesHelper'
    ],

    inheritableStatics: {
        // включен / выключен (выключеные не учитываются)
        active: false,

        // тип (место использования)
        menuType: Unidata.uiuserexit.dataviewer.MenuButtonTypes.UNKNOWN,

        // функция соединитель точки расширения с логикой кастомера
        provide: Ext.emptyFn,

        isProvideable: function () {
            console.log(Unidata.i18n.t('other>warningCalledBaseMethod', {method: 'MenuButtonBase->isProvideable'}));

            return false;
        }
    },

    statics: {
        /**
         * Возвращает все классы кастомера для расширения menu button
         *
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllCustomerDottedMenuClasses: function () {
            var baseClass = Unidata.uiuserexit.dataviewer.MenuButtonBase,
                result;

            result = Unidata.uiuserexit.CustomerClassesHelper.getAllCustomerClasses(baseClass);

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все активные классы кастомера для расширения menu button
         *
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllActiveCustomerDottedMenuClasses: function () {
            var baseClass = Unidata.uiuserexit.dataviewer.MenuButtonBase,
                result;

            result = Unidata.uiuserexit.CustomerClassesHelper.getAllActiveCustomerClasses(baseClass);

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все классы кастомера для расширения menu button по типу (месту использования)
         *
         * @param menuType
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllCustomerDottedMenuClassesByType: function (menuType) {
            var classes = Unidata.uiuserexit.dataviewer.MenuButtonBase.getAllCustomerDottedMenuClasses(),
                result;

            result = Ext.Array.filter(classes, function (item) {
                return item.menuType === menuType;
            });

            return Ext.Array.unique(result);
        },

        /**
         * Возвращает все активные классы кастомера для расширения menu button по типу (месту использования)
         *
         * @param menuType
         * @returns {*|{displayName, editor, renderer}}
         */
        getAllActiveCustomerDottedMenuClassesByType: function (menuType) {
            var classes = Unidata.uiuserexit.dataviewer.MenuButtonBase.getAllActiveCustomerDottedMenuClasses(),
                result;

            result = Ext.Array.filter(classes, function (item) {
                return item.menuType === menuType;
            });

            return Ext.Array.unique(result);
        }
    }
});
