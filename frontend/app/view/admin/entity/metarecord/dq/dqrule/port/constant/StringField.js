/**
 * Поле ввода константы DQ порта строкового типа (однострочный + многострочный)
 *
 * @author Sergey Shishigin
 * @date 2018-03-12
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.StringField', {
    extend: 'Ext.container.Container',

    alias: 'widget.admin.entity.metarecord.dq.dqrule.port.constant.stringfield',

    config: {
        value: null,
        readOnly: null,
        changeFn: null,
        isExtendedConstantInput: false, // признак, того, что инпут в extended режиме
        isDisableExtended: false, // признак отключения Extended режима
        ui: null
    },

    portConstantInput: null,
    portConstantExtendedInput: null,

    viewModelAccessors: ['isExtendedConstantInput', 'isDisableExtended'],

    viewModel: {
        data: {
            isExtendedConstantInput: false,
            isDisableExtended: false
        },
        formulas: {
            isExtendedHidden: {
                bind: {
                    isDisableExtended: '{isDisableExtended}',
                    isExtendedConstantInput: '{isExtendedConstantInput}'
                },
                get: function (getter) {
                    var isDisableExtended       = getter.isDisableExtended,
                        isExtendedConstantInput = getter.isExtendedConstantInput;

                    return isDisableExtended || !isExtendedConstantInput;
                }
            }
        }
    },

    layout: {
        type: 'hbox',
        align: 'begin'
    },

    referenceHolder: true,

    initItems: function () {
        var items,
            value,
            readOnly,
            changeFn,
            ui;

        this.callParent(arguments);
        value = this.getValue();
        readOnly = this.getReadOnly();
        changeFn = this.getChangeFn();
        ui = this.getUi();
        ui = ui || 'default';

        changeFn = changeFn || Ext.emptyFn;

        items = [
            {
                xtype: 'container',
                layout: {
                    type: 'vbox'
                },
                flex: 1,
                items: [
                    {
                        xtype: 'textfield',
                        width: '100%',
                        reference: 'portConstantInput',
                        msgTarget: 'under',
                        value: value,
                        readOnly: readOnly,
                        ui: ui,
                        listeners: {
                            change: changeFn
                        },
                        bind: {
                            hidden: '{!isExtendedHidden}'
                        },
                        triggers: {
                            clear: {
                                hideOnReadOnly: false,
                                cls: 'x-form-clear-trigger',
                                handler: function (self) {
                                    self.setValue(null);
                                }
                            }
                        }
                    },
                    {
                        xtype: 'textarea',
                        width: '100%',
                        ui: ui,
                        height: 160,
                        reference: 'portConstantExtendedInput',
                        msgTarget: 'under',
                        value: value,
                        readOnly: readOnly,
                        listeners: {
                            change: changeFn
                        },
                        bind: {
                            hidden: '{isExtendedHidden}'
                        }
                    }
                ]
            },
            {
                xtype: 'button',
                ui: 'un-toolbar-admin',
                focusable: false,
                height: 28,
                width: 24,
                margin: '0 0 0 4',
                scale: 'small',
                iconCls: 'icon-expand2',
                tooltip: Unidata.i18n.t('admin.dq>expand'),
                handler: this.onConstantInputToggle.bind(this),
                bind: {
                    hidden: '{isDisableExtended}'
                }
            }
        ];

        this.add(items);

        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.portConstantInput = this.lookupReference('portConstantInput');
        this.portConstantExtendedInput = this.lookupReference('portConstantExtendedInput');
    },

    initListeners: function () {
        this.portConstantInput.on('change', this.onPortConstantInputChange, this);
        this.portConstantExtendedInput.on('change', this.onPortConstantExtendedInputChange, this);
    },

    onPortConstantInputChange: function (self, value) {
        this.setValue(value);
    },

    onPortConstantExtendedInputChange: function (self, value) {
        this.setValue(value);
    },

    updateValue: function (value) {
        var portConstantInput = this.portConstantInput,
            portConstantExtendedInput = this.portConstantExtendedInput;

        if (!portConstantInput || !portConstantExtendedInput) {
            return;
        }

        portConstantExtendedInput.setValue(value);
        portConstantInput.setValue(value);
    },

    onConstantInputToggle: function (self) {
        var isExtendedConstantInput = this.getIsExtendedConstantInput(),
            iconCls,
            tooltip;

        this.setIsExtendedConstantInput(!isExtendedConstantInput);

        if (this.getIsExtendedConstantInput()) {
            iconCls = 'icon-contract2';
            tooltip = Unidata.i18n.t('admin.dq>collapse');
        } else {
            iconCls = 'icon-expand2';
            tooltip = Unidata.i18n.t('admin.dq>expand');
        }

        self.setIconCls(iconCls);
        self.setTooltip(tooltip);
    },

    updateReadOnly: function (readOnly) {
        if (this.portConstantInput) {
            this.portConstantInput.setReadOnly(readOnly);
        }

        if (this.portConstantExtendedInput) {
            this.portConstantExtendedInput.setReadOnly(readOnly);
        }
    },

    clearValue: function () {
        this.portConstantInput.setValue(null);
        this.portConstantExtendedInput.setValue(null);
    },

    getSubmitValue: function () {
        return this.getValue();
    },

    validate: function () {
        var valid = false;

        if (this.portConstantExtendedInput) {
            valid = this.portConstantInput.validate();
        } else {
            valid = this.portConstantExtendedInput.validate();
        }

        return valid;
    }
});
