/**
 * Обрабатывает конфигурирование таймаута AJAX запросов
 * Варианты использования:
 *      1) задать параметры в оверайде
 *          Ext.define('CUX.override.uiuserexit.overridable.config.ConnectionTimeout', {
 *              override: 'Unidata.uiuserexit.overridable.config.ConnectionTimeout'
 *          }, function () {
 *              Unidata.uiuserexit.overridable.config.ConnectionTimeout.setTimeout(5000);
 *          });
 *      2) Задать значение customer.json в параметре AJAX_REQUEST_TIMEOUT
 *
 * @author Ivan Marshalkin
 * @date 2017-11-24
 */

Ext.define('Unidata.uiuserexit.overridable.config.ConnectionTimeout', {
    singleton: true,

    config: {
        timeout: null // в милисекундах
    },

    updateTimeout: function (timeout) {
        if (Ext.isNumber(timeout)) {
            Ext.Ajax.setTimeout(timeout);
        }
    }
}, function () {
    var cfg = Unidata.Config.getCustomerCfg();

    if (cfg && cfg.AJAX_REQUEST_TIMEOUT) {
        this.setTimeout(cfg.AJAX_REQUEST_TIMEOUT);
    }
});
