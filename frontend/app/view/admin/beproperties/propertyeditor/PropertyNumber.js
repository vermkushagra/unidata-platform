/**
 * Класс представления backend property численного типа
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyNumber', {
    extend: 'Unidata.view.admin.beproperties.propertyeditor.PropertyBase',

    alias: 'widget.admin.beproperties.propertynumber',

    requires: [
        'Unidata.view.admin.beproperties.propertyeditor.PropertyBase'
    ],

    items: [
        {
            xtype: 'numberfield',
            reference: 'input',
            allowDecimals: true,
            readOnlyCls: 'opacity_5'
        }
    ]
});
