Ext.define('Ext.overrides.Function', {
    requires: [
        'Ext.Function'
    ]
}, function () {

    var hasImmediate = !!(window.setImmediate && window.clearImmediate),
        ExtFunction = Ext.Function;

    // удаляем override т.к. это фейковы класс
    delete Ext.overrides.Function;

    // перенесено из ExtJS 6, нужно для работы Ext.Deferred

    /**
     * @member Ext
     * @method asap
     * Schedules the specified callback function to be executed on the next turn of the
     * event loop. Where available, this method uses the browser's `setImmediate` API. If
     * not available, this method substitutes `setTimeout(0)`. Though not a perfect
     * replacement for `setImmediate` it is sufficient for many use cases.
     *
     * For more details see [MDN](https://developer.mozilla.org/en-US/docs/Web/API/Window/setImmediate).
     *
     * @param {Function} fn Callback function.
     * @param {Object} [scope] The scope for the callback (`this` pointer).
     * @param {Mixed[]} [parameters] Additional parameters to pass to `fn`.
     * @return {Number} A cancelation id for `{@link Ext#asapCancel}`.
     */
    Ext.asap = hasImmediate ?
        function (fn, scope, parameters) {
            if (scope != null || parameters != null) {
                fn = ExtFunction.bind(fn, scope, parameters);
            }

            return setImmediate(function () {
                if (Ext.elevateFunction) {
                    Ext.elevateFunction(fn);
                } else {
                    fn();
                }
            });
        } :
        function (fn, scope, parameters) {
            if (scope != null || parameters != null) {
                fn = ExtFunction.bind(fn, scope, parameters);
            }

            return setTimeout(function () {
                if (Ext.elevateFunction) {
                    Ext.elevateFunction(fn);
                } else {
                    fn();
                }
            }, 0, true);
        };

    /**
     * @member Ext
     * @method asapCancel
     * Cancels a previously scheduled call to `{@link Ext#asap}`.
     *
     *      var asapId = Ext.asap(me.method, me);
     *      ...
     *
     *      if (nevermind) {
     *          Ext.apasCancel(asapId);
     *      }
     *
     * @param {Number} id The id returned by `{@link Ext#asap}`.
     */
    Ext.asapCancel = hasImmediate ?
        function (id) {
            clearImmediate(id);
        } : function (id) {
        clearTimeout(id);
    };

});
