/**
 * Picker для выбора классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-09
 */
Ext.define('Unidata.view.component.ClassifierNodePicker', {

    extend: 'Ext.form.field.Picker',

    alias: 'widget.un.classifiernodepicker',

    mixins: [
        'Unidata.mixin.Tooltipable'
    ],

    requires: [
        //TODO: proxies
        //TODO: stores
        'Unidata.view.component.ClassifierTree'
    ],

    config: {
        classifierName: null,
        classifierNode: null,
        classifiers: null
    },

    matchFieldWidth: false,
    editable: false,

    emptyText: Unidata.i18n.t('classifier>selectNode'),

    triggers: {
        reset: {
            cls: 'x-form-clear-trigger',
            // TODO: extract to method onResetTrigger
            handler: function () {
                var classifierNodePanel = this.getPicker();

                if (!classifierNodePanel) {
                    return;
                }

                // если нечего сбрасывать
                if (!this.getClassifierNode()) {
                    return;
                }

                this.collapse();
                //this.setClassifiers(null);
                classifierNodePanel.reset();
                this.fireComponentEvent('classifierreset');
            }
        }
    },

    loadClassifierNodeFailureText: Unidata.i18n.t('glossary:loadClassifierNodeFailure'),

    // храним здесь созданное дерево, чтобы можно было его получить без вызова getPicker
    classifierTree: null,
    classifierComboBox: null,

    initComponent: function () {
        this.callParent(arguments);
        this.initListeners();
    },

    onCollapseIf: function (e) {
        var picker = this.getPicker();

        if (picker && picker.rendered && e.within(picker.el)) {
            if (e.type !== 'mousewheel') {
                e.preventDefault();
                e.stopPropagation();
            }
        } else {
            this.collapse();
        }
    },

    initListeners: function () {
        this.on('collapseIf', this.onCollapseIf, this);
        this.on('change', this.onChange, this);

        this.addComponentListener('nodeselect', this.onClassifierNodeSelect, this);
        this.addComponentListener('nodereset', this.onClassifierNodeReset, this);
    },

    /**
     * Обработчик потери фокуса компонентом
     * @param e
     */
    onFocusLeave: function (e) {
        var fromComponent = e.fromComponent,
            isCollapseEnabled = true,
            maskTarget;

        if (fromComponent instanceof Ext.tree.View) {
            if (fromComponent.grid instanceof Unidata.view.component.ClassifierTree) {
                isCollapseEnabled = false;
            }
        } else if (fromComponent instanceof Ext.toolbar.Toolbar) {
            isCollapseEnabled = false;
        } else if (fromComponent instanceof Ext.LoadMask) {
            maskTarget = fromComponent.getMaskTarget();

            if (maskTarget &&
                maskTarget.component &&
                maskTarget.component instanceof Unidata.view.component.ClassifierTree) {
                isCollapseEnabled = false;
            }
        }

        if (isCollapseEnabled) {
            this.collapse();
            this.callParent(arguments);
        }
    },

    loadByNodeId: function (classifierName, nodeId) {
        var picker = this.getPicker(),
            promise;

        promise = picker.loadClassifierNodeById(classifierName, nodeId);

        return promise;
    },

    createPicker: function () {
        var classifiers = this.getClassifiers(),
            classifierPanel,
            classifierTree;

        classifierPanel = this.createClassifierNodePanel();
        classifierTree = classifierPanel.classifierTree;

        classifierTree.on('resize', function () {
            this.alignPicker();
        }, this);

        if (classifiers) {
            classifierPanel.filterClassifierComboBox(classifiers);
        }

        return classifierPanel;
    },

    onClassifierNodeSelect: function (classifierNode) {
        var value,
            classifierPanel,
            classifier;

        classifierPanel = this.getPicker();

        if (!classifierPanel) {
            return;
        }

        classifier = classifierPanel.getSelectedClassifier();

        this.setClassifierNode(classifierNode);
        value = this.buildClassifierNodeValue(classifier, classifierNode);
        this.setValue(value);
        this.collapse();
    },

    onClassifierNodeReset: function () {
        this.setValue(null);
    },

    createClassifierNodePanel: function (customCfg) {
        var panel,
            cfg;

        cfg = {
            xtype: 'un.classifiernodepanel',
            autoRender: true,
            floating: true,
            hidden: true,
            maxHeight: 500,
            width: 500
        };

        Ext.apply(cfg, customCfg);

        panel = Ext.create(cfg);

        return panel;
    },

    onChange: function () {
        this.refreshTooltip();
    },

    refreshTooltip: function () {
        var ClassifierUtil = Unidata.util.Classifier,
            tooltip = '',
            classifier,
            classifierNode,
            classifierNodePanel = this.getPicker();

        if (!classifierNodePanel) {
            return;
        }

        classifier = classifierNodePanel.getSelectedClassifier();
        classifierNode = classifierNodePanel.getClassifierNode();

        if (classifier && classifierNode) {
            tooltip = ClassifierUtil.buildClassifierNodeTitle(classifier, classifierNode);
        }
        this.setTooltipText(tooltip);
    },

    updateClassifiers: function (classifiers) {
        var classifierNodePanel = this.getPicker();

        if (!classifierNodePanel) {
            return;
        }

        classifierNodePanel.setClassifiers(classifiers);
        classifierNodePanel.filterClassifierComboBox(classifiers);
    },

    buildClassifierNodeValue: function (classifier, classifierNode) {
        var tpl = '{0}{1}',
            value,
            tooltip,
            classifierNodeDisplayName,
            classifierDisplayName,
            classifierBranchPrefix = '',
            parentNode;

        if (!classifier || !classifierNode) {
            return '';
        }

        parentNode = classifierNode.parentNode;

        if (parentNode && !parentNode.isRoot()) {
            classifierBranchPrefix = '../';
        }
        classifierNodeDisplayName = classifierNode.get('name');
        classifierDisplayName = classifier.get('displayName');

        value = Ext.String.format(tpl, classifierBranchPrefix, classifierNodeDisplayName);
        tooltip = Ext.String.format(tpl, classifierBranchPrefix, classifierNodeDisplayName, classifierDisplayName);
        // TODO: implement tooltip

        return value;
    }
});
