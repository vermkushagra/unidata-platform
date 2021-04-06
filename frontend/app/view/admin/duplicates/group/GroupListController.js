/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */
Ext.define('Unidata.view.admin.duplicates.group.GroupListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.duplicates.grouplist',

    init: function () {
        var view = this.getView();

        this.callParent(arguments);

        view.treeGroupNameColumn.renderer = this.treeGroupNameColumnRenderer.bind(this);
        view.on('afterrender', this.onViewAfterRender, this);
    },

    /**
     * Обработчик обновления метаомдели
     *
     * @param metaRecord
     */
    updateMetaRecord: function (metaRecord) {
        var view = this.getView();

        view.groupTree.setEntityName(metaRecord.get('name'));

        view.groupTree.reloadRootNode();
    },

    updateRules: function () {
        var view = this.getView();

        view.groupTree.getView().refresh();
    },

    /**
     * Обработчик клика по кнопке добавления новой группы
     */
    onAddGroupButtonClick: function () {
        this.showGroupInfoWindow(null);
    },

    /**
     * Удаление группы / правила
     */
    onDeleteGroupActionClick: function (tree, row, column, e, eOpts, node) {
        if (!node.isLeaf()) {
            this.onDeleteGroup(node);
        } else {
            this.onDeleteRule(node);
        }
    },

    /**
     * Обрабатывает запрос на удаления группы
     *
     * @param node
     */
    onDeleteGroup: function (node) {
        var title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmRemoveGroup');

        Unidata.showPrompt(title, msg, this.deleteGroup, this, null, [node]);
    },

    /**
     * Обрабатывает запрос на удаления правила
     *
     * @param node
     */
    onDeleteRule: function (node) {
        var title = Unidata.i18n.t('common:confirmation'),
            msg   = Unidata.i18n.t('admin.duplicates>confirmRemoveRule');

        Unidata.showPrompt(title, msg, this.deleteRule, this, null, [node]);
    },

    /**
     * Удаляет группу
     *
     * @param node
     */
    deleteGroup: function (node) {
        var view = this.getView();

        node.get('record').erase({
            success: function () {
                view.groupTree.reloadRootNode();
            }
        });
    },

    /**
     * Удаляет правило
     *
     * @param node
     */
    deleteRule: function (node) {
        var groupNode = node.parentNode;

        groupNode.removeChild(node);

        this.updateRuleIdsInGroupNode(groupNode);
    },

    /**
     * При перетаскивании ноды
     *
     * @param targetNode
     * @param position
     * @param dragData
     * @param e
     * @param eOpts
     * @returns {boolean}
     */
    onNodeDragOver: function (targetNode, position, dragData) {
        var targetRecord = targetNode.get('record'),
            targetRecordIsMatchingGroup = targetRecord instanceof Unidata.model.matching.Group,
            dragNode = dragData.records[0],
            dragRecord = dragNode.get('record'),
            dragRecordIsMatchingGroup = dragRecord instanceof Unidata.model.matching.Group,
            dragRecordIsMatchingRule = dragRecord instanceof Unidata.model.matching.Rule,
            draggedFromGroupNode,
            dropToGroupNode;

        draggedFromGroupNode = dragNode.parentNode;
        dropToGroupNode = targetNode.parentNode;

        // нельзя бросать группу в группу
        if (position == 'append' && targetRecordIsMatchingGroup && dragRecordIsMatchingGroup) {
            return false;
        }

        // нельзя бросать правило рядом с группой
        if ((position == 'before' || position == 'after') && dragRecordIsMatchingRule && targetRecordIsMatchingGroup) {
            return false;
        }

        // нельзя бросать правило в другую правило ("своих не бросаем!")
        if (dragRecordIsMatchingRule && draggedFromGroupNode !== dropToGroupNode) {
            return false;
        }
    },

    /**
     * Предварительный обработчик drop ноды в дерево групп
     *
     * @param node
     * @param data
     * @param overModel
     * @param dropPosition
     * @param dropHandlers
     * @param eOpts
     */
    onBeforeDropRule: function (node, data, overModel, dropPosition, dropHandlers) {
        var view      = this.getView(),
            treeStore = view.groupTree.getStore(),
            dragRecord;

        dragRecord = data.records[0];

        // пользователь сортирует
        if (treeStore.contains(dragRecord)) {
            //dropHandlers.cancelDrop();
            this.onBeforeDropRuleInside(node, data, overModel, dropPosition, dropHandlers);
        } else {
            this.onBeforeDropRuleExternal(node, data, overModel, dropPosition, dropHandlers);
        }
    },

    /**
     * Бросаем ноду перетаскиваемую внутри дерева
     *
     * @param node
     * @param data
     * @param overModel
     * @param dropPosition
     * @param dropHandlers
     */
    onBeforeDropRuleInside: function (node, data, overModel, dropPosition, dropHandlers) {
        var view = this.getView(),
            dragRecord,
            draggedFromGroupNode,
            dropToGroupNode,
            groupTree = view.groupTree,
            store = groupTree.getStore(),
            insertIndex;

        dragRecord = data.records[0];

        if (!dragRecord.isLeaf()) {
            return;
        }

        draggedFromGroupNode = dragRecord.parentNode;

        dropToGroupNode = overModel;

        if (overModel.isLeaf()) {
            dropToGroupNode = overModel.parentNode;
        }

        if (dropToGroupNode === draggedFromGroupNode) {

            insertIndex = store.indexOf(overModel);

            if (dropPosition === 'before') {
                --insertIndex;
            }

            dropToGroupNode.insertChild(insertIndex, dragRecord);

            this.updateRuleIdsInGroupNode(draggedFromGroupNode);
            this.updateRuleIdsInGroupNode(dropToGroupNode);
        }

        dropHandlers.cancelDrop();
    },

    /**
     * Бросаем ноду перетаскиваемую из списка правил
     *
     * @param node
     * @param data
     * @param overModel
     * @param dropPosition
     * @param dropHandlers
     * @returns {boolean}
     */
    onBeforeDropRuleExternal: function (node, data, overModel, dropPosition, dropHandlers) {
        var groupNode = overModel,
            index     = -1,
            dragRecord,
            newNode;

        dragRecord = data.records[0];

        if (overModel.isLeaf()) {
            groupNode = overModel.parentNode;

            index = groupNode.indexOf(overModel);
        }

        // если в группе уже есть правило то добавлять его нельзя
        if (this.isRuleInGroup(dragRecord, groupNode)) {
            dropHandlers.cancelDrop();

            return false;
        }

        newNode = {
            text: dragRecord.get('name'),
            ruleId: dragRecord.get('id'),
            leaf: true
        };

        if (index !== -1) {
            groupNode.insertChild(index, newNode);
        } else {
            groupNode.appendChild(newNode);
        }

        this.updateRuleIdsInGroupNode(overModel);

        dropHandlers.cancelDrop();
    },

    /**
     * Возвращает истину если правило уже добавлено в группу
     *
     * @param rule
     * @param groupNode
     * @returns {*}
     */
    isRuleInGroup: function (rule, groupNode) {
        var record     = groupNode.get('record'),
            dropRuleId = rule.get('id'),
            ruleIds,
            result;

        if (!record) {
            return false;
        }

        ruleIds = record.get('ruleIds');

        result = Ext.Array.some(ruleIds, function (ruleId) {
            return ruleId === dropRuleId;
        });

        return result;
    },

    /**
     * Обновляем список идентификаторов привязанных правил к группе
     *
     * @param node
     */
    updateRuleIdsInGroupNode: function (node) {
        var groupNode = node,
            ruleIds   = [];

        if (node.isLeaf()) {
            groupNode = node.parentNode;
        }

        groupNode.eachChild(function (childNode) {
            ruleIds.push(childNode.get('ruleId'));
        });

        groupNode.get('record').set('ruleIds', ruleIds);

        groupNode.get('record').save();
    },

    /**
     * Рендерит наименование групп / правил в дереве
     *
     * @param a
     * @param b
     * @param node
     * @returns {*}
     */
    treeGroupNameColumnRenderer: function (a, b, node) {
        var view  = this.getView(),
            rules = view.getRules(),
            findRule;

        if (!node.isLeaf()) {
            return Ext.util.Format.htmlEncode(node.get('text')); // не забываем предотвращение XSS
        } else {
            findRule = Ext.Array.findBy(rules, function (rule) {
                return rule.get('id') === node.get('ruleId');
            });

            if (findRule) {
                return Ext.util.Format.htmlEncode(findRule.get('name')); // не забываем предотвращение XSS
            }
        }

        return Unidata.i18n.t('admin.duplicates>unknownRule');
    },

    /**
     * Обработчик двойного клика по рекорду в дереве
     * \
     * @param tree
     * @param node
     */
    onGroupDoubleClick: function (tree, node) {
        if (!Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'update')) {
            return;
        }

        if (node.isLeaf()) {
            return;
        }

        this.showGroupInfoWindow(node.get('record'));
    },

    /**
     * Отображает окно для редактирования / добавления новой группы
     *
     * @param groupModel
     */
    showGroupInfoWindow: function (groupModel) {
        var view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            wnd;

        wnd = Ext.create('Unidata.view.admin.duplicates.group.GroupInfoWindow', {
            entityName: metaRecord.get('name'),
            groupModel: groupModel
        });

        wnd.on('modeledited', function (groupModel) {
            var rootNode = view.groupTree.getRootNode(),
                nameExist;

            nameExist = Ext.Array.some(rootNode.childNodes, function (childNode) {
                var nodeRecord = childNode.get('record'),
                    nameEq     = false;

                if (nodeRecord.get('name') === groupModel.get('name') &&
                    nodeRecord.get('id') !== groupModel.get('id')) {
                    nameEq = true;
                }

                return nameEq;
            });

            if (nameExist) {
                Unidata.showError(Unidata.i18n.t('admin.duplicates>groupWithNameExists'));

                return false;
            }

            groupModel.save({
                success: function () {
                    view.groupTree.reloadRootNode();

                    wnd.close();
                }
            });
        }, this);

        wnd.show();
    },

    /**
     * Определяет доступность колонки удаления
     *
     * @returns {*|boolean}
     */
    isDeleteGroupButtonDisabled: function (view, recordIndex, cellIndex, item, record) {
        var disabled = true;

        // проверяем можно ли исключать правило из группы
        if (record.isLeaf()) {
            if (Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'update')) {
                disabled = false;
            }
        // проверяем можно ли удалять группу
        } else {
            if (Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'delete')) {
                disabled = false;
            }
        }

        return disabled;
    },

    /**
     * Обработка события afterrender
     */
    onViewAfterRender: function () {
        var view = this.getView(),
            plugin;

        plugin = view.groupTree.getView().getPlugin('ruleddplugin');

        // если у пользователя нет прав на редактирование то ему запрещено перемещать элементы
        if (!Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'update')) {
            plugin.dropZone.lock();
        }
    }
});
