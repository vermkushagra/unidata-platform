/**
 * Точка расширения для виджетов дашборда
 *
 * @author Aleksandr Bavin
 * @date 2017-10-20
 */
Ext.define('Unidata.uiuserexit.overridable.dashboard.DashboardWidgets', {

    singleton: true,

    /**
     * @typedef {Object} WidgetItem
     * @property {string} text - название виджета, отображается в меню выбора
     * @property {Object} widget - конфигурационный объект виджета
     */

    /**
     * Список доступных виджетов
     * @see Unidata.view.component.grid.masonry.MasonryGridCell.getWidgetsList
     *
     * @param {WidgetItem[]} widgets
     */
    editWidgetsList: function (widgets) {
    }

});
