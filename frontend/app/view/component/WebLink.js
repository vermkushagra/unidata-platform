Ext.define('Unidata.view.component.WebLink', {
    extend: 'Ext.Component',

    xtype: 'weblink',

    cls: 'un-weblink',

    tpl: '<a href="{link:htmlEncode}" target="{target}">{text:htmlEncode}</a>',

    config: {
        value: '#', // link alias
        link: '#',
        target: '_blank',
        text: null,

        handler: null, // обработчик при клике
        scope: null    // scope обработчика
    },

    onRender: function () {
        this.callParent(arguments);
        this.getEl().on('click', this.onLinkClick, this, {delegate: 'a'});
    },

    onLinkClick: function () {
        var handler = this.getHandler(),
            scope = this.getScope() || this;

        if (handler) {
            handler.apply(scope, arguments);
        }
    },

    setValue: function () {
        return this.setLink.apply(this, arguments);
    },

    getValue: function () {
        return this.getLink.apply(this, arguments);
    },

    updateLink: function () {
        this.updateTplData();
    },

    updateTarget: function () {
        this.updateTplData();
    },

    updateText: function () {
        this.updateTplData();
    },

    updateTplData: function () {
        var link = this.getLink(),
            target = this.getTarget(),
            text = this.getText() || link;

        this.update({
            link: link,
            target: target,
            text: text
        });
    }
});
