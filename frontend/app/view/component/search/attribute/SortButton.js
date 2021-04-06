/**
 * Кнопка включения поля в сортировку
 *
 * @author Ivan Marshalkin
 * @date 2016-04-06
 */

Ext.define('Unidata.view.component.search.attribute.SortButton', {
    extend: 'Ext.Component',

    alias: 'widget.component.search.attribute.sortbutton',

    margin: '0 5',
    html: '<i class="" style="cursor: pointer; vertical-align: middle;"></i>',

    iconEl: null,        // элемент содержащий иконку
    tip: null,           // всплывашка с текстом подсказкой к элементу

    includeSortCls: 'icon-sort-amount-asc',
    excludeSortCls: 'icon-cross2',
    included: false,

    initComponent: function () {
        this.callParent(arguments);

        this.on('render', this.onComponentRender, this, {single: true});
    },

    onComponentRender: function () {
        var me     = this,
            el     = me.getEl(),
            iconEl = el.down('i');

        this.iconEl = iconEl;
        this.tip = Ext.create('Ext.tip.ToolTip', {
            target: iconEl,
            html: ''
        });

        iconEl.on('click', this.onIconElClick, this);

        me.updateIconCls();
    },

    /**
     * Обработчик события клики по элементу
     *
     * @param event
     */
    onIconElClick: function (event) {
        this.toggleState();

        //this.fireEvent('change', this, this.included);

        event.preventDefault();
        event.stopPropagation();
    },

    /**
     * Обвновляем компонент
     */
    updateIconCls: function () {
        var me     = this,
            iconEl = me.iconEl,
            cls,
            tip;

        if (me.included) {
            cls = me.excludeSortCls;
            tip = Unidata.i18n.t('search>query.removeFromSort');
        } else {
            cls = me.includeSortCls;
            tip = Unidata.i18n.t('search>query.addToSort');
        }

        iconEl.removeCls([me.includeSortCls, me.excludeSortCls]);
        iconEl.addCls(cls);

        me.tip.setHtml(tip);
    },

    toggleState: function (silent) {
        this.included = !this.included;

        this.updateIconCls();

        if (!silent) {
            this.fireEvent('change', this, this.included);
        }
    },

    /**
     * @param {boolean}  flag
     * @param {boolean} [silent]
     */
    setIncluded: function (flag, silent) {
        if (this.included === flag) {
            return;
        }

        this.toggleState(silent);
    },

    isIncluded: function () {
        return this.included;
    }
});
