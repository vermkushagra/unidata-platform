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
        value: null
    },

    titleItem: null,
    valueItem: null,

    items: [],

    initItems: function () {
        this.callParent(arguments);

        this.titleItem = this.add(this.initTitle()).addCls(this.baseCls + '-title');
        this.valueItem = this.add(this.initValue()).addCls(this.baseCls + '-value');
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
