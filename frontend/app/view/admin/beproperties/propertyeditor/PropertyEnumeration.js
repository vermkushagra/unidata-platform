/**
 * Класс представления backend property типа перечисления
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyEnumeration', {
    extend: 'Unidata.view.admin.beproperties.propertyeditor.PropertyBase',

    alias: 'widget.admin.beproperties.propertyenumeration',

    requires: [
        'Unidata.view.admin.beproperties.propertyeditor.PropertyBase'
    ],

    items: [
        {
            xtype: 'combobox',
            reference: 'input',
            readOnlyCls: 'opacity_5',
            editable: false,
            displayField: 'displayValue',
            valueField: 'value',
            queryMode: 'local',
            store: {
                fields: [
                    'value',
                    'displayValue'
                ],
                data: []
            }
        }
    ],

    syncInput: function () {
        var property = this.getProperty(),
            data = [],
            meta,
            store;

        if (!property) {
            return;
        }

        store = this.input.getStore();
        meta = property.get('meta');

        if (meta && meta.availableValues) {
            data = Ext.clone(meta.availableValues);
        }

        store.getProxy().setData(data);

        store.load();

        this.callParent(arguments);
    }
});
