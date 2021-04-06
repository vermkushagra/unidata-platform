/**
 * Боримся с багом ExtJS после отрисовки не производится восстановление позиции прокрутки
 *
 * Смотри подробности:
 * https://www.sencha.com/forum/showthread.php?297259-layout-causes-panel-to-reset-scroll-position-when-editing-form
 * https://www.sencha.com/forum/showthread.php?296531
 *
 * В версии 5.1.1.451 и выше поведение должно быть корректным
 *
 * @author Ivan Marshalkin
 * @date 2016-02-24
 */

Ext.define('Ext.overrides.layout.container.VBox', {
    override: 'Ext.layout.container.VBox',

    compatibility: '5.1.0.107',

    beginLayout: function () {
        var scrollable = this.owner.getScrollable();

        if (scrollable) {
            this.lastScrollPosition = scrollable.getPosition();
        }

        this.callParent(arguments);
    },

    completeLayout: function () {
        var scrollable = this.owner.getScrollable();

        this.callParent(arguments);

        if (scrollable) {
            scrollable.scrollTo(this.lastScrollPosition);
        }
    }
});
