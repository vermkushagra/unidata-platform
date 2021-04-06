/**
 * DateTime field component
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.DateTimeField', {
    extend: 'Ext.form.field.Base',

    xtype: 'datetimefield',

    referenceHolder: true,

    childEls: [
        'containerHolder'
    ],

    cls: 'un-datetimefield',

    fieldSubTpl: '<div id="{cmpId}-containerHolder" data-ref="containerHolder"></div>',

    combineErrors: true,

    msgTarget: 'under',

    readOnly: false,
    allowBlank: true,
    dateCfg: null,
    timeCfg: null,
    readFormat: Unidata.Config.getDateTimeFormatServer(),
    writeFormat: Unidata.Config.getDateTimeFormatProxy(),

    initComponent: function () {
        var me = this;

        me.dateCfg = me.dateCfg || {};
        me.timeCfg = me.timeCfg || {};

        me.buildField();
        me.callParent(arguments);

        // set listeners to child items
        me.dateField.on('change', this.onDateFieldValueChange, this);
        me.timeField.on('change', this.onTimeTextFieldValueChange, this);

        if (this.config.listeners && this.config.listeners.blur) {
            this.addListenerToSubComponents('blur', this.config.listeners.blur);
        }

        if (this.config.listeners && this.config.listeners.focus) {
            this.addListenerToSubComponents('focus', this.config.listeners.focus);
        }

        // запускаем updateLayout после добавления элемента
        this.on('added', function () {
            Ext.defer(this.updateLayout, 1, this);
        }, this);
    },

    addListenerToSubComponents: function (eventName, listener) {
        var scope = this,
            fn;

        if (Ext.isObject(listener)) {
            fn = listener.fn;
            scope = listener.scope || scope;
        } else if (Ext.isFunction(listener)) {
            fn = listener;
        } else {
            throw new Error('Incorrent listener type in Unidata.view.component.DateTimeField.addListenerToSubComponents');
        }

        this.dateField.on(eventName, fn, scope);
        this.timeField.on(eventName, fn, scope);
    },

    buildField: function () {
        var me = this,
            triggers;

        me.dateField = Ext.create(Ext.apply({
            xtype: 'datefield',
            emptyText: this.emptyText,
            ui: this.ui,
            msgTarget: 'none',
            hideLabel: true,
            submitValue: false,
            inputAttrTpl: me.inputAttrTpl,
            readOnly: me.readOnly,
            readOnlyCls: me.readOnlyCls,
            allowBlank: me.allowBlank,
            format: 'd.m.Y',
            width: 110,
            listeners: {
                scope: this,
                specialkey: this.onSpecialKeyDate,
                focus: this.onFieldFocus,
                blur: this.onFieldBlur,
                errorchange: me.onComponentFieldErrorChange.bind(this)
            }
        }, me.dateCfg));

        me.timeField = Ext.create(Ext.apply({
            xtype: 'timetextfield',
            ui: this.ui,
            msgTarget: 'none',
            hideLabel: true,
            inputAttrTpl: me.inputAttrTpl,
            readOnly: me.readOnly,
            readOnlyCls: me.readOnlyCls,
            allowBlank: me.allowBlank,
            flex: 1,
            triggers: this.config.triggers,
            listeners: {
                scope: this,
                specialkey: this.onSpecialKeyTime,
                focus: this.onFieldFocus,
                blur: this.onFieldBlur,
                errorchange: me.onComponentFieldErrorChange.bind(this)
            }
        }, me.timeCfg));

        // прокидываем триггера с родительского компонента (fieldcontainer)
        // на дочерний (textfield)
        triggers = me.timeField.getTriggers();

        if (triggers) {
            Ext.Object.each(triggers, function (triggerName, trigger) {
                if (trigger.handler) {
                    trigger.handler = Ext.bind(trigger.handler, this);
                }
            }, this);
        }

        me.childComponent = Ext.create('Ext.container.Container', {
            referenceHolder: true,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                me.dateField,
                me.timeField
            ]
        });
    },

    onSpecialKeyDate: function (field, e) {
        if (e.getKey() === e.ENTER) {
            this.timeField.focus();
        }
    },

    onSpecialKeyTime: function (field, e) {
        this.fireEvent('specialkey', field, e);
    },

    onFieldFocus: function (field, event, eOpts) {
        this.fireEvent('focus', this, event, eOpts);
    },

    onFieldBlur: function (field, event, eOpts) {
        this.fireEvent('blur', this, event, eOpts);
    },

    focus: function () {
        this.dateField.focus.apply(this.dateField, arguments);
    },

    onComponentFieldErrorChange: function () {
        var errors = this.getComponentFieldsErrors();

        if (errors) {
            this.markInvalid(errors);
        } else {
            this.clearInvalid();
        }

        // wasValid сброшен не просто так уже не помню подробностей :)
        this.wasValid = null;
        this.validate();
    },

    getErrors: function () {
        return this.getComponentFieldsErrors();
    },

    isValid: function () {
        var errors = this.getComponentFieldsErrors(),
            valid = !Boolean(errors.length);

        if (!this.dateField.isValid() || !this.timeField.isValid()) {
            valid = false;
        }

        return valid;
    },

    getComponentFieldsErrors: function () {
        var errors;

        errors = Ext.Array.merge(
            this.getComponentFieldErrors(this.dateField),
            this.getComponentFieldErrors(this.timeField)
        );

        return Ext.Array.unique(errors);
    },

    getComponentFieldErrors: function (field) {
        field.validate();

        return field.getActiveErrors();
    },

    setReadOnly: function (readOnly) {
        this.readOnly = readOnly;
        this.dateField.setReadOnly(readOnly);
        this.timeField.setReadOnly(readOnly);
    },

    onDateFieldValueChange: function (self, dateValue, oldDateValue) {
        var value = this.getValue(),
            oldValue = this.buildValue(oldDateValue, this.timeField.getValue());

        this.fireEvent('change', this, value, oldValue);
    },

    onTimeTextFieldValueChange: function (self, timeValue, oldTimeValue) {
        var value = this.getValue(),
            oldValue = this.buildValue(this.dateField.getValue(), oldTimeValue);

        this.fireEvent('change', this, value, oldValue);
    },

    getValue: function () {
        var value = this.buildValue();

        return value || null;
    },

    buildValue: function (date, time) {
        var me = this,
            value,
            dateFormat = me.dateField.format,
            timeFormat = me.timeField.format,
            timeDateObj,
            writeValue;

        if (date === undefined) {
            date = me.dateField.getValue();
        }

        if (time === undefined) {
            time = me.timeField.getValue();
        }

        value = Ext.String.format('{0}{1}', date, time);

        if (Ext.isDate(date)) {
            date = Ext.Date.format(date, dateFormat);
            timeDateObj = Ext.Date.parse(time, me.timeField.readFormat);

            if (Ext.isDate(timeDateObj)) {
                time = Ext.Date.format(timeDateObj, timeFormat);
                value = Ext.Date.parse(date + ' ' + time, me.getFormat());
            } else {
                value = Ext.Date.parse(date, dateFormat);
            }

            writeValue = Ext.Date.format(value, me.writeFormat);
        }

        return writeValue || value;
    },

    setValue: function (value) {
        var me = this,
            oldDateValue = me.dateField.getValue(),
            oldTimeValue = me.timeField.getValue(),
            oldValue = this.buildValue(oldDateValue, oldTimeValue);

        if (me.readFormat) {
            value = Ext.Date.parse(value, me.readFormat);
        }

        if (Ext.isDate(value)) {
            me.dateField.setValue(Ext.Date.format(value, me.dateField.format));
            me.timeField.setValue(Ext.Date.format(value, me.timeField.format));
        } else {
            me.dateField.setValue(null);
            me.timeField.setValue(null);
        }

        if (!me.suspendCheckChange) {
            this.fireEvent('change', this, value || null, oldValue || null);
        }
    },

    setMinValue: function (value) {
        this.dateField.setMinValue(value);
    },

    setMaxValue: function (value) {
        this.dateField.setMaxValue(value);
    },

    getMinValue: function () {
        return this.dateField.getMinValue();
    },

    getMaxValue: function () {
        return this.dateField.getMaxValue();
    },

    getFormat: function () {
        var me = this;

        return me.dateField.format + ' ' + me.timeField.format;
    },

    getWriteFormat: function () {
        return this.writeFormat;
    },

    setDisabled: function (value) {
        this.dateField.setDisabled(value);
        this.timeField.setDisabled(value);

        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);

        this.childComponent.render(this.containerHolder);
    },

    onResize: function () {
        this.callParent(arguments);

        this.updateLayoutChildComponent();
    },

    updateLayoutChildComponent: function () {
        if (this.childComponent.rendered) {
            this.childComponent.updateLayout();
        }
    },

    updateLayout: function () {
        this.callParent(arguments);

        this.updateLayoutChildComponent();
    }
});
