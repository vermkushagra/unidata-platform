/**
 * Компонент отображения warning message
 * @author: Denis Makarov
 * @date 2018-04-18
 */
Ext.define('Unidata.view.component.WarningMessage', {
    extend: 'Ext.Component',

    alias: 'widget.warning-message',

    baseCls: 'un-warning-card',

    iconHtml: null,

    text: null,

    textPadding: '8',

    tpl: [
        '<div class="un-warning-card-icon">' +
        '{iconHtml}' +
        '</div>' +
        '<div class="un-warning-card-text">' +
        '<span>{text}</span>' +
        '</div>'
    ],

    constructor: function () {
        this.callParent(arguments);

        this.on('render', this.onComponentRender, this, {single: true});
    },

    onComponentRender: function () {
        this.updateView();
    },

    updateDomRef: function () {
        var el = this.getEl();

        this.textDiv = el.down('.un-warning-card-text');
    },

    updateView: function () {
        if (!this.rendered) {
            return;
        }

        this.update({
            iconHtml: this.iconHtml,
            text: this.text
        });

        this.updateDomRef();
        this.setTextPadding();

        this.updateLayout();
    },

    setTextPadding: function () {
        if (this.textPadding) {
            this.textDiv.setPadding(this.textPadding);
        }
    },

    setText: function (text) {
        this.text = text;

        this.updateView();
    },

    getText: function () {
        return this.text;
    },

    setIconHtml: function (iconHtml) {
        this.iconHtml = iconHtml;

        this.updateView();
    },

    getIconHtml: function () {
        return this.iconHtml;
    }
});

