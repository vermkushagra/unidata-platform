/**
 * Абстрактный компонент, который умеет отображать типовые айтемы внутри себя
 *
 * @author Aleksandr Bavin
 * @date 2017-05-22
 */
Ext.define('Unidata.view.component.AbstractComponentItems', {

    extend: 'Unidata.view.component.AbstractComponent',

    requires: [
        'Ext.util.Collection'
    ],

    config: {
        items: null
    },

    defaults: null,

    itemsCollection: null,

    delayedRender: false,

    getItemsCollection: function () {
        if (!this.itemsCollection) {
            this.itemsCollection = new Ext.util.Collection();
            this.itemsCollection.on('add', this.onCollectionAdd, this);
            this.itemsCollection.on('remove', this.onCollectionRemove, this);
        }

        return this.itemsCollection;
    },

    onDestroy: function () {
        this.getItemsCollection().each(function (item) {
            item.destroy();
        });

        this.callParent(arguments);
    },

    onCollectionAdd: function (collection, details) {
        Ext.Array.each(details.items, function (item, index) {
            this.onItemAdd(item, details.at + index);
        }, this);

        this.renderItemsDelayed();
    },

    onItemAdd: function (item, index) {
        item.on('destroy', this.onItemDestroy, this);
        item.onAdded(this, index, false);
    },

    onAdded: function (container) {
        this.callParent(arguments);

        if (!(container instanceof Unidata.view.component.AbstractComponentItems)) {
            container.on('afterlayout', this.onContainerAfterLayout, this);
        }
    },

    onContainerAfterLayout: function (container) {
        container.suspendLayouts();
        this.updateLayout();
        container.resumeLayouts();
    },

    onItemDestroy: function (item) {
        this.getItemsCollection().remove(item);
    },

    onCollectionRemove: function (collection, details) {
        Ext.Array.each(details.items, function (item, index) {
            this.onItemRemove(item, details.at + index);
        }, this);

        this.renderItemsDelayed();
    },

    onItemRemove: function (item, index) {
        item.un('destroy', this.onItemDestroy, this);

        item.onRemoved(false);

        this.updateLayoutDelayed();
    },

    onRemoved: function () {
        if (this.ownerCt) {
            this.ownerCt.un('afterlayout', this.onContainerAfterLayout, this);
        }

        this.callParent(arguments);
    },

    onComponentRender: function () {
        this.callParent(arguments);

        this.renderItemsDelayed();
    },

    updateItems: function (items) {
        this.getItemsCollection().removeAll(true);
        Ext.Array.each(items, this.addItem, this);
    },

    getRenderTarget: function () {
        return this.getTargetEl();
    },

    /**
     * @param {Object|Ext.Component} component
     * @returns {Ext.Component}
     */
    createItemComponent: function (component) {
        if (component instanceof Ext.Component) {
            return component;
        }

        if (this.defaults) {
            component = Ext.Object.merge(Ext.clone(this.defaults), component);
        }

        component = Ext.create(component);

        return component;
    },

    /**
     * @param {number} index
     * @param {Ext.Component} item
     * @returns {Ext.Component}
     */
    insertItem: function (index, item) {
        item = this.createItemComponent(item);

        if (this.getItemsCollection().indexOf(item) !== -1) {
            this.getItemsCollection().remove(item);
        }

        item = this.beforeCollectionInsert(item);

        this.getItemsCollection().insert(index, item);

        return item;
    },

    /**
     * @param {Ext.Component} item
     * @returns {Ext.Component}
     */
    addItem: function (item) {
        item = this.createItemComponent(item);
        item = this.beforeCollectionInsert(item);

        return this.getItemsCollection().add(item);
    },

    /**
     * @param {Ext.Component} item
     * @param {boolean} destroy
     */
    removeItem: function (item, destroy) {
        var itemsCollection = this.getItemsCollection();

        if (itemsCollection.indexOf(item)) {
            if (destroy) {
                item.destroy();
            } else {
                itemsCollection.remove(item);
            }
        }
    },

    /**
     * @param {boolean} destroy
     */
    removeAllItems: function (destroy) {
        var itemsCollection = this.getItemsCollection(),
            items = itemsCollection.getRange();

        itemsCollection.removeAll();

        if (destroy) {
            Ext.Array.each(items, function (item) {
                item.destroy();
            });
        }
    },

    /**
     * @param {Ext.Component} item
     * @returns {Ext.Component}
     */
    beforeCollectionInsert: function (item) {
        var itemOwnerCtItemsCollection;

        // убираем из старой коллекции
        if (item.ownerCt && item.ownerCt.getItemsCollection) {
            itemOwnerCtItemsCollection = item.ownerCt.getItemsCollection();

            if (itemOwnerCtItemsCollection !== this.getItemsCollection()) {
                itemOwnerCtItemsCollection.remove(item);
            }
        }

        return item;
    },

    /**
     * @param {Ext.Component} item
     */
    renderItem: function (item) {
        var position,
            renderTarget,
            itemEl,
            itemElParent,
            domNodeAtPosition;

        if (!this.rendered) {
            return;
        }

        position = this.getItemsCollection().indexOf(item);
        renderTarget = this.getRenderTarget();

        if (item.rendered) {

            itemEl = item.getEl();
            itemElParent = itemEl.parent();

            // находится в правильном контейнере
            if (itemElParent === renderTarget) {
                domNodeAtPosition = itemElParent.dom.childNodes[position];

                // элемент уже на своём месте
                if (domNodeAtPosition === itemEl.dom) {
                    return;
                }

                if (domNodeAtPosition) {
                    itemEl.insertBefore(domNodeAtPosition);
                } else {
                    renderTarget.appendChild(itemEl);
                }

            } else {
                domNodeAtPosition = renderTarget.dom.childNodes[position];

                if (domNodeAtPosition) {
                    itemEl.insertBefore(domNodeAtPosition);
                } else {
                    renderTarget.appendChild(itemEl);
                }
            }
        } else {
            item.render(renderTarget, position);
        }

        this.updateLayoutDelayed();
    },

    updateLayout: function () {

        if (this.isLayoutSuspended()) {
            return;
        }

        this.getItemsCollection().each(function (item) {
            item.updateLayout();
        });
    },

    updateLayoutDelayed: function () {
        clearTimeout(this.updateLayoutTimer);
        this.updateLayoutTimer = Ext.defer(this.updateLayout, 10, this);
    },

    renderItems: function () {
        if (!this.rendered) {
            return;
        }

        this.getItemsCollection().each(this.renderItem, this);
    },

    renderItemsDelayed: function () {
        if (this.delayedRender) {
            clearTimeout(this.renderTimer);
            this.renderTimer = Ext.defer(this.renderItems, 50, this);
        } else {
            this.renderItems();
        }
    }

});
