/**
 * Attribute search combobox
 *
 * Используется для поиска атрибутов в дереве
 *
 * Создание:
 *
 * items: [
 * {
 *     xtype: 'un.treesearchcombobox',
 *     reference: 'attributeComboBox'
 * },
 * {
 *     xtype: 'component.attributeTree',
 *     reference: 'attributeTreePanel',
 *     ...
 *     listeners: {
 *     ...
 *     beforerender: 'onAttributeTreeBeforeRender'
 * }]
 *
 * Инициализация:
 *
 * onAttributeTreeBeforeRender: function () {
 *     var attributeTreePanel = this.lookupReference('attributeTreePanel'),
 *         attributeComboBox  = this.lookupReference('attributeComboBox');
 *
 *     attributeComboBox.setAttributeTree(attributeTreePanel);
 * }
 *
 * Обновление:
 *
 * attributeComboBox.updateAttributeComboBoxStore();
 *
 * @author Sergey Shishigin
 */

Ext.define('Unidata.view.component.TreeSearchComboBox', {
    extend: 'Ext.form.field.ComboBox',

    xtype: 'un.treesearchcombobox',

    enableKeyEvents: true,

    displayField: 'fullDisplayName',
    valueField: 'path',
    queryMode: 'local',
    typeAheadDelay: 150,
    attributeTree: null,
    anyMatch: true,
    emptyText: Unidata.i18n.t('classifier>selectSearchAttribute'),
    triggers: {
        clear: {
            hideOnReadOnly: false,
            cls: 'x-form-clear-trigger',
            handler: function (self) {
                self.suspendEvent('change');
                self.setValue(null);
                self.resumeEvent('change');
            }
        }
    },

    typeAhead: true,
    cls: 'un-tree-search-combobox',
    listConfig: {
        cls: 'un-tree-search-combobox-boundlist',
        itemTpl: '{fullDisplayName}'
    },

    initComponent: function () {
        var boundList;

        this.callParent(arguments);
        boundList = this.getPicker();
        boundList.on('select', this.onBoundListSelect, this);
    },

    onBoundListSelect: function (self) {
        self.suspendEvent('select');
        this.setValue('');
        self.resumeEvent('select');
    },

    setAttributeTree: function (attributeTree) {
        this.attributeTree = attributeTree;
        this.attributeTree.on('select', this.onAttributeTreeStoreSelect, this);
        this.attributeTree.getStore().on('refresh', this.onAttributeTreeStoreRefresh, this);

        this.updateAttributeComboBoxStore();
    },

    onAttributeTreeStoreRefresh: function () {
        this.updateAttributeComboBoxStore();
    },

    onAttributeTreeStoreSelect: function () {
        this.suspendEvent('change');
        this.setValue('');
        this.resumeEvent('change');
    },

    updateAttributeComboBoxStore: function () {
        var treeStore = this.attributeTree.getStore(),
            store = Unidata.view.component.TreeSearchComboBox.createStore(treeStore);

        this.setStore(store);
    },

    listeners: {
        keyup: function (self, e) {
            if (!e.isSpecialKey()) {
                this.store.filterBy(function (item) {
                    var rawValue = self.getRawValue().toUpperCase(),
                        attributeDisplayName;

                    if (rawValue.indexOf('.') !== -1) {
                        attributeDisplayName = item.get('fullDisplayName');
                    } else {
                        attributeDisplayName = item.get('displayName');
                    }

                    attributeDisplayName = attributeDisplayName.toUpperCase();

                    return !rawValue || attributeDisplayName.indexOf(rawValue) !== -1;
                });
            }
        },
        select: function (self, node) {
            var store = self.attributeTree.getStore(),
                path = node.get('path'),
                found = store.findNode('path', path);

            if (found) {
                self.attributeTree.selectPath(found.getPath());
            }
        },
        collapse: function () {
            this.store.clearFilter();
        }
    },

    statics: {
        /**
         * Формирует плоский массив всех узлов
         *
         * @param node
         * @returns {Array}
         */
        getDeepAllChildNodes: function (node) {
            var allNodes = [],
                me = this;

            if (!node) {
                return [];
            }

            if (!node.hasChildNodes()) {
                allNodes.push(node);

                return allNodes;
            } else {
                allNodes.push(node);
                node.eachChild(function (mynode) {
                    allNodes = allNodes.concat(me.getDeepAllChildNodes(mynode));
                });
            }

            return allNodes;
        },

        wrapHtmlComboItem: function (itemHtml) {
            return Ext.String.format('<span class="un-htmlcombo-treeItem">{0}</span>', itemHtml);
        },

        getDelimiter: function () {
            var delimiter;

            delimiter = '<span class="un-htmlcombo-treeItem-delimiter"></span>';

            return delimiter;
        },

        createStore: function (treeStore) {
            var me = this,
                nodes = Unidata.view.component.TreeSearchComboBox.getDeepAllChildNodes(treeStore.getRoot()),
                delimiter = this.getDelimiter(),
                data = [],
                store;

            nodes.forEach(function (node) {
                var record,
                    displayNames = [],
                    tmpNode;

                if (!node.isRoot()) {
                    record = node.get('record');

                    if (record) {
                        tmpNode = node;

                        do {
                            displayNames.push(tmpNode.get('record').get('displayName'));
                            tmpNode = tmpNode.parentNode;
                        } while (tmpNode.parentNode);

                        displayNames = displayNames.reverse();
                        displayNames = Ext.Array.map(displayNames, function (displayName) {
                            return me.wrapHtmlComboItem(displayName);
                        });

                        data.push({
                                'path': node.get('path'),
                                'displayName': record.get('displayName'),
                                'fullDisplayName': displayNames.join(delimiter),
                                'displayNames': Ext.clone(displayNames)
                            }
                        );
                    }
                }
            });

            store = Ext.create('Ext.data.Store', {
                fields: [
                    'path',
                    'displayName',
                    'fullDisplayName',
                    'displayNames'
                ],
                data: data
            });

            return store;
        }
    }
});
