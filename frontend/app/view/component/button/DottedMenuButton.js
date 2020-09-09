/**
 *
 * Кнопка для отображения меню (символ "три точки, вертикально расположенные")
 *
 * @author Sergey Shishigin
 * @date 2017-04-14
 */
Ext.define('Unidata.view.component.button.DottedMenuButton', {
    extend: 'Ext.button.Button',

    xtype: 'un.dottedmenubtn',

    config: {
        // обработчики действий при нажатии на пункт меню
        handlers: null,
        // элементы меню
        menuItems: null,
        // разделители меню
        menuSeparators: null
    },

    //@const
    MENU_ITEM_REF_POSTFIX: 'MenuItem',

    referenceHolder: true,
    ui: 'un-dottedmenu',
    scale: 'large',
    shadow: false,
    iconCls: 'default',

    arrowVisible: false,

    /**
     * @override
     * Переопределяем метод Ext.button.Button.showMenu ради возможности вызова showBy с аргументом offset
     * Shows this button's menu (if it has one)
     */
    showMenu: function (/* private */ clickEvent) {
        var me = this,
            menu = me.menu,
            isPointerEvent = !clickEvent || clickEvent.pointerType,
            pos = 'tr-b',       // default position
            offset = [17, 9],   //default offset
            menuSize,
            bodySize,
            elPosition;

        if (menu && me.rendered) {
            if (me.tooltip && Ext.quickTipsActive && me.getTipAttr() !== 'title') {
                Ext.tip.QuickTipManager.getQuickTip().cancelShow(me.el);
            }

            if (menu.isVisible()) {
                // Click/tap toggles the menu visibility.
                if (isPointerEvent) {
                    menu.hide();
                } else {
                    menu.focus();
                }
            } else if (!clickEvent || me.showEmptyMenu || menu.items.getCount() > 0) {
                // Pointer-invoked menus do not auto focus, key invoked ones do.
                menu.autoFocus = !isPointerEvent;
                // сначала рисуем меню в дефолтном представлении
                // ИЗМЕНИЛИ НАБОР ПАРАМЕТРОВ ОТНОСИТЕЛЬНО РОДНОГО МЕТОДА showMenu
                menu.showBy(me.el, pos, [17, 9]);

                // вычисляем размер компонента, всего экрана и позицию кнопки "три точки"
                menuSize = menu.getSize();
                bodySize = Ext.getBody().getViewSize();
                elPosition = me.el.getXY();

                // если вылезаем за нижнюю границу экрана
                if (elPosition[1] + menuSize.height > bodySize.height) {
                    offset = [17, -9];
                    menu.setArrowAnchor(Unidata.view.component.menu.DottedMenu.ARROW_ANCHOR.BOTTOM_RIGHT);
                    pos = 'br-t';

                    // если вылезаем за левую границу экрана
                    if (elPosition[0] < menuSize.weight) {
                        menu.setArrowAnchor(Unidata.view.component.menu.DottedMenu.ARROW_ANCHOR.BOTTOM_LEFT);
                        pos = 'bl-t';
                    }
                } else {
                    menu.setArrowAnchor(Unidata.view.component.menu.DottedMenu.ARROW_ANCHOR.TOP_RIGHT);

                    // если вылезаем за левую границу экрана
                    if (elPosition[0] < menuSize.weight) {
                        menu.setArrowAnchor(Unidata.view.component.menu.DottedMenu.ARROW_ANCHOR.TOP_LEFT);
                        pos = 'tl-b';
                    }
                }

                menu.alignTo(me.el, pos, offset);
            }
        }

        return me;
    },

    /**
     * Установка обработчиков кликов на пункты меню
     * @param handlers
     * @param oldHandlers
     */
    updateHandlers: function (handlers, oldHandlers) {
        var menuItems = this.getMenuItems(),
            oldHandler,
            item;

        if (!menuItems) {
            return;
        }

        // use handlers options to config click event listeners
        Ext.Object.each(handlers, function (key, handler) {
            item = this.getMenuItem(key);

            if (Ext.isObject(oldHandlers)) {
                oldHandler = oldHandlers[key];
            }

            if (oldHandler) {
                item.removeListener('click', oldHandler);
            }

            item.on('click', handler);
        }, this);
    },

    getMenuItem: function (key) {
        var menuItems = this.getMenuItems();

        if (!menuItems) {
            return null;
        }

        return menuItems[key];
    },

    initComponent: function () {
        var handlers = this.getHandlers(),
            menu;

        this.callParent(arguments);

        this.initReferences();

        if (handlers) {
            this.updateHandlers(handlers);
        }

        menu = this.getMenu();
        menu.on('show', function () {
            var menu = this.getMenu();

            menu.hide();
            Ext.defer(this.showMenu, 100, this);
        }, this, {single: true});
    },

    initReferences: function () {
        var menuItems = this.getMenuItems(),
            menuSeparators = this.getMenuSeparators(),
            menu = this.getMenu(),
            postfix = this.MENU_ITEM_REF_POSTFIX;

        Ext.Object.each(menuItems, function (key) {
            menuItems[key] = menu.lookupReference(key + postfix);
        });

        if (Ext.isObject(menuSeparators)) {
            Ext.Object.each(menuSeparators, function (key) {
                menuSeparators[key] = menu.lookupReference(key);
            });
        }
    }
});
