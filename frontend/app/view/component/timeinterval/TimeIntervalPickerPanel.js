/**
 * Pickerfield для диапазона значений (from, to)
 *
 * Для использования необходимо забиндить valueFrom, valueTo, readOnly
 *
 * Пример использования:
 * {
 *     xtype: 'timeintervalpickerpanel',
 *      title: 'Текущий период',
 *      bind: {
 *         validFrom: '{currentRecord.validFrom}',
 *         validTo: '{currentRecord.validTo}',
 *         readOnly: '{!timeIntervalDataView.isCopyMode}'
 *     },
 *    listeners: {
 *          deletebuttonclick: 'onDeleteVersionButtonClick'
 *    }
 * }
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.timeinterval.TimeIntervalPickerPanel', {
    extend: 'Ext.container.Container',

    xtype: 'timeintervalpickerpanel',
    publishes: ['validFrom', 'validTo', 'readOnly'],
    config: {
        readOnly: true,
        minDate: null,
        maxDate: null
    },
    hidden: true,
    layout: 'hbox',
    cls: 'timeinterval-picker-container',
    referenceHolder: true,
    tools: [
        {
            type: 'remove',
            handler: function (event, el, header, btn) {
                this.findParentByType('timeintervalpickerpanel').fireEvent('deletebuttonclick', btn, header);
            },
            tooltip: Unidata.i18n.t('dataviewer>removeCurrentTimeInterval')
        }
    ],
    items: [
        {
            xtype: 'datefield',
            readOnlyCls: 'readonly-textfield',
            ui: 'un-field-default',
            showToday: false,
            maxWidth: 130,
            flex: 1,
            value: null,
            //тексты временно заданы пустыми, т.к. в штатном режиме сообщения не должны появляться
            hideLabel: true,
            name: 'validFrom',
            reference: 'validFromDatePicker',
            //modelValidation: true,
            emptyText: Unidata.Config.getMinDateSymbol(),
            cls: 'datepicker-from',
            loadingText: Unidata.i18n.t('common:loading'),
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (component) {
                        var picker = component.getPicker();

                        component.setValue(null);

                        function beforeHideHandler () {
                            return false;
                        }

                        if (component.isExpanded) {
                            picker.setLoading(component.loadingText);
                            picker.on('beforehide', beforeHideHandler, this, {single: true});
                            component.collapse();
                            component.expand();
                            picker.setLoading(false);
                        }
                    }
                }
            },
            listeners: {
                render: function (component) {
                    component.minValue = this.ownerCt.minDate;
                },
                change: function (component, value) {
                    this.ownerCt.publishState('validFrom', value);
                    this.ownerCt.onValidFromChange(component, value);
                }
            }
        },
        {
            xtype: 'datefield',
            readOnlyCls: 'readonly-textfield',
            ui: 'un-field-default',
            showToday: false,
            maxWidth: 130,
            flex: 1,
            //тексты временно заданы пустыми, т.к. в штатном режиме сообщения не долcжны появляться
            hideLabel: true,
            name: 'validTo',
            reference: 'validToDatePicker',
            emptyText: Unidata.Config.getMaxDateSymbol(),
            //modelValidation: true,
            loadingText: Unidata.i18n.t('common:loading'),
            value: null,
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (component) {
                        var picker = component.getPicker();

                        component.setValue(null);

                        function beforeHideHandler () {
                            return false;
                        }

                        if (component.isExpanded) {
                            picker.setLoading(component.loadingText);
                            picker.on('beforehide', beforeHideHandler, this, {single: true});
                            component.collapse();
                            component.expand();
                            picker.setLoading(false);
                        }
                    }
                }
            },
            listeners: {
                render: function (component) {
                    component.maxValue = this.ownerCt.maxDate;
                },
                change: function (component, value) {
                    this.ownerCt.publishState('validTo', value);
                    this.ownerCt.onValidToChange(component, value);
                }
            }
        }
    ],
    updateMinDate: function (value) {
        var validFromDatePicker = this.lookupReference('validFromDatePicker'),
            validToDatePicker = this.lookupReference('validToDatePicker'),
            minDateEmptyText = '- \u221E',
            dateFormat = Unidata.Config.getDateFormat();

        if (validFromDatePicker) {
            validFromDatePicker.setMinValue(value);
        }

        if (validToDatePicker) {
            validToDatePicker.setMinValue(value);
        }

        if (value) {
            minDateEmptyText = Ext.Date.format(value, dateFormat);
        }
        validFromDatePicker.emptyText = minDateEmptyText;
    },
    updateMaxDate: function (value) {
        var validFromDatePicker = this.lookupReference('validFromDatePicker'),
            validToDatePicker   = this.lookupReference('validToDatePicker'),
            maxDateEmptyText = '- \u221E',
            dateFormat = Unidata.Config.getDateFormat();

        if (validToDatePicker) {
            validToDatePicker.setMaxValue(value);
        }

        if (validFromDatePicker) {
            validFromDatePicker.setMaxValue(value);
        }

        if (value) {
            maxDateEmptyText = Ext.Date.format(value, dateFormat);
        }
        validToDatePicker.emptyText = maxDateEmptyText;
    },
    setValidFrom: function (value) {
        this.lookupReference('validFromDatePicker').setValue(value);
    },
    setValidTo: function (value) {
        this.lookupReference('validToDatePicker').setValue(value);
    },
    getValidFrom: function () {
        return this.lookupReference('validFromDatePicker').getValue();
    },
    getValidTo: function () {
        return this.lookupReference('validToDatePicker').getValue();
    },
    setReadOnly: function (value) {
        var validFromDatePicker = this.lookupReference('validFromDatePicker'),
            validToDatePicker   = this.lookupReference('validToDatePicker');

        if (validFromDatePicker) {
            validFromDatePicker.setReadOnly(value);
        }

        if (validToDatePicker) {
            validToDatePicker.setReadOnly(value);
        }

        this.callParent(arguments);
    },

    onValidFromChange: function (component, value) {
        this.updateValidToMinValue(value);
    },

    onValidToChange: function (component, value) {
        this.updateValidFromMaxValue(value);
    },

    updateValidFromMaxValue: function (toValue) {
        var validFromDatePicker = this.lookupReference('validFromDatePicker');

        toValue = toValue || this.getMaxDate();

        validFromDatePicker.setMaxValue(toValue);
    },

    updateValidToMinValue: function (fromValue) {
        var validToDatePicker = this.lookupReference('validToDatePicker');

        fromValue = fromValue || this.getMinDate();

        validToDatePicker.setMinValue(fromValue);
    },

    resetDateLimits: function () {
        this.updateValidToMinValue(this.getMinDate());
        this.updateValidFromMaxValue(this.getMaxDate());
    }
});
