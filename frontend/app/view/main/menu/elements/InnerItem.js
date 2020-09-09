/**
 * @author Aleksandr Bavin
 * @date 2017-05-04
 */
Ext.define('Unidata.view.main.menu.elements.InnerItem', {

    extend: 'Unidata.view.main.menu.MainMenuItem',

    alias: 'widget.un.list.item.mainmenu.item.inner',

    cls: 'un-mainmenu-item-inner',

    config: {
        pinned: false,
        pinnedTemp: false // временный пин, что бы при отмене выделения, элемент не пропадал сразу
    },

    inheritableStatics: {
        innerItems: [],
        localStorageId: 'ud-',
        localStorageKey: 'pinned-menu-items',

        unpinTemp: function () {
            Ext.Array.each(this.innerItems, function (innerItem) {
                if (!innerItem.getSelected()) {
                    innerItem.setPinnedTemp(false);
                }
            });
        },

        updatePinnedStore: function () {
            var InnerItem = Unidata.view.main.menu.elements.InnerItem,
                pinnedMenuItems = [],
                lsKey;

            Ext.Array.each(this.innerItems, function (innerItem) {
                if (innerItem.getPinned()) {
                    pinnedMenuItems.push(innerItem.getReference());
                }
            }, this);

            lsKey = InnerItem.localStorageId + InnerItem.localStorageKey;
            localStorage.setItem(lsKey, JSON.stringify(pinnedMenuItems));
        },

        getSelectedMenuItem: function () {
            var selected = null;

            Ext.Array.each(Unidata.view.main.menu.elements.InnerItem.innerItems, function (item) {
                if (item.getSelected()) {
                    selected = item;
                }
            });

            return selected;
        }
    },

    constructor: function () {
        this.callParent(arguments);

        //TODO: удалять из массива при destroy
        this.self.innerItems.push(this);
    },

    onDestroy: function () {
        Ext.Array.remove(this.self.innerItems, this);
        this.callParent(arguments);
    },

    updateSelected: function (selected) {
        this.callParent(arguments);

        if (selected) {
            this.setPinnedTemp(true);

            Ext.Array.each(this.self.innerItems, function (innerItem) {
                if (innerItem !== this) {
                    innerItem.setSelected(false);
                }
            }, this);
        }
    },

    updatePinnedTemp: function (pinned) {
        if (pinned) {
            this.addCls(this.baseCls + '-pinned-temp');
        } else {
            this.removeCls(this.baseCls + '-pinned-temp');
        }
    },

    setPinned: function (value) {
        var InnerItem = Unidata.view.main.menu.elements.InnerItem,
            pinnedMenuItems,
            lsKey;

        // на этапе конфигурирования, подменяем значение, если сохранено в localStorage
        if (this.isConfiguring) {
            lsKey = InnerItem.localStorageId + InnerItem.localStorageKey;
            pinnedMenuItems = JSON.parse(localStorage.getItem(lsKey));

            if (pinnedMenuItems && pinnedMenuItems.indexOf(this.getReference()) !== -1) {
                value = true;
            }
        }

        return this.callParent([value]);
    },

    updatePinned: function (pinned) {
        if (pinned) {
            this.addCls(this.baseCls + '-pinned');
        } else {
            this.removeCls(this.baseCls + '-pinned');
        }

        if (!this.isConfiguring) {
            this.self.updatePinnedStore();
        }
    },

    togglePinned: function () {
        this.setPinned(!this.getPinned());
    },

    onComponentRender: function () {
        this.callParent(arguments);

        this.elPin.on('click', this.togglePinned, this);
    },

    onClick: function () {
        this.callParent(arguments);

        if (this.getSelected()) {
            return;
        }

        this.setSelected(true);
    },

    isVisible: function () {
        var isVisible = this.callParent(arguments);

        if (isVisible && !this.getPinned() && Unidata.view.main.menu.MainMenu.isCollapsed()) {
            return false;
        }

        return isVisible;
    }

});
