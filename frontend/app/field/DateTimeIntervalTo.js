/**
 * Field to store datetime for time intervals
 * @author Sergey Shishigin
 */
Ext.define('Unidata.field.DateTimeIntervalTo', {
    extend: 'Unidata.field.DateTimeInterval',

    alias: 'data.field.datetimeintervalto',
    serialize: function (value) {
        var result = null;

        if (Ext.isDate(value)) {
            value.setHours(23);
            value.setMinutes(59);
            value.setSeconds(59);
            value.setMilliseconds(999);
            result = Ext.Date.format(value, this.getDateWriteFormat());
        }

        return result;
    }
});
