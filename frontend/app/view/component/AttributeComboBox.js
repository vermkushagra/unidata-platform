/**
 * Attribute search combobox
 *
 * Используется для поиска атрибутов в дереве
 *
 * Создание:
 *
 * items: [
 * {
 *     xtype: 'attributecombobox',
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

//TODO: rename class to AttributeSearchComboBox
Ext.define('Unidata.view.component.AttributeComboBox', {
    extend: 'Ext.form.field.ComboBox',

    xtype: 'attributecombobox',

    enableKeyEvents: true,

    displayField: 'displayName',
    valueField: 'name',
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
                self.setValue('');
                self.resumeEvent('change');
            }
        }
    },

    typeAhead: true,

    setAttributeTree: function (attributeTree) {
        this.attributeTree = attributeTree;
        this.attributeTree.on('select', this.onAttributeTreeStoreSelect, this);
        this.attributeTree.getStore().on('refresh', this.onAttributeTreeStoreRefresh, this);
    },

    onAttributeTreeStoreRefresh: function () {
        this.updateAttributeComboBoxStore();
    },

    onAttributeTreeStoreSelect: function (self, node) {
        var value = '';

        if (!node.isRoot()) {
            value = node.get('record').get('displayName');
        }

        this.suspendEvent('change');
        this.setValue(value);
        this.resumeEvent('change');
    },

    updateAttributeComboBoxStore: function () {
        var treeStore          = this.attributeTree.getStore(),
            store              = Unidata.view.component.AttributeComboBox.createStore(treeStore);

        this.setStore(store);
    },

    listeners: {
        keyup: function (self, e) {
            if (!e.isSpecialKey()) {
                this.store.filterBy(function (item) {
                    var attributeDisplayName = item.get('displayName'),
                        rawValue = self.getRawValue().toUpperCase();

                    attributeDisplayName = attributeDisplayName.toUpperCase();

                    return !rawValue || attributeDisplayName.indexOf(rawValue) !== -1;
                });
            }
        },
        change: function (self, name) {
            var store = self.attributeTree.getStore(),
                found = store.findNode('name', name);

            if (found) {
                self.attributeTree.selectPath(found.getPath());
            }
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

        createStore: function (treeStore) {
            var nodes = Unidata.view.component.AttributeComboBox.getDeepAllChildNodes(treeStore.getRoot()),
                data = [],
                store;

            nodes.forEach(function (node) {
                if (!node.isRoot()) {
                    var record = node.get('record');

                    if (record) {
                        data.push({
                                'name': record.get('name'),
                                'displayName': record.get('displayName')
                            }
                        );
                    }
                }
            });

            store = Ext.create('Ext.data.Store', {
                fields: ['name', 'displayName'],
                data: data
            });

            return store;
        }
    }
});
