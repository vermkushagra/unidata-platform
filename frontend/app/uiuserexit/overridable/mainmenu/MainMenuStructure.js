/**
 * Точка расширения для конфигурации главного меню
 *
 * @author Aleksandr Bavin
 * @date 2017-09-28
 */

Ext.define('Unidata.uiuserexit.overridable.mainmenu.MainMenuStructure', {

    singleton: true,

    /**
     * @typedef {Object} ListItem
     * @property {string} text - название, отображаемое в меню
     * @property {string} reference - уникальный идентификатор, используется в роутинге
     * @property {string} view - alias компонента, который будет открыт в рабочей области
     * @property {string} iconCls - css класс иконки
     * @property {boolean} pinned - отмеченные элементы меню
     */

    /**
     * Верхний список - логотип, переключатель
     * @see Unidata.view.main.menu.MainMenuStructure.getTopList
     *
     * @param {ListItem[]} topListItems
     */
    editTopListItems: function (topListItems) { // jscs:ignore disallowUnusedParams
    },

    /**
     * Центральный список - основные разделы системы
     * @see Unidata.view.main.menu.MainMenuStructure.getCenterList
     *
     * @param {ListItem[]} centerListItems
     */
    editCenterListItems: function (centerListItems) { // jscs:ignore disallowUnusedParams
    },

    /**
     * Обработка данных
     * @see Unidata.view.main.menu.MainMenuStructure.getDataProcessingList
     *
     * @param {ListItem[]} dataProcessingListItems
     */
    editDataProcessingListItems: function (dataProcessingListItems) { // jscs:ignore disallowUnusedParams
    },

    /**
     * Управление данными
     * @see Unidata.view.main.menu.MainMenuStructure.getDataManagementList
     *
     * @param {ListItem[]} dataManagementListItems
     */
    editDataManagementListItems: function (dataManagementListItems) { // jscs:ignore disallowUnusedParams
    },

    /**
     * Администрирование
     * @see Unidata.view.main.menu.MainMenuStructure.getAdminList
     *
     * @param {ListItem[]} adminListItems
     */
    editAdminListItems: function (adminListItems) { // jscs:ignore disallowUnusedParams
    },

    /**
     * Нижний список - пользователь, уведомления, выход
     * @see Unidata.view.main.menu.MainMenuStructure.getBottomList
     *
     * @param {ListItem[]} bottomListItems
     */
    editBottomListItems: function (bottomListItems) { // jscs:ignore disallowUnusedParams
    }

});
