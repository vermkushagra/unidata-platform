/**
 * Секция настройки валидационных правил редактора правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleValidation', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.dqrulevalidation',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {
        metaRecord: null,
        dqRule: null,
        dqRaise: null,
        cleanseFunction: null,
        outputPorts: null
    },

    referenceHolder: true,

    errorPortInput: null,
    categoryPortInput: null,
    categoryTextInput: null,
    messagePortInput: null,
    messageTextInput: null,

    title: Unidata.i18n.t('admin.dq>dqValidationConfig'),

    viewModelAccessors: ['dqRaise'],

    viewModel: {
        data: {
            dqRaise: null
        }
    },

    stores: {},

    /**
     * Создает chained store, зависимый от главного, с учетом фильтрации
     *
     * @param filters
     * @return {Ext.data.ChainedStore|*}
     */
    createPortChainedStore: function (filters) {
        var outputPorts,
            store;

        outputPorts = this.getOutputPorts();
        filters = filters || [];

        store = Ext.create('Ext.data.ChainedStore', {
            source: outputPorts,
            remoteSort: false,
            remoteFilter: false,
            filters: filters
        });

        return store;
    },

    initOutputPorts: function (cleanseFunction) {
        if (cleanseFunction) {
            this.setOutputPorts(cleanseFunction.outputPorts());
        } else {
            // use empty store
            this.setOutputPorts(Ext.create('Ext.data.Store', {
                model: 'Unidata.model.cleansefunction.OutputPort'
            }));
        }
    },

    initItems: function () {
        var items,
            stores = this.stores,
            cleanseFunction;

        this.callParent(arguments);

        cleanseFunction = this.getCleanseFunction();
        this.initOutputPorts(cleanseFunction);

        stores['error'] = this.createPortChainedStore([
            {
                property: 'dataType',
                value: 'Boolean'
            }
        ]);

        stores['message'] = this.createPortChainedStore([
            {
                property: 'dataType',
                value: 'String'
            }
        ]);

        stores['category'] = this.createPortChainedStore([
            {
                property: 'dataType',
                value: 'String'
            }
        ]);

        items = [
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'left'
                },
                flex: 1,
                margin: 10,
                items: [
                    {

                        xtype: 'combo',
                        width: '100%',
                        fieldLabel: Unidata.i18n.t('admin.metamodel>makeErrorOnBase'),
                        allowBlank: true,
                        autoSelect: true,
                        queryMode: 'local',
                        forceSelection: true,
                        editable: false,
                        labelAlign: 'top',
                        displayField: 'description',
                        valueField: 'name',
                        store: stores['error'],
                        emptyText: Unidata.i18n.t(
                            'admin.common>defaultSelect',
                            {entity: Unidata.i18n.t('admin.cleanseFunction>port').toLowerCase()}
                        ),
                        modelValidation: true,
                        msgTarget: 'under',
                        reference: 'errorPortInput',
                        bind: {
                            value: '{dqRaise.functionRaiseErrorPort}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: true,
                                cls: 'x-form-clear-trigger',
                                handler: function (self) {
                                    self.setValue(null);
                                }
                            }
                        }
                        // пока не ясно нужен ли emptyText, поэтому оставляем
                        // emptyText: Unidata.i18n.t(
                        //     'admin.common>defaultSelect',
                        //     {entity: Unidata.i18n.t('admin.cleanseFunction>port').toLowerCase()}
                        // )
                    },
                    {
                        xtype: 'label',
                        text: Unidata.i18n.t('admin.metamodel>messageText')
                    },
                    {
                        xtype: 'radiogroup',
                        qaId: 'messageRadioGroup',
                        columns: 2,
                        listeners: {
                            change: 'onMessageInputTypeRadioChange',
                            scope: this
                        },
                        width: 300,
                        items: [
                            {
                                boxLabel: Unidata.i18n.t('admin.dq>portInput'),
                                name: 'message_input_type',
                                inputValue: 'port',
                                checked: !this.isMessageInputTypeConstant(),
                                bind: {
                                    readOnly: '{dqRuleEditorReadOnly}'
                                }
                            },
                            {
                                boxLabel: Unidata.i18n.t('admin.dq>constantInput'),
                                name: 'message_input_type',
                                inputValue: 'constant',
                                checked: this.isMessageInputTypeConstant(),
                                bind: {
                                    readOnly: '{dqRuleEditorReadOnly}'
                                }
                            }
                        ]
                    },
                    {

                        xtype: 'combo',
                        width: '100%',
                        // fieldLabel: Unidata.i18n.t('admin.metamodel>messageText'),
                        allowBlank: true,
                        autoSelect: true,
                        queryMode: 'local',
                        forceSelection: true,
                        editable: false,
                        labelAlign: 'top',
                        displayField: 'description',
                        valueField: 'name',
                        reference: 'messagePortInput',
                        store: stores['message'],
                        hidden: this.isMessageInputTypeConstant(),
                        bind: {
                            value: '{dqRaise.messagePort}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: true,
                                cls: 'x-form-clear-trigger',
                                handler: function (self) {
                                    self.setValue(null);
                                }
                            }
                        }
                    },
                    {
                        xtype: 'textfield',
                        width: '100%',
                        labelAlign: 'top',
                        reference: 'messageTextInput',
                        allowBlank: false,
                        msgTarget: 'under',
                        hidden: !this.isMessageInputTypeConstant(),
                        bind: {
                            value: '{dqRaise.messageText}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: true,
                                cls: 'x-form-clear-trigger',
                                handler: this.clearPortConstantInput.bind(this)
                            }
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                flex: 1,
                margin: '10 10 10 0',
                items: [
                    {
                        xtype: 'combo',
                        reference: 'severityValue',
                        fieldLabel: Unidata.i18n.t('glossary:criticalness'),
                        width: '100%',
                        allowBlank: true,
                        queryMode: 'local',
                        forceSelection: true,
                        editable: false,
                        valueField: 'value',
                        autoSelect: true,
                        labelAlign: 'top',
                        emptyText: Unidata.i18n.t('admin.metamodel>userCriticalness'),
                        store: {
                            fields: ['text', 'value'],
                            autoLoad: true,
                            data: Unidata.model.dataquality.DqRaise.getSeverityList()
                        },
                        modelValidation: true,
                        msgTarget: 'under',
                        bind: {
                            value: '{dqRaise.severityValue}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: true,
                                cls: 'x-form-clear-trigger',
                                handler: function (self) {
                                    self.setValue(null);
                                }
                            }
                        }
                    },
                    {
                        xtype: 'label',
                        text: Unidata.i18n.t('glossary:category')
                    },
                    {
                        xtype: 'radiogroup',
                        qaId:  'categoryRadioGroup',
                        columns: 2,
                        listeners: {
                            change: 'onCategoryInputTypeRadioChange',
                            scope: this
                        },
                        width: 300,
                        items: [
                            {
                                boxLabel: Unidata.i18n.t('admin.dq>portInput'),
                                name: 'category_input_type',
                                inputValue: 'port',
                                checked: !this.isCategoryInputTypeConstant(),
                                bind: {
                                    readOnly: '{dqRuleEditorReadOnly}'
                                }
                            },
                            {
                                boxLabel: Unidata.i18n.t('admin.dq>constantInput'),
                                name: 'category_input_type',
                                inputValue: 'constant',
                                checked: this.isCategoryInputTypeConstant(),
                                bind: {
                                    readOnly: '{dqRuleEditorReadOnly}'
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'combo',
                        width: '100%',
                        // fieldLabel: Unidata.i18n.t('glossary:category'),
                        queryMode: 'local',
                        allowBlank: true,
                        autoSelect: true,
                        forceSelection: true,
                        editable: false,
                        labelAlign: 'top',
                        displayField: 'description',
                        valueField: 'name',
                        reference: 'categoryPortInput',
                        store: stores['category'],
                        hidden: this.isCategoryInputTypeConstant(),
                        bind: {
                            value: '{dqRaise.categoryPort}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: true,
                                cls: 'x-form-clear-trigger',
                                handler: function (self) {
                                    self.setValue(null);
                                }
                            }
                        }

                    },
                    {
                        xtype: 'textfield',
                        width: '100%',
                        labelAlign: 'top',
                        reference: 'categoryTextInput',
                        hidden: !this.isCategoryInputTypeConstant(),
                        bind: {
                            value: '{dqRaise.categoryText}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: true,
                                cls: 'x-form-clear-trigger',
                                handler: this.clearPortConstantInput.bind(this)
                            }
                        }
                    }
                ]
            }
        ];

        this.add(items);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initPanelEvents();
    },

    initPanelEvents: function () {
        this.on('collapse', this.onPanelCollapse.bind(this));
        this.on('expand', this.onPanelExpand.bind(this));
    },

    initReferences: function () {
        this.categoryPortInput = this.lookupReference('categoryPortInput');
        this.categoryTextInput = this.lookupReference('categoryTextInput');
        this.messagePortInput = this.lookupReference('messagePortInput');
        this.messageTextInput = this.lookupReference('messageTextInput');
        this.errorPortInput = this.lookupReference('errorPortInput');
    },

    isMessageInputTypeConstant: function () {
        return this.isInputTypeConstant('messageText');
    },

    isCategoryInputTypeConstant: function () {
        return this.isInputTypeConstant('categoryText');
    },

    isInputTypeConstant: function (textFieldName) {
        var dqRule = this.getDqRule(),
            dqRaise,
            isConstant = false; // значение по умолчанию

        if (!dqRule) {
            return isConstant;
        }

        dqRaise = dqRule.getRaise();

        if (dqRaise) {
            isConstant = !Ext.isEmpty(dqRaise.get(textFieldName));
        }

        return isConstant;
    },

    onMessageInputTypeRadioChange: function (self, value) {
        var isShowConstantInput,
            fieldName = 'message_input_type';

        if (!Ext.isObject(value) || !value.hasOwnProperty(fieldName)) {
            return;
        }

        isShowConstantInput = value[fieldName] === 'constant';
        this.messageTextInput.setHidden(!isShowConstantInput);
        this.messagePortInput.setHidden(isShowConstantInput);

        if (this.messagePortInput.hidden) {
            this.messagePortInput.setValue(null);
        }

        if (this.messageTextInput.hidden) {
            this.messageTextInput.setValue(null);
        }
    },

    onCategoryInputTypeRadioChange: function (self, value) {
        var isShowConstantInput,
            fieldName = 'category_input_type';

        if (!Ext.isObject(value) || !value.hasOwnProperty(fieldName)) {
            return;
        }

        isShowConstantInput = value[fieldName] === 'constant';
        this.categoryTextInput.setHidden(!isShowConstantInput);
        this.categoryPortInput.setHidden(isShowConstantInput);

        if (this.categoryPortInput.hidden) {
            this.categoryPortInput.setValue(null);
        }

        if (this.categoryTextInput.hidden) {
            this.categoryTextInput.setValue(null);
        }
    },

    updateCleanseFunction: function (cleanseFunction) {
        this.initOutputPorts(cleanseFunction);
        this.rebuildInputs();
    },

    rebuildInputs: function () {
        var cleanseFunction = this.getCleanseFunction(),
            outputPorts;

        if (!cleanseFunction) {
            return;
        }

        outputPorts = cleanseFunction.outputPorts();

        Ext.Object.each(this.stores, function (key, value, obj) {
            obj[key].setSource(outputPorts);
        });

        // сбросить установленные порты в блоке валидации
        if (this.messagePortInput) {
            this.messagePortInput.setValue(null);
        }

        if (this.categoryPortInput) {
            this.categoryPortInput.setValue(null);
        }

        if (this.errorPortInput) {
            this.errorPortInput.setValue(null);
        }

        if (this.messageTextInput) {
            this.messageTextInput.setValue(null);
        }

        if (this.categoryTextInput) {
            this.categoryTextInput.setValue(null);
        }
    },

    clearPortConstantInput: function (self) {
        self.setValue(null);
    },

    buildTitle: function () {
        var DqRaiseModel = Unidata.model.dataquality.DqRaise,
            collapsed    = this.getCollapsed(),
            dqRule       = this.getDqRule(),
            dqRaise      = dqRule.getRaise(),
            functionRaiseErrorPort,
            severityValue,
            title        = Unidata.i18n.t('admin.dq>dqValidationConfig'),
            isValidation = dqRule.get('isValidation'),
            parts        = [],
            severityList = DqRaiseModel.getSeverityList(),
            found,
            titleInfo;

        if (collapsed && isValidation && dqRaise) {
            functionRaiseErrorPort = dqRaise.get('functionRaiseErrorPort');
            severityValue = dqRaise.get('severityValue');

            if (functionRaiseErrorPort) {
                parts.push(functionRaiseErrorPort);
            }

            if (severityValue) {
                found = Ext.Array.findBy(severityList, function (item) {
                    return item.value === severityValue;
                }, this);

                if (found && found.text) {
                    parts.push(found.text + ' критичность');
                }
            }

            if (parts.length > 0) {
                titleInfo = parts.join(' ');
                title = Ext.String.format('{0}: <span class="un-dq-rule-title-info">{1}</span>', title, titleInfo);
            }
        }

        return title;
    },

    buildAndUpdateTitle: function () {
        var title = this.buildTitle();

        this.setTitle(title);
    },

    onPanelCollapse: function () {
        this.buildAndUpdateTitle();
    },

    onPanelExpand: function () {
        this.buildAndUpdateTitle();
    }
});
