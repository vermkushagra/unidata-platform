/**
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertygroup.PropertyGroupController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.beproperties.group',

    init: function () {
        var view = this.getView();

        this.callParent(arguments);

        this.updateProperties(view.properties);
        this.setReadOnlyComponentState(view.getReadOnly());
    },

    /**
     * Обработчик обновления списка свойств
     *
     * @param properties
     */
    updateProperties: function (properties) {
        var view = this.getView(),
            items = [],
            item;

        if (!Ext.isArray(properties)) {
            return;
        }

        view.removeAll();

        Ext.Array.each(properties, function (property) {
            item = Ext.create('Unidata.view.admin.beproperties.propertyeditor.PropertyEditor', {
                property: property
            });

            items.push(item);
        });

        view.add(items);
    },

    updateReadOnly: function (readOnly) {
        this.setReadOnlyComponentState(readOnly);
    },

    setReadOnlyComponentState: function (readOnly) {
        var view = this.getView(),
            items = view.items;

        if (!items.isMixedCollection) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    }
});
