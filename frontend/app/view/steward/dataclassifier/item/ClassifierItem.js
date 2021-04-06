/**
 * Панель реализующая выбор и отображение группы классификатора для экрана записи
 *
 * @author Ivan Marshalkin
 * @date 2016-08-08
 */

Ext.define('Unidata.view.steward.dataclassifier.item.ClassifierItem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.dataclassifier.item.ClassifierItemController',
        'Unidata.view.steward.dataclassifier.item.ClassifierItemModel'
    ],

    alias: 'widget.dataclassifier.classifieritempanel',

    controller: 'dataclassifier.classifieritempanel',

    viewModel: {
        type: 'dataclassifier.classifieritempanel'
    },

    statics: {
        buildClassifierItemPanelTitle: function (classifierName, classifierNodeName) {
            var title = classifierName;

            if (classifierNodeName) {
                title += ': ' + classifierNodeName;
            }

            return title;
        },

        buildClassifierItemPanelTitleByNode: function (classifierNode) {
            var title = '',
                classifierName,
                text;

            if (classifierNode) {
                classifierName = classifierNode.get('classifierName');
                text           = classifierNode.get('text');
                title = this.buildClassifierItemPanelTitle(classifierName, text);
            }

            return title;
        }
    },

    referenceHolder: true,

    eventBusHolder: true,
    bubbleBusEvents: [
        'datarecordclassifiernodechange'
    ],

    // cls: 'un-dataclassifier-itempanel un-card',
    cls: 'un-dataclassifier-itempanel',
    ui: 'un-card',
    title: Unidata.i18n.t('classifier>selectClassifierNode'),
    animCollapse: false,

    classifierTree: null,          // ссылка на компонент отображающий классификатор

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'applyReadOnly'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'updateClassifierNodeId'
        }
    ],

    config: {
        metaRecord: null,
        dataRecord: null,
        classifier: null,
        classifierNodeId: null,
        classifierNode: null,
        readOnly: null
    },

    collapsible: true,
    titleCollapse: true,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        titlePosition: 1
    },

    listeners: {
        expand: 'onClassifierItemExpand'
    },

    viewModelAccessors: ['readOnly', 'classifierNode', 'classifierNodeId'],

    bind: {
        disabled: '{isDisabled}'
    },

    /**
     * Инициализация компонента
     */
    initComponent: function () {

        this.callParent(arguments);

        this.initClassifierTree();
    },

    /**
     * Инициализирует начальное состояние дерева
     */
    initClassifierTree: function () {
        var readOnly = this.getReadOnly(),
            classifier = this.getClassifier(),
            classifierNodeId = this.getClassifierNodeId();

        this.classifierTree = this.add({
            xtype: 'un.classifiertree',
            reference: 'classifierTree',
            classifierNodeView: 'DATA',
            rootVisible: false,
            hidden: readOnly,
            readOnly: readOnly,
            classifier: classifier,
            classifierNodeId: classifierNodeId,
            selectionMode: 'dblclick',
            listeners: {
                nodeselectionchange: 'onNodeSelectionChange'
            },
            maxHeight: 500,
            animate: false
        });
    },

    onDestroy: function () {
        var me = this;

        me.classifierTree = null;

        me.callParent(arguments);
    }
});
