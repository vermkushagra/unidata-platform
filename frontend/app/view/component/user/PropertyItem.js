/**
 * Реализует один элемент, содержащийся в панели свойств
 *
 * @date 2015-11-24
 * @author Mаrshalkin Ivan
 */

Ext.define('Unidata.view.component.user.PropertyItem', {
    extend: 'Ext.container.Container',

    referenceHolder: true,

    fieldId: null,
    fieldName: null,
    fieldDisplayName: null,
    fieldValue: null,

    collapsible: true,

    publishes: {
        readOnly: false
    },

    config: {
        field: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    cls: 'un-property-item',

    items: [],

    initComponent: function () {
        var field;

        this.callParent(arguments);

        field = Ext.create('Ext.form.field.Text', {
            fieldLabel: Ext.String.htmlEncode(this.fieldDisplayName), // не забываем предотвращение XSS
            value: this.fieldValue,
            flex: 1,
            labelWidth: 150,
            labelAttrTpl: 'data-qtip="' + this.fieldDisplayName + '"'
        });

        field.on('change', this.onFieldChange, this);

        this.setField(field);
        this.add(field);
    },

    destroy: function () {
        this.setField(null);

        this.callParent();
    },

    setReadOnly: function (readOnly) {
        var field = this.getField();

        field.setReadOnly(readOnly);
    },

    getValue: function () {
        var value;

        value = {
            id: this.fieldId,
            name: this.fieldName,
            displayName: this.fieldDisplayName,
            value: this.getField().getValue()
        };

        return value;
    },

    onFieldChange: function (field, newValue) {
        this.fieldValue = newValue;

        this.fireEvent('change', this.fieldName, this.fieldDisplayName, this.fieldValue, this.fieldId);
    }
});
