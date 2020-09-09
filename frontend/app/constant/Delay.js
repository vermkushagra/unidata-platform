/**
 * Константы интервалов времени в миллисекундах
 *
 * @date 2016-05-12
 * @author Mаrshalkin Ivan
 */

Ext.define('Unidata.constant.Delay', {
    singleton: true,

    MILLI: 1,               // миллисекунда

    SECOND: 1000,           // секунда
    SECOND_5: null,
    SECOND_10: null,
    SECOND_15: null,
    SECOND_20: null,
    SECOND_25: null,
    SECOND_30: null,
    SECOND_35: null,
    SECOND_40: null,
    SECOND_45: null,
    SECOND_50: null,
    SECOND_55: null,

    QR_MINUTE: null,        // четверть минуты (15 сек)
    HALF_MINUTE: null,      // половина минуты (30 сек)
    MINUTE: null,           // минута

    QR_HOUR: null,          // четверть часа (15 мин)
    HALF_HOUR: null,        // половина часа (30 мин)
    HOUR: null,             // час

    DAY: null               // день

}, function () {
    var c = Unidata.constant.Delay;

    c.SECOND_5    = 5  * c.SECOND;
    c.SECOND_10   = 10 * c.SECOND;
    c.SECOND_15   = 15 * c.SECOND;
    c.SECOND_20   = 20 * c.SECOND;
    c.SECOND_25   = 25 * c.SECOND;
    c.SECOND_30   = 30 * c.SECOND;
    c.SECOND_35   = 35 * c.SECOND;
    c.SECOND_40   = 40 * c.SECOND;
    c.SECOND_45   = 45 * c.SECOND;
    c.SECOND_50   = 50 * c.SECOND;
    c.SECOND_55   = 55 * c.SECOND;

    c.QR_MINUTE   = 15 * c.SECOND;
    c.HALF_MINUTE = 30 * c.SECOND;
    c.MINUTE      = 60 * c.SECOND;

    c.QR_HOUR     = 15 * c.MINUTE;
    c.HALF_HOUR   = 30 * c.MINUTE;
    c.HOUR        = 60 * c.MINUTE;

    c.DAY         = 24 * c.HOUR;
});
