/**
 * Расширяем функции Ext.String
 *
 * Так как класс Ext.String определяется без использования Ext.define => сделать переопределение через override
 * невозможно.
 *
 * @author Ivan Marshalkin
 * @date 2016-01-18
 */

Ext.define('Ext.overrides.String', {
    requires: [
        'Ext.String'
    ]
}, function () {
    // удаляем override т.к. это фейковы класс
    delete Ext.overrides.String;

    Ext.String.nullSafeTrim = function (string) {
        if (!string) {
            return string;
        }

        return Ext.String.trim(string);
    };

    /**
     * Функция кодирует строку string несколько раз подряд (count раз)
     *
     * @param string
     * @param count
     * @returns {*}
     */
    Ext.String.htmlEncodeMulti = function (string, count) {
        var tempString = string,
            i;

        count = (count && count > 1) ? parseInt(count, 10) : 1;

        for (i = 1; i <= count; i++) {
            tempString = Ext.String.htmlEncode(tempString);
        }

        return tempString;
    };
});
