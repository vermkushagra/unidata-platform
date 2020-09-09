/**
 * Плагин для Ext.grid.Panel.
 *
 * В меню для колонок скрывает пункты "Сортировать по возврастанию/убыванию", если они недоступны
 *
 * @author Ivan Marshalkin
 * 2016-05-12
 */

Ext.define('Unidata.plugin.grid.HiddenSortableMenuItem', {
    extend: 'Ext.AbstractPlugin',

    alias: 'plugin.grid.hiddensortablemenuitem',

    init: function (grid) {
        grid.on('beforereconfigure', this.onBeforeReconfigureGrid, this);
        grid.on('reconfigure', this.onReconfigureGrid, this);

        this.addEventHandler();
    },

    destroy: function () {
        var grid = this.getCmp();

        grid.un('beforereconfigure', this.onBeforeReconfigureGrid, this);
        grid.un('reconfigure', this.onReconfigureGrid, this);

        this.removeEventHandler();
    },

    addEventHandler: function () {
        var grid = this.getCmp();

        grid.headerCt.on('menucreate', this.onHeaderCtMenuCreate, this);
    },

    removeEventHandler: function () {
        var grid = this.getCmp(),
            menu;

        grid.headerCt.un('menucreate', this.onHeaderCtMenuCreate, this);

        if (grid.headerCt) {
            menu = grid.headerCt.getMenu();

            if (menu) {
                menu.un('beforeshow', this.onMenuBeforeShow, this);
            }
        }
    },

    onHeaderCtMenuCreate: function (component, menu) {
        menu.on('beforeshow', this.onMenuBeforeShow, this);
    },

    onMenuBeforeShow: function (menu) {
        menu.items.each(function (menuItem) {
            if (menuItem.itemId === 'ascItem' || menuItem.itemId === 'descItem') {
                menuItem.setHidden(false);

                if (menuItem.isDisabled()) {
                    menuItem.setHidden(true);
                }
            }
        });
    },

    onBeforeReconfigureGrid: function () {
        this.removeEventHandler();
    },

    onReconfigureGrid: function () {
        this.addEventHandler();
    }
});
