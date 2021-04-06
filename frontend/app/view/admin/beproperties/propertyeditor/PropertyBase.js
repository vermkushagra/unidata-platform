/**
 * Базовый класс представления backend property
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyBase', {
    extend: 'Ext.container.Container',

    alias: 'widget.admin.beproperties.propertybase',

    referenceHolder: true,

    config: {
        property: null,
        readOnly: false
    },

    layout: 'fit',

    input: null,      // ссылка на инпут редактирования свойства

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();

        this.initInput();
        this.syncInput();

        this.setReadOnlyComponentState(this.getReadOnly());
    },

    initReferences: function () {
        this.input = this.lookupReference('input');
    },

    onDestroy: function () {
        this.input = null;

        this.callParent(arguments);
    },

    updateProperty: function () {
        if (!this.input) {
            return;
        }

        this.syncInput();
    },

    initInput: function () {
        var input = this.input;

        if (input instanceof Ext.form.field.Text) {
            input.setUI('un-field-default');
        }

        input.on('change', this.onChangeInputValue, this);
    },

    syncInput: function () {
        var property = this.getProperty();

        this.input.setValue(property.get('value'));
    },

    onChangeInputValue: function (input, newValue) {
        var property = this.getProperty();

        property.set('value', newValue);
    },

    updateReadOnly: function (readOnly) {
        this.setReadOnlyComponentState(readOnly);
    },

    setReadOnlyComponentState: function (readOnly) {
        var property = this.getProperty(),
            meta;

        if (!this.input) {
            return;
        }

        // если свойство readOnly его редактировать недопускается в любом случае
        if (property) {
            meta = property.get('meta');

            if (meta.readonly) {
                this.input.setReadOnly(true);

                return;
            }
        }

        this.input.setReadOnly(readOnly);
    }
});
