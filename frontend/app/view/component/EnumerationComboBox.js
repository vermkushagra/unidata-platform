Ext.define('Unidata.view.component.EnumerationComboBox', {
    extend: 'Ext.form.field.ComboBox',

    xtype: 'un.enumerationcombobox',

    displayField: 'displayName',
    valueField: 'name',
    autoSelect: false,
    anyMatch: true,
    forceSelection: true,
    emptyText: Unidata.i18n.t('classifier>selectValue'),
    queryMode: 'local',
    emptyCls: Ext.baseCSSPrefix + 'form-empty-field',
    triggers: {
        clear: {
            cls: 'x-form-clear-trigger',
            handler: function () {
                this.setValue(null);
            }
        }
    },

    onRender: function () {
        this.callParent(arguments);

        this.inputEl.on('click', this.onInputElementClick, this);
    },

    onInputElementClick: function () {
        // если компонент readOnly то раскрывать его нельзя
        if (this.readOnly) {
            return;
        }

        this.expand();
    },

    setReadOnly: function (readOnly) {
        this.emptyText = !readOnly ? Unidata.i18n.t('classifier>selectValue') : '';
        this.applyEmptyText();
        this.superclass.setReadOnly.call(this, readOnly);
    }
});
