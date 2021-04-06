Ext.define('Unidata.view.component.ClassifierComboBox', {
    extend: 'Ext.form.field.ComboBox',

    xtype: 'un.classifiercombobox',

    ui: 'un-field-default',

    cls: 'un-classifier-combobox',
    displayField: 'displayName',
    valueField: 'name',
    autoSelect: false,
    emptyText: Unidata.i18n.t('classifier>selectClassifier'),

    store: {
        type: 'un.classifier',
        autoLoad: true
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
    }
});
