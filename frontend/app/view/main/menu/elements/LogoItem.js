/**
 * @author Aleksandr Bavin
 * @date 2017-05-11
 */
Ext.define('Unidata.view.main.menu.elements.LogoItem', {
    extend: 'Unidata.view.main.menu.MainMenuItem',

    requires: [
        'Unidata.uiuserexit.overridable.UnidataPlatform'
    ],

    alias: 'widget.un.list.item.mainmenu.item.logo',

    reference: 'logo',

    tpl: null, // настраивается в initComponent для возможности кастомизации

    iconTpl: null, // настраивается в initComponent для возможности кастомизации

    initComponent: function () {
        this.tpl = Unidata.uiuserexit.overridable.UnidataPlatform.getMainMenuPlatformTextTpl();
        this.iconTpl = Unidata.uiuserexit.overridable.UnidataPlatform.getMainMenuPlatformIconTpl();

        this.callParent(arguments);
    },

    showTooltip: function () {
        return;
    }
});
