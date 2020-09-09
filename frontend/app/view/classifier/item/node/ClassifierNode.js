/**
 * Экран "Узел классификатора"
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */

Ext.define('Unidata.view.classifier.item.node.ClassifierNode', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifier.item.node.ClassifierNodeController',
        'Unidata.view.classifier.item.node.ClassifierNodeModel',

        'Unidata.view.classifier.item.attribute.ClassifierAttribute',
        'Unidata.view.classifier.item.attribute.ClassifierArrayAttribute'
    ],

    alias: 'widget.classifier.item.node',

    viewModel: {
        type: 'classifier.item.node'
    },

    controller: 'classifier.item.node',

    referenceHolder: true,

    mixins: {
        headertooltip: 'Unidata.mixin.HeaderTooltipable'
    },

    methodMapper: [
        {
            method: 'updateClassifierNode'
        },
        {
            method: 'highlightErrors'
        },
        {
            method: 'resetErrors'
        },
        {
            method: 'updateReadOnly'
        }
    ],

    attributeContainer: null, // ссылка на контейнер содержащий панели из атрибутивного состава
    classifierNodePanel: null,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-classifier-node',
    config: {
        classifier: null,
        classifierNode: null,
        readOnly: null,
        headerTooltip: null
    },

    bind: {
        readOnly: '{classifierNodeReadOnly}'
    },

    title: Unidata.i18n.t('glossary:classifierNode'),

    header: false,

    items: [
        {
            xtype: 'panel',
            layout: {
                type: 'vbox'
            },
            qaId: 'classifier-node-panel',
            ui: 'un-card',
            reference: 'classifierNodePanel',
            header: {
                titlePosition: 1
            },
            bind: {
                title: '{classifierNodePanelTitle:htmlEncode}'
            },
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'top'
                    },
                    margin: '0 0 10 0',
                    width: '100%',
                    scrollable: true,
                    items: [
                        {
                            xtype: 'textfield',
                            ui: 'un-field-default' ,
                            fieldLabel: Unidata.i18n.t('glossary:naming'),
                            labelWidth: 130,
                            qaId: 'classifier-node-name',
                            flex: 3,
                            readOnly: true,
                            bind: {
                                value: '{classifierNode.name}',
                                readOnly: '{classifierNodeNameAndCodeReadOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under'
                        },
                        {
                            xtype: 'textfield',
                            ui: 'un-field-default' ,
                            fieldLabel: Unidata.i18n.t('glossary:code'),
                            qaId: 'classifier-node-code',
                            margin: '0 0 0 20',
                            labelWidth: 30,
                            flex: 2,
                            readOnly: true,
                            bind: {
                                value: '{classifierNode.code}',
                                readOnly: '{classifierNodeNameAndCodeReadOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under'
                        }
                    ]
                },
                {
                    xtype: 'textarea',
                    ui: 'un-field-default' ,
                    width: '100%',
                    labelWidth: 130,
                    fieldLabel: Unidata.i18n.t('glossary:description'),
                    qaId: 'classifier-node-description',
                    readOnly: true,
                    bind: {
                        value: '{classifierNode.description}',
                        readOnly: '{classifierNodeNameAndCodeReadOnly}'
                    },
                    modelValidation: true,
                    msgTarget: 'under',
                    height: 90
                },
                {
                    xtype: 'keyvalue.input',
                    fieldLabel: Unidata.i18n.t('admin.metamodel>customProperties'),
                    width: '100%',
                    labelWidth: 130,
                    bind: {
                        gridStore: '{classifierNode.customProperties}',
                        readOnly: '{readOnly}'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'panel',
                    title: Unidata.i18n.t('classifier>attributeContainer'),
                    ui: 'un-card',
                    cls: 'un-classifier-node-attribute-container x-panel-card-disable-shadow',
                    reference: 'attributeContainer',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    tools: [
                        {
                            xtype: 'un.dottedmenubtn',
                            scale: 'small',
                            iconCls: 'icon-plus-circle',
                            menu: {
                                xtype: 'un.dottedmenu',
                                plain: true,
                                items: [
                                    {
                                        text: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:attribute')}),
                                        qaId: 'add-attribute',
                                        hidden: true,
                                        bind: {
                                            hidden: '{readOnly}'
                                        },
                                        listeners: {
                                            click: 'onClassifierNodeAttributeAddButtonClick'
                                        }
                                    },
                                    {
                                        text: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:arrayAttribute')}),
                                        qaId: 'add-array-attribute',
                                        hidden: true,
                                        bind: {
                                            hidden: '{readOnly}'
                                        },
                                        listeners: {
                                            click: 'onClassifierNodeArrayAttributeAddButtonClick'
                                        }
                                    }
                                ]
                            }
                        }
                    ],
                    scrollable: true,
                    flex: 1
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initListeners();
    },

    initComponentReference: function () {
        var me = this;

        me.attributeContainer = me.lookupReference('attributeContainer');
        me.classifierNodePanel = me.lookupReference('classifierNodePanel');
    },

    initListeners: function () {
        // тултип временно отключен до тех пор, пока на BE не будет реализована задача UN-8514
        // this.classifierNodePanel.on('render', this.onClassifierNodePanelRender.bind(this));
    },

    onDestroy: function () {
        var me = this;

        me.attributeContainer = null;

        me.callParent(arguments);
    },

    setClassifierNode: function (classifierNode) {
        var viewModel = this.getViewModel();

        viewModel.set('classifierNode', classifierNode);
        viewModel.notify();

        // вызываем метод вручную, т.к. setter переопределен, а вызов callParent отсутствует
        this.updateClassifierNode(classifierNode);
    },

    getClassifierNode: function () {
        var viewModel = this.getViewModel();

        if (!viewModel) {
            return null;
        }

        return viewModel.get('classifierNode');
    },

    setClassifier: function (classifier) {
        var viewModel = this.getViewModel();

        viewModel.set('classifier', classifier);
        viewModel.notify();

        // вызываем метод вручную, т.к. setter переопределен, а вызов callParent отсутствует
        if (Ext.isFunction(this.updateClassifier)) {
            this.updateClassifier(classifier);
        }
    },

    getClassifier: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('classifier');
    },

    buildHeaderTooltip: function () {
        var classifier = this.getClassifier(),
            classifierNode = this.getClassifierNode();

        return Unidata.util.Classifier.buildClassifierNodeTitle(classifier, classifierNode);
    },

    /**
     * Рендеринг компонента
     */
    onClassifierNodePanelRender: function () {
        var headerTooltip,
            classifierNodePanel;

        headerTooltip = this.buildHeaderTooltip();
        this.setHeaderTooltip(headerTooltip);
        classifierNodePanel = this.classifierNodePanel;

        if (headerTooltip) {
            classifierNodePanel.getHeader().on('render', function () {
                this.initTitleTooltip();
            }, this);
        }
    },

    buildBaseToolTip: function () {
        return '{0}';
    },

    getHeader: function () {
        return this.classifierNodePanel.getHeader();
    }
});
