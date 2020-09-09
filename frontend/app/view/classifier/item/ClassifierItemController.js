/**
 * Экран "Классификатор" (контроллер)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.item.ClassifierItemController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifier.item',

    classifierTree: null,
    classifierNodeContainer: null,
    classifierSettingsPanel: null,

    init: function () {
        var view = this.getView(),
            classifier = view.getClassifier();

        this.initReferences();
        this.initComponentListeners();
        this.initClassifierTreeListeners();

        if (classifier && !classifier.phantom) {
            this.displayClassifierTreeRootChildren(classifier);
        }
        this.callParent(arguments);

        this.isNodeAttrsColumn.tpl = this.createIsNodeAttrsColumnTpl();

        this.classifierTree.header = {
            listeners: {
                click: {
                    element: 'el',
                    fn: this.onClassifierTreeHeaderClick.bind(this)
                }
            }
        };
    },

    initReferences: function () {
        this.classifierTree = this.lookupReference('classifierTree');
        this.classifierNodeContainer = this.lookupReference('classifierNodeContainer');
        this.classifierSettingsPanel = this.lookupReference('classifierSettingsPanel');
        this.isNodeAttrsColumn = this.classifierTree.lookupReference('isNodeAttrsColumn');
    },

    initComponentListeners: function () {
        var view = this.getView();

        view.addComponentListener('classifiernodeloadfailure', this.onClassifierNodeLoadFailure, this);
        view.addComponentListener('classifiernodeready', this.onClassifierNodeReady, this);
    },

    initClassifierTreeListeners: function () {
        var tableView = this.classifierTree.getView();

        tableView.on('itemkeyup', this.onItemKeyUp, this);
    },

    displayClassifierTreeRootChildren: function (classifier) {
        var codePattern = classifier.get('codePattern'),
            classifierTree = this.classifierTree;

        classifierTree.setClassifier(classifier);
        classifierTree.setCodePattern(codePattern);

        // права на чтение теперь не играют роли для классификаторов (см. баг UN-5604)
        // но пока оставляем
        //if (this.userHasClassifierNodeReadRight()) {
        classifierTree.displayRootNode();
        //}
    },

    userHasClassifierNodeReadRight: function () {
        var hasRight = false,
            view = this.getView(),
            classifier = view.getClassifier(),
            classifierName;

        if (classifier) {
            classifierName = classifier.get('name');
            hasRight = Unidata.Config.userHasRight(classifierName, 'read');
        }

        return hasRight;
    },

    updateClassifier: function (classifier) {
        if (!classifier) {
            return;
        }

        this.classifierNodeContainer.setClassifier(classifier);

        if (classifier.phantom || !this.classifierTree) {
            return;
        }

        Unidata.util.DataRecord.bindManyToOneAssociationListeners(classifier);

        this.displayClassifierTreeRootChildren(classifier);
    },

    onAddChildClassifierNodeButtonClick: function (self, cell, recordIndex, cellIndex, e, parentClassifierNode) {
        this.createClassifierNode(parentClassifierNode, {
            //name: '...новый узел...'
        });
    },

    promptDeleteClassifierNode: function (cell, classifierNode) {
        var me = this,
            title = Unidata.i18n.t('classifier>removeNode'),
            msg = Unidata.i18n.t('classifier>confirmRemoveNode'),
            classifierTree = this.lookupReference('classifierTree'),
            promise;

        if (classifierNode.phantom) {
            Unidata.showPrompt(title, msg, this.deleteClassifierNode, this, cell, [classifierNode]);

            return;
        }

        // подгружаем узел, чтобы подгрузить hasData
        classifierTree.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getClassifierNode(classifierNode, 'META');
        promise
            .then(function (fullClassifierNode) {
                var hasData = fullClassifierNode.get('hasData');

                classifierTree.setStatus(Unidata.StatusConstant.READY);

                if (hasData) {
                    msg = Unidata.i18n.t('classifier>haveRecordsClassifiedByNode') + '<br>' + msg;
                }
                Unidata.showPrompt(title, msg, me.deleteClassifierNode, me, cell, [classifierNode]);
            }, function () {
                classifierTree.setStatus(Unidata.StatusConstant.NONE);
            })
            .done();
    },

    onDeleteClassifierNodeButtonClick: function (self, cell, recordIndex, cellIndex, e, classifierNode) {
        this.promptDeleteClassifierNode(cell, classifierNode);
    },

    onClassifierTreeSelect: function (classifierTree, classifierNode) {
        var view                 = this.getView(),
            classifierTreeSaving = view.getClassifierTreeSaving(),
            classifier           = view.getClassifier(),
            classifierNodePanel = view.classifierNodeContainer.classifierNodePanel,
            currentNode;

        if (classifierNodePanel) {
            currentNode = classifierNodePanel.getClassifierNode();
        }

        if (currentNode && !classifierTreeSaving && this.filterModified(currentNode)) {
            this.saveClassifierNode(currentNode);
        }

        // если находим в состоянии сохранения узла, то подгружаем и отображаем узел только когда сохранение будет завершено
        if (classifierTreeSaving) {
            view.on('classifiernodesavefinish', function () {
                this.classifierNodeContainer.loadAndSetClassifierNode(classifierNode, classifier);
            }, this, {single: true});
        } else {
            this.classifierNodeContainer.loadAndSetClassifierNode(classifierNode, classifier);
        }
    },

    onClassifierNodeSaveSuccess: function (node, operation) {
        var view = this.getView(),
            classifierTree = this.classifierTree,
            store = classifierTree.getStore(),
            tableView = classifierTree.getView(),
            foundNode;

        view.setClassifierTreeSaving(false);

        foundNode = store.findRecord('id', node.getId());
        if (foundNode) {
            foundNode.set('ownNodeAttrs', node.get('ownNodeAttrs'), {dirty: false});
            foundNode.set('name', node.get('name'), {dirty: false});
            foundNode.set('code', node.get('code'), {dirty: false});
            tableView.refresh();
        }

        if (operation instanceof Ext.data.operation.Create) {
            Unidata.showMessage(view.nodesSyncCreateSuccessText);
        } else if (operation instanceof Ext.data.operation.Update) {
            Unidata.showMessage(view.nodesSyncUpdateSuccessText);
        }

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
        view.fireEvent('classifiernodesavefinish', classifierTree, node);
    },

    /**
     *
     * @param restoreNodeOnFailure Признак восстановления узла в случая failure операции
     * @param node
     * @param operation
     */
    onClassifierNodeSaveFailure: function (restoreNodeOnFailure, node, operation) {
        var view = this.getView(),
            classifierTree = this.classifierTree;

        view.setClassifierTreeSaving(false);

        if (operation instanceof Ext.data.operation.Create) {
            this.decrementParentNodeChildCount(node);
            Unidata.showError(view.nodesSyncFailureText);
        } else if (operation instanceof Ext.data.operation.Update) {
            Unidata.showError(view.nodesSyncFailureText);
        }

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);

        if (restoreNodeOnFailure && !node.phantom) {
            // удаляем узел из списка на удаление
            this.classifierNodeContainer.loadClassifierNode(node);
        }

        view.fireEvent('classifiernodesavefinish', classifierTree, node);
    },

    /**
     * Сохранить узел классификатора
     *
     * @param node {Unidata.model.classifier.ClassifierNode}
     * @param restoreNodeOnFailure Признак восстановления узла в случая failure операции
     */
    saveClassifierNode: function (node, restoreNodeOnFailure) {
        var classifierTree = this.classifierTree,
            classifier = classifierTree.getClassifier(),
            classifierName = classifier.get('name'),
            view = this.getView(),
            hasClassifierNodeRights = false;

        restoreNodeOnFailure = Ext.isBoolean(restoreNodeOnFailure) ? restoreNodeOnFailure : false;

        if (this.isClassifierNodeValid(node)) {
            view.setClassifierTreeEdit(false);

            // сбрасываем предыдущие отображенные ошибки
            this.resetClassifierNodeErrors(node);

            if ((node.dirty || node.phantom)) {
                node.getProxy().setExtraParam('classifierName', classifierName);
                hasClassifierNodeRights = Unidata.Config.userHasAnyRights(classifierName, ['update', 'create']);

                if (hasClassifierNodeRights) {
                    view.setClassifierTreeSaving(true);
                    view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.LOADING);
                    node.save({
                        scope: this,
                        success: this.onClassifierNodeSaveSuccess,
                        failure: this.onClassifierNodeSaveFailure.bind(this, restoreNodeOnFailure)
                    });
                }
            }
        } else {
            Unidata.showError(view.classifierNodeValidationErrorText);
            this.highlightClassifierNodeErrors(node);
        }
        view.fireEvent('classifiernodesavefinish', classifierTree, node);
    },

    onClassifierTreeBeforeDeselect: function (self, node) {
        if (!this.isClassifierNodeValid(node)) {
            this.highlightClassifierNodeErrors(node);
        }

        return this.isClassifierNodeValid(node);
    },

    onClassifierTreeSelectionChange: function (self, records) {
        if (records.length === 0) {
            this.classifierNodeContainer.setClassifierNode(null);
        }
    },

    isClassifierNodeValid: function (node) {
        var viewModel = this.getViewModel();

        return viewModel.isClassifierNodeValid(node);
    },

    isNodeCodeFieldValid: function (node) {
        var viewModel = this.getViewModel();

        return viewModel.isNodeCodeFieldValid(node);
    },

    onClassifierTreeDeselect: function (self, node) {
        var view = this.getView();

        node = view.classifierNodeContainer.classifierNodePanel.getClassifierNode();
        this.saveClassifierNode(node);
        this.classifierNodeContainer.setClassifierNode(null);
    },

    onDeleteClassifierNodeFulfilled: function (node) {
        var view = this.getView(),
            classifierTree = this.classifierTree,
            classifierNodeContainer = this.classifierNodeContainer,
            currentClassifierNode = classifierNodeContainer.getClassifierNode();

        view.setClassifierTreeEdit(false);
        view.setClassifierTreeSaving(false);
        classifierTree.resumeEvent('beforedeselect');
        classifierTree.resumeEvent('deselect');

        // если удаляемый узел открыт, то принудительно закрыть панель
        if (currentClassifierNode && node.get('id') === currentClassifierNode.get('id')) {
            classifierNodeContainer.setClassifierNode(null);
        }

        if (!node.phantom) {
            Unidata.showMessage(view.nodesSyncDeleteSuccessText);
        }
    },

    onDeleteClassifierNodeRejected: function (node) {
        var view = this.getView();

        this.incrementParentNodeChildCount(node);
        Unidata.showError(view.nodesSyncFailureText);

        view.setClassifierTreeEdit(false);
        view.setClassifierTreeSaving(false);
        this.classifierTree.resumeEvent('beforedeselect');
        this.classifierTree.resumeEvent('deselect');
    },

    decrementParentNodeChildCount: function (node) {
        this.alterParentNodeChildCount(node, -1);
    },

    incrementParentNodeChildCount: function (record) {
        this.alterParentNodeChildCount(record, 1);
    },

    decrementNodeChildCount: function (node) {
        this.alterNodeChildCount(node, -1);
    },

    incrementNodeChildCount: function (node) {
        this.alterNodeChildCount(node, 1);
    },

    alterParentNodeChildCount: function (record, value) {
        var parentNode = record.parentNode;

        this.alterNodeChildCount(parentNode, value);
    },

    alterNodeChildCount: function (node, value) {
        var childCount;

        if (!node) {
            return;
        }

        childCount = node.get('childCount');
        childCount = childCount + value;
        childCount = childCount > 0 ? childCount : 0;
        node.set('childCount', childCount);
    },

    createClassifierNode: function (parentClassifierNode, cfg) {
        var promise,
            view = this.getView(),
            classifier = view.getClassifier(),
            viewModel = this.getViewModel(),
            validAndNotDirty,
            isAddActionDisabled = false;

        if (!parentClassifierNode.isRoot()) {
            validAndNotDirty    = viewModel.isClassifierNodeValidAndNotDirty(parentClassifierNode);
            isAddActionDisabled = viewModel.isAddActionDisabled(classifier, validAndNotDirty);
        }

        if (isAddActionDisabled) {
            return;
        }

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.LOADING);

        promise = Unidata.util.api.Classifier.getClassifierNode(parentClassifierNode, 'META');
        promise
            .then(this.onGetParentClassifierNodeForCreateFulfilled.bind(this, parentClassifierNode, cfg),
                this.onGetParentClassifierNodeForCreateRejected.bind(this, parentClassifierNode, cfg))
            .done();
    },

    onGetParentClassifierNodeForCreateFulfilled: function (parentClassifierNode, cfg) {
        var parentClassifierNodeLeaf,
            classifierNode,
            classifierTree = this.classifierTree,
            inheritedNodeAttrs,
            view = this.getView(),
            parentInheritedNodeAttrs,
            parentOwnNodeAttrs;

        function onAfterItemExpand (classifierTree, classifierNode, parentClassifierNode) {
            var view = this.getView();

            this.appendClassifierNode(parentClassifierNode, classifierNode, classifierTree);
            view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
        }

        this.incrementNodeChildCount(parentClassifierNode);
        parentClassifierNodeLeaf = parentClassifierNode.get('leaf');

        classifierNode = Ext.create('Unidata.model.classifier.ClassifierNode', cfg);
        inheritedNodeAttrs = classifierNode.inheritedNodeAttrs();
        parentInheritedNodeAttrs = parentClassifierNode.inheritedNodeAttrs().getRange();
        parentOwnNodeAttrs = parentClassifierNode.nodeAttrs().getRange();

        parentOwnNodeAttrs  = Ext.Array.filter(parentOwnNodeAttrs, function (nodeAttr) {
            var name = nodeAttr.get('name'),
                found;

            found = Ext.Array.findBy(parentInheritedNodeAttrs, function (parentInheritedNodeAttr) {
                var inheritedNodeAttrName = parentInheritedNodeAttr.get('name');

                return name === inheritedNodeAttrName;
            });

            return !found;
        });
        inheritedNodeAttrs.add(parentInheritedNodeAttrs);
        inheritedNodeAttrs.add(parentOwnNodeAttrs);

        if (!parentClassifierNodeLeaf && !parentClassifierNode.isExpanded() && !parentClassifierNode.isRoot()) {
            classifierTree.on('afteritemexpand',
                onAfterItemExpand.bind(this, classifierTree, classifierNode), this, {single: true});
            parentClassifierNode.expand();
        } else {
            this.appendClassifierNode(parentClassifierNode, classifierNode, classifierTree);
            view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
        }
    },

    onGetParentClassifierNodeForCreateRejected: function () {
        var view = this.getView();

        Unidata.showError(view.classifierNodeLoadFailureText);

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
    },

    appendClassifierNode: function (parentClassifierNode, classifierNode, classifierTree) {
        var column = classifierTree.getColumnByDataIndex('name') || classifierTree.getColumnByDataIndex('code');

        parentClassifierNode.insertChild(0, classifierNode);
        this.selectAndbeginEditClassifierNode(classifierNode, column);
    },

    deleteClassifierNode: function (classifierNode) {
        var classifierTree = this.classifierTree,
            ClassifierApi = Unidata.util.api.Classifier;

        classifierTree.suspendEvent('beforedeselect');
        classifierTree.suspendEvent('deselect');
        this.decrementParentNodeChildCount(classifierNode);
        classifierNode.remove();

        if (!classifierNode.phantom) {
            ClassifierApi.deleteClassifierNode(classifierNode)
                .then(this.onDeleteClassifierNodeFulfilled.bind(this),
                    this.onDeleteClassifierNodeRejected.bind(this))
                .done();
        } else {
            classifierNode.erase({
                scope: this,
                success: this.onDeleteClassifierNodeFulfilled,
                failure: this.onDeleteClassifierNodeRejected
            });
        }
    },

    onCellBeforeEdit: function (editor, context) {
        var grid = editor.grid,
            readOnly = grid.getReadOnly(),
            view = this.getView(),
            node = context.record,
            cancelEdit = true;

        view.setClassifierTreeEdit(true);

        cancelEdit = readOnly || (node && node.isRoot());

        return !cancelEdit;
    },

    onCellEdit: function () {
        var view = this.getView();

        view.setClassifierTreeEdit(false);
        this.classifierTree.focusOnSelection();
    },

    onSaveClassifierButtonClick: function () {
        this.saveClassifier();
        // сохраняем классификатор в ClassifierList
        //view.fireComponentEvent('saveclassifier', classifier);
    },

    onDeleteClassifierButtonClick: function (btn) {
        var view = this.getView(),
            classifier = view.getClassifier(),
            title = Unidata.i18n.t('classifier>removeClassifier'),
            msg = Unidata.i18n.t('classifier>confirmRemoveClassifier');

        Unidata.showPrompt(title, msg, this.deleteClassifier, this, btn, [classifier]);
    },

    saveClassifier: function () {
        var view = this.getView(),
            classifier = view.getClassifier(),
            viewModel = this.getViewModel(),
            hasClassifierRights = false,
            isCreate = false;

        if (classifier.phantom) {
            isCreate = true;
        }

        if (this.isClassifierValid()) {
            hasClassifierRights = viewModel.userHasClassifierCreateOrUpdateRights(classifier);

            if (hasClassifierRights) {
                view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.LOADING);
                classifier.save({
                    scope: this,
                    success: this.onClassifierSaveSuccess.bind(this, isCreate),
                    failure: this.onClassifierSaveFailure
                });
            } else {
                this.saveModifiedClassifierNodes();
            }
        } else {
            Unidata.showError(Unidata.i18n.t('classifier>checkValues'));
        }
    },

    isClassifierValid: function () {
        var isModelValid,
            fields,
            view = this.getView(),
            classifier = view.getClassifier(),
            isFieldsValid = true,
            isValid;

        function isFieldValid (field) {
            var isValid;

            // мы хотим вызвать появление сообщений о провале валидациии. Поэтому validate().
            isValid = field.validate();
            isFieldsValid = isFieldsValid && isValid;
        }

        isModelValid = classifier.isValid();
        fields = this.classifierSettingsPanel.query('field');
        Ext.Array.forEach(fields, isFieldValid);

        isValid = isModelValid && isFieldsValid;

        return isValid;
    },

    deleteClassifier: function (classifier) {
        var view = this.getView();

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.LOADING);

        classifier.erase({
            success: this.onClassifierDeleteSuccess,
            failure: this.onClassifierDeleteFailure,
            callback: this.onClassifierNodeOperationCallback,
            scope: this
        });
    },

    onClassifierDeleteSuccess: function (classifier) {
        var view = this.getView();

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
        view.setClassifier(null);
        view.fireComponentEvent('classifierdelete', classifier);
        Unidata.showMessage(view.classifierDeleteSuccessText);
    },

    onClassifierDeleteFailure: function () {
        var view = this.getView();

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
        Unidata.showError(view.classifierDeleteFailureText);
    },

    cleanSpecialNodes: function (store) {
        var byIdMap = store.byIdMap,
            nodes;

        function filterFn (node) {
            return !node.get('isSpecialNode');
        }

        function getNodeId (node) {
            return node.getId();
        }

        nodes = Ext.Object.getValues(byIdMap);
        nodes = Ext.Array.filter(nodes, filterFn);
        store.byIdMap = Ext.Array.toValueMap(nodes, getNodeId);
    },

    onClassifierSaveSuccess: function (isCreate, classifier) {
        var view = this.getView();

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);

        if (isCreate) {
            this.displayClassifierTreeRootChildren(classifier);
        } else {
            this.classifierTree.setClassifier(classifier);
        }

        Unidata.showMessage(view.classifierSaveSuccessText);
        this.saveModifiedClassifierNodes();
    },

    saveModifiedClassifierNodes: function () {
        var toUpdate,
            currentClassifierNode,
            found;

        currentClassifierNode = this.classifierNodeContainer.getClassifierNode();

        if (!currentClassifierNode) {
            return;
        }

        toUpdate = this.getStoreModifiedRecords();
        toUpdate.forEach(this.saveClassifierNode.bind(this));

        found = Ext.Array.findBy(toUpdate, function (item) {
            return item.get('id') === currentClassifierNode.get('id');
        });

        if (this.filterModified(currentClassifierNode) && !Boolean(found)) {
            this.saveClassifierNode(currentClassifierNode);
        }

        return;
    },

    getStoreModifiedRecords: function () {
        var classifierTree = this.classifierTree,
            store = classifierTree.getStore();

        return Ext.Array.filter(Ext.Object.getValues(store.byIdMap), this.filterModified, this);
    },

    filterModified: function (item) {
        var isSpecialNode = item.get('isSpecialNode'),
            isPhantom = item.phantom,
            isDirty = item.dirty,
            isModified;

        isModified = !isSpecialNode &&
                     ((isPhantom !== true && isDirty === true) || isPhantom === true);

        return isModified;
    },

    onClassifierSaveFailure: function () {
        var view = this.getView();

        view.fireComponentEvent('classifierstatuschanged', Unidata.StatusConstant.READY);
        Unidata.showError(view.classifierSaveFailureText);
    },

    //TODO: Ivan Marshalkin: move to util class
    onClassifierTreeHeaderClick: function (event) {
        var view = this.classifierTree,
            header = view.getHeader(),
            headerEl,
            titleEl,
            targetEl;

        if (header && header.rendered) {
            headerEl = header.getEl();
            titleEl  = header.titleCmp.getEl();
            targetEl = Ext.get(event.target);

            // отменяем событие если кликнули не по дом элементу, например по задизабленому tools
            if (Ext.Array.contains([headerEl.component, titleEl.component], targetEl.component) &&
                view.collapsible &&
                view.titleCollapse) {

                view.toggleCollapse();
            }
        }
    },

    createIsNodeAttrsColumnTpl: function () {
        var tpl,
            tplText;

        tplText = '<tpl if="ownNodeAttrs"><i class="fa fa-check-circle un-classifier-tree-has-own-attributes" title="' + Unidata.i18n.t('classifier>havePersonalAttrs') + '"></i></tpl>';  // jscs:ignore maximumLineLength

        tpl = Ext.create('Ext.XTemplate', tplText);

        return tpl;
    },

    onClassifierNodeLoadFailure: function () {
        var view = this.getView();

        Unidata.showError(view.classifierNodeLoadFailureText);
    },

    onClassifierNodeReady: function () {
        this.classifierTree.focusOnSelection();
    },

    onCodePatternRender: function (textfield) {
        textfield.tip = Ext.create('Ext.tip.ToolTip', {
            dismissDelay: 0,
            target: textfield.getEl(),
            html: this.getView().codePatternTooltipText
        });
    },

    /**
     * Обновляем имя рутового узла при изменении отображаемого имени классификатора
     *
     * @param input
     * @param value
     */
    onClassifierDisplayNameChange: function (input, value) {
        var rootNode = this.classifierTree.getRootNode();

        if (rootNode) {
            rootNode.set('name', value);
        }
    },

    resetClassifierNodeErrors: function (classifierNode) {
        var classifierNodeContainer = this.classifierNodeContainer;

        // 1. reset errors in a tree
        this.resetClassifierTreeErrors(classifierNode);

        // 2. reset errors in attributes panel
        classifierNodeContainer.resetErrors(classifierNode);
    },

    highlightClassifierNodeErrors: function (classifierNode) {
        var classifierNodeContainer = this.classifierNodeContainer;

        // 1. highlight errors in a tree
        this.highlightClassifierTreeErrors(classifierNode);

        // 2. highlight errors in attributes panel
        classifierNodeContainer.highlightErrors(classifierNode);
    },

    highlightClassifierTreeErrors: function () {
        //TODO: implement me
    },

    resetClassifierTreeErrors: function () {
        //TODO: implement me
    },

    cancelCreateClassifierNode: function (node) {
        var modified = node.modified,
            isModified = false;

        if (modified) {
            delete modified.parentId;
            isModified = Ext.Object.getKeys(node.modified).length > 0;
        }

        if (node.phantom && !isModified) {
            this.deleteClassifierNode(node);
        }
    },

    onItemKeyUp: function (self, node, elem, index, e) {
        var key = e.getKey();

        this.handleKey(key, node, elem, e.ctrlKey, e.altKey);
    },

    handleKey: function (key, node, elem, ctrlKey, altKey) {
        var evt = Ext.event.Event;

        switch (key) {
            // экспериментальный функционал пока скрыт
            //case evt.ENTER:
            //    this.beginEditClassifierNode(node, 3);
            //    break;
            case evt.NUM_PLUS:
                this.createClassifierNode(node);
                break;
            case evt.INSERT:
                this.createClassifierNode(node.parentNode);
                break;
            case evt.DELETE:
                this.promptDeleteClassifierNode(elem, node);
                break;
            case evt.ESC:
                this.cancelCreateClassifierNode(node);
                break;
            case evt.S:
                if (altKey && ctrlKey) {
                    this.saveClassifierNode(node);
                }
                break;
        }
    },

    selectAndbeginEditClassifierNode: function (classifierNode, column) {
        var selModel = this.classifierTree.getSelectionModel();

        this.classifierTree.on('select', this.beginEditClassifierNode.bind(this, classifierNode, column),
                                                                                                this, {single: true});
        selModel.doSelect(classifierNode);
    },

    beginEditClassifierNode: function (classifierNode, column) {
        var plugin = this.classifierTree.getPlugin('cellediting'),
            view = this.getView(),
            savingMode = view.getClassifierTreeSaving();

        if (!savingMode) {
            // небольшой костыль для синхронизации событий
            // иначе editbox появляется слишком рано и не там где надо
            setTimeout(function () {
                plugin.startEdit(classifierNode, column);
            }, 50);
        }
    },

    /**
     * Коллбэк, вызывается при рендеринге дерева столбца экшенов
     *
     * @param view
     * @param rowIdx
     * @param colIdx
     * @param item
     * @param {Unidata.model.entity.Catalog} record
     * @returns {Boolean}
     */
    deleteActionIsDisabled: function (tableView, rowIdx, colIdx, item, node) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            classifier = view.getClassifier(),
            disabled;

        disabled = viewModel.isDeleteActionDisabled(classifier, node);

        return disabled;
    },

    addActionIsDisabled: function (tableView, rowIdx, colIdx, item, node) {
        var viewModel = this.getViewModel(),
            view = this.getView(),
            classifier = view.getClassifier(),
            validAndNotDirty,
            disabled;

        validAndNotDirty = viewModel.isClassifierNodeValidAndNotDirty(node);
        disabled = viewModel.isAddActionDisabled(classifier, validAndNotDirty);

        return disabled;
    }
});
