/**
 * Класс реализует поля воода для указания фильтров для типа данных Time
 *
 * @author Sergey Shishigin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.tablet.Time', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.time',

    attributePath: '',

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'timetextfield',
                    ui: 'un-field-default',
                    reference: 'eqValue',
                    fieldLabel: '',
                    msgTarget: 'none',
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function () {
                                this.setValue('');
                            }
                        }
                    }
                }, {
                    xtype: 'container',
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'timetextfield',
                            ui: 'un-field-default',
                            reference: 'leftRange',
                            msgTarget: 'none',
                            flex: 1,
                            emptyText: Unidata.i18n.t('common:from'),
                            triggers: {
                                clear: {
                                    cls: 'x-form-clear-trigger',
                                    handler: function () {
                                        this.setValue('');
                                    }
                                }
                            }
                        }, {
                            xtype: 'timetextfield',
                            ui: 'un-field-default',
                            reference: 'rightRange',
                            msgTarget: 'none',
                            flex: 1,
                            emptyText: Unidata.i18n.t('common:to'),
                            triggers: {
                                clear: {
                                    cls: 'x-form-clear-trigger',
                                    handler: function () {
                                        this.setValue('');
                                    }
                                }
                            }
                        }
                    ]
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.lookupReference('eqValue').on('change', this.onEqValueChange, this);

        this.lookupReference('leftRange').on('change', this.onRangeChange, this);
        this.lookupReference('rightRange').on('change', this.onRangeChange, this);

        this.initValue();
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

    getFilter: function (options) {
        var param,
            paramPath,
            isNullable = options.isNullable,
            isInverted = options.isInverted,
            eqValue = this.lookupReference('eqValue'),
            leftRange = this.lookupReference('leftRange'),
            rightRange = this.lookupReference('rightRange'),
            formatFn;

        formatFn = function (timetext) {
            var date,
                result = null;

            date = Ext.Date.parse(timetext.getValue(), timetext.getWriteFormat());

            if (date) {
                result = 'T' + Ext.Date.format(date, 'H:i:s.u') + 'Z';
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
            //TODO: по умному задача должна решаться на сервере
            leftRange = leftRange.getValue() ? formatFn(leftRange) : null;
            rightRange = rightRange.getValue() ? formatFn(rightRange) : null;

            param = {
                range: [leftRange, rightRange]
            };
        }

        param = Ext.apply(param, paramPath);

        return param;
    },

    getType: function () {
        return 'Time';
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

        leftRangeField = this.lookupReference('leftRange');
        rightRangeField = this.lookupReference('rightRange');

        leftRangeValue = leftRangeField.getValue();
        rightRangeValue = rightRangeField.getValue();

        if (leftRangeValue === null && rightRangeValue === null) {
            disable = false;
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
