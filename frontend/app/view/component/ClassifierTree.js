/**
 * Дерево узлов классификатора
 *
 * Необходимо установить classifierName
 * Если устанавливаем после создания, то необходимо вручную вызвать displayRootChildren
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.component.ClassifierTree', {
    extend: 'Ext.tree.Panel',

    alias: 'widget.un.classifiertree',

    requires: [
        'Unidata.view.component.LargeTreeView'
    ],

    animate: false, // не убирать, иначе не будет работать скролл при "largetree"
    rootVisible: true,
    hideHeaders: true,
    enableKeyEvents: true,

    config: {
        classifier: null,
        classifierNodeId: null,
        classifierNodeView: 'EMPTY',    // EMPTY or DATA or META or PATH
        selectionMode: 'oneclick',       // oneclick or dblclick
        readOnly: false,
        previousSelected: null,
        codePattern: null,
        disableSearchIfReadOnly: true
    },

    lastSelected: null,

    listeners: {
        scope: 'this',
        itemclick: 'onItemClick',
        beforeselect: 'onBeforeSelect',
        select: 'onSelect',
        afteritemexpand: 'onAfterItemExpand',
        viewready: 'onGridViewReady'
    },

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    cls: 'un-classifier-tree',

    referenceHolder: true,

    tbar: [{
        xtype: 'textfield',
        reference: 'searchTextField',
        ui: 'un-field-default',
        hideLabel: true,
        enableKeyEvents: true,
        emptyText: Unidata.i18n.t('classifier>searchNode'),
        width: '100%',
        triggers: {
            reset: {
                cls: 'x-form-clear-trigger',
                reference: 'searchTextfieldResetTrigger'
            }
        },
        bind: {
            hidden: '{searchTextFieldHidden}'
        }
    }
    ],

    viewConfig: {
        xtype: 'largetree',
        deferEmptyText: false
    },

    searchTextField: null,
    codeColumn: null,

    initListeners: function () {
        var tableView = this.getView();

        // TODO: Обработчик beforedeselect вызывается дважды если возвращает false (даже если он присваивается статично и других обработчиков нет)
        this.on('beforeselect', this.onBeforeSelect, this);
        this.on('beforedeselect', this.onBeforeDeselect, this);
        this.on('beforeitemdblclick', this.onBeforeItemDblClick, this);
        tableView.on('itemkeyup', this.onItemKeyUp, this);
        tableView.on('refresh', this.onViewRefresh, this);
    },

    resetRootNode: function () {
        var store = this.getStore();

        store.root = null;

        store.removedNodes = [];
        Ext.Object.clear(store.byIdMap);
        store.sync();
    },

    initComponent: function () {
        var classifierName,
            inheritedColumnsCfg = this.config.columns,
            columnsCfg,
            classifierNodeView,
            selModel,
            readOnly,
            treeView,
            searchTextFieldResetTrigger,
            selectionMode,
            disableSearchIfReadOnly;

        if (!this.config.store &&
            !(this.config.bind && this.config.bind.store)) {
            this.store = this.createStoreCfg();
        }

        columnsCfg = this.createColumnsCfg();

        if (this.config.columns) {
            this.columns = Ext.Array.merge(inheritedColumnsCfg, columnsCfg);
            this.config.columns = this.columns ;
        } else {
            this.columns = columnsCfg;
        }

        if (this.config.selectionMode === 'dblclick') {
            this.allowDeselect = true;
        }

        this.callParent(arguments);

        classifierName = this.getClassifierName();

        classifierNodeView = this.getClassifierNodeView();

        if (classifierNodeView) {
            this.setProxyExtraParam('view', classifierNodeView);
        }

        if (classifierName) {
            this.setProxyExtraParam('classifierName', classifierName);
        }

        this.initListeners();
        this.initReferences();

        readOnly = this.getReadOnly();
        disableSearchIfReadOnly = this.getDisableSearchIfReadOnly();

        this.searchTextField.on('keypress', this.onSearchFieldKeyPress, this);
        this.searchTextField.setHidden(readOnly && disableSearchIfReadOnly);
        searchTextFieldResetTrigger = this.searchTextField.getTrigger('reset');

        if (searchTextFieldResetTrigger) {
            searchTextFieldResetTrigger.handler = this.resetSearchField.bind(this);
        }

        selectionMode = this.getSelectionMode();

        if (selectionMode === 'dblclick') {
            // lock selection if classifier tree is readOnly
            selModel = this.getSelectionModel();
            selModel.setLocked(readOnly);
        }

        this.on('render', this.initClassifierNode, this);

        treeView = this.getView();

        // обновляем ширину колонки с именем
        treeView.on('afteritemexpand', this.updateNameColumnSize, this);
        treeView.on('afteritemcollapse', this.updateNameColumnSize, this);
    },

    initClassifierNode: function () {
        var classifierNodeId = this.getClassifierNodeId();

        if (classifierNodeId) {
            this.displayClassifierNode();
        } else {
            this.displayRootNode();
        }
    },
    initReferences: function () {
        this.searchTextField = this.lookupReference('searchTextField');
        this.codeColumn = this.lookupReference('codeColumn');
    },

    createStoreCfg: function () {
        var storeCfg;

        // TODO: replace with type
        storeCfg = {
            model: 'Unidata.model.classifier.ClassifierNode',
            proxy: {
                type: 'un.classifiernode'
            }
        };

        return storeCfg;
    },

    highlightValueRenderer: function (value) {
        var searchTextField,
            searchValue,
            valueHighlighted,
            valueHighlightedEncoded,
            result,
            foundValue,
            re;

        searchTextField = this.searchTextField;
        searchValue = searchTextField.getValue();

        if (searchValue) {
            re = new RegExp(Ext.String.escapeRegex(searchValue), 'i');
            result = value.match(re);

            if (result && result.length > 0) {
                foundValue = result[0];
                valueHighlighted = Ext.String.format('<span class="un-highlight">{0}</span>', foundValue);
                valueHighlightedEncoded = Ext.String.htmlEncode(valueHighlighted);

                value = value.replace(foundValue, valueHighlighted);
                value = Ext.String.htmlEncode(value);

                value = value.replace(valueHighlightedEncoded, valueHighlighted);
            }
        } else {
            value = Ext.String.htmlEncode(value);
        }

        return value;
    },

    onEditorBeforeFocus: function (editor) {
        var selection = this.getSelection(),
            editorValue,
            fieldValue;

        if (selection.length !== 1) {
            return;
        }

        // матчим фокусированный editor и editor для выбранного узла по значению поля
        // значение является уникальным, поэтому матчинг надежный
        // пришлось так делать в связи с тем, что в editor нет информации к какому node он относится
        //TODO: Сделать матчинг по другому принципу. Но пока способ проще найти не удалось
        fieldValue = selection[0].get(editor.dataIndex);
        editorValue = editor.getValue();
        editor.setReadOnly(editorValue !== fieldValue);
    },

    createColumnsCfg: function () {
        var columnsCfg,
            me = this,
            codePattern = this.getCodePattern();

        function onEditorBeforeFocusWrapper () {
            Array.prototype.unshift.apply(arguments, [this]);
            //arguments.unshift(this);
            me.onEditorBeforeFocus.apply(me, arguments);
        }

        columnsCfg = [
            {
                dataIndex: 'code',
                reference: 'codeColumn',
                text: Unidata.i18n.t('glossary:code'),
                hideable: false,
                sortable: false,
                isAutoSize: true,
                editor: {
                    xtype: 'textfield',
                    cls: 'un-field',
                    allowBlank: true,
                    modelValidation: true,
                    beforeFocus: onEditorBeforeFocusWrapper
                },
                minWidth: 100,
                renderer: this.highlightValueRenderer.bind(this)
            },
            {
                xtype: 'un.treecolumn',
                dataIndex: 'name',
                text: Unidata.i18n.t('glossary:displayName'),
                hideable: false,
                sortable: false,
                isAutoSize: true,
                flex: 1,
                editor: {
                    xtype: 'textfield',
                    cls: 'un-field',
                    allowBlank: true,
                    modelValidation: true,
                    beforeFocus: onEditorBeforeFocusWrapper
                },
                renderer: this.highlightValueRenderer.bind(this)
                // TODO: SS, прицеплять при наследовании
            }
        ];

        return columnsCfg;
    },

    updateNameColumnSize: function () {
        this.getColumnByDataIndex('name').autoSize();
        this.autoHeight();
    },

    setProxyExtraParam: function (key, value) {
        var store,
            proxy;

        store = this.getStore();

        if (!store) {
            return;
        }

        proxy = store.getProxy();

        if (!proxy) {
            return;
        }

        if (!Ext.isFunction(proxy.setExtraParam)) {
            return;
        }

        proxy.setExtraParam(key, value);
    },

    updateClassifier: function (classifier) {
        var classifierName;

        if (!classifier || !this.rendered) {
            return;
        }

        this.setCodePattern(classifier.get('codePattern'));

        classifierName = classifier.get('name');

        this.setProxyExtraParam('classifierName', classifierName);

        this.resetRootNode();
        this.getStore().sync();
    },

    updateCodePattern: function (codePattern) {
        if (!this.codeColumn) {
            return;
        }
    },

    /**
     * Отобразить узлы на родительском уровне
     * Режим "Бродилка"
     */
    displayRootNode: function () {
        var classifierName = this.getClassifierName(),
            promise,
            hasClassifierNodeReadRight;

        function onGetClassifierNodeFulfilled (rootNodeCfg) {
            var store = this.getStore();

            if (!store) {
                return;
            }

            this.resetRootNode();
            this.setRootNode(rootNodeCfg);
            this.setStatus(Unidata.StatusConstant.READY);
        }

        function onGetClassifierNodeRejected (classifierNodeId) {
            this.fireComponentEvent('classifiernodeloadfailure', classifierNodeId);
            this.setStatus(Unidata.StatusConstant.READY);
        }

        // права на чтение теперь не играют роли для классификаторов (см. баг UN-5604)
        //hasClassifierNodeReadRight = this.userHasClassifierNodeReadRight();

        if (!classifierName /*|| !hasClassifierNodeReadRight */) {
            return;
        }

        this.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getRootNode(classifierName, 'META');
        promise
            .then(onGetClassifierNodeFulfilled.bind(this),
                onGetClassifierNodeRejected.bind(this))
            .done();
    },

    /**
     * Отобразить дерево в режиме "Открывалка"
     * Id узла может быть установлен предварительно
     */
    displayClassifierNode: function (classifierNodeId) {
        var classifierName = this.getClassifierName(),
            hasClassifierNodeReadRight,
            promise;

        // если передали идентификатор то сохраняем
        if (classifierNodeId) {
            this.setClassifierNodeId(classifierNodeId);
        }

        classifierNodeId = this.getClassifierNodeId();

        function onGetClassifierNodeFulfilled (classifierNodeId, rootNodeCfg) {
            var root,
                classifierNode;

            this.resetRootNode();

            function onExpand () {
                classifierNode = root.findChild('id', classifierNodeId, true);

                if (classifierNode) {
                    this.toggleNodeSelectionInDblClickMode(classifierNode, true);
                }

                this.appendSpecialNodes(root);
                this.setStatus(Unidata.StatusConstant.READY);
            }

            this.setRootNode(rootNodeCfg);
            root = this.getRootNode();
            classifierNode = root.findChild('id', classifierNodeId, true)
            classifierNode.expand(false, onExpand.bind(this), this);

            this.updateNameColumnSize();
        }

        function onGetClassifierNodeRejected (classifierNodeId) {
            this.setStatus(Unidata.StatusConstant.READY);
            this.fireComponentEvent('classifiernodeloadfailure', classifierNodeId);
        }

        // права на чтение теперь не играют роли для классификаторов (см. баг UN-5604)
        //hasClassifierNodeReadRight = this.userHasClassifierNodeReadRight();

        if (!classifierName || !classifierNodeId /*|| !hasClassifierNodeReadRight*/) {
            return;
        }

        this.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getRootNodeForClassifierNode(classifierName, classifierNodeId, 'PATH');
        promise
            .then(onGetClassifierNodeFulfilled.bind(this, classifierNodeId),
                onGetClassifierNodeRejected.bind(this, classifierNodeId))
            .done();
    },

    displaySearchedClassifierNodes: function (text) {
        var classifierName = this.getClassifierName(),
            promise;

        function onGetClassifierNodeFulfilled (text, rootNodeCfg) {
            var root;

            this.resetRootNode();
            this.searchTextField.originalValue = text;

            if (rootNodeCfg) {

                this.setRootNode(rootNodeCfg);
                root = this.getRootNode();
                this.expandAll(this.appendSpecialNodes.bind(this, root), this);
            } else {
                this.clearNodes();
            }
            this.setStatus(Unidata.StatusConstant.READY);
        }

        function onGetClassifierNodeRejected () {
            this.setStatus(Unidata.StatusConstant.READY);
            this.fireComponentEvent('classifiernodesearchfailure');
        }

        if (!classifierName) {
            return;
        }

        if (!text) {
            this.displayRootNode();
        }

        this.setStatus(Unidata.StatusConstant.LOADING);

        promise = Unidata.util.api.Classifier.getRootNodeBySearchRequest(classifierName, text);
        promise
            .then(onGetClassifierNodeFulfilled.bind(this, text),
                onGetClassifierNodeRejected.bind(this))
            .done();
    },

    expandAllBesidesLastChildren: function () {
        var me = this,
            root = me.getRootNode(),
            spec;

        if (root) {
            Ext.suspendLayouts();

            function onBeforeCascade (node) {
                var childCount = node.get('childCount'),
                    shownChildCount = node.childNodes.length,
                    moreChildCount = childCount - shownChildCount;

                if (moreChildCount === 0) {
                    // создаем специальный фиктивный узел по клику на который будем грузить доп. детей
                    node.expandChildren();
                }
            }

            spec = {
                scope: this,
                before: onBeforeCascade
            };

            root.cascadeBy(spec);
            Ext.resumeLayouts(true);
        }
    },

    /**
     * Подгрузка дополнительных дочерних узлов по клику на спец.узел
     * @param specialChildNode
     */
    loadMoreChildNodes: function (node) {
        var promise,
            hasClassifierNodeReadRight;

        if (!node) {
            return;
        }

        // права на чтение теперь не играют роли для классификаторов (см. баг UN-5604)
        // но пока оставляем
        //hasClassifierNodeReadRight = this.userHasClassifierNodeReadRight();

        //if (!hasClassifierNodeReadRight) {
        //    return;
        //}

        this.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getClassifierNode(node, 'DATA');
        promise
            .then(this.onGetClassierNodeBySpecialNodeFulfilled.bind(this),
                this.onGetClassifierNodeBySpecialNodeRejected.bind(this))
            .done();
    },

    onGetClassierNodeBySpecialNodeFulfilled: function (node) {
        var children = node.data.children,
            childrenIdsSorted,
            childrenIdsSortedMap,
            specialNode,
            tableView = this.getView();

        function filterChildren (node, child) {
            var id = child.id;

            // добавялем только если такого узла еще нет
            return !node.findChild('id', id);
        }

        function sort (idsMap, a, b) {
            var aIx,
                bIx,
                result = 0;

            aIx = idsMap[a.get('id')];
            bIx = idsMap[b.get('id')];

            aIx = aIx ? aIx : -1;
            bIx = bIx ? bIx : -1;

            if (aIx > bIx) {
                result = 1;
            } else if (aIx < bIx) {
                result = -1;
            } else {
                result = 0;
            }

            return result;
        }

        childrenIdsSorted = Ext.Array.pluck(children, 'id');
        childrenIdsSortedMap = Ext.Array.toMap(childrenIdsSorted);
        children = Ext.Array.filter(children, filterChildren.bind(this, node), this);
        node.appendChild(children);
        specialNode = node.findChild('isSpecialNode', true);

        if (specialNode) {
            node.removeChild(specialNode, false);
        }

        node.sort(sort.bind(this, childrenIdsSortedMap));

        tableView.on('refresh', function () {
            this.setStatus(Unidata.StatusConstant.READY);
            this.autoSizeColumns();
        }, this, {single: true});
        tableView.refresh();
    },

    onGetClassifierNodeBySpecialNodeRejected: function () {
        this.setStatus(Unidata.StatusConstant.READY);
        this.fireComponentEvent('classifiernodeloadfailure', classifierNodeId);
    },

    /**
     * Добавляем спец.узлы там где это необходимо
     * @param node
     */
    appendSpecialNodes: function (node) {
        var spec,
            view = this.getView();

        function onBeforeCascade (node) {
            var childCount = node.get('childCount'),
                shownChildCount = node.childNodes.length,
                moreChildCount = childCount - shownChildCount;

            if (node.get('isSpecialNode') || !node.data.children) {
                return;
            }

            if (moreChildCount > 0) {
                // создаем специальный фиктивный узел по клику на который будем грузить доп. детей
                node.appendSpecialNode(moreChildCount);
            }
        }

        spec = {
            scope: this,
            before: onBeforeCascade
        };

        node.cascadeBy(spec);
        view.refresh();
    },

    updateClassifierNodeView: function (classifierNodeView) {
        this.setProxyExtraParam('view', classifierNodeView);
    },

    onItemClick: function (self, node) {
        if (node.get('isSpecialNode')) {
            this.cancelEditClassifierNode();
            this.loadMoreChildNodes(node.parentNode);
        }
    },

    autoSizeColumns: function () {
        Ext.each(this.columns, function (column) {
            if (column.isAutoSize === true) {
                column.autoSize();
            }
        });
    },

    onAfterItemExpand: function () {
        this.autoSizeColumns();
        this.autoHeight();
    },

    /**
     * Расчет высоты панели в зависимоси от содержимого
     * (также неявно будет применен параметр maxHeight)
     */
    autoHeight: function () {
        var tableView = this.getView(),
            body,
            bodyHeight,
            searchTextField = this.searchTextField,
            searchTextFieldHeight;

        if (!tableView) {
            return;
        }

        body = tableView.body;

        if (!body.dom) {
            return;
        }

        bodyHeight = body.getHeight();
        searchTextFieldHeight = searchTextField.getHeight();
        this.setHeight(bodyHeight + searchTextFieldHeight + 20);
    },

    onSelect: function (self, node) {
        this.setPreviousSelected(this.lastSelected);
        this.lastSelected = node;
    },

    onBeforeSelect: function (self, node) {
        var selectionMode = this.getSelectionMode(),
            isSpecialNode = node.get('isSpecialNode');

        if (selectionMode === 'dblclick') {
            return false;
        } else {
            return !isSpecialNode;
        }
    },

    onBeforeDeselect: function (self, node) {
        var selectionMode = this.getSelectionMode();

        if (selectionMode === 'dblclick') {
            return false;
        } else {
            return node.isValid();
        }
    },

    /**
     * Обработчик dblClick, используется в DBLCLICK режиме
     * @param self
     * @param node
     * @returns {boolean}
     */
    onBeforeItemDblClick: function (self, node) {
        var selectionMode = this.getSelectionMode(),
            isSpecialNode = node.get('isSpecialNode');

        if (selectionMode === 'dblclick') {
            if (!isSpecialNode) {
                this.toggleNodeSelectionInDblClickMode(node);
            }

            return false;
        }
    },

    toggleNodeSelectionInDblClickMode: function (node, isInitSelect) {
        var selModel = this.selModel,
            previousSelected = [],
            locked = selModel.isLocked();

        /**
         * Вычисление узлов, выбранных на текущем шаге
         * @param selected {Array} Все выбранные узлы
         * @param previousSelected {Array} Все узлы, выбранные ранее
         * @returns {Array}
         */
        function calcSelectedNodes (selected, previousSelected) {
            var diff;

            diff = Ext.Array.difference(selected, previousSelected);

            return diff;
        }

        /**
         * Вычисление узлов, сброшенных (deselect) на текущем шаге
         * @param selected {Array} Все выбранные узлы
         * @param previousSelected {Array} Все узлы, выбранные ранее
         * @returns {Array}
         */
        function calcDeselectedNodes (selected, previousSelected) {
            var diff;

            diff = Ext.Array.difference(previousSelected, selected);

            return diff;
        }

        /**
         * Бросить событие Ext.tree.Panel
         * @param selected {Array} Все выбранные узлы
         * @param previousSelected {Array} Все узлы, выбранные ранее
         * @param self {Ext.tree.Panel} Панель дерева
         */
        function fireNodeSelectionChange (previousSelected, self, selected) {
            var classifierTree = self.view.grid,
                selectedNodes,
                deselectedNodes;

            selectedNodes = calcSelectedNodes(selected, previousSelected);
            deselectedNodes = calcDeselectedNodes(selected, previousSelected);

            /**
             * Параметры события
             *
             * @param self {Ext.tree.Panel} Панель дерева
             * @param selected {Array} Все выбранные узлы
             * @param previousSelected {Array} Все узлы, выбранные ранее
             * @param selectedNodes {Array} Узлы, выбранные на текущем шаге
             * @param deselectedNodes {Array} Узлы, сброшенные (deselect) на текущем шаге
             */
            classifierTree.fireEvent('nodeselectionchange', classifierTree, selected, previousSelected,
                selectedNodes, deselectedNodes);
        }

        isInitSelect = isInitSelect !== undefined ? isInitSelect : false;

        if (isInitSelect) {
            this.suspendEvent('deselect');
            this.suspendEvent('select');
        }
        this.suspendEvent('beforeselect');
        this.suspendEvent('beforedeselect');

        if (!isInitSelect) {
            previousSelected = this.getSelection();

            this.on('selectionchange', fireNodeSelectionChange, this, {
                single: true,
                args: [previousSelected]
            });
        }

        if (isInitSelect) {
            selModel.setLocked(false);
        }

        if (selModel.isSelected(node)) {
            selModel.deselect(node);
        } else {
            selModel.select(node);
        }

        if (isInitSelect) {
            selModel.setLocked(locked);
        }

        this.resumeEvent('beforeselect');
        this.resumeEvent('beforedeselect');

        if (isInitSelect) {
            this.resumeEvent('deselect');
            this.resumeEvent('select');
        }
    },

    updateReadOnly: function (readOnly) {
        var selModel,
            selectionMode = this.getSelectionMode(),
            disableSearchIfReadOnly = this.getDisableSearchIfReadOnly();

        if (!this.rendered) {
            return;
        }

        if (selectionMode === 'dblclick') {
            selModel = this.getSelectionModel();
            selModel.setLocked(readOnly);
        }

        this.searchTextField.setHidden(readOnly && disableSearchIfReadOnly);
    },

    onSearchFieldKeyPress: function (searchfield, e) {
        var key = e.getKey(),
            text = searchfield.getValue();

        if (key === Ext.event.Event.ENTER) {
            if (text) {
                this.displaySearchedClassifierNodes(text);
            } else {
                this.resetSearchField(this.searchTextField);
            }
        }
    },

    cancelEditClassifierNode: function () {
        var plugin = this.getPlugin('cellediting');

        if (!plugin) {
            return;
        }

        plugin.cancelEdit();
    },

    resetSearchField: function (searchField) {
        if (searchField.originalValue) {
            this.displayRootNode();
        }
        searchField.originalValue = '';
        searchField.setValue('');
    },

    userHasClassifierNodeReadRight: function () {
        var hasRight = false,
            classifierName = this.getClassifierName();

        if (classifierName) {
            hasRight = Unidata.Config.userHasRight(classifierName, 'read');
        }

        return hasRight;
    },

    onItemKeyUp: function (self, node, elem, index, e) {
        var key = e.getKey();

        this.handleKey(key, node);
    },

    handleKey: function (key, node) {
        var evt = Ext.event.Event;

        switch (key) {
            case evt.SPACE:
                if (node.isExpanded()) {
                    node.collapse();
                } else {
                    node.expand();
                }
                break;
        }
    },

    onViewRefresh: function () {
        this.autoSizeColumns();
        this.autoHeight();
    },

    selectPreviousSelectedNode: function () {
        var previousSelected = this.getPreviousSelected(),
            selModel = this.getSelectionModel(),
            tableView = this.getView();

        if (previousSelected) {
            selModel.doSelect(previousSelected);
            tableView.focusNode(previousSelected);
        }
    },

    focusOnSelection: function () {
        var selModel = this.getSelectionModel(),
            tableView = this.getView(),
            selection;

        selection = selModel.getSelection();

        if (selection.length === 1) {
            tableView.focusNode(selection[0]);
        }
        tableView.focus();
    },

    deselectAll: function () {
        var selModel = this.getSelectionModel();

        selModel.deselectAll();
    },

    setRootNode: function () {
        var rootNode;

        this.callParent(arguments);

        rootNode = this.getRootNode();

        if (rootNode) {
            rootNode.phantom = false;
        }
    },

    getClassifierName: function () {
        var classifierName = null,
            classifier = this.getClassifier();

        if (classifier) {
            classifierName = classifier.get('name');
        }

        return classifierName;
    },

    clearNodes: function () {
        var store = this.getStore();

        store.removeAll();
    },

    getColumnByDataIndex: function (dataIndex) {
        var columns = this.columns,
            found;

        found = Ext.Array.findBy(columns, function (column) {
            return column.dataIndex === dataIndex;
        });

        return found;
    },

    onGridViewReady: function (grid) {
        this.initTooltip(grid);
    },

    initTooltip: function (grid) {
        var me       = this,
            gridView        = me.getView();

        this.toolTip = Ext.create('Ext.tip.ToolTip', {
            target: gridView.el,
            delegate: '.x-grid-cell',
            trackMouse: true,
            renderTo: Ext.getBody(),
            listeners: {
                beforeshow: function (tip) {
                    var row,
                        classifierNode,
                        name,
                        code,
                        values = [],
                        tipTemplate,
                        tipHtml;

                    if (!gridView.rendered) {
                        return false;
                    }

                    row = tip.triggerElement.parentElement;
                    classifierNode = gridView.getRecord(row);

                    if (!classifierNode || classifierNode.isRoot()) {
                        return false;
                    }

                    name = classifierNode.get('name');
                    code = classifierNode.get('code');

                    if (code) {
                        values.push('Код: ' + code);
                    }

                    if (name) {
                        values.push(name);
                    }

                    tipTemplate = Ext.create('Ext.Template', [
                        '{value:htmlEncode}'
                    ]);
                    tipTemplate.compile();
                    tipHtml = tipTemplate.apply({
                        value: values.join(', ')
                    });

                    tip.update(tipHtml);
                }
            }
        });
    }
});
