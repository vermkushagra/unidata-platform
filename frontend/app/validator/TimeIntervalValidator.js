/**
 * Validate time intervals (validFrom < validTo)
 *
 *
 * В валидатор не передается запись BUG: EXTJS-18997
 * https://www.sencha.com/forum/showthread.php?304946-Custom-data-field-validator-not-passing-in-record-just-the-value
 * Ошибка исправлена в Ext JS 5.1.2 release October 9, 2015
 * По всей видимости исправлено и в 6.0.2
 *
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.validator.TimeIntervalValidator', {
    extend: 'Ext.data.validator.Validator',
    alias: 'data.validator.timeinterval',

    validate: function () {
        var result = false,
            validFrom,
            validTo;

        if (this.record === undefined) {
            result = true;
        } else {
            validFrom = this.record.get('validFrom');
            validTo = this.record.get('validTo');

            if (validFrom === null || validTo === null) {
                result = true;
            } else {
                result = validTo > validFrom;
            }
        }

        return result ? true : this.message;
    }
});

