Ext.define('Unidata.view.component.YesNoComboBox', {
    extend: 'Ext.form.field.ComboBox',

    xtype: 'yesnocombobox',

    initComponent: function () {
        var store,
            data;

        this.callParent(arguments);

        store = {
            fields: ['key', 'value']
        };

        if (!this.isInverted()) {
            data = [
                {'key': true, 'value': Unidata.i18n.t('common:yes').toLowerCase()},
                {'key': false, 'value': Unidata.i18n.t('common:no').toLowerCase()}
            ];
        } else {
            data = [
                {'key': false, 'value': Unidata.i18n.t('common:yes').toLowerCase()},
                {'key': true, 'value': Unidata.i18n.t('common:no').toLowerCase()}
            ];
        }

        store.data = data;

        this.setStore(store);
    },

    isInverted: function () {
        return this.hasOwnProperty('inverted') && this.inverted === true;
    },

    valueField: 'key',
    displayField: 'value',
    forceSelection: true,
    editable: false,
    allowBlank: true
});
