/**
 * Контейнер узла классификатора (контроллер)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.item.nodecontainer.ClassifierNodeContainerController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifier.item.nodecontainer',

    classifierNodePanel: null,

    createClassifierNodePanel: function (customCfg) {
        var classifierItem,
            view = this.getView(),
            classifier = view.getClassifier(),
            cfg;

        cfg = {
            classifier: classifier
        };

        //customCfg = customCfg || {};

        Ext.apply(cfg, customCfg);

        classifierItem = Ext.create('Unidata.view.classifier.item.node.ClassifierNode', cfg);

        return classifierItem;
    },

    setClassifierNode: function (classifierNode) {
        var classifierNodePanel,
            view = this.getView();

        if (!classifierNode) {
            view.removeAll();
        } else {
            Unidata.util.DataRecord.bindManyToOneAssociationListeners(classifierNode);
            classifierNodePanel = this.createClassifierNodePanel();
            this.setClassifierNodePanel(classifierNodePanel);
            classifierNodePanel.setClassifierNode(classifierNode);
        }
        view.fireComponentEvent('classifiernodeready');
    },

    setClassifierNodePanel: function (classifierNodePanel) {
        var view = this.getView();

        view.removeAll();
        view.add(classifierNodePanel);
        this.classifierNodePanel = classifierNodePanel;
    },

    // TODO: try to extract methods loadAndSetClassifierNode, onGetClassierNodeFulfilled, onGetClassifierNodeRejected to ClassifierTree (reuse in ClassifierNodePicker)
    loadAndSetClassifierNode: function (classifierNode) {
        var view = this.getView(),
            classifier = view.getClassifier(),
            promise;

        if (!classifier || classifierNode.phantom) {
            this.setClassifierNode(classifierNode);

            return;
        }

        view.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getClassifierNode(classifierNode, 'META');
        promise
            .then(this.onGetClassierNodeFulfilled.bind(this),
                this.onGetClassifierNodeRejected.bind(this))
            .done();
    },

    /**
     * Загрузить узел классификатора с бекенда
     * @param classifierNode {Unidata.model.classifier.ClassifierNode}
     */
    loadClassifierNode: function (classifierNode) {
        var view = this.getView(),
            promise,
            me = this;

        view.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getClassifierNode(classifierNode, 'META');
        promise
            .then(function (classifierNode) {
                    var view = me.getView();

                    view.setStatus(Unidata.StatusConstant.READY);

                    classifierNode.applyAttributes(classifierNode.raw);
                },
                this.onGetClassifierNodeRejected.bind(this))
            .done();
    },

    onGetClassierNodeFulfilled: function (classifierNode) {
        var view = this.getView();

        view.setStatus(Unidata.StatusConstant.READY);

        classifierNode.applyAttributes(classifierNode.raw);
        this.setClassifierNode(classifierNode);
    },

    onGetClassifierNodeRejected: function () {
        var view = this.getView();

        view.setStatus(Unidata.StatusConstant.NONE);
        Unidata.showError(view.loadClassifierNodeFailureText);
    },

    highlightErrors: function (classifierNode) {
        var classifierNodePanel = this.classifierNodePanel;

        classifierNodePanel.highlightErrors(classifierNode);
    },

    resetErrors: function (classifierNode) {
        var classifierNodePanel = this.classifierNodePanel;

        if (classifierNodePanel) {
            classifierNodePanel.resetErrors(classifierNode);
        }
    },

    updateClassifier: function (classifier) {
        if (!this.classifierNodePanel) {
            return;
        }

        this.classifierNodePanel.setClassifier(classifier);
    }
});
