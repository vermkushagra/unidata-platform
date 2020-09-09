/**
 * Переопределяем кофигурацию грида, т.к. там есть баг буферизованно вывода
 *
 * @see Unidata.overrides.grid.Panel
 *
 * @author Ivan Marshalkin
 * @date 2016-06-02
 */
Ext.define('Unidata.overrides.tree.Panel', {
    override: 'Ext.tree.Panel',

    config: {
        bufferedRenderer: false,
        treeState: null
    },

    // глючит анимация. приводит к неработоспособному интерфейсу
    animate: false,

    initComponent: function () {
        this.initTreeState();

        this.callParent(arguments);
    },

    initTreeState: function () {
        this.treeState = {
            accessField: null,
            scrollTopPosition: 0,
            scrollLeftPosition: 0,
            expanded: []
        };
    },

    /**
     * Сохранение состояния дерева
     * @param {Object}          [options] Опции сохранения состояния дерева.
     * @param {String}          [options.field] Id модели для сохранения и восстановления состояния expand / collapse.
     *                          по умолчанию idProperty модели
     */
    saveTreeState: function (options) {
        this.treeState.accessField = (options && options.field) || this.store.model.idProperty;

        this.saveExpanded();
        this.saveScrollData();
    },

    saveExpanded: function () {
        var expandedItems = [],
            me = this,
            field = me.treeState.accessField;

        this.store.each(function (item) {
            var itemPath;

            if (item.get('leaf') === false && item.get('expanded') === true) {
                itemPath = item.getPath(field);
                expandedItems.push(itemPath);
            }
        });

        this.treeState.expanded = expandedItems;
    },

    saveScrollData: function () {
        var view = this.getView(),
            scrollable;

        if (!view) {
            return;
        }

        scrollable = view.getScrollable();

        if (!scrollable) {
            return;
        }

        this.treeState.scrollTopPosition = scrollable.getElement().dom.scrollTop;
        this.treeState.scrollLeftPosition = scrollable.getElement().dom.scrollLeft;
    },

    restoreTreeState: function () {
        this.restoreExpanded();
        this.restoreScroll();

        this.initTreeState();
    },

    restoreExpanded: function () {
        var me = this,
            field = this.treeState.accessField,
            options;

        options = {
            field: field
        };

        me.collapseAll();

        Ext.Array.each(this.treeState.expanded, function (path) {
            me.expandPath(path, options);
        });
    },

    restoreScroll: function () {
        var view = this.getView(),
            scrollable;

        if (!view) {
            return;
        }

        scrollable = view.getScrollable();

        if (!scrollable) {
            return;
        }

        scrollable.getElement().dom.scrollTop = this.treeState.scrollTopPosition;
        scrollable.getElement().dom.scrollLeft = this.treeState.scrollLeftPosition;
    }

});
