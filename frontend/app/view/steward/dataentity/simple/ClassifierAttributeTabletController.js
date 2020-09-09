Ext.define('Unidata.view.steward.dataentity.simple.ClassifierAttributeTabletController', {
    // extend: 'Ext.container.Container',
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataentity.simple.classifierattributetablet',

    buildClassifierNodeEditorWindow: function (customCfg, okCallback, cancelCallback) {
        var window,
            cfg;

        cfg = {
            width: 880,
            height: 620,
            modal: true
        };

        cfg = Ext.apply(cfg, customCfg);

        window = Ext.create('Unidata.view.steward.dataentity.classifier.ClassificationEditorWindow', cfg);

        if (okCallback) {
            if (Ext.isFunction(okCallback)) {
                window.on('okbtnclick', okCallback);
            } else if (Ext.isObject(callback)) {
                window.on('okbtnclick', okCallback.fn, okCallback.scope);
            }
        }

        if (cancelCallback) {
            if (Ext.isFunction(cancelCallback)) {
                window.on('cancelbtnclick', cancelCallback);
            } else if (Ext.isObject(cancelCallback)) {
                window.on('cancelbtnclick', cancelCallback.fn, cancelCallback.scope);
            }
        }

        return window;
    },

    onSelectClassifierNodeButtonClick: function (btn, e) {
        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        this.showClassifierNodeEditorWindow(this.onShowClassifierNodeEditorWindowOkButtonClick.bind(this));
    },

    showClassifierNodeEditorWindow: function (okCallback, cancelCallback) {
        var view = this.getView(),
            cfg,
            classifierTreeConfig,
            metaRecord,
            dataRecord,
            classifier,
            metaClassifierNode,
            dataClassifierNode,
            window;

        classifierTreeConfig = view.getClassifierTreeConfig();
        metaRecord         = view.getMetaRecord();
        dataRecord         = view.getDataRecord();
        classifier         = view.getClassifier();
        metaClassifierNode = view.getMetaClassifierNode();
        dataClassifierNode = view.getDataClassifierNode();

        cfg = {
            classifierTreeConfig: classifierTreeConfig,
            metaRecord: metaRecord,
            dataRecord: dataRecord,
            classifier: classifier,
            metaClassifierNode: metaClassifierNode,
            dataClassifierNode: dataClassifierNode
        };

        window = this.buildClassifierNodeEditorWindow(cfg, okCallback, cancelCallback);
        window.show();
    },

    onShowClassifierNodeEditorWindowOkButtonClick: function (self, selected) {
        var view = this.getView(),
            previousSelectedClassifierNode = view.getPreviousClassifierNodeSelected();

        this.onNodeSelectionChange(selected, previousSelectedClassifierNode);
        this.useSelectedClassifierNode(selected);
    },

    useSelectedClassifierNode: function (selected) {
        var view = this.getView(),
            dataClassifierNode = view.getDataClassifierNode(),
            classifier = view.getClassifier();

        view.setPreviousClassifierNodeSelected(selected);

        if (!dataClassifierNode) {
            dataClassifierNode = this.createClassifierNode(classifier.get('name'), selected[0].get('id'));
            view.setDataClassifierNode(dataClassifierNode);
        }
        view.buildAndRenderAttributeContainers();
    },

    calcSelectedNodes: function (selected, previousSelected) {
        return Ext.Array.difference(selected, previousSelected);
    },

    calcDeselectedNodes: function (selected, previousSelected) {
        return Ext.Array.difference(previousSelected, selected);
    },

    /**
     *
     * @param classifierTree
     * @param {Unidata.model.classifier.ClassifierNode[]} selected
     * @param {Unidata.model.classifier.ClassifierNode[]} previousSelected
     * @param selectedNodes
     * @param deselectedNodes
     */
    onNodeSelectionChange: function (selected, previousSelected) {
        var view = this.getView(),
            selectedClassifierNode = null,
            selectedNodes;

        selectedNodes = this.calcSelectedNodes(selected, previousSelected);

        if (Ext.isArray(selectedNodes) && selectedNodes.length) {
            selectedClassifierNode = selectedNodes[0];
        }

        this.syncDataClassifiers(selected, previousSelected);
        view.setMetaClassifierNode(selectedClassifierNode);
        this.useUIUserExit(selected, previousSelected);
    },

    /**
     * Синхронизировать информацию о классификации в дата рекорде
     * @param selected Все выбранные узлы
     * @param previousSelected Все ранее выбранные узлы
     * @param selectedNodes Новые выбранные узлы
     * @param deselectedNodes Новые узлы со сброшенным выбором
     */
    syncDataClassifiers: function (selected, previousSelected) {
        var view                       = this.getView(),
            dataRecord                 = view.getDataRecord(),
            ClassifierDataRecord       = Unidata.util.ClassifierDataRecord,
            selectedClassifierNode     = null,
            selectedClassifierNodeId   = null,
            classifierName,
            found,
            selectedNodes,
            deselectedNodes;

        selectedNodes = this.calcSelectedNodes(selected, previousSelected);
        deselectedNodes = this.calcDeselectedNodes(selected, previousSelected);

        // обрабатываем выбранные ноды
        if (Ext.isArray(selectedNodes) && selectedNodes.length) {
            selectedClassifierNode = selectedNodes[0];
            selectedClassifierNodeId = selectedClassifierNode.get('id');

            classifierName = selectedClassifierNode.get('classifierName');

            found = ClassifierDataRecord.findClassifierNodeByClassifierNameAndNodeId(dataRecord, classifierName, selectedClassifierNodeId);

            if (found) {
                ClassifierDataRecord.removeClassifierNodes(dataRecord, found);
            }

            this.createClassifierNode(classifierName, selectedClassifierNodeId, found ? found.get('etalonId') : null);
        }

        // обрабатываем сброшеные ноды
        if (Ext.isArray(deselectedNodes) && deselectedNodes.length) {
            this.updateDeselectedClassifier(selected, previousSelected, selectedNodes, deselectedNodes);
        }
    },

    createClassifierNode: function (classifierName, classifierNodeId, etalonId) {
        var view                       = this.getView(),
            dataRecord                 = view.getDataRecord(),
            dataClassifierNode;

        dataClassifierNode = Ext.create('Unidata.model.data.ClassifierNode', {
            classifierName: classifierName,
            classifierNodeId: classifierNodeId,
            etalonId: etalonId
        });

        dataClassifierNode.dirty = true;
        dataRecord.classifiers().add(dataClassifierNode);
        dataRecord.classifiers().dirty = true;

        return dataClassifierNode;
    },

    /**
     * Выполнить UI UE
     */
    useUIUserExit: function (selected, previousSelected) {
        var view                       = this.getView(),
            // используем родительский view для отправки ComponentEvent, т.к. данный может быть удалён
            parentView                 = view.up() || view,
            classifier                 = view.getClassifier(),
            deferredNodeChange,
            selectedClassifierNode,
            userExitChangeEventResult,
            selectedNodes;

        selectedNodes = this.calcSelectedNodes(selected, previousSelected);

        if (Ext.isArray(selectedNodes) && selectedNodes.length) {
            selectedClassifierNode = selectedNodes[0];
        }

        deferredNodeChange = new Ext.Deferred();

        // событие обрабатывается в user-exit
        userExitChangeEventResult = view.fireEvent(
            'userexitdatarecordclassifiernodechange',
            view,
            selected,
            previousSelected,
            deferredNodeChange
        );

        if (userExitChangeEventResult === false) {
            // откладываем перезагрузку карточки
            deferredNodeChange.promise.always(function () {
                parentView.fireComponentEvent('datarecordclassifiernodechange', classifier, selectedClassifierNode);
            });
        } else {
            parentView.fireComponentEvent('datarecordclassifiernodechange', classifier, selectedClassifierNode);
        }
    },

    /**
     * обрабатываем сброшеные ноды
     */
    updateDeselectedClassifier: function (selected, previousSelected, selectedNodes, deselectedNodes) {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            dataRecordClassifiers = dataRecord.classifiers(),
            deselectedClassifierNode = deselectedNodes[0],
            deselectedClassifierNodeId = deselectedClassifierNode.get('id'),
            deselectedClassifierName = deselectedClassifierNode.get('classifierName');

        this.doDeclassify(deselectedClassifierName, deselectedClassifierNodeId);

        dataRecordClassifiers.dirty = true;
    },

    doDeclassify: function (classifierName, classifierNodeId) {
        var index,
            view = this.getView(),
            dataRecord = view.getDataRecord(),
            dataRecordClassifiers = dataRecord.classifiers(),
            dataRecordClassifier;

        index = dataRecordClassifiers.findBy(function (classifier) {
            return classifier.get('classifierNodeId') === classifierNodeId &&
                    classifier.get('classifierName') === classifierName;
        });

        if (index !== -1) {
            dataRecordClassifier = dataRecordClassifiers.getAt(index);

            if (dataRecordClassifier.get('etalonId')) {
                dataRecordClassifier.set('classifierNodeId', null);
                dataRecordClassifier.simpleAttributes().removeAll();
            } else {
                dataRecordClassifiers.removeAt(index);
            }
        }
    },

    onRemoveClassifierNodeButtonClick: function (btn, e) {
        var view = this.getView(),
            title = Unidata.i18n.t('admin.metamodel>removeClassifierNode'),
            msg = Unidata.i18n.t('admin.metamodel>confirmRemoveClassifierNode'),
            classifierNode = view.getDataClassifierNode();
        // TODO: move to lng file

        // отменяем реакцию на клик - чтоб не закрывалась панелька
        e.preventDefault();
        e.stopPropagation();

        if (view.fireEvent('beforeremove') != false) {
            this.showPrompt(title, msg, this.removeClassifierNode, this, btn, [classifierNode]);
        }
    },

    removeClassifierNode: function (classifierNode) {
        var view = this.getView();

        if (classifierNode) {
            this.doDeclassify(classifierNode.get('classifierName'), classifierNode.get('classifierNodeId'));
            this.useUIUserExit([], [classifierNode]);
        }
        view.fireEvent('removeclassifiernode', view, classifierNode);
    },

    /**
     * Удаляет данный узел классификатора
     */
    remove: function () {
        var view = this.getView(),
            classifierNode = view.getDataClassifierNode();

        this.removeClassifierNode(classifierNode);
    },

    /**
     * Иницилазирует базовое содержание тултипа
     *
     * @returns {*}
     */
    buildBaseToolTip: function () {
        var tooltip;

        tooltip = '{0}'; // плейсхолдер для value

        return tooltip;
    },

    updateMetaClassifierNode: function (metaClassifierNode) {
        var view = this.getView(),
            qaId = null;

        if (metaClassifierNode) {
            qaId = metaClassifierNode.get('id');
        }

        view.setQaId(qaId);
    }
});
