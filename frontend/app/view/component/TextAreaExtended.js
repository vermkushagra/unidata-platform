Ext.define('Unidata.view.component.TextareaExtended', {
    extend: 'Ext.form.field.TextArea',

    xtype: 'textareaextended',

    cls: 'unidata-textfield-extended un-field-extended',
    overCls: 'un-field-extended__over',

    initComponent: function () {
        this.callParent(arguments);

        this.on('blur', this.onBlurField, this);
        this.on('focus', this.onFocusField, this);
        this.on('beforerender', this.onBeforeRenderField, this);
    },

    onBlurField: function () {
        var value = this.getValue();

        if (value !== '' && value !== null) {
            this.addCls('readonly-textfield');
        }
    },

    onFocusField: function () {
        this.removeCls('readonly-textfield');
    },

    onBeforeRenderField: function () {
        var value = this.getValue();

        if (this.isEmptyValue(value)) {
            this.removeCls('readonly-textfield');
        } else {
            this.addCls('readonly-textfield');
        }
    },

    setValue: function (value) {
        this.callParent(arguments);

        if (!this.hasFocus && !this.isEmptyValue(value)) {
            this.addCls('readonly-textfield');
        }
    },

    isEmptyValue: function (value) {
        return value === '' || value === null;
    }
});
