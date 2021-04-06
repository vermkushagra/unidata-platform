Ext.define('Unidata.view.component.HtmlComboBox', {
    extend: 'Ext.form.field.ComboBox',

    alias: 'widget.un.htmlcombo',

    editable: false,

    fieldSubTpl: [
        '<div id="{id}" type="{type}" ',
            '<tpl if="size">size="{size}" </tpl>',
            '<tpl if="tabIdx">tabIndex="{tabIdx}" </tpl>',
            'class="{fieldCls} {typeCls} un-htmlcombo-value" autocomplete="off">{value}</div>',
            '<div class="un-htmlcombo-placeholder">{placeholder}</div>',
        {
            compiled: true,
            disableFormats: true
        }
    ],

    cls: 'un-htmlcombo',

    getRawValue: function () {
        return this.rawValue;
    },

    applyEmptyText: function () {
        var me = this,
            emptyText = me.emptyText,
            isEmpty;

        if (me.rendered && emptyText) {
            isEmpty = me.getRawValue().length < 1 && !me.hasFocus;

            if (Ext.supports.Placeholder) {
                me.inputEl.dom.placeholder = emptyText;
            } else if (isEmpty) {
                me.setRawValue(emptyText);
                me.valueContainsPlaceholder = true;
            }

            //all browsers need this because of a styling issue with chrome + placeholders.
            //the text isnt vertically aligned when empty (and using the placeholder)
            if (isEmpty) {
                me.inputEl.addCls(me.emptyUICls);
            } else {
                me.inputEl.removeCls(me.emptyUICls);
            }

            me.autoSize();
        }
    },

    setRawValue: function (value) {
        var me = this;

        value = value || '';

        me.rawValue = value;

        if (me.inputEl) {
            me.inputEl.dom.innerHTML = value;
        }

        return value;
    }

});
