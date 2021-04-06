/**
 * Класс представления backend property целого типа
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyInteger', {
    extend: 'Unidata.view.admin.beproperties.propertyeditor.PropertyBase',

    alias: 'widget.admin.beproperties.propertyinteger',

    requires: [
        'Unidata.view.admin.beproperties.propertyeditor.PropertyBase'
    ],

    items: [
        {
            xtype: 'numberfield',
            reference: 'input',
            allowDecimals: false,
            readOnlyCls: 'opacity_5'
        }
    ]
});
