/**
 * Класс реализует поля воода для указания фильтров для типа данных Date
 *
 * events:
 *        change - событие изменения фильтра
 *
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.tablet.Date', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.date',

    mixins: [
        'Unidata.mixin.search.SearchTabletValidatable'
    ],

    attributePath: '',

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    cls: 'un-search-attribute-tablet-date',

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
                        xtype: 'datefield',
                        ui: 'un-field-default',
                        reference: 'eqValue',
                        publishWithErrors: false,
                        bind: {
                            value: '{term.value}'
                        },
                        fieldLabel: '',
                        // editable: false,
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
                            },
                            change: function () {
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
                        xtype: 'datefield',
                        ui: 'un-field-default',
                        reference: 'leftRange',
                        publishWithErrors: false,
                        bind: {
                            value: '{term.rangeFrom}'
                        },
                        minText: Unidata.i18n.t('search>query.invalidDateRange'),
                        maxText: Unidata.i18n.t('search>query.invalidDateRange'),
                        // editable: false,
                        validateOnChange: false,
                        validateOnBlur: false,
                        margin: '0 0 10 0',
                        flex: 1,
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
                            },
                            change: function () {
                                me.validate();
                            }
                        }
                    }, {
                        xtype: 'datefield',
                        ui: 'un-field-default',
                        reference: 'rightRange',
                        publishWithErrors: false,
                        bind: {
                            value: '{term.rangeTo}'
                        },
                        minText: Unidata.i18n.t('search>query.invalidDateRange'),
                        maxText: Unidata.i18n.t('search>query.invalidDateRange'),
                        // editable: false,
                        validateOnChange: false,
                        validateOnBlur: false,
                        margin: '0 0 5 0',
                        flex: 1,
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
                            },
                            change: function () {
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

    initValue: function () {
        var data = this.value;

        if (Ext.isObject(data)) {
            if (data.hasOwnProperty('value')) {
                this.lookupReference('eqValue').setValue(this.parseDate(data.value));
            } else if (data.hasOwnProperty('range') && Ext.isArray(data.range)) {
                this.lookupReference('leftRange').setValue(this.parseDate(data.range[0]));
                this.lookupReference('rightRange').setValue(this.parseDate(data.range[1]));
            }
        }
    },

    initReferences: function () {
        this.errorField = this.lookupReference('errorField');
        this.eqValue = this.lookupReference('eqValue');
        this.leftRange = this.lookupReference('leftRange');
        this.rightRange = this.lookupReference('rightRange');
    },

    parseDate: function (strOrDate) {
        var date = null;

        if (Ext.isString(strOrDate)) {
            date = Ext.Date.parse('2017-08-01T00:00:00.000', Unidata.Config.getDateTimeFormatProxy());
        } else if (Ext.isDate(strOrDate)) {
            date = strOrDate;
        }

        return date;
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
            eqValue = this.lookupReference('eqValue'),
            leftRange = this.lookupReference('leftRange'),
            rightRange = this.lookupReference('rightRange'),
            isNullable = options.isNullable,
            isInverted = options.isInverted,
            formatFn;

        formatFn = function (datePicker) {
            var date,
                result = null;

            date = datePicker.getValue();

            if (date) {
                result = Ext.Date.format(date, Unidata.Config.getDateTimeFormatProxy());
            }

            return result;
        };

        paramPath = {
            name: this.attributePath,
            type: this.getType(),
            inverted: isInverted
        };

        if (!eqValue.isDisabled() || isNullable) {
            param = {
                value: isNullable ? null : formatFn(eqValue)
            };
        } else {
            leftRange  = leftRange.getValue()  ? formatFn(leftRange)  : null;
            rightRange = rightRange.getValue() ? formatFn(rightRange) : null;

            param = {
                range: [leftRange, rightRange]
            };
        }

        param = Ext.apply(param, paramPath);

        return param;
    },

    getType: function () {
        return 'Date';
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

        this.fireEvent('change', this);
    },

    onRangeChange: function () {
        var disable = true,
            leftRangeField,
            rightRangeField,
            leftRangeValue,
            rightRangeValue;

        leftRangeField  = this.lookupReference('leftRange');
        rightRangeField = this.lookupReference('rightRange');

        leftRangeValue = leftRangeField.getValue();
        rightRangeValue = rightRangeField.getValue();

        if (leftRangeValue === null && rightRangeValue === null) {
            disable = false;
        }

        if (leftRangeValue) {
            rightRangeField.setMinValue(leftRangeValue);
        } else {
            rightRangeField.setMinValue();
        }

        if (rightRangeValue) {
            leftRangeField.setMaxValue(rightRangeValue);
        } else {
            leftRangeField.setMaxValue();
        }

        this.lookupReference('eqValue').setDisabled(disable);

        this.fireEvent('change', this);
    },

    setDisabled: function (disabled) {
        this.lookupReference('leftRange').setDisabled(disabled);
        this.lookupReference('rightRange').setDisabled(disabled);
        this.lookupReference('eqValue').setDisabled(disabled);
    }
});
