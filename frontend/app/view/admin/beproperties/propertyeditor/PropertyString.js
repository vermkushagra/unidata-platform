/**
 * Класс представления backend property строкового типа
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyString', {
    extend: 'Unidata.view.admin.beproperties.propertyeditor.PropertyBase',

    alias: 'widget.admin.beproperties.propertystring',

    requires: [
        'Unidata.view.admin.beproperties.propertyeditor.PropertyBase'
    ],

    items: [
        {
            xtype: 'textfield',
            reference: 'input',
            readOnlyCls: 'opacity_5'
        }
    ]
});
