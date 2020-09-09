/**
 * @author Aleksandr Bavin
 * @date 2016-08-26
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.settings.modifyrecord.ClassifierItemController', {
    extend: 'Unidata.view.steward.dataclassifier.item.ClassifierItemController',

    alias: 'controller.classifieritempanel',

    /**
     * Инициализируем dataRecord классификатор, при добавлении айтема
     */
    onClassifierItemAdded: function () {
        var view = this.getView(),
            dataRecord = view.getDataRecord(),
            classifier = view.getClassifier(),
            classifierName = null;

        if (classifier) {
            classifierName = classifier.get('name');
        }

        if (!view.getClassifierNodeId()) {
            dataRecord.classifiers().add({
                classifierName: classifierName,
                classifierNodeId: null
            });
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
            index = dataRecordClassifiers.findExact('classifierNodeId', deselectedClassifierNodeId);

        if (index !== -1) {
            if (selectedNodes.length) {
                dataRecordClassifiers.removeAt(index);
            } else {
                dataRecordClassifiers.getAt(index).set('classifierNodeId', null);
            }
        }

        dataRecordClassifiers.dirty = true;
    }

});
