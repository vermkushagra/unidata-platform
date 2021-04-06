/**
 * @author Aleksandr Bavin
 * @date 2017-05-04
 */
Ext.define('Unidata.view.main.menu.elements.GroupItem', {

    extend: 'Unidata.view.main.menu.MainMenuItem',

    alias: 'widget.un.list.item.mainmenu.item.group',

    cls: 'un-mainmenu-item-group',

    config: {
        hideable: true
    },

    onComponentRender: function () {
        var el = this.getEl(),
            elDom = el.dom,
            transitionEndCallback = Ext.bind(this.onTransitionEnd, this);

        this.callParent(arguments);

        elDom.addEventListener('webkitTransitionEnd', transitionEndCallback, false);
        elDom.addEventListener('transitionend', transitionEndCallback, false);
        elDom.addEventListener('msTransitionEnd', transitionEndCallback, false);
        elDom.addEventListener('oTransitionEnd', transitionEndCallback, false);

        this.syncHideableCls(this.getHideable());
    },

    onTransitionEnd: function (e) {
        var el = this.getEl();

        if (e.target === el.dom) {
            this.fireEvent('transitionend', this);
        }
    },

    updateHideable: function (hideable) {
        this.syncHideableCls(hideable);
    },

    syncHideableCls: function (hideable) {
        var el = this.getEl(),
            className = 'un-mainmenu-item-group__hideable';

        if (!el) {
            return;
        }

        if (hideable) {
            el.addCls(className);
        } else {
            el.removeCls(className);
        }
    },

    updateCollapsed: function (collapsed) {
        var cls = this.cls + '-collapsed';

        this.callParent(arguments);

        if (collapsed) {
            this.addCls(cls);
        } else {
            this.removeCls(cls);
        }
    },

    onClick: function () {
        var sublist = this.getSublist();

        this.callParent(arguments);

        sublist.getItemsCollection().each(function (innerItem) {
            if (innerItem instanceof Unidata.view.main.menu.elements.InnerItem) {
                if (!innerItem.getSelected()) {
                    innerItem.setPinnedTemp(false);
                }
            }
        });

        this.setCollapsed(!this.getCollapsed());
    }

});
