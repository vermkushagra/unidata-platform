/**
 * Простой тултип
 *
 * @author Aleksandr Bavin
 * @date 2017-05-22
 *
 * @property elBody
 * @property elArrow
 */
Ext.define('Unidata.view.component.tooltip.Tooltip', {

    extend: 'Unidata.view.component.AbstractComponent',

    alias: 'widget.un.tooltip',

    baseCls: 'un-tooltip',

    childEls: [
        {
            itemId: 'body',
            name: 'elBody'
        },
        {
            itemId: 'arrow',
            name: 'elArrow'
        }
    ],

    tpl: [
        '{text:htmlEncode}'
    ],

    shadow: false,

    config: {
        text: null,
        anchorTo: 'bottom', // top, right, bottom, left
        arrow: false,
        arrowWidth: 15, // ширина основание треугольника
        arrowHeight: 8 // высота треугольника
    },

    renderTpl: [
        '<div class="{baseCls}-body" id="{id}-body" data-ref="body">',
        '{% this.renderContent(out, values) %}',
        '</div>',
        '<div class="{baseCls}-arrow" id="{id}-arrow" data-ref="arrow"></div>'
    ],

    floating: true,

    privates: {
        getTargetEl: function () {
            return this.elBody;
        }
    },

    onComponentRender: function () {
        this.updateArrowStyle();
    },

    updateText: function (text) {
        this.setTplValue('text', text);
    },

    updateArrowSize: function () {
        this.updateArrowStyle();
    },

    updateAnchorTo: function () {
        this.updateArrowStyle();
    },

    updateArrowStyle: function () {
        // top, right, bottom, left
        var colors = ['transparent', 'transparent', 'transparent', 'transparent'],
            format = '{0}px {1}px {2}px {3}px',
            arrowWidth,
            arrowHeight,
            borderWidth,
            anchorTo;

        if (!this.getArrow()) {
            return;
        }

        if (!this.rendered) {
            return;
        }

        anchorTo = this.getAnchorTo();

        arrowWidth = this.getArrowWidth();
        arrowHeight = this.getArrowHeight();

        switch (anchorTo) {
            case 'top':
                colors[0] = 'inherit';
                borderWidth = Ext.String.format(format, arrowHeight, arrowWidth / 2, 0, arrowWidth / 2);
                break;
            case 'right':
                colors[1] = 'inherit';
                borderWidth = Ext.String.format(format, arrowWidth / 2, arrowHeight, arrowWidth / 2, 0);
                break;
            case 'bottom':
                colors[2] = 'inherit';
                borderWidth = Ext.String.format(format, 0, arrowWidth / 2, arrowHeight, arrowWidth / 2);
                break;
            case 'left':
                colors[3] = 'inherit';
                borderWidth = Ext.String.format(format, arrowWidth / 2, 0, arrowWidth / 2, arrowHeight);
                break;
        }

        this.elArrow.setStyle({
            'border-top-color': colors[0],
            'border-right-color': colors[1],
            'border-bottom-color': colors[2],
            'border-left-color': colors[3],
            'border-width': borderWidth
        });
    },

    showOver: function (el) {
        var anchorTo = this.getAnchorTo(),
            pos = '-' + anchorTo.charAt(0),
            arrowHeight = this.getArrowHeight(),
            arrow = this.getArrow(),
            arrowOffset,
            offset;

        switch (anchorTo) {
            case 'top':
                pos = 'b' + pos;
                offset = [0, -arrowHeight];
                break;
            case 'right':
                pos = 'l' + pos;
                offset = [arrowHeight, 0];
                break;
            case 'bottom':
                pos = 't' + pos;
                offset = [0, arrowHeight];
                break;
            case 'left':
                pos = 'r' + pos;
                offset = [-arrowHeight, 0];
                break;
        }

        switch (anchorTo) {
            case 'top':
                arrowOffset = [0, -1];
                break;
            case 'bottom':
                arrowOffset = [0, 1];
                break;
            case 'right':
                arrowOffset = [1, 0];
                break;
            case 'left':
                arrowOffset = [-1, 0];
                break;
        }

        if (!arrow) {
            offset = [0, 0];
        }

        // позиционируем тултип
        this.showBy(el, pos, offset);

        // позиционируем стрелку
        if (arrow) {
            this.elArrow.alignTo(el, pos, arrowOffset);
        }
    },

    showTooltip: function (targetEl, text) {
        if (text) {
            this.updateText(text);
        }
        this.showOver(targetEl);
    }

});
