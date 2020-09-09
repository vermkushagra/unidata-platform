/**
 * @author Aleksandr Bavin
 * @date 2017-05-18
 */
Ext.define('Unidata.view.main.menu.elements.PasswordItem', {

    extend: 'Unidata.view.main.menu.MainMenuItem',

    alias: 'widget.un.list.item.mainmenu.item.password',

    iconCls: 'un-icon-8',

    reference: 'password',

    text: Unidata.i18n.t('menu>changePassword'),

    onClick: function () {
        this.callParent(arguments);

        Unidata.getApplication().showViewPort('changepassword');
    }

});
