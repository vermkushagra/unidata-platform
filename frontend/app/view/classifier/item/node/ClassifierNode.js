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

        'Unidata.view.classifier.item.attribute.ClassifierAttribute'
    ],

    alias: 'widget.classifier.item.node',

    viewModel: {
        type: 'classifier.item.node'
    },

    controller: 'classifier.item.node',

    referenceHolder: true,

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

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-classifier-node',
    config: {
        classifier: null,
        classifierNode: null,
        readOnly: null
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
            ui: 'un-card',
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
                    fieldLabel: Unidata.i18n.t('glossary:description'),
                    readOnly: true,
                    bind: {
                        value: '{classifierNode.description}',
                        readOnly: '{classifierNodeNameAndCodeReadOnly}'
                    },
                    modelValidation: true,
                    msgTarget: 'under',
                    height: 90
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
                    margin: '0 0 90 0',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    tools: [
                        {
                            xtype: 'un.roundbtn.add',
                            handler: 'onAddClassifierNodeAddAttribute',
                            buttonSize: 'extrasmall',
                            shadow: false,
                            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:attribute')}),
                            hidden: true,
                            bind: {
                                hidden: '{readOnly}'
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
    },

    initComponentReference: function () {
        var me = this;

        me.attributeContainer = me.lookupReference('attributeContainer');
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
    }
});
