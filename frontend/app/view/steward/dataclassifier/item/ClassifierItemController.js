/**
 * @author Ivan Marshalkin
 * @date 2016-08-08
 */

Ext.define('Unidata.view.steward.dataclassifier.item.ClassifierItemController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.dataclassifier.classifieritempanel',

    applyReadOnly: function (readOnly) {
        var view           = this.getView(),
            classifier = view.getClassifier(),
            classifierName = null;

        if (classifier) {
            classifierName = classifier.get('name');
        }

        // если у пользователя нет права на классификатор то для него readOnly
        if (!Unidata.Config.userHasRight(classifierName, 'read')) {
            readOnly = true;
        }

        return readOnly;
    },

    updateReadOnly: function (readOnly) {
        var view = this.getView();

        if (view.classifierTree) {
            view.classifierTree.hide();
        }
    },

    updateClassifier: function (classifier) {
        var view = this.getView();

        if (view.classifierTree) {
            view.classifierTree.setClassifier(classifier);
        }
    },

    updateClassifierNodeId: function (classifierNodeId) {
        var view = this.getView();

        if (view.classifierTree) {
            view.classifierTree.setClassifierNodeId(classifierNodeId);
        }
    },

    refreshTitle: function () {
        var view               = this.getView(),
            classifier         = view.getClassifier(),
            classifierNode     = view.getClassifierNode(),
            classifierDisplayName     = classifier.get('displayName'),
            classifierNodeName = '',
            title;

        if (classifierNode) {
            classifierNodeName = classifierNode.get('text');
        }

        title = Unidata.view.steward.dataclassifier.item.ClassifierItem
                                            .buildClassifierItemPanelTitle(classifierDisplayName, classifierNodeName);

        view.setTitle(title);
    },

    onNodeSelectionChange: function (classifierTree, selected, previousSelected, selectedNodes, deselectedNodes) {
        var view                       = this.getView(),
            classifier                 = view.getClassifier(),
            dataRecord                 = view.getDataRecord(),
            ClassifierDataRecord       = Unidata.util.ClassifierDataRecord,
            selectedClassifierNode     = null,
            selectedClassifierNodeId   = null,
            classifierName,
            firstItem,
            storeAddedRecords;

        // обрабатываем выбранные ноды
        if (Ext.isArray(selectedNodes) && selectedNodes.length) {
            selectedClassifierNode = selectedNodes[0];
            selectedClassifierNodeId = selectedClassifierNode.get('id');

            classifierName = selectedClassifierNode.get('classifierName');

            firstItem = ClassifierDataRecord.getFirstClassifierNodeByClassifierName(dataRecord, classifierName);

            if (firstItem) {
                ClassifierDataRecord.removeClassifierNodes(dataRecord, firstItem);
            }

            storeAddedRecords = dataRecord.classifiers().add({
                classifierName: classifierName,
                classifierNodeId: selectedClassifierNodeId,
                etalonId: firstItem ? firstItem.get('etalonId') : null
            });

            dataRecord.classifiers().dirty = true;

            Ext.Array.each(storeAddedRecords, function (record) {
                record.dirty = true;
            });
        }

        // обрабатываем сброшеные ноды
        if (Ext.isArray(deselectedNodes) && deselectedNodes.length) {
            this.updateDeselectedClassifier(classifierTree, selected, previousSelected, selectedNodes, deselectedNodes);
        }

        // обновляем инфу текущей панельки
        view.setClassifierNode(selectedClassifierNode);
        view.setClassifierNodeId(selectedClassifierNodeId);

        // this.refreshTitle();

        view.fireComponentEvent('datarecordclassifiernodechange', classifier, selectedClassifierNode);
    },

    /**
     * обрабатываем сброшеные ноды
     */
    updateDeselectedClassifier: function (classifierTree, selected, previousSelected, selectedNodes, deselectedNodes) {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            dataRecordClassifiers = dataRecord.classifiers(),
            deselectedClassifierNode = deselectedNodes[0],
            deselectedClassifierNodeId = deselectedClassifierNode.get('id'),
            index = dataRecordClassifiers.findExact('classifierNodeId', deselectedClassifierNodeId);

        if (index !== -1) {
            dataRecordClassifiers.getAt(index).set('classifierNodeId', null);
            dataRecordClassifiers.getAt(index).simpleAttributes().removeAll();
        }

        dataRecordClassifiers.dirty = true;
    },

    onClassifierItemExpand: function () {
        var view = this.getView();

        if (view.classifierTree) {
            view.classifierTree.autoSizeColumns();
        }
    }
});
