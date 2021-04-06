/**
 * @author Aleksandr Bavin
 * @date 2017-05-04
 */
Ext.define('Unidata.view.main.menu.elements.BottomList', {

    extend: 'Unidata.view.main.menu.MainMenuList',

    alias: 'widget.un.list.mainmenu.bottom',

    reference: 'bottom',

    onComponentRender: function () {
        this.callParent(arguments);

        this.getEl().dom.addEventListener('mouseleave', Ext.bind(this.onListMouseLeave, this));
    },

    onListMouseLeave: function () {
        this.getItemsCollection().each(function (item) {
            item.setCollapsed(true);
        });
    }

});
