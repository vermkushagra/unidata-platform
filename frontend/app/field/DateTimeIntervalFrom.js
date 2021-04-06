/**
 * Field to store datetime for time intervals
 * @author Sergey Shishigin
 */
Ext.define('Unidata.field.DateTimeIntervalFrom', {
    extend: 'Unidata.field.DateTimeInterval',

    alias: 'data.field.datetimeintervalfrom',
    serialize: function (value) {
        var result = null;

        if (Ext.isDate(value)) {
            value.setHours(0);
            value.setMinutes(0);
            value.setSeconds(0);
            value.setMilliseconds(0);
            result = Ext.Date.format(value, this.getDateWriteFormat());
        }

        return result;
    }

});
