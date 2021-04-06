/**
 * @author Aleksandr Bavin
 * @date 2017-05-02
 *
 * @property elBody
 */
Ext.define('Unidata.view.component.list.List', {

    extend: 'Unidata.view.component.list.AbstractListElement',

    requires: [
        'Unidata.view.component.list.ListItem'
    ],

    mixins: [
        'Ext.mixin.Factoryable'
    ],

    alias: 'widget.un.list.default',

    factoryConfig: {
        defaultType: 'default'
    },

    // autoEl: 'ul',

    childEls: [
        {
            itemId: 'body',
            name: 'elBody'
        }
    ],

    renderTpl: [
        '<div class="{baseCls}-body" id="{id}-body" data-ref="body">',
            '{% this.renderContent(out, values) %}',
        '</div>'
    ],

    baseCls: 'un-list',

    config: {
        collapsed: false,
        defaults: null,
        items: null // элементы списка
    },

    collapseAnimation: false,

    itemsCollection: null, // коллекция элементов списка

    onDestroy: function () {
        Ext.Array.each(this.getItems(), function (item) {
            item.destroy();
        });

        this.callParent(arguments);
    },

    onComponentRender: function () {
        this.callParent(arguments);
        this.renderItems();
    },

    /**
     * Возвращает коллекцию элементов списка
     *
     * @returns {Ext.util.Collection}
     */
    getItemsCollection: function () {
        if (this.itemsCollection === null) {
            this.itemsCollection = new Ext.util.Collection();
            this.itemsCollection.on('add', this.onListItemAdd, this);
        }

        return this.itemsCollection;
    },

    applyItems: function (items) {
        if (items) {
            Ext.Array.each(items, this.add, this);
        }

        return this.getItemsCollection().getRange();
    },

    /**
     * Возвращает массив элементов
     *
     * @param {boolean} [withSublist = false] - включая sublist
     * @returns {Unidata.view.component.list.ListItem[]}
     */
    getItems: function (withSublist) {
        var result = this.getItemsCollection().getRange(),
            sublistItems = [];

        if (withSublist) {
            Ext.Array.each(result, function (item) {
                var sublist = item.getSublist();

                if (sublist) {
                    sublistItems = sublistItems.concat(sublist.getItems(true));
                }
            }, this);
        }

        result = result.concat(sublistItems);

        return result;
    },

    /**
     * Возвращает элемент списка по referenceName
     *
     * @param {String} referenceName
     * @returns {null|Unidata.view.component.list.ListItem}
     */
    getItem: function (referenceName) {
        return Ext.Array.findBy(this.getItems(true), function (item) {
            return item.getReference() === referenceName;
        });
    },

    /**
     * Рендерим элементы при добавлении в коллекцию
     *
     * @param collection
     * @param details
     */
    onListItemAdd: function (collection, details) {
        Ext.Array.each(details.items, function (item, index) {
            this.renderItem(item, details.at + index);
        }, this);
    },

    /**
     * Добавляет новый элемент списка
     *
     * @param listItem
     */
    add: function (listItem) {
        this.insert(this.getItemsCollection().length, listItem);
    },

    /**
     * Добавляет новый элемент списка в position
     *
     * @param position
     * @param listItem
     */
    insert: function (position, listItem) {
        var initializedListItem = this.initItem(listItem);

        // добавляем только если есть права
        if (initializedListItem && initializedListItem.hasComponentRights()) {
            initializedListItem.ownerCt = this;
            this.getItemsCollection().insert(position, initializedListItem);
        }
    },

    /**
     * Инициализирует элемент списка, используется перед вставкой в collection
     *
     * @param listItem
     * @returns {boolean|Unidata.view.component.list.ListItem}
     */
    initItem: function (listItem) {
        if (!(listItem instanceof Ext.Component)) {

            listItem = Ext.Object.merge({}, this.getDefaults(), listItem);

            if (!this.self.hasComponentRights(listItem.componentRights)) {
                return false;
            }

            listItem = Unidata.view.component.list.ListItem.create(listItem);
        }

        listItem.on('itemselected', this.onItemSelected, this);
        listItem.on('itemclick', this.fireItemClick, this);

        return listItem;
    },

    onItemSelected: function (component, item) {
        this.fireEvent('itemselected', this, item);
    },

    fireItemClick: function (component, item) {
        this.fireEvent('itemclick', this, item);
    },

    renderItems: function () {
        if (!this.rendered) {
            return;
        }

        this.getItemsCollection().each(this.renderItem, this);
    },

    renderItem: function (listItem, position) {
        if (!this.rendered) {
            return;
        }

        listItem.render(this.elBody, position);
    },

    toggleCollapsed: function () {
        this.setCollapsed(!this.getCollapsed());
    },

    updateCollapsed: function (collapsed) {
        var collapsedCls = this.baseCls + '-collapsed';

        if (collapsed) {
            this.addCls(collapsedCls);
        } else {
            this.removeCls(collapsedCls);
        }

        if (this.collapseAnimation) {
            this.runCollapseAnimation(collapsed);
        }
    },

    /**
     * Анимация сворачивания/разворачивания списка
     *
     * @param {boolean} collapsed
     */
    runCollapseAnimation: function (collapsed) {
        var el = this.getEl(),
            height;

        if (!el) {
            return;
        }

        if (collapsed) {

            el.animate({
                duration: 100,
                to: {
                    height: 0
                },
                listeners: {
                    beforeanimate:  function () {
                    },
                    afteranimate: function () {
                        this.hide();
                    },
                    scope: this
                }
            });

        } else {
            this.show();

            height = this.elBody.getHeight();

            el.animate({
                duration: 100,
                to: {
                    height: height
                },
                listeners: {
                    beforeanimate:  function () {
                    },
                    afteranimate: function () {
                        el.setHeight('auto');
                    },
                    scope: this
                }
            });
        }
    }

});
