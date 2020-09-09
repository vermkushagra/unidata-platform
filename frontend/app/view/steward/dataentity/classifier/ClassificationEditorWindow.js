/**
 * Окно выбора узла классификации
 * @author Sergey Shishigin
 * @date 2018-05-31
 */
Ext.define('Unidata.view.steward.dataentity.classifier.ClassificationEditorWindow', {
    extend: 'Ext.window.Window',

    alias: 'widget.steward.dataentity.classifier.classificationeditorwindow',

    requires: ['Unidata.view.steward.dataclassifier.item.ClassifierItem'],

    mixins: [
        'Unidata.mixin.StatusManageable'
    ],

    config: {
        classifierTreeConfig: null, // кастомный конфиг для дерева
        metaRecord: null,
        dataRecord: null,
        classifier: null,
        metaClassifierNode: null,
        dataClassifierNode: null
    },

    title: 'Выбор узла классификации',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    referenceHolder: true,

    classifierItem: null,
    buttonToolbar: null,
    saveButton: null,
    cancelButton: null,

    buildDockedItems: function () {
        var dockedItems;

        dockedItems = {
            xtype: 'toolbar',
            reference: 'buttonToolbar',
            ui: 'footer',
            dock: 'bottom',
            layout: {
                pack: 'center'
            },
            items: [
                {
                    xtype: 'button',
                    reference: 'saveButton',
                    text: Unidata.i18n.t('common:select'),
                    disabled: true,
                    listeners: {
                        click: this.onSaveButtonClick.bind(this)
                    }
                },
                {
                    xtype: 'button',
                    reference: 'cancelButton',
                    color: 'transparent',
                    text: Unidata.i18n.t('common:cancel'),
                    listeners: {
                        click: this.onCancelButtonClick.bind(this)
                    }
                }
            ]
        };

        return dockedItems;
    },

    initComponent: function () {
        this.dockedItems = this.buildDockedItems();
        this.callParent(arguments);
        this.initReferences();
    },

    initReferences: function () {
        this.classifierItem = this.lookupReference('classifierItem');
        this.buttonToolbar = this.lookupReference('buttonToolbar');
        this.saveButton = this.lookupReference('saveButton');
        this.cancelButton = this.lookupReference('cancelButton');
    },

    onClassifierTreeSelectionChange: function (classifierTree, classifierNode) {
        this.saveButton.setDisabled(!Boolean(classifierNode));
    },

    initItems: function () {
        var items,
            classifierTreeConfig,
            metaRecord,
            dataRecord,
            classifier,
            metaClassifierNode,
            dataClassifierNode,
            classifierNodeId,
            classifierName;

        classifierTreeConfig = this.getClassifierTreeConfig();
        metaRecord         = this.getMetaRecord();
        dataRecord         = this.getDataRecord();
        classifier         = this.getClassifier();
        metaClassifierNode = this.getMetaClassifierNode();
        dataClassifierNode = this.getDataClassifierNode();
        classifierName = classifier.get('name');
        classifierNodeId = metaClassifierNode ? metaClassifierNode.getId() : null;

        this.callParent(arguments);

        items = [
            {
                xtype: 'dataclassifier.classifieritempanel',
                qaId: classifierName,
                collapsed: false,
                collapsible: false,
                width: '100%',
                reference: 'classifierItem',
                flex: 1,
                header: false,
                // vars
                classifierTreeConfig: classifierTreeConfig,
                metaRecord: metaRecord,
                dataRecord: dataRecord,
                classifier: classifier,
                classifierNodeId: classifierNodeId,
                metaClassifierNode: metaClassifierNode,
                dataClassifierNode: dataClassifierNode,
                listeners: {
                    classifiertreeselectionchange: this.onClassifierTreeSelectionChange.bind(this)
                }
            }
        ];

        this.add(items);
        this.initReferences();
    },

    onSaveButtonClick: function () {
        var me = this,
            classifierItem = this.classifierItem,
            classifierTree = classifierItem.classifierTree,
            selected;

        this.setStatus(Unidata.StatusConstant.LOADING);
        selected = classifierTree.getSelection();
        Unidata.util.api.Classifier.getClassifierNode(selected[0], 'DATA')
            .then(function (classifierNode) {
                    me.setStatus(Unidata.StatusConstant.READY);
                    me.fireEvent('okbtnclick', this, [classifierNode]);
                    me.close();
                },
            function () {
                Unidata.showError(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:classifierNode')}));
                me.setStatus(Unidata.StatusConstant.NONE);
            }).done();
    },

    onCancelButtonClick: function () {
        this.fireEvent('cancelbtnclick', this);
        this.close();
    }
});

