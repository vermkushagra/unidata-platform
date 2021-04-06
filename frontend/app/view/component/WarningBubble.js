/**
 * Компонент отображения warning bubble
 * @author: Sergey Shishigin
 * @date 2017-12-07
 */
Ext.define('Unidata.view.component.WarningBubble', {
    extend: 'Ext.Component',

    xtype: 'un.warningbubble',

    config: {
        iconName: 'icon-warning',
        text: ''
    },

    baseCls: 'un-warningbubble',

    initComponent: function () {
        var html;

        this.callParent(arguments);
        html = this.buildHtml();
        this.setHtml(html);
    },

    getIconCls: function () {
        return this.baseCls + '-icon';
    },

    getTextCls: function () {
        return this.baseCls + '-text';
    },

    buildHtml: function () {
        var htmlTpl,
            html,
            iconCls,
            iconName,
            textCls,
            text;

        iconCls = this.getIconCls();
        iconName = this.getIconName();
        text = this.getText();
        textCls = this.getTextCls();

        htmlTpl = '<div class="{0}"><span class="{1}">&nbsp;</span></span></div><div class="{2}">{3}</div>';
        html = Ext.String.format(htmlTpl, iconCls, iconName, textCls, text);

        return html;
    }
});
