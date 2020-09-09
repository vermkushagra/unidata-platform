/**
 * TimeText field component
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.TimeTextField', {
    extend: 'Ext.form.field.Text',

    xtype: 'timetextfield',

    regex: /^(0[0-9]|1[0-9]|2[0-3])(:[0-5][0-9]:[0-5][0-9]|:[0-5][0-9])$/,
    emptyText: '00:00:00',
    invalidText: Unidata.i18n.t('validation:invalidTime'),
    regexText: Unidata.i18n.t('validation:invalidTime'),
    format: 'H:i:s',
    readFormat: Unidata.Config.getDateTimeFormatServer(),
    writeFormat: Unidata.Config.getDateTimeFormatProxy(),
    enableKeyEvents: true,
    hoursRegex: /^(0[0-9]|1[0-9]|2[0-3])$/,
    hoursMinutesRegex: /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/,

    initComponent: function () {
        this.callParent();
        this.on('keyup', this.onTimeFieldKeyUp, this);
        this.on('blur', this.onTimeFieldBlur, this);
    },

    onTimeFieldKeyUp: function (self, e) {
        if (e.getKey() !== e.BACKSPACE) {
            this.fillTimeTextMask();
        }
    },

    onTimeFieldBlur: function () {
        var rawValue = this.getRawValue();

        if (rawValue[rawValue.length - 1] === ':') {
            rawValue = rawValue.substr(0, rawValue.length - 1);
        }

        if (this.hoursRegex.test(rawValue)) {
            this.setValue(rawValue + ':00:00');
        } else if (this.hoursMinutesRegex.test(rawValue)) {
            this.setValue(rawValue + ':00');
        }
    },

    fillTimeTextMask: function () {
        var rawValue = this.getRawValue();

        if (this.hoursRegex.test(rawValue) || this.hoursMinutesRegex.test(rawValue)) {
            this.setValue(rawValue + ':');
        }
    },

    getValue: function () {
        var rawValue = this.getRawValue(),
            timeValue = Ext.Date.parse(rawValue, this.format),
            value = null;

        if (Ext.isDate(timeValue)) {
            timeValue.setYear(1970);
            timeValue.setMonth(0);
            timeValue.setDate(1);
            value = Ext.Date.format(timeValue, this.writeFormat);
        }

        if (Ext.isEmpty(value) && Ext.isEmpty(rawValue)) {
            return null;
        }

        return value || rawValue;
    },

    setValue: function (value) {
        var dt = Ext.Date.parse(value, this.readFormat);

        if (Ext.isDate(dt)) {
            value = Ext.Date.format(dt, this.format);
        }

        this.callParent(arguments);
    },

    setReadOnly: function (readOnly) {
        this.emptyText = !readOnly ? '00:00:00' : '';
        this.applyEmptyText();
        this.callParent(arguments);
    },

    getWriteFormat: function () {
        return this.writeFormat;
    },

    getSubmitValue: function () {
        return this.getValue();
    }
});
