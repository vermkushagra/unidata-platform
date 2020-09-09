/**
 * Миксин для предоставления возможности создания тултипов для компонента
 * @author Sergey Shishigin
 * @date 2016-09-02
 */
Ext.define('Unidata.mixin.Tooltipable', {
    extend: 'Ext.Mixin',

    tooltipText: '',

    /**
     * Задаёт тултип для компонента
     *
     * @param tooltipText
     * @returns {Unidata.view.component}
     */
    setTooltipText: function (tooltipText) {

        var me = this;

        me.tooltipText = tooltipText;

        if (!me.rendered) {
            return me;
        }

        if (!tooltipText) {

            if (me.tooltip) {
                me.removeTooltip();
            }

            return me;

        }

        if (!me.tooltip) {
            me.tooltip = Ext.widget({
                xtype: 'tooltip',
                target: me.el,
                html: tooltipText
            });
        } else {
            me.tooltip.update(tooltipText);
        }

        return me;
    },

    getTooltipText: function () {
        return this.tooltipText;
    },

    /**
     * Удаляет тултип
     */
    removeTooltip: function () {

        var tooltip = this.tooltip;

        if (tooltip && tooltip instanceof Ext.Component) {
            tooltip.destroy();
            delete this.tooltip;
        }

        return this;
    }
});
