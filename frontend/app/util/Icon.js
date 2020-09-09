/**
 * Класс реализует методы построения иконок
 *
 * @author Ivan Marshalkin
 * @date 2016-04-07
 */

Ext.define('Unidata.util.Icon', {
    singleton: true,

    /**
     * Для шрифта fontawesome
     *
     * @param name
     * @returns {string}
     */
    getAwesomeIcon: function (name) {
        return '<i class="fa fa-' + name + '"></i>';
    },

    /**
     * Для шрифта linearicons
     *
     * @param name
     * @returns {string}
     */
    getLinearIcon: function (name) {
        return '<i class="icon-' + name + '"></i>';
    }
});
