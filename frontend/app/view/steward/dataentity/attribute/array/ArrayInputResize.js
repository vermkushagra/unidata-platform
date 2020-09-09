/**
 * @author Aleksandr Bavin
 * @date 2017-01-24
 */
Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayInputResize', {

    extend: 'Ext.container.Container',

    alias: 'widget.arrayinputresize',

    cls: 'un-array-attribute-input-resize',

    referenceHolder: true,

    config: {
        count: 1,
        expandCount: 5,
        shrinkCount: 5,
        minCount: 1,
        maxCount: 10
    },

    margin: '5 10 0 10',
    layout: {
        type: 'hbox'
    },
    items: [],

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'textfield',
                reference: 'countInput',
                fieldLabel: Unidata.i18n.t('dataentity>itemsCount'),
                readOnly: true,
                labelWidth: 150,
                width: 190,
                value: this.getCount(),
                listeners: {
                    change: this.onCountInputChange,
                    scope: this
                }
            },
            {
                xtype: 'segmentedbutton',
                items: [
                    {
                        text: '+',
                        reference: 'expandButton',
                        listeners: {
                            click: this.expand,
                            scope: this
                        }
                    },
                    {
                        text: '-',
                        reference: 'shrinkButton',
                        listeners: {
                            click: this.shrink,
                            scope: this
                        }
                    }
                ]
            }
        ]);
    },

    onRender: function () {
        this.updateButtons(this.getCount());

        return this.callParent(arguments);
    },

    updateButtons: function (value) {
        var minCount = this.getMinCount(),
            maxCount = this.getMaxCount(),
            expandButton = this.lookupReference('expandButton'),
            shrinkButton = this.lookupReference('shrinkButton');

        if (shrinkButton) {
            shrinkButton.setDisabled(value <= minCount);
        }

        if (expandButton) {
            expandButton.setDisabled(value >= maxCount);
        }
    },

    onCountInputChange: function (input, newValue) {
        this.updateButtons(newValue);
    },

    setCount: function (value) {
        var minCount = this.getMinCount(),
            maxCount = this.getMaxCount();

        if (value < minCount) {
            value = minCount;
        }

        if (value > maxCount) {
            value = maxCount;
        }

        this.fireEvent('update', value);

        this.callParent([value]);
    },

    updateCount: function (value) {
        var countInput = this.lookupReference('countInput');

        if (countInput) {
            countInput.setValue(value);
        }
    },

    expand: function () {
        this.setCount(this.getCount() + this.getExpandCount());
    },

    shrink: function () {
        this.setCount(this.getCount() - this.getShrinkCount());
    }

});
