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

    updateReadOnly: function () {
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

    onClassifierItemExpand: function () {
        var view = this.getView();

        if (view.classifierTree) {
            view.classifierTree.autoSizeColumns();
        }
    }
});
