/**
 * Оверрайд viewport. Расширяющий функционал для поиска не уничтоженых компонентов
 *
 * @author Ivan Marshalkin
 * @date 2017-07-21
 */

Ext.define('Ext.overrides.container.Viewport', {
    override: 'Ext.container.Viewport',

    initComponent: function () {
        this.callParent(arguments);
    },

    onDestroy: function () {
        var data;

        this.callParent(arguments);

        data = this.collectAliveComponentsData();

        console.log('Alive components count = ', data.length);

        // для других браузеров не выводим т.к. в IE нет например console.table
        // а разработчики все на хроме и фича только для них
        if (Ext.isChrome) {
            this.printFlatAliveComponentsList(data);
            this.printTableAliveComponentsList(data);
            this.printTreeAliveComponentsList(data);
        }
    },

    /**
     * Собирает информацию для вывода информации по компонентам
     *
     * @returns {*}
     */
    collectAliveComponentsData: function () {
        var components = Ext.ComponentManager.getAll(),
            data = [],
            collection;

        collection = Ext.create('Ext.util.Collection');

        Ext.Array.each(components, function (cmp, index) {
            var className = String(cmp.$className),
                cmpInfo;

            cmpInfo = {
                id: index, // id необходим для Ext.util.Collection
                className: className,
                cmpId: String(cmp.id),
                hasOwner: Boolean(cmp.ownerCt),
                destroyed: Boolean(cmp.destroying || cmp.destroyed || cmp.isDestroyed),
                component: cmp
            };

            data.push(cmpInfo);
        });

        collection.add(data);

        collection.sort([
            {
                property: 'destroyed',
                direction: 'ASC'
            },
            {
                property: 'className',
                direction: 'DESC'
            }
        ]);

        data = collection.getRange();

        Ext.Array.each(data, function (item) {
            delete item.id;
        });

        collection.destroy();

        return data;
    },

    /**
     * Выводит список в табличном виде
     *
     * @param data
     */
    printTableAliveComponentsList: function (data) {
        console.groupCollapsed('Alive components (table view)');
        console.table(data);
        console.groupEnd();
    },

    /**
     * Выводит список в плоском виде с небольшим форматированием
     *
     * @param data
     */
    printFlatAliveComponentsList: function (data) {
        console.group('Alive components (flat view)');

        Ext.Array.each(data, function (cmpData) {
            var className = String(cmpData.className);

            if (cmpData.destroyed) {
                console.warn('Destroyed: ', className);
            } else {
                if (className.indexOf('Unidata') === 0) {
                    console.warn('%c' + className, 'color:red; background:lightpink;');
                } else {
                    console.warn('%c' + className, '');
                }
            }
        });

        console.groupEnd();
    },

    /**
     * Выводит список в виде дерева
     *
     * @param data
     */
    printTreeAliveComponentsList: function (data) {
        var me = this,
            roots = this.findRootComponents(data),
            tree = this.buildTreeAliveComponentsList(data, roots);

        console.groupCollapsed('Alive components (tree view)');

        Ext.Array.each(tree, function (node) {
            me.printTreeAliveComponentsNode(node);
        });

        console.groupEnd();
    },

    printTreeAliveComponentsNode: function (node) {
        var me = this;

        console.groupCollapsed(node.component.$className);

        console.log(node);

        if (Ext.isArray(node.children)) {
            Ext.Array.each(node.children, function (child) {
                me.printTreeAliveComponentsNode(child);
            });
        }

        console.groupEnd();
    },

    findRootComponents: function (data) {
        var roots = [];

        Ext.Array.each(data, function (item) {
            if (!item.component.ownerCt) {
                roots.push(item);
            }
        });

        return roots;
    },

    buildTreeAliveComponentsList: function (data, roots) {
        var me = this,
            tree = [];

        Ext.Array.each(roots, function (root) {
            tree.push(root);

            me.buildBranch(data, root);
        });

        return tree;
    },

    buildBranch: function (data, leaf) {
        var me = this,
            branch = leaf,
            directChildren = [];

        if (Ext.isFunction(leaf.component.query)) {
            directChildren = leaf.component.query('>');
        }

        branch.children = [];

        Ext.Array.each(directChildren, function (child) {
            var dataItem;

            dataItem = Ext.Array.findBy(data, function (item) {
                return item.component === child;
            });

            branch.children.push(dataItem);

            me.buildBranch(data, dataItem);
        });

        return leaf;
    }
});
