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
    cls: 'un-dottedmenu-bottom-right',

    config: {
        arrowAnchor: null   // где рисуется "стрелочка"
    },

    initComponent: function () {
        this.callParent(arguments);
    },

    statics: {
        MENU_ITEM_SELECTED_CLS: 'un-menu-item-selected',
        ARROW_ANCHOR: {
            TOP_RIGHT: 'top-right',
            BOTTOM_RIGHT: 'bottom-right',
            TOP_LEFT: 'top-left',
            BOTTOM_LEFT: 'bottom-left'
        }
    },

    plain: true,
    frame: false,

    updateArrowAnchor: function (arrowAnchor) {
        var DottedMenu = Unidata.view.component.menu.DottedMenu,
            clsPrefix = 'un-dottedmenu-';

        if (!this.rendered) {
            return;
        }

        Ext.Object.each(DottedMenu.ARROW_ANCHOR, function (key, value) {
            this.removeCls(clsPrefix + value);
        }, this);

        this.addCls(clsPrefix + arrowAnchor);
    }
});
