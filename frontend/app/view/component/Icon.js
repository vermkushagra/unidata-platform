/**
 * Иконка
 *
 * @author Sergey Shishigin
 * @date 2017-08-05
 */
Ext.define('Unidata.view.component.Icon', {
    extend: 'Unidata.view.component.AbstractComponent',

    alias: 'widget.component.icon',

    baseCls: 'un-icon',

    config: {
        iconCls: null
    },

    childEls: [
        {
            itemId: 'body',
            name: 'elBody'
        }
    ],

    iconTpl: [
        '<span class="{iconCls}"></span>'
    ],

    renderTpl: [
        '<div class="{baseCls}-body" id="{id}-body" data-ref="body">',
            '<div class="{baseCls}-icon">{% this.renderIcon(out, values) %}</div>',
        '</div>'
    ],

    privates: {
        setupRenderTpl: function (renderTpl) {
            this.callParent(arguments);

            renderTpl.renderIcon = this.renderIcon;
        }
    },

    renderIcon: function (out, values) {
        var menuItem = values.$comp,
            iconTpl = menuItem.getTpl('iconTpl');

        out.push(iconTpl.apply(values));
    },

    updateIconCls: function (iconCls) {
        this.setTplValue('iconCls', iconCls);
    }
});
