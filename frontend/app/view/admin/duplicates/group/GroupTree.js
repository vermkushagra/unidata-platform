/**
 * Дерево групп / правил поиска дубликатов
 *
 * @author Ivan Marshalkin
 * @date 2106-10-10
 */

Ext.define('Unidata.view.admin.duplicates.group.GroupTree', {
    extend: 'Ext.tree.Panel',

    alias: 'widget.un.duplicategrouptree',

    requires: [
        'Unidata.proxy.entity.GroupProxy'
    ],

    config: {
        entityName: null,                // кодовое имя реестра / справочника для которого отображаем правила поиска дубликатов
        showRules: true                  // признак отображать или фильтровать правила
    },

    ruleFilter: null,                    // фильтр отсеивает не группы

    rootVisible: false,

    initComponent: function () {
        var store;

        store = Ext.create('Ext.data.TreeStore', {
            proxy: 'un.duplicate.grouplist',
            autoLoad: false,
            root: {
                id: 'root'
            }
        });

        this.store = store;

        this.initRuleFilter();

        this.callParent(arguments);

        this.on('beforeload', this.onBeforeLoad, this);
        store.on('load', this.onStoreLoad, this);
    },

    initRuleFilter: function () {
        var store = this.getStore(),
            filter;

        filter = new Ext.util.Filter({
            filterFn: function (item) {
                var record = item.get('record'),
                    filtered = false;

                if (!item.isRoot() && record && record.get('ruleIds')) {
                    filtered = true;
                }

                return filtered;
            }
        });

        this.ruleFilter = filter;

        if (!this.showRules) {
            store.addFilter(filter);
        }
    },

    /**
     * Устанавливаем дополнительные параметры перед отправкой запроса на сервер
     *
     * @param store
     * @param operation
     */
    onBeforeLoad: function (store, operation) {
        var params = operation.getParams(),
            node = operation.node;

        if (node && !node.isLeaf() && !node.isRoot()) {
            params = Ext.apply(params, {
                nodeId: node.get('record').get('id')
            });
        }

        params = Ext.apply(params, {
            entityName: this.getEntityName()
        });

        operation.setParams(params);
    },

    /**
     * Перезагружает рутовскую ноду
     */
    reloadRootNode: function () {
        var me = this,
            rootNode = me.getRootNode(),
            entityName = this.getEntityName();

        if (!entityName) {
            return;
        }

        this.store.load({
            node: rootNode,
            callback: function () {
                rootNode.expand();
            }
        });
    },

    /**
     * Загружаем правила
     *
     * @returns {Ext.promise.Promise}
     */
    loadClusterRules: function () {
        var deferred = Ext.create('Ext.Deferred'),
            store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.matching.Rule',
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/matching/rules',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        });

        store.on('load', function (store, records, successful) {
            if (successful) {
                deferred.resolve(records);
            } else {
                deferred.reject();
            }
        });

        store.reload({
            params: {
                entityName: this.getEntityName()
            }
        });

        return deferred.promise;
    },

    /**
     * Обработка события загрузки стора
     *
     * После загрузки групп, загружаются правила, и дополняются
     */
    onStoreLoad: function (store, groups, successful, operation) {
        var me = this;

        // после загрузки правил - создаём дочерние ноды с правилами
        this.loadClusterRules().then(function (rules) {
            var rulesIdsInGroup = []; // id правил в группе

            // создаём ноды с правилами в группах
            Ext.Array.each(groups, function (group) {
                var groupRecord = group.get('record'),
                    ruleIds = groupRecord.get('ruleIds');

                Ext.Array.each(ruleIds, function (ruleId) {
                    var rule = Ext.Array.findBy(rules, function (rule) {
                        return ruleId === rule.get('id');
                    });

                    if (rule) {
                        rulesIdsInGroup.push(ruleId);

                        group.appendChild({
                            record: rule,
                            text: rule.get('name'),
                            ruleId: ruleId,
                            leaf: true
                        });
                    }
                });
            });
        }).always(function () {
            me.fireEvent('groupload', groups, successful, operation);
        });

    },

    /**
     * Возвращает массив записей из дерева по их классу
     *
     * @param itemClass
     * @returns {Array}
     */
    geiTreeItems: function (itemClass) {
        var root = this.getRootNode(),
            result = [];

        root.cascadeBy(function (node) {
            var record = node.get('record');

            if (record instanceof itemClass) {
                result.push(record);
            }
        });

        return Ext.Array.unique(result);
    },

    /**
     * Возвращает все группы сопоставления
     *
     * @returns {Unidata.model.matching.Group[]}
     */
    getMatchingGroups: function () {
        return this.geiTreeItems(Unidata.model.matching.Group);
    },

    /**
     * Возвращает все правила сопоставления
     *
     * @returns {Unidata.model.matching.Rule[]}
     */
    getMatchingRules: function () {
        return this.geiTreeItems(Unidata.model.matching.Rule);
    }

});
