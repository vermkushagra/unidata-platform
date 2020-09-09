/**
 * Экран "Классификатор"
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.item.ClassifierItem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifier.item.ClassifierItemController',
        'Unidata.view.classifier.item.ClassifierItemModel'
    ],

    alias: 'widget.classifier.item',

    viewModel: {
        type: 'classifier.item'
    },

    controller: 'classifier.item',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    cls: 'un-classifier-item un-content-inner',
    scrollable: true,

    config: {
        classifier: null,
        classifierTreeEdit: false,
        classifierTreeSaving: false
    },

    methodMapper: [
        {
            method: 'updateClassifier'
        }
    ],

    bind: {
        hidden: '{!classifier}'
    },

    referenceHolder: true,

    loadingText: Unidata.i18n.t('common:loading'),
    nodesSyncCreateSuccessText: Unidata.i18n.t('classifier>createdClassifierNode'),
    nodesSyncUpdateSuccessText: Unidata.i18n.t('classifier>updatedClassifierNode'),
    nodesSyncDeleteSuccessText: Unidata.i18n.t('classifier>removedClassifierNode'),
    nodesSyncFailureText: Unidata.i18n.t('classifier>cantExecClassifierNodeOperation'),
    classifierSaveSuccessText: Unidata.i18n.t('classifier>saveSettingsSuccess'),
    classifierSaveFailureText: Unidata.i18n.t('classifier>saveSettingsFailure'),
    classifierNodeLoadFailureText: Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:classifierNode')}),
    classifierDeleteSuccessText: Unidata.i18n.t('classifier>removeClassifierSuccess'),
    classifierDeleteFailureText: Unidata.i18n.t('classifier>removeClassifierFailure'),
    classifierNodeValidationErrorText: Unidata.i18n.t('classifier>validationErrorNodeNotSaved'),

    codePatternTooltipText: null,
    classifierNodeContainer: null,

    initComponent: function () {
        var tpl = Ext.create('Ext.XTemplate', '{msg}'),
            data;

        data = {
            msg: Unidata.i18n.t('classifier>patternExplanation')
        };

        this.codePatternTooltipText = tpl.apply(data);

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'panel',
            flex: 4,
            cls: 'un-classifier-item-inner',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'panel',
                    bind: {
                        title: '{classifierItemPanelTitle:htmlEncode}'
                    },
                    ui: 'un-card',
                    cls: 'un-classifier-item-settings',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    collapsible: true,
                    collapsed: false,
                    titleCollapse: true,
                    reference: 'classifierSettingsPanel',
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: Unidata.i18n.t('glossary:name'),
                            labelWidth: 150,
                            qaId: 'classifier-name',
                            bind: {
                                value: '{classifier.name}',
                                readOnly: '{nameFieldReadOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            ui: 'un-field-default'
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: Unidata.i18n.t('glossary:displayName'),
                            labelWidth: 150,
                            qaId: 'classifier-display-name',
                            bind: {
                                value: '{classifier.displayName}',
                                readOnly: '{displayNameFieldReadOnly}'
                            },
                            listeners: {
                                change: 'onClassifierDisplayNameChange'
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            ui: 'un-field-default'
                        },
                        {
                            xtype: 'textarea',
                            fieldLabel: Unidata.i18n.t('glossary:description'),
                            labelWidth: 150,
                            qaId: 'classifier-description',
                            bind: {
                                value: '{classifier.description}',
                                readOnly: '{descriptionFieldReadOnly}'

                            },
                            height: 100,
                            modelValidation: true,
                            msgTarget: 'under',
                            ui: 'un-field-default'
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: Unidata.i18n.t('classifier>codePattern'),
                            labelWidth: 150,
                            qaId: 'classifier-code-pattern',
                            bind: {
                                value: '{classifier.codePattern}',
                                readOnly: '{patternCodeFieldReadOnly}'
                            },
                            modelValidation: true,
                            msgTarget: 'under',
                            listeners: {
                                render: 'onCodePatternRender'
                            },
                            ui: 'un-field-default',
                            triggers: {
                                warning: {
                                    cls: 'un-form-warning-trigger'
                                }
                            }
                        },
                        {
                            xtype: 'warning-message',
                            iconHtml: Unidata.util.Icon.getLinearIcon('warning'),
                            text: Unidata.i18n.t('classifier>codePatternWarning'),
                            padding: 0,
                            width: 328,
                            margin: '0 0 0 156',
                            hidden: true,
                            bind: {
                                hidden: '{patternCodeFieldReadOnly}'
                            }
                        },
                        {
                            xtype: 'checkbox',
                            fieldLabel: Unidata.i18n.t('classifier>validateCodeByLevel'),
                            labelWidth: 150,
                            qaId: 'classifier-validate-by-level',
                            bind: {
                                value: '{classifierNotValidateByLevel}',
                                disabled: '{validateCodeByLevelDisabled}'
                            },
                            modelValidation: true,
                            msgTarget: 'under'
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    ui: 'un-card',
                    title: Unidata.i18n.t('classifier>classifierNodes'),
                    collapsible: false,
                    minHeight: 100,
                    layout: 'fit',
                    flex: 1,
                    bind: {
                        hidden: '{classifierTreeHidden}'
                    },
                    items: {
                        xtype: 'un.classifiertree',
                        reference: 'classifierTree',
                        header: false,
                        emptyText: Unidata.i18n.t('classifier>noClassifierNodes'),
                        bind: {
                            readOnly: '{classifierTreeReadOnly}'
                        },
                        classifierNodeView: 'META',
                        plugins: [
                            {
                                ptype: 'cellediting',
                                pluginId: 'cellediting',
                                clicksToEdit: 2,
                                listeners: {
                                    beforeedit: 'onCellBeforeEdit',
                                    edit: 'onCellEdit'
                                }
                            }
                        ],
                        columns: [
                            {
                                xtype: 'un.actioncolumn',
                                width: 25,
                                hideable: false,
                                items: [
                                    {
                                        faIcon: 'plus-circle',
                                        handler: 'onAddChildClassifierNodeButtonClick',
                                        isDisabled: 'addActionIsDisabled',
                                        tooltip: Unidata.i18n.t('classifier>addNestedNode')
                                    }
                                ]
                            },
                            {
                                xtype: 'un.actioncolumn',
                                width: 25,
                                hideable: false,
                                items: [
                                    {
                                        faIcon: 'trash-o',
                                        handler: 'onDeleteClassifierNodeButtonClick',
                                        isDisabled: 'deleteActionIsDisabled',
                                        tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:node')})
                                    }
                                ]
                            },
                            {
                                xtype: 'templatecolumn',
                                width: 25,
                                reference: 'isNodeAttrsColumn',
                                dataIndex: 'ownNodeAttrs',
                                hideable: false,
                                sortable: false,
                                tpl: ''  // tpl is in controller
                            }
                        ],
                        listeners: {
                            select: 'onClassifierTreeSelect',
                            deselect: 'onClassifierTreeDeselect',
                            beforedeselect: 'onClassifierTreeBeforeDeselect',
                            selectionchange: 'onClassifierTreeSelectionChange'
                        }
                    }
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            cls: 'right-toolbar',
            dock: 'right',
            width: 45,
            defaults: {
                xtype: 'button',
                ui: 'un-toolbar-admin',
                scale: 'medium'
            },
            items: [
                {
                    handler: 'onSaveClassifierButtonClick',
                    iconCls: 'icon-floppy-disk',
                    tooltip: Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:classifier')}),
                    hidden: true,
                    bind: {
                        hidden: '{saveButtonHidden}'
                    }
                },
                '->',
                {
                    handler: 'onDeleteClassifierButtonClick',
                    iconCls: 'icon-trash2',
                    margin: '0 0 80 0',
                    tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:classifier')}),
                    securedResource: 'ADMIN_CLASSIFIER_MANAGEMENT',
                    securedEvent: 'delete'
                }
            ]
        }
    ],

    initItems: function () {
        var classifier,
            classifierNodeContainerCfg;

        this.callParent(arguments);
        classifier = this.getClassifier();

        classifierNodeContainerCfg = this.createClassifierNodeContainerCfg(classifier);

        this.classifierNodeContainer = this.add(classifierNodeContainerCfg);
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    createClassifierNodeContainerCfg: function (classifier) {
        var classifierNodeCfg;

        classifierNodeCfg = {
            xtype: 'classifier.item.nodecontainer',
            reference: 'classifierNodeContainer',
            classifier: classifier,
            flex: 3
        };

        return classifierNodeCfg;
    },

    setClassifier: function (classifier) {
        var viewModel = this.getViewModel();

        viewModel.set('classifier', classifier);
        viewModel.notify();

        // вызываем метод вручную, т.к. setter переопределен, а вызов callParent отсутствует
        this.updateClassifier(classifier);
    },

    getClassifier: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('classifier');
    },

    setClassifierTreeEdit: function (classifierTreeEdit) {
        var viewModel = this.getViewModel();

        viewModel.set('classifierTreeEdit', classifierTreeEdit);
        viewModel.notify();

        if (Ext.isFunction(this.updateClassifierTreeEdit)) {
            // вызываем метод вручную, т.к. setter переопределен, а вызов callParent отсутствует
            this.updateClassifierTreeEdit(classifierTreeEdit);
        }
    },

    getClassifierTreeEdit: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('classifierTreeEdit');
    },

    setClassifierTreeSaving: function (classifierTreeSaving) {
        var viewModel = this.getViewModel();

        viewModel.set('classifierTreeSaving', classifierTreeSaving);
        viewModel.notify();

        if (Ext.isFunction(this.updateClassifierTreeEdit)) {
            // вызываем метод вручную, т.к. setter переопределен, а вызов callParent отсутствует
            this.updateClassifierTreeSaving(classifierTreeSaving);
        }
    },

    getClassifierTreeSaving: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('classifierTreeSaving');
    }
});
