/**
 * Поле ввода с указанием единицы измерения
 *
 * @author Ivan Marshalkin
 * @date 2016-11-15
 */

Ext.define('Unidata.view.component.MeasurementNumberField', {
    extend: 'Ext.form.field.Base',

    xtype: 'measurementnumberfield',

    childEls: [
        'containerHolder'
    ],

    referenceHolder: true,

    fieldsContainer: null,
    numberField: null,
    measurementField: null,

    numberFieldCfg: null,
    measurementFieldCfg: null,

    fieldSubTpl: '<div id="{cmpId}-containerHolder" data-ref="containerHolder"></div>',

    msgTarget: 'under',
    combineErrors: true,

    readOnly: false,
    allowBlank: false,

    measurementValueId: null,  // измеряемая величина
    measurementUnitId: null,   // единица измерения

    initComponent: function () {
        this.numberFieldCfg = this.numberFieldCfg || {};
        this.measurementFieldCfg = this.measurementFieldCfg || {};

        this.buildFieldsContainer();
        this.buildFields();

        this.callParent(arguments);

        this.numberField.on('render', this.onFieldRender, this);
        this.measurementField.on('render', this.onFieldRender, this);
    },

    buildFieldsContainer: function () {
        this.fieldsContainer = Ext.create('Ext.container.Container', {
            referenceHolder: true,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: []
        });
    },

    buildFields: function () {
        this.buildNumberField();
        this.buildMeasurementField();

        this.fieldsContainer.add(this.numberField);
        this.fieldsContainer.add(this.measurementField);
    },

    buildNumberField: function () {
        var cfg;

        cfg = {
            xtype: 'numberfield',
            flex: 1,
            listeners: {
                focus: this.onFieldFocus,
                blur: this.onFieldBlur,
                specialkey: this.onSpecialKeyNumberField,
                errorchange: this.onComponentFieldErrorChange,
                scope: this
            }
        };
        cfg = Ext.apply(cfg, this.numberFieldCfg);

        this.numberField = Ext.widget(cfg);

        this.numberField.on('change', this.onNumberFieldChange, this);
    },

    buildMeasurementField: function () {
        var MeasurementValuesApi  = Unidata.util.api.MeasurementValues,
            measurementValueStore = MeasurementValuesApi.getStore(),
            MeasurementValueUtil  = Unidata.util.MeasurementUnit,
            cfg;

        cfg = {
            value: this.measurementUnitId,
            width: 100,
            listeners: {
                focus: this.onFieldFocus,
                blur: this.onFieldBlur,
                specialkey: this.onSpecialKeyMeasurementField,
                errorchange: this.onComponentFieldErrorChange,
                scope: this
            }
        };

        cfg = Ext.apply(cfg, this.measurementFieldCfg);
        this.measurementField = MeasurementValueUtil.createMeasurementUnitComboBox(measurementValueStore, this.measurementValueId, cfg);

        this.measurementField.on('change', this.onMeasurementFieldChange, this);
    },

    focus: function () {
        this.numberField.focus.apply(this.numberField, arguments);
    },

    onFieldBlur: function (field, event, eOpts) {
        this.fireEvent('blur', this, event, eOpts);
    },

    onFieldFocus: function (field, event, eOpts) {
        this.fireEvent('focus', this, event, eOpts);
    },

    onSpecialKeyNumberField: function (field, e) {
        if (e.getKey() === e.ENTER) {
            this.measurementField.focus();
        }
    },

    onSpecialKeyMeasurementField: function (field, e) {
        this.fireEvent('specialkey', field, e);
    },

    onNumberFieldChange: function (field, value, oldValue) {
        this.fireEvent('change', this, value, oldValue);
    },

    onMeasurementFieldChange: function (field, value, oldValue) {
        this.fireEvent('measurementchange', this, value, oldValue);
    },

    onRender: function () {
        this.callParent(arguments);

        this.fieldsContainer.render(this.containerHolder);

        this.updateLayoutFieldsContainer();
    },

    onResize: function () {
        this.callParent(arguments);

        this.updateLayoutFieldsContainer();
    },

    updateLayoutFieldsContainer: function () {
        if (this.fieldsContainer.rendered) {
            this.fieldsContainer.updateLayout();
        }
    },

    onFieldRender: function () {
        this.updateLayoutFieldsContainer();
    },

    updateLayout: function () {
        this.callParent(arguments);

        this.updateLayoutFieldsContainer();
    },

    getValue: function () {
        return this.numberField.getValue();
    },

    setValue: function (value) {
        return this.numberField.setValue(value);
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

    getComponentFieldsErrors: function () {
        var errors;

        errors = Ext.Array.merge(
            this.getComponentFieldErrors(this.numberField),
            this.getComponentFieldErrors(this.measurementField)
        );

        return Ext.Array.unique(errors);
    },

    getComponentFieldErrors: function (field) {
        return field.getActiveErrors();
    },

    setReadOnly: function (readOnly) {
        var value = this.getValue(),
            hidden;

        hidden = readOnly && !value;

        this.readOnly = readOnly;

        this.numberField.setReadOnly(readOnly);
        this.measurementField.setReadOnly(readOnly);
        this.measurementField.setHidden(hidden);
        this.superclass.setReadOnly.call(this, readOnly);
    },

    setDisabled: function (value) {
        this.numberField.setDisabled(value);
        this.measurementField.setDisabled(value);

        this.callParent(arguments);
    },

    isValid: function () {
        var errors = this.getComponentFieldsErrors(),
            valid = !Boolean(errors.length);

        if (!this.numberField.isValid() || !this.measurementField.isValid()) {
            valid = false;
        }

        return valid;
    }
});
