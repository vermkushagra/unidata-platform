/**
 * @author Aleksandr Bavin
 * @date 2017-01-20
 */
Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayAttributeCompare', {

    extend: 'Unidata.view.steward.dataentity.attribute.array.ArrayAttributeRead',

    alias: 'widget.arrayattribute.compare',

    config: {
        tagLimit: 2
    },

    cls: 'un-array-attribute-read-compare un-array-attribute-read',

    allValuesWindow: null,

    onDestroy: function () {
        if (this.allValuesWindow) {
            this.allValuesWindow.hide();
        }

        this.callParent(arguments);
    },

    onFakeTagClick: function () {
        var metaAttribute = this.getMetaAttribute();

        if (this.allValuesWindow) {
            this.allValuesWindow.hide();
        }

        this.allValuesWindow = Ext.create('Ext.window.Window', {
            width: 400,
            layout: 'fit',
            title: metaAttribute.get('displayName'),
            items: {
                xtype: 'arrayattribute.read',
                value: this.getValue(),
                metaRecord: this.getMetaRecord(),
                dataRecord: this.getDataRecord(),
                metaAttribute: metaAttribute,
                dataAttribute: this.getDataAttribute(),
                readOnly: this.getReadOnly(),
                preventMarkField: this.getPreventMarkField()
            }
        });

        this.allValuesWindow.showBy(this.getEl());
    }

});
