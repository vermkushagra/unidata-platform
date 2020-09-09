/**
 * Информация о системе
 *
 * @author Aleksandr Bavin
 * @date 2017-06-17
 */
Ext.define('Unidata.view.main.menu.elements.AboutItem', {

    extend: 'Unidata.view.main.menu.MainMenuItem',

    alias: 'widget.un.list.item.mainmenu.item.about',

    iconCls: 'un-icon-7',

    reference: 'about',

    text: Unidata.i18n.t('menu>about'),

    onClick: function () {
        var wnd;

        this.callParent(arguments);

        wnd = Ext.create('Unidata.view.main.menu.elements.AboutWindow', {
        });
        wnd.show();
    }

});
