/**
 * Счетчик (текст + цифра)
 *
 * @author Sergey Shishigin
 * @date 2017-08-05
 */
Ext.define('Unidata.view.component.Counter', {
    extend: 'Ext.form.field.Display',

    alias: 'widget.component.counter',

    baseCls: 'un-counter',

    handler: null,

    initComponent: function () {
        this.callParent(arguments);
        //this.updateCounterView();
    },

    initHandler: function () {
        if (this.rendered) {
            this.bindClickHandler();
        } else {
            this.on('render', this.bindClickHandler , this, {single: true});
        }
    },

    bindClickHandler: function () {
        var handler = this.handler,
            el = this.getEl();

        el.down('.un-counter-count').on('click', function () {
            if (Ext.isObject(handler)) {
                handler.fn.call(handler.scope);
            } else {
                handler.call();
            }
        }, this);
    },

    getValue: function () {
        return Ext.Number.from(this.value, 0);
    },

    updateCounterView: function () {
        var counter = this.getValue(),
            zeroCls = this.baseCls + '-zero';

        if (counter) {
            this.removeCls(zeroCls);
        } else {
            this.addCls(zeroCls);
        }
    },

    onRender: function () {
        this.callParent(arguments);
        this.updateCounterView();
    },

    setValue: function (value) {
        arguments[0] = Math.floor(Ext.Number.from(value, 0)).toString();
        this.callParent(arguments);

        if (this.rendered) {
            this.updateCounterView();
        }
        this.initHandler();
    },

    renderer: function (value) {
        return '<span class="un-counter-count">' + value + '</span>';
    }
});
