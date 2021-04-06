/**
 * Класс реализует поля воода для указания фильтров для типа данных DateTime (Timestamp)
 *
 * @author Sergey Shishigin
 */

Ext.define('Unidata.view.component.search.attribute.tablet.DateTime', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.datetime',

    mixins: [
        'Unidata.mixin.search.SearchTabletValidatable'
    ],

    attributePath: '',

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    buildErrorFieldId: function () {
        var errorId;

        errorId = 'error-' + this.getId();

        return errorId;
    },

    initItems: function () {
        var me = this,
            items;

        this.callParent(arguments);

        items = [
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                flex: 1,
                items: [
                    {
                        xtype: 'datetimefield',
                        ui: 'un-field-default',
                        reference: 'eqValue',
                        fieldLabel: '',
                        // editable: false,
                        msgTarget: 'none',
                        dateCfg: {
                            minText: Unidata.i18n.t('search>query.invalidDateRange'),
                            maxText: Unidata.i18n.t('search>query.invalidDateRange')
                        },
                        validateOnChange: false,
                        validateOnBlur: false,
                        triggers: {
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.setValue('');
                                    me.validate();
                                }
                            }
                        },
                        listeners: {
                            blur: function () {
                                me.validate();
                            },
                            focus: function () {
                                me.validate();
                            }
                        }
                    },
                    {
                        xtype: 'label',
                        margin: '10 0 5 0',
                        text: Unidata.i18n.t('search>query.range')
                    },
                    {
                        xtype: 'datetimefield',
                        ui: 'un-field-default',
                        reference: 'leftRange',
                        fieldLabel: '',
                        // editable: false,
                        msgTarget: 'none',
                        margin: '0 0 10 0',
                        validateOnChange: false,
                        validateOnBlur: false,
                        dateCfg: {
                            minText: Unidata.i18n.t('search>query.invalidDateRange'),
                            maxText: Unidata.i18n.t('search>query.invalidDateRange')
                        },
                        emptyText: Unidata.i18n.t('common:from'),
                        triggers: {
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.setValue('');
                                    me.validate();
                                }
                            }
                        },
                        listeners: {
                            blur: function () {
                                me.validate();
                            },
                            focus: function () {
                                me.validate();
                            }

                        }
                    }, {
                        xtype: 'datetimefield',
                        ui: 'un-field-default',
                        reference: 'rightRange',
                        fieldLabel: '',
                        // editable: false,
                        dateCfg: {
                            minText: Unidata.i18n.t('search>query.invalidDateRange'),
                            maxText: Unidata.i18n.t('search>query.invalidDateRange')
                        },

                        msgTarget: 'none',
                        margin: '0 0 5 0',
                        validateOnChange: false,
                        validateOnBlur: false,
                        emptyText: Unidata.i18n.t('common:to'),
                        triggers: {
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.setValue('');
                                    me.validate();
                                }
                            }
                        },
                        listeners: {
                            blur: function () {
                                me.validate();
                            },
                            focus: function () {
                                me.validate();
                            }
                        }
                    },
                    {
                        xtype: 'component',
                        cls: 'un-search-attribute-tablet-error',
                        reference: 'errorField',
                        id: this.buildErrorFieldId()
                    }
                ]
            }
        ];

        this.add(items);

        return items;
    },

    initComponent: function () {
        this.callParent(arguments);

        this.lookupReference('eqValue').on('change', this.onEqValueChange, this);

        this.lookupReference('leftRange').on('change', this.onRangeChange, this);
        this.lookupReference('rightRange').on('change', this.onRangeChange, this);

        this.initValue();
        this.initReferences();
    },

    initReferences: function () {
        this.errorField = this.lookupReference('errorField');
        this.eqValue = this.lookupReference('eqValue');
        this.leftRange = this.lookupReference('leftRange');
        this.rightRange = this.lookupReference('rightRange');
    },

    initValue: function () {
        var data = this.value;

        if (Ext.isObject(data)) {
            if (data.hasOwnProperty('value')) {
                this.lookupReference('eqValue').setValue(data.value);
            } else if (data.hasOwnProperty('range') && Ext.isArray(data.range)) {
                this.lookupReference('leftRange').setValue(data.range[0]);
                this.lookupReference('rightRange').setValue(data.range[1]);
            }
        }
    },

    /**
     * Сформировать объект поискового фасета (formField) для атрибута
     *
     * @param options {{isNullable: boolean, isInverted: boolean, startWith: boolean, like: boolean}}
     * @returns {{name: string, value: *|null, type: string, inverted: boolean}}
     */
    getFilter: function (options) {
        var param,
            paramPath,
            isNullable = options.isNullable,
            isInverted = options.isInverted,
            eqValue = this.lookupReference('eqValue'),
            leftRange = this.lookupReference('leftRange'),
            rightRange = this.lookupReference('rightRange');

        paramPath = {
            name: this.attributePath,
            type: this.getType(),
            inverted: isInverted
        };

        if (!eqValue.isDisabled() || isNullable) {
            param = {
                value: isNullable ? null : eqValue.getValue()
            };
        } else {
            //TODO: по умному задача должна решаться на сервере
            leftRange = leftRange.getValue() ? leftRange.getValue() : null;
            rightRange = rightRange.getValue() ? rightRange.getValue() : null;

            param = {
                range: [leftRange, rightRange]
            };
        }

        param = Ext.apply(param, paramPath);

        return param;
    },

    getType: function () {
        return 'Timestamp';
    },

    isEmptyFilter: function () {
        var result = false,
            eqValue = this.lookupReference('eqValue'),
            leftRange = this.lookupReference('leftRange'),
            rightRange = this.lookupReference('rightRange');

        if (eqValue.getValue() === null && leftRange.getValue() === null && rightRange.getValue() === null) {
            result = true;
        }

        return result;
    },

    onEqValueChange: function (field, newValue) {
        var disable = true;

        if (newValue === null) {
            disable = false;
        }

        this.lookupReference('leftRange').setDisabled(disable);
        this.lookupReference('rightRange').setDisabled(disable);

        this.validate();

        this.fireEvent('change', this);
    },

    onRangeChange: function () {
        var disable = true,
            leftRangeField,
            rightRangeField,
            leftRangeValue,
            rightRangeValue;

        leftRangeField = this.lookupReference('leftRange');
        rightRangeField = this.lookupReference('rightRange');

        leftRangeValue = leftRangeField.getValue();
        rightRangeValue = rightRangeField.getValue();

        if (leftRangeValue === null && rightRangeValue === null) {
            disable = false;
        }

        if (leftRangeValue) {
            rightRangeField.setMinValue(Ext.Date.parse(leftRangeValue, leftRangeField.getWriteFormat()));
        } else {
            rightRangeField.setMinValue();
        }

        if (rightRangeValue) {
            leftRangeField.setMaxValue(Ext.Date.parse(rightRangeValue, rightRangeField.getWriteFormat()));
        } else {
            leftRangeField.setMaxValue();
        }

        this.lookupReference('eqValue').setDisabled(disable);

        this.validate();

        this.fireEvent('change', this);
    },

    setDisabled: function (disabled) {
        this.lookupReference('leftRange').setDisabled(disabled);
        this.lookupReference('rightRange').setDisabled(disabled);
        this.lookupReference('eqValue').setDisabled(disabled);
    }
});
