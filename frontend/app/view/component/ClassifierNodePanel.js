/**
 * Панель выбор классификатора + узлы классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-09
 */
Ext.define('Unidata.view.component.ClassifierNodePanel', {

    extend: 'Ext.panel.Panel',

    alias: 'widget.un.classifiernodepanel',

    requires: [
        //TODO: proxies
        //TODO: stores
        'Unidata.view.component.ClassifierTree'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-classifier-node-panel',

    config: {
        classifier: null,
        classifierNode: null,
        classifiers: null,
        classifierTreeUi: null,
        classifierName: null,
        classifierComboBoxHidden: false
    },

    viewModel: {
        data: {
            classifierComboBoxHidden: false
        }
    },

    classifierTree: null,
    classifierComboBox: null,
    referenceHolder: true,

    viewModelAccessors: ['classifierComboBoxHidden'],

    loadClassifierNodeFailureText: Unidata.i18n.t('glossary:loadClassifierNodeFailure'),

    items: [
        {
            xtype: 'un.classifiercombobox',
            reference: 'classifierComboBox',
            scrollable: false,
            height: 30,
            bind: {
                hidden: '{classifierComboBoxHidden}'
            }
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.createClassifierTree();
        this.initReferences();
        this.initListeners();
    },

    createClassifierTree: function () {
        var tree,
            classifierTreeUi;

        classifierTreeUi = this.getClassifierTreeUi();

        tree = this.buildClassifierTree({
            ui: classifierTreeUi
        });

        this.add(tree);
    },

    buildClassifierTree: function (customCfg) {
        var cfg,
            cmp;

        customCfg = customCfg || {};

        cfg = {
            xtype: 'un.classifiertree',
            classifierNodeView: 'DATA',
            hidden: true,
            reference: 'classifierTree',
            cls: 'un-classifier-tree',
            //listeners: {
            //    selectionchange: this.onClassifierTreeSelectionChange,
            //    scope: this
            //},
            bind: {
                hidden: '{!classifierComboBox.selection}'
            },
            minHeight: 100,
            flex: 1,
            //maxHeight: 500,
            // так сделано в Ext.form.field.Date
            // Key events are listened from the input field which is never blurred
            focusable: false,
            animate: false
        };

        Ext.apply(cfg, customCfg);

        cmp = Ext.create(cfg);

        return cmp;
    },

    initReferences: function () {
        this.classifierTree = this.lookupReference('classifierTree');
        this.classifierComboBox = this.lookupReference('classifierComboBox');
    },

    initListeners: function () {
        var classifierComboBox = this.classifierComboBox,
            classifierTree = this.classifierTree;

        classifierComboBox.on('change', this.onClassifierComboBoxChange, this);
        classifierTree.on('selectionchange', this.onClassifierTreeSelectionChange, this);
    },

    onClassifierComboBoxChange: function (combobox) {
        var classifier = combobox.selection,
            classifierTree = this.classifierTree,
            codePattern = null;

        if (classifier) {
            codePattern = classifier.get('codePattern');
        }

        classifierTree.setCodePattern(codePattern);
        this.setClassifier(classifier);
        classifierTree.deselectAll();
        this.refreshClassifierTree();
    },

    refreshClassifierTree: function () {
        var classifierTree = this.classifierTree,
            classifier = this.getClassifier();

        if (!classifierTree) {
            return;
        }

        classifierTree.setClassifier(classifier);

        if (classifier) {
            classifierTree.displayRootNode();
        }
    },

    filterClassifierComboBox: function (classifiers) {
        var store,
            selectedClassifier;

        if (!this.classifierComboBox) {
            return;
        }

        store = this.classifierComboBox.getStore();

        store.clearFilter();

        if (!classifiers || !Ext.isArray(classifiers)) {
            return;
        }

        function doFilter (classifiers, classifier) {
            var classifierName = classifier.get('name'),
                contains;

            contains = Ext.Array.contains(classifiers, classifierName);

            return contains;
        }

        store.filterBy(doFilter.bind(this, classifiers));

        if (selectedClassifier) {
            if (!Ext.Array.contains(classifiers, selectedClassifier.get('name'))) {
                this.reset();
            }
        }
    },

    onClassifierTreeSelectionChange: function (tree, selected) {
        var node = null,
            classifier = this.getClassifier(),
            promise;

        if (selected.length === 1) {
            node = selected[0];
        }

        if (!node) {
            this.fireComponentEvent('nodeselect', null, classifier);

            return;
        }

        promise = this.loadClassifierNode(node);

        promise
            .then(this.onGetClassierNodeFulfilled.bind(this),
                this.onGetClassifierNodeRejected.bind(this))
            .done();
    },

    // TODO: try to extract methods loadAndSetClassifierNode, onGetClassierNodeFulfilled, onGetClassifierNodeRejected to ClassifierTree
    loadClassifierNode: function (classifierNode) {
        var promise,
            view = this.classifierTree;

        // TODO: implement statuses
        view.setStatus(Unidata.StatusConstant.LOADING);
        promise = Unidata.util.api.Classifier.getClassifierNode(classifierNode, 'DATA');

        return promise;
    },

    onGetClassierNodeFulfilled: function (classifierNode) {
        var nodeAttrs = classifierNode.nodeAttrs(),
            inheritedNodeAttrs = classifierNode.inheritedNodeAttrs(),
            view = this.classifierTree,
            classifier;

        classifier = this.getSelectedClassifier();

        // TODO: implement statuses
        view.setStatus(Unidata.StatusConstant.READY);
        nodeAttrs.removeAll();
        inheritedNodeAttrs.removeAll();
        nodeAttrs.add(classifierNode.raw.nodeAttrs);
        inheritedNodeAttrs.add(classifierNode.raw.inheritedNodeAttrs);

        // TODO: move to picker

        this.setClassifierNode(classifierNode);
    },

    onGetClassifierNodeRejected: function () {
        var view = this.classifierTree;

        // TODO: implement statuses
        view.setStatus(Unidata.StatusConstant.NONE);
        Unidata.showError(this.loadClassifierNodeFailureText);
    },

    updateClassifierName: function (classifierName) {
        var classifierComboBox = this.lookupReference('classifierComboBox'),
            picker,
            store;

        if (!classifierComboBox) {
            return;
        }

        picker = classifierComboBox.getPicker();
        store = picker.getStore();

        if (store.isLoaded()) {
            this.selectClassifierByName(classifierName);
        } else {
            store.on('load', this.onClassifierStoreLoad.bind(this, classifierName), this);
        }
    },

    /**
     * @private
     * @param classifierName
     */
    selectClassifierByName: function (classifierName) {
        var classifierComboBox = this.classifierComboBox,
            picker = classifierComboBox.getPicker(),
            store = picker.getStore(),
            classifier;

        classifier = store.findRecord('name', classifierName, 0, false, false, true);

        if (classifier) {
            picker.setSelection(classifier);
        }
    },

    onClassifierStoreLoad: function (classifierName) {
        this.selectClassifierByName(classifierName);
    },

    updateClassifier: function (classifier) {
        this.fireComponentEvent('classifierchange', classifier);
    },

    updateClassifierNode: function (classifierNode) {
        var classifier = this.getClassifier();

        this.fireComponentEvent('nodeselect', classifierNode, classifier);
    },

    getSelectedClassifier: function () {
        var classifierComboBox = this.classifierComboBox,
            selection;

        if (!classifierComboBox) {
            return;
        }

        selection = classifierComboBox.selection;

        return selection;
    },

    getSelectedClassifierNode: function () {
        var classifierTree = this.classifierTree,
            classifierNode = null,
            selected;

        if (!classifierTree) {
            return classifierNodel;
        }

        selected = classifierTree.getSelection();

        if (selected.length === 1) {
            classifierNode = selected[0];
        }

        return classifierNode;
    },

    reset: function () {
        var classifierComboBox = this.classifierComboBox,
            classifiers = this.getClassifiers(),
            classifier,
            classifierName,
            contains = false,
            isClassifiers;

        if (!classifierComboBox) {
            return;
        }

        isClassifiers = Ext.isArray(classifiers);
        classifier = this.getClassifier();

        if (classifier) {
            classifierName = classifier.get('name');
        }

        if (isClassifiers && classifierName) {
            contains = Ext.Array.contains(classifiers, classifierName);
        }

        this.setClassifierNode(null);

        // если среди списка доступных классификаторов есть текущий то ничего сбрасывать не надо
        if (!contains) {
            this.classifierTree.deselectAll();
            this.classifierTree.getView().focusNode(null);

            // не нужна реакция на change, т.к. дерево и так скрываем в связи со сбросом classifierComboBox.selection
            classifierComboBox.suspendEvent('change');
            classifierComboBox.setValue(null);
            classifierComboBox.resumeEvent('change');

            this.fireComponentEvent('nodereset');
        }
    }
});
