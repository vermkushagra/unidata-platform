/**
 * Переопределение конфигурации для локализации
 *
 * @author Ivan Marshalkin
 * @date 2018-05-07
 */

Ext.define('Unidata.locale.en.unidata.Config', {
    override: 'Unidata.Config'
}, function () {
    Unidata.Config.setDateFormat('m/d/Y');
    Unidata.Config.setTimeFormat('H:i:s');
    Unidata.Config.setDateTimeFormat('m/d/Y H:i:s');

    Unidata.Config.setDecimalSeparator('.'); // использовать только "." или ","
    Unidata.Config.setThousandSeparator(',');
});
