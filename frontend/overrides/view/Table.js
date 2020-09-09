/**
 *
 * Исправление косяка с гридом (при клике в область не занимаемую row ошибка, вроде бы только с гридом с залочеными колонками)
 * https://www.sencha.com/forum/showthread.php?295950-5.1.0-Release-grid-not-working-when-using-reconfigure-Cannot-read-isNonData
 *
 * Success! Looks like we've fixed this one. According to our records the fix was applied for EXTJS-16196 in 5.1.3.
 *
 * BUG: EXTJS-16196
 *
 * Данный патч применим только к версии 5.1.0.107
 *
 * !!! ПРИ ИСПОЛЬЗОВАНИИ БИБЛИОТЕКИ ВЕРСИЕЙ ВЫШЕ - УДАЛИТЬ !!!
 *
 * @author Ivan Marshalkin
 * @date 2015-11-26
 */

Ext.define('Ext.overrides.view.Table', {
    override: 'Ext.view.Table',

    compatibility: '5.1.0.107',

    // по умолчанию не различает четные / нечетные строки см UN-3751
    stripeRows: true,

    onFocusEnter: function (e) {
        var me = this,
            targetView,
            navigationModel = me.getNavigationModel(),
            lastFocused,
            focusPosition,
            br = me.bufferedRenderer,
            firstRecord,
            focusTarget;

        // The underlying DOM event
        e = e.event;

        // We can only focus if there are rows in the row cache to focus *and* records
        // in the store to back them. Buffered Stores can produce a state where
        // the view is not cleared on the leading end of a reload operation, but the
        // store can be empty.
        if (!me.cellFocused && me.all.getCount() && me.dataSource.getCount()) {
            focusTarget = e.getTarget(null, null, true);

            // If what is being focused an interior element, but is not a cell, allow it to proceed.
            // The position silently restores to what it was when we were focused last.
            if (focusTarget && me.el.contains(focusTarget) && focusTarget !== me.el && !focusTarget.is(me.getCellSelector())) {
                if (navigationModel.lastFocused) {
                    navigationModel.position = navigationModel.lastFocused;
                }
                me.cellFocused = true;
            } else {
                lastFocused = focusPosition = me.getLastFocused();

                // Default to the first cell if the NavigationModel has never focused anything
                if (!focusPosition) {
                    targetView = me.isLockingView ? (me.lockedGrid.isVisible() ? me.lockedView : me.normalView) : me;
                    firstRecord = me.dataSource.getAt(br ? br.getFirstVisibleRowIndex() : 0);

                    // A non-row producing record like a collapsed placeholder.
                    // We cannot focus these yet.

                    // ОРИГИНАЛЬНАЯ СТРОКА
                    // if (!firstRecord.isNonData) {
                    if (firstRecord && !firstRecord.isNonData) {
                        focusPosition = new Ext.grid.CellContext(targetView).setPosition({
                            row: firstRecord,
                            column: 0
                        });
                    }
                }

                // Not a descendant which we allow to carry focus. Blur it.
                if (!focusPosition) {
                    e.stopEvent();
                    e.getTarget().blur();

                    return;
                }
                navigationModel.setPosition(focusPosition, null, e, null, true);

                // We now contain focus is that was successful
                me.cellFocused = !!navigationModel.getPosition();
            }
        }

        if (me.cellFocused) {
            me.el.dom.setAttribute('tabindex', '-1');
        }
    }
});
