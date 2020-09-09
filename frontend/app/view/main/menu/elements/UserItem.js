/**
 * @author Aleksandr Bavin
 * @date 2017-05-23
 */
Ext.define('Unidata.view.main.menu.elements.UserItem', {

    extend: 'Unidata.view.main.menu.elements.GroupItem',

    alias: 'widget.un.list.item.mainmenu.item.user',

    iconCls: 'icon-user',

    reference: 'user',

    initComponent: function () {
        var user      = Unidata.Config.getUser(),
            firstName = Ext.String.htmlEncode(user.get('firstName')),
            lastName  = Ext.String.htmlEncode(user.get('lastName')),
            text = Ext.String.trim(firstName + ' ' + lastName);

        this.callParent(arguments);
        this.setText(text || Unidata.i18n.t('menu>user'));
    }

});
