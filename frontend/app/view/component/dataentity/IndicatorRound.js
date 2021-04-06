/**
 *
 * Компонент для DataEntity
 *
 * @author Ivan Marshalkin
 * @date 2016-01-26
 */

// Пример использования:
//
// var indicator = Ext.create('Unidata.view.component.dataentity.IndicatorRound', {
//     floating: true,
//     radius: 5,
//     color: 'gray',
//     active: true
// });
//
// indicator.showAt(100, 100);

Ext.define('Unidata.view.component.dataentity.IndicatorRound', {

    extend: 'Ext.Component',

    mixins: [
        'Unidata.mixin.Tooltipable'
    ],

    config: {
        radius: 2.5,
        /**
         * Цвет индикатора
         *
         * Возможные значения: white, gray, green, yellow, red, blue
         */
        color: 'white',
        active: false
    },

    autoEl: 'span',
    componentCls: 'un-ndraw-indicatorround',
    cls: '',

    initComponent: function () {

        this.callParent(arguments);

        this.CLASS_ACTIVE = this.componentCls + '__active';
    },

    setRadius: function (radius) {

        var diameter = 2 * radius;

        this.radius = radius;

        this.setHeight(diameter);
        this.setWidth(diameter);

        this.setMaxHeight(diameter);
        this.setMaxWidth(diameter);

        this.setSize(diameter, diameter);

    },

    setActive: function (active) {

        var me = this,
            el = me.el,
            CLASS_ACTIVE = me.CLASS_ACTIVE;

        me.active = active;

        if (!me.rendered) {
            return me;
        }

        if (active) {
            el.addCls(CLASS_ACTIVE);
        } else {
            el.removeCls(CLASS_ACTIVE);
        }

        return me;
    },

    onRender: function () {

        var me = this,
            el;

        me.callParent(arguments);

        el = me.el;

        el.addCls(me.componentCls + '__' + me.color);

        el.on('click', me.onIndicatorClick, me);

        me
            .setTooltipText(me.tooltipText)
            .setActive(me.active);
    },

    onIndicatorClick: function () {

        if (this.fireEventArgs('beforeclick', arguments) === false) {
            return;
        }

        this.fireEventArgs('click', arguments);

    }
});
