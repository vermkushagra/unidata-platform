/**
 * @author Aleksandr Bavin
 * @date 2016-08-11
 */
Ext.define('Unidata.view.workflow.task.items.TaskProperty', {
    extend: 'Ext.container.Container',

    alias: 'widget.workflow.task.property',

    baseCls: 'un-workflow-task-property',

    config: {
        title: null,
        value: null,
        titleCls: null,
        valueCls: null
    },

    titleItem: null,
    valueItem: null,

    items: [],
    subItems: [],

    initItems: function () {
        var titleCls = [],
            valueCls = [];

        this.callParent(arguments);

        titleCls.push(this.baseCls + '-title');
        valueCls.push(this.baseCls + '-value');

        if (this.getTitleCls()) {
            titleCls.push(this.getTitleCls());
        }

        if (this.getValueCls()) {
            valueCls.push(this.getValueCls());
        }

        this.titleItem = this.add(this.initTitle()).addCls(titleCls);
        this.valueItem = this.add(this.initValue()).addCls(valueCls);

        this.add(this.subItems);
    },

    updateTitle: function (value) {
        if (!this.titleItem) {
            return;
        }

        this.titleItem.update(value);
    },

    applyValue: function (value) {
        if (Ext.isObject(value)) {
            if (value.data) {
                Ext.Object.each(value.data, function (key, item) {
                    this[key] = Ext.String.htmlEncode(item);
                });
            }
        } else {
            value = Ext.String.htmlEncode(value);
        }

        return value;
    },

    updateValue: function (value) {
        if (!this.valueItem) {
            return;
        }

        this.valueItem.update(value ? value : '&nbsp;');
    },

    initTitle: function () {
        return this.initItem(this.getTitle());
    },

    initValue: function () {
        return this.initItem(this.getValue());
    },

    initItem: function (item) {
        if (Ext.isObject(item)) {
            return item;
        } else {
            return {
                xtype: 'container',
                html: item
            };
        }
    }

});
