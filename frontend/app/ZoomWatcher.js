/**
 * Класс содержит реализацию уведомления пользователя при изменении масштаба
 *
 * @author Ivan Marshalkin
 * @date 2016-01-18
 *
 * Известные проблемы: определение масштаба работает не корректно, если открыта консоль разработчика
 * и располагается справа или слева
 */

Ext.define('Unidata.ZoomWatcher', {
    singleton: true,

    requires: [
        'Unidata.util.ZoomDetector'
    ],

    // флаг указывает, что уже было начато отслеживание
    initialized: false,
    // флаг указывает, что после изменения масштаба пользователь уже был уведомлен
    confirmedZoom: false,

    constructor: function () {
    },

    /**
     * Функция инициализации наблюдения за изменением масштаба
     */
    startWatch: function () {
        var detector = Unidata.util.ZoomDetector,
            zoom     = detector.zoom();

        if (this.initialized) {
            return;
        }

        // Срабатывает при изменении размера окна, так же при изменении масштаба
        Ext.on('resize', this.onResize, this);

        if (this.isZoomed(zoom)) {
            this.confirmUser();
        }

        this.initialized = true;
    },

    /**
     * Обработчик изменения размера окна
     */
    onResize: function () {
        var detector = Unidata.util.ZoomDetector,
            zoom     = detector.zoom();

        if (!this.isZoomed(zoom)) {
            this.confirmedZoom = false;
        } else if (!this.confirmedZoom) {
            this.confirmUser();
        }
    },

    /**
     * Возвращает true если масштаб изменен.
     *
     * @param zoom
     * @returns {boolean}
     */
    isZoomed: function (zoom) {
        var delta = 0.05; // допустимое отклонение от 1

        return Math.abs(zoom - 1) > delta;
    },

    /**
     * Уведомление пользователя
     */
    confirmUser: function () {
        this.confirmedZoom = true;
        Unidata.util.UserDialog.showWarning(Unidata.i18n.t('application>realScale'));
    }
});
