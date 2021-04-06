/**
 *
 * @author Ivan Marshalkin
 * @date 2016-12-19
 */

Ext.define('Ext.overrides.window.Window', {
    override: 'Ext.window.Window',

    // Supports CSS-style margin declarations e.g. 10, "10", "10 10", "10 10 10" and "10 10 10 10"
    // флаг работает совместно с флагом monitorResize
    fullSizeMargin: null, // растягивает окно на весь экран и добавляет отступы

    // флаг работает совместно с флагом monitorResize
    alwaysCentered: false, // центрировать окно при размере окна браузера

    statics: {
        /**
         * Закрывает все окна
         *
         * @param silent - закрыть потихому
         */
        closeAllWindows: function (silent) {
            var silent = silent || false,
                windows = Ext.ComponentQuery.query('window');

            // удаляем все всплывашки
            Ext.Array.each(windows, function (window) {
                if (silent) {
                    window.suspendEvents();
                }

                window.close();

                if (silent) {
                    window.resumeEvents();
                }
            });
        },

        /**
         * Открыто ли хоть одно окно (можно исключить exclude)
         *
         * @param exclude {Ext.window.Window)
         * @returns {boolean}
         */
        hasAnyModalWindowOpen: function (exclude) {
            var windows = Ext.ComponentQuery.query('window'),
                isAnyModalWindowOpen;

            isAnyModalWindowOpen = Ext.Array.some(windows, function (window) {
                return window !== exclude &&
                       window.modal &&
                       !window.isHidden();
            });

            return isAnyModalWindowOpen;
        }
    },

    closeOnOutsideClick: false,  // закрывать модальное окно при клике вне области окна
    outsideClickListener: null,

    ghost: false, // см UN-4104 какие то проблемы с анимацией, отключено

    initComponent: function () {
        this.callParent(arguments);
    },

    outSideclickHandler: function (e, t) {
        var wnd = this,
            el;

        if (wnd && wnd.rendered) {
            el = wnd.getEl();

            if (wnd.isVisible() && el && !(el.dom === t || el.contains(t))) {
                wnd.close();
            }
        }
    },

    resizeFullSizeMarginWindow: function () {
        var size = Ext.getBody().getViewSize(),
            margin = Ext.dom.Element.parseBox(this.fullSizeMargin);

        this.setSize(
            size.width - margin.left - margin.right,
            size.height - margin.top - margin.bottom
        );
        this.center();
    },

    initEvents: function () {
        this.callParent(arguments);

        if (this.monitorResize && !Ext.isEmpty(this.fullSizeMargin)) {
            this.on('show', this.onWindowResize, this, {single: true});
        }

        if (this.modal && this.closeOnOutsideClick === true) {
            this.outsideClickListener = Ext.getBody().on('click', this.outSideclickHandler, this);
        }
    },

    onDestroy: function () {
        if (this.outsideClickListener) {
            Ext.getBody().un('click', this.outSideclickHandler, this);
        }

        this.callParent(arguments);
    },

    // метод переопределен см Ext.window.Window.monitorResize
    onWindowResize: function () {
        var me = this;

        this.callParent(arguments);

        // This is called on a timer. Window may have been destroyed in the interval.
        if (!me.isDestroyed) {
            if (!Ext.isEmpty(this.fullSizeMargin)) {
                me.resizeFullSizeMarginWindow();
            }

            if (me.alwaysCentered) {
                me.center();
            }
        }
    }

});
