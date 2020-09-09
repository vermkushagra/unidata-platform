/**
 * Реализует панельку, для отображения свойств пользователя
 *
 * @date 2015-11-24
 * @author Mаrshalkin Ivan
 */

Ext.define('Unidata.view.component.user.Properties', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.user.properties',

    requires: [
        'Unidata.view.component.user.PropertyItem'
    ],

    referenceHolder: true,

    collapsible: true,

    cls: 'un-userprop-panel',
    ui: 'un-card',

    publishes: {
        readOnly: false
    },

    config: {
        propertyContainer: null
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: '',

    items: [
        {
            xtype: 'container',
            propertyItems: null,
            reference: 'propertyContainer'
        }
    ],

    initComponent: function () {
        var propertyContainer;

        this.callParent(arguments);

        propertyContainer = this.lookupReference('propertyContainer');
        this.setPropertyContainer(propertyContainer);
        propertyContainer.propertyItems = [];
    },

    getAllPropertyItems: function () {
        var propertyContainer = this.getPropertyContainer();

        return propertyContainer.propertyItems;
    },

    addPropertyItem: function (name, displayName, value, id) {
        var item,
            itemCfg,
            propertyContainer = this.getPropertyContainer();

        itemCfg = {
            fieldId: id,
            fieldName: name,
            fieldDisplayName: displayName,
            fieldValue: value,
            margin: '5 0'
        };
        item = Ext.create('Unidata.view.component.user.PropertyItem', itemCfg);

        item.on('change', this.onPropertyItemChange, this);

        propertyContainer.add(item);
        propertyContainer.propertyItems.push(item);
    },

    removePropertyItem: function (item) {
        var propertyContainer = this.getPropertyContainer();

        Ext.Array.remove(propertyContainer.propertyItems, item);
        propertyContainer.remove(item);
    },

    removeAllPropertyItem: function () {
        var me = this,
            propertyItems = Ext.Array.clone(this.getAllPropertyItems());

        Ext.Array.each(propertyItems, function (item) {
            me.removePropertyItem(item);
        });
    },

    setReadOnlyPropertyItem: function (readOnly, item) {
        item.setReadOnly(readOnly);
    },

    setReadOnly: function (readOnly) {
        var me = this,
            propertyItems = this.getAllPropertyItems();

        Ext.Array.each(propertyItems, function (item) {
            me.setReadOnlyPropertyItem(readOnly, item);
        });
    },

    getValues: function () {
        var values = [],
            propertyItems = this.getAllPropertyItems();

        Ext.Array.each(propertyItems, function (item) {
            values.push(item.getValue());
        });

        return values;
    },

    onPropertyItemChange: function (fieldName, fieldDisplayName, fieldValue) {
        this.fireEvent('propertychange', fieldName, fieldDisplayName, fieldValue);
    }
});
