/**
 * Переопределение конфигурации для локализации
 *
 * @author Ivan Marshalkin
 * @date 2018-05-07
 */

Ext.define('Unidata.locale.ru.unidata.Config', {
    override: 'Unidata.Config'
}, function () {
    Unidata.Config.setDateFormat('d.m.Y');
    Unidata.Config.setTimeFormat('H:i:s');
    Unidata.Config.setDateTimeFormat('d.m.Y H:i:s');

    Unidata.Config.setDecimalSeparator(','); // использовать только "." или ","
    Unidata.Config.setThousandSeparator(' ');
});
