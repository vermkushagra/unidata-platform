/**
 * @author Aleksandr Bavin
 * @date 2017-05-04
 *
 * @property elBody
 * @property elText
 * @property elCounter
 * @property elPin
 */
Ext.define('Unidata.view.main.menu.MainMenuItem', {

    extend: 'Unidata.view.component.list.ListItem',

    alias: 'widget.un.list.item.mainmenu.item.default',

    baseCls: 'un-mainmenu-item',

    childEls: [
        {
            itemId: 'body',
            name: 'elBody'
        },
        {
            itemId: 'text',
            name: 'elText'
        },
        {
            itemId: 'counter',
            name: 'elCounter'
        },
        {
            itemId: 'pin',
            name: 'elPin'
        },
        {
            itemId: 'icon-wrap',
            name: 'elIconWrap'
        },
        {
            itemId: 'icon',
            name: 'elIcon'
        }
    ],

    config: {
        view: null, // xtype компонента, который открывается по клику
        counter: 0,
        colorIndex: 0,
        iconCls: null,
        counterUi: null
    },

    iconTpl: [
        '<span class="{iconCls}" id="{id}-icon" data-ref="icon"></span>'
    ],

    targetEl: 'elText',

    renderTpl: [
        '<div class="{baseCls}-body {baseCls}-color-{colorIndex}" id="{id}-body" data-ref="body">',
            '<div class="{baseCls}-icon" id="{id}-icon-wrap" data-ref="icon-wrap">',
                '{% this.renderIcon(out, values) %}',
                '<div style="display: none;" class="{baseCls}-counter<tpl if="counterUi">-{counterUi}</tpl>" id="{id}-counter" data-ref="counter"></div>',
             '</div>',
            '<div class="{baseCls}-text" id="{id}-text" data-ref="text">{% this.renderContent(out, values) %}</div>',
        '</div>',
        '<div class="{baseCls}-pin" id="{id}-pin" data-ref="pin" title="' + Unidata.i18n.t('menu>attach') + '"><span></span></div>'
    ],

    privates: {
        setupRenderTpl: function (renderTpl) {
            var counterUi      = this.counterUi;

            this.callParent(arguments);

            renderTpl.renderIcon = this.renderIcon;
            renderTpl.counterUi = counterUi;
        }
    },

    renderIcon: function (out, values) {
        var menuItem = values.$comp,
            iconTpl = menuItem.getTpl('iconTpl');

        out.push(iconTpl.apply(values));
    },

    updateCounter: function () {
        if (this.rendered) {
            this.updateCounterView();
        }
    },

    updateCounterView: function () {
        var counter = this.getCounter();

        if (counter) {
            this.elCounter.setHtml(counter);
            this.elCounter.show();
        } else {
            this.elCounter.hide();
        }
    },

    updateSelected: function (selected) {
        var view,
            reference;

        this.callParent(arguments);

        if (selected && (view = this.getView())) {
            // в качестве ключа кэша экрана (cacheKey) используем reference кнопки меню
            reference = this.getReference();
            Unidata.module.MainViewManager.showComponent(view, reference);
        }
    },

    updateColorIndex: function (colorIndex) {
        this.setTplValue('colorIndex', colorIndex);
    },

    updateCounterUi: function (counterUi) {
        this.setTplValue('counterUi', counterUi);
    },

    updateIconCls: function (iconCls, oldIconCls) {
        this.setTplValue('iconCls', iconCls);

        if (this.elIcon) {
            this.elIcon.removeCls(oldIconCls);
            this.elIcon.addCls(iconCls);
        }
    },

    onComponentRender: function () {
        this.callParent(arguments);

        this.elBody.on('click', this.onClick, this);
        this.initToolTip();

        this.updateCounterView();
    },

    initToolTip: function () {
        this.elBody.dom.addEventListener('mouseenter', this.onElBodyMouseEnter.bind(this));
        this.elBody.dom.addEventListener('mouseleave', this.onElBodyMouseLeave.bind(this));

        this.elBody.on('click', this.hideTooltip, this);
    },

    elTextVisible: function () {
        var elTextDom;

        if (!this.elText) {
            return false;
        }

        elTextDom = this.elText.dom;

        // если текст умещается то тултип не отображаем
        if (elTextDom.offsetWidth >= elTextDom.scrollWidth) {
            return true;
        }

        return false;
    },

    onElBodyMouseEnter: function (event) {
        var elTextDom = this.elText.dom,
            tooltipCfg;

        tooltipCfg = {
            text: elTextDom.textContent
        };

        // если текст умещается то тултип не отображаем
        if (this.elTextVisible()) {
            return false;
        }

        this.showTooltipDelayed(event, this.elBody, tooltipCfg);
    },

    onElBodyMouseLeave: function (event) {
        this.hideTooltip();
    },

    initClickEvent: function () {
        this.elBody.on('click', function () {
            this.fireItemClick(this, this);
        }, this);
    },

    showTooltipDelayed: function (event, component, tooltipCfg) {
        clearTimeout(this.tipShowTimer);
        this.tipShowTimer = Ext.defer(this.showTooltip, 500, this, [event, component, tooltipCfg]);
    },

    showTooltip: function (event, component, tooltipCfg) {
        var tooltipText;

        tooltipCfg = tooltipCfg || {};
        tooltipText = tooltipCfg.text || '';

        Unidata.view.main.menu.MainMenu.showTooltip(tooltipText, component);
    },

    hideTooltip: function () {
        clearTimeout(this.tipShowTimer);
        Unidata.view.main.menu.MainMenu.hideTooltip();
    },

    onClick: function (event) {
        event.stopEvent();
    }
});
