/**
 * Миксин для предоставления возможности создания тултипов в header панели
 * @author Sergey Shishigin
 * @date 2018-06-07
 */
Ext.define('Unidata.mixin.HeaderTooltipable', {
    extend: 'Ext.Mixin',

    baseTooltip: null,

    initTitleTooltip: function () {
        var me          = this,
            header       = me.getHeader(),
            baseTooltip,
            toolTip;

        if (!header) {
            return;
        }

        if (header.tip !== undefined) {
            return;
        }

        baseTooltip = me.buildBaseToolTip();

        if (!baseTooltip) {
            return;
        }

        toolTip = Ext.create('Ext.tip.ToolTip', {
            target: header.getEl(),
            html: '',
            dismissDelay: 8000
        });

        me.baseTooltip = baseTooltip;
        header.tip = toolTip;

        header.getEl().on('mouseenter', this.onInputMouseEnterTip, this);
    },

    /**
     * Обработчик наведения на поле ввода
     * Производит настройку содержимого тултипа
     */
    onInputMouseEnterTip: function () {
        var me    = this,
            header = me.getHeader(),
            valueHtml = this.getHeaderTooltip(),
            html;

        html = Ext.String.format(me.baseTooltip, valueHtml);

        header.tip.setHtml(html);
    }
});
