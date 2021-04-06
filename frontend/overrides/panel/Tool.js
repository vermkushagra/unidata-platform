/**
 * Премер использования с иконкой fontawesome
 *
 * {
 *    type: 'search'
 * }
 *
 * Премер использования с иконкой linearicons
 *
 * {
 *    type: 'search',
 *    fontCls: 'la'
 * }
 *
 * @author Igor Redkin
 * @date 2015-07-13
 */

Ext.define('Ext.overrides.panel.Tool', {
    override: 'Ext.panel.Tool',

    fontCls: 'fa',

    renderTpl: [
        '<i role="presentation" data-ref="toolEl" id="{id}-toolEl" class="{fontCls} {baseCls}-img {baseCls}-{type}{childElCls}"></i>'
    ],

    initRenderData: function () {
        var data,
            defaultData;

        data = this.callParent(arguments);

        defaultData = {
            fontCls: this.fontCls
        };

        data = Ext.apply(defaultData, data);

        return data;
    }
});
