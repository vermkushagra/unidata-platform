/**
 * Столбец грида. Код почти полностью скопирован из класса Ext.grid.column.Action версии 5.1.0.107
 * Основное отличие - иконки реализуются не с помощью тега img и иконок png/jpg/etc, а с
 * помощью span и шрифта Font Awesome
 *
 * Пример конфига:
 *
 * {
 *      xtype: 'un.actioncolumn',
 *      items: [
 *          {
 *              faIcon: 'eye', // иконка font awesome
 *              handler: 'onShowErrorMessageClick', // обработчик клика (смотри actioncolumn)
 *              isDisabled: 'stepActionIsDisabled' // вызывается при рендеринге. В отличие от стандартного isDisabled
 *                                                 // столбца actioncolumn, может быть и в контроллере
 *          }
 *      ]
 * }
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-04
 */
Ext.define('Unidata.view.component.grid.column.FontAwesomeAction', {
    extend: 'Ext.grid.column.Action',

    alias: 'widget.un.actioncolumn',

    config: {
        sortable: false,
        menuDisabled: true
    },

    actionIconCls: 'un-faaction-col-icon',

    /**
     * Переопределяем рендеринг столбца, заменяется img на span, добавляются нужные классы
     * и меняется вызов обработчика isDisabled на Ext.callback вместо Function.apply
     *
     * @param v
     * @param cellValues
     * @param record
     * @param rowIdx
     * @param colIdx
     * @param store
     * @param view
     * @returns {*}
     */
    defaultRenderer: function (v, cellValues, record, rowIdx, colIdx, store, view) {
        var me = this,
            prefix = Ext.baseCSSPrefix,
            scope = me.origScope || me,
            items = me.items,
            len = items.length,
            i = 0,
            item, ret, disabled, tooltip;

        // Allow a configured renderer to create initial value (And set the other values in the "metadata" argument!)
        // Assign a new variable here, since if we modify "v" it will also modify the arguments collection, meaning
        // we will pass an incorrect value to getClass/getTip
        ret = Ext.isFunction(me.origRenderer) ? me.origRenderer.apply(scope, arguments) || '' : '';

        cellValues.tdCls += ' ' + Ext.baseCSSPrefix + 'action-col-cell';

        for (; i < len; i++) {
            item = items[i];

            Ext.callback(item.isDisabled, item.scope || me.origScope, [
                view, rowIdx, colIdx, item, record
            ], undefined, me);

            disabled = item.disabled;

            if (!disabled && item.isDisabled) {
                disabled = Ext.callback(item.isDisabled, item.scope || me.origScope, [
                    view,
                    rowIdx,
                    colIdx,
                    item,
                    record
                ], undefined, me);
            }

            tooltip = disabled ? null : (item.tooltip || (item.getTip ? item.getTip.apply(item.scope || scope, arguments) : null));

            // Only process the item action setup once.
            if (!item.hasActionConfiguration) {
                // Apply our documented default to all items
                item.stopSelection = me.stopSelection;
                item.disable = Ext.Function.bind(me.disableAction, me, [i], 0);
                item.enable = Ext.Function.bind(me.enableAction, me, [i], 0);
                item.hasActionConfiguration = true;
            }

            ret += '<span role="button" alt="' + (item.altText || me.altText) + '"' +
                ' class="fa fa-' + item.faIcon + ' ' + me.actionIconCls + ' ' + prefix + 'action-col-' + String(i) + ' ' +
                (disabled ? prefix + 'item-disabled' : ' ') +
                (Ext.isFunction(item.getClass) ?
                    item.getClass.apply(item.scope || scope, arguments)
                    : (item.iconCls || me.iconCls || '')
                ) + '"' +
                (tooltip ? ' data-qtip="' + tooltip + '"' : '') + '></span>';
        }

        return ret;
    },

    /**
     * Переопределяем Ext.grid.column.Action.prototype.processEvent и в конце вызываем не
     * родительский класс, а сразу Ext.grid.column.Column
     *
     * Смысл - меняется вызов обработчика isDisabled на Ext.callback вместо Function.apply
     *
     * В остальном, код идентичен оригинальному
     *
     * @param type
     * @param view
     * @param cell
     * @param recordIndex
     * @param cellIndex
     * @param e
     * @param record
     * @param row
     * @returns {*}
     */
    processEvent: function (type, view, cell, recordIndex, cellIndex, e, record, row) {
        var me = this,
            target = e.getTarget(),
            key = type === 'keydown' && e.getKey(),
            match, item, disabled;

        if (key && !Ext.fly(target).findParent(view.getCellSelector())) {
            target = Ext.fly(cell).down('.' + Ext.baseCSSPrefix + 'action-col-icon', true);
        }

        if (target && (match = target.className.match(me.actionIdRe))) {
            item = me.items[parseInt(match[1], 10)];

            disabled = item.disabled;

            if (!disabled && item.isDisabled) {
                disabled = Ext.callback(item.isDisabled, item.scope || me.origScope, [
                    view,
                    recordIndex,
                    cellIndex,
                    item,
                    record
                ], undefined, me);
            }

            if (item && !disabled) {

                if (type === 'mousedown') {
                    if (item.stopSelection) {
                        e.preventDefault();
                    }

                    return false;
                }

                if (type === 'click' || (key === e.ENTER || key === e.SPACE)) {
                    Ext.callback(item.handler || me.handler, item.scope || me.origScope, [
                        view,
                        recordIndex,
                        cellIndex,
                        item,
                        e,
                        record,
                        row
                    ], undefined, me);

                    if (item.stopSelection !== false) {
                        return false;
                    }
                }
            }
        }

        return Ext.grid.column.Column.prototype.processEvent.apply(me, arguments);

    }

});
