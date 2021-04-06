/**
 * Класс представления backend property неподерживаемого типа
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyUnknown', {
    extend: 'Unidata.view.admin.beproperties.propertyeditor.PropertyBase',

    alias: 'widget.admin.beproperties.propertyunknown',

    requires: [
        'Unidata.view.admin.beproperties.propertyeditor.PropertyBase'
    ],

    items: [
        {
            xtype: 'label',
            reference: 'input',
            text: Unidata.i18n.t('backendProperties>unsupportedPropertyType')
        }
    ],

    syncInput: function () {
        return;
    },

    setReadOnlyComponentState: function () {
        return;
    }
});
