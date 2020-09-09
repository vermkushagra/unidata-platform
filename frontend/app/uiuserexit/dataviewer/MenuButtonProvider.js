/**
 * Провайдер
 * Вспомогательный класс для корректировок выпадающего меню
 *
 * @author Ivan Marshalkin
 * @date 2017-07-06
 */

Ext.define('Unidata.uiuserexit.dataviewer.MenuButtonProvider', {
    requires: [
        'Unidata.uiuserexit.dataviewer.MenuButtonBase'
    ],

    statics: {
        /**
         * Провайдит расширения menu button кастомера
         *
         * @param menuButton
         * @param menuButtonType - тип (место использования)
         * @param opts - объект содержащий дополнительные параметры
         */
        provideActiveUiUserExit: function (menuButton, menuButtonType, opts) {
            var menuClasses = Unidata.uiuserexit.dataviewer.MenuButtonBase
                .getAllActiveCustomerDottedMenuClassesByType(menuButtonType);

            // оставляем только те которые могут быть применены для данного контекста
            menuClasses = Ext.Array.filter(menuClasses, function (menuClass) {
                return menuClass.isProvideable(opts) === true;
            });

            Ext.Array.each(menuClasses, function (menuClass) {
                menuClass.provide(menuButton, menuButtonType, opts);
            });
        }
    }
});
