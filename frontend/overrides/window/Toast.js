/**
 *
 * @author Ivan Marshalkin
 * @date 2016-12-19
 */

Ext.define('Ext.overrides.window.Toast', {
    override: 'Ext.window.Toast',

    statics: {
        /**
         * Закрывает все всплывашки
         *
         * @param silent - закрыть потихому
         */
        closeAllToasts: function (silent) {
            var silent = silent || false,
                toasts = Ext.ComponentQuery.query('toast');

            // удаляем все всплывашки
            Ext.Array.each(toasts, function (toast) {
                if (silent) {
                    toast.suspendEvents();
                }

                toast.close();

                if (silent) {
                    toast.resumeEvents();
                }
            });
        }
    }
});
