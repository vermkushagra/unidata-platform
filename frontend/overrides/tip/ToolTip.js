/**
 * Тултипы на windows 10 + chrome не адекватно себя ведут. Отображаются только после клика на элементе.
 *
 * подробнее
 * https://www.sencha.com/forum/showthread.php?294714-5-0-0-736-Tooltip-Not-Shown-Until-Clicked-Chrome/page2
 * https://www.sencha.com/forum/showthread.php?301258
 *
 * @author Ivan Marshalkin
 * @date 2017-08-08
 */
Ext.define('Unidata.overrides.tip.ToolTip', {
    override: 'Ext.tip.ToolTip',

    compatibility: '5.1.0.107',

    setTarget: function (target) {
        var me = this,
            t = Ext.get(target),
            tg;

        if (me.target) {
            tg = Ext.get(me.target);

            if (Ext.supports.Touch) {
                me.mun(tg, 'tap', me.onTargetOver, me);
            }

            // It was inside "if (Ext.supports.Touch) {} else { here }"
            me.mun(tg, {
                mouseover: me.onTargetOver,
                mouseout: me.onTargetOut,
                mousemove: me.onMouseMove,
                scope: me
            });
        }

        me.target = t;

        if (t) {
            if (Ext.supports.Touch) {
                me.mon(t, {
                    tap: me.onTargetOver,
                    scope: me
                });
            }

            // It was inside "if (Ext.supports.Touch) { } else { here }"
            me.mon(t, {
                mouseover: me.onTargetOver,
                mouseout: me.onTargetOut,
                mousemove: me.onMouseMove,
                scope: me
            });
        }

        if (me.anchor) {
            me.anchorTarget = me.target;
        }
    }
});
