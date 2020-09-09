/**
 * @author Aleksandr Bavin
 * @date 2017-05-04
 */
Ext.define('Unidata.view.main.menu.elements.BottomList', {

    extend: 'Unidata.view.main.menu.MainMenuList',

    alias: 'widget.un.list.mainmenu.bottom',

    reference: 'bottom',

    autoCollapseTimer: null,

    onComponentRender: function () {
        var element;

        this.callParent(arguments);

        element = this.getEl().dom;

        element.addEventListener('mouseleave', Ext.bind(this.onListMouseLeave, this));
        element.addEventListener('mouseenter', Ext.bind(this.onListMouseEnter, this));
    },

    onListMouseLeave: function () {
        clearTimeout(this.autoCollapseTimer);
        this.autoCollapseTimer = Ext.defer(this.collapseList, 750, this);
    },

    onListMouseEnter: function () {
        clearTimeout(this.autoCollapseTimer);
    },

    collapseList: function () {
        this.getItemsCollection().each(function (item) {
            item.setCollapsed(true);
        });
    }

});
