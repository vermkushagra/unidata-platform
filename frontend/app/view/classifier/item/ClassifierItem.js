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
        'Unidata.view.classifier.item.ClassifierItemModel',

        'Unidata.view.component.WarningBubble'
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
        },
        {
            method: 'onSaveClassifierButtonClick'
        },
        {
            method: 'onDeleteClassifierButtonClick'
        }
    ],

    bind: {
        hidden: '{!classifier}'
    },

    referenceHolder: true,

    buttons: null, // контейнер с кнопками

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
                    height: 380,
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
                            //ui: 'un-field-default' ,
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
                            //ui: 'un-field-default' ,
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
                            //ui: 'un-field-default' ,
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
                            //ui: 'un-field-default' ,
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
                            xtype: 'un.warningbubble',
                            margin: '0 0 0 157',
                            text: Unidata.i18n.t('classifier>codePatternWarning'),
                            hidden: true,
                            bind: {
                                hidden: '{patternCodeFieldReadOnly}'
                            }
                        },
                        {
                            xtype: 'checkbox',
                            fieldLabel: Unidata.i18n.t('classifier>validateCodeByLevel'),
                            labelWidth: 150,
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
                    //TODO: refactoring. extract to a separate class, extended from ClassifierTree
                    xtype: 'un.classifiertree',
                    reference: 'classifierTree',
                    ui: 'un-card',
                    title: Unidata.i18n.t('classifier>classifierNodes'),
                    collapsible: false,
                    emptyText: Unidata.i18n.t('classifier>noClassifierNodes'),
                    bind: {
                        hidden: '{classifierTreeHidden}',
                        readOnly: '{classifierTreeReadOnly}'
                    },
                    classifierNodeView: 'META',
                    minHeight: 100,
                    flex: 1,
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
            ]
        }
    ],

    initItems: function () {
        var classifier,
            classifierNodeContainerCfg;

        this.callParent(arguments);
        classifier = this.getClassifier();

        classifierNodeContainerCfg = this.createClassifierNodeContainerCfg(classifier);

        this.add(classifierNodeContainerCfg);
    },

    onRender: function () {
        this.callParent(arguments);

        this.buttons = this.buildButtonsContainer();
        this.on('afterlayout', this.updateButtonsPosition, this);
    },

    onDestroy: function () {
        this.buttons.destroy();

        this.callParent(arguments);
    },

    // TODO: Возможно стоит вынести в отдельный файл
    buildButtonsContainer: function () {
        var viewModel = this.getViewModel(),
            buttons,
            saveButton,
            deleteButton;

        buttons = Ext.create({
            xtype: 'container',
            floating: true,
            renderTo: this.getEl(),
            anchor: '100% 100%',
            shadow: false,
            padding: 10,
            hidden: true,
            referenceHolder: true,
            layout: {
                type: 'hbox',
                align: 'bottom'
            },
            setDisabled: function (disabled) {
                var saveButton = this.lookupReference('saveButton'),
                    deleteButton = this.lookupReference('deleteButton');

                if (saveButton) {
                    saveButton.setDisabled(disabled);
                }

                if (deleteButton) {
                    deleteButton.setDisabled(disabled);
                }

                this.disabled = false;
                //this.callParent(arguments);
            },
            saveButton: null,
            deleteButton: null,
            items: [
                {
                    xtype: 'un.roundbtn.delete',
                    reference: 'deleteButton',
                    tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:classifier')}),
                    buttonSize: 'medium',
                    shadow: false,
                    margin: 10,
                    securedResource: 'ADMIN_CLASSIFIER_MANAGEMENT',
                    securedEvent: 'delete'
                }, {
                    xtype: 'un.roundbtn.save',
                    reference: 'saveButton',
                    tooltip: Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:classifier')}),
                    shadow: false,
                    margin: 10
                }]
        });

        saveButton = buttons.lookupReference('saveButton');
        deleteButton = buttons.lookupReference('deleteButton');
        saveButton.on('click', this.onSaveClassifierButtonClick, this);
        deleteButton.on('click', this.onDeleteClassifierButtonClick, this);

        viewModel.bind('{saveButtonHidden}', function (hidden) {
            saveButton.setHidden(hidden);
        });

        return buttons;
    },

    updateButtonsPosition: function () {
        this.buttons.show();
        this.buttons.alignTo(this, 'br-br');
    },

    createClassifierNodeContainerCfg: function (classifier) {
        var classifierNodeCfg;

        classifierNodeCfg = {
            xtype: 'classifier.item.nodecontainer',
            classifier: classifier,
            reference: 'classifierNodeContainer',
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
