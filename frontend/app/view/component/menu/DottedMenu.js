/**
 *
 * Выпадающее меню, появляющееся по нажатию доп.кнопки
 *
 * @author Sergey Shishigin
 * @date 2017-04-14
 */
Ext.define('Unidata.view.component.menu.DottedMenu', {
    extend: 'Ext.menu.Menu',

    xtype: 'un.dottedmenu',

    ui: 'un-dottedmenu',

    statics: {
        MENU_ITEM_SELECTED_CLS: 'un-menu-item-selected'
    },

    plain: true,
    frame: false
});
