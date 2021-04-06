/**
 * Field to store datetime for time intervals
 * @author Sergey Shishigin
 */
Ext.define('Unidata.field.DateTimeInterval', {
    extend: 'Ext.data.field.Date',

    alias: 'data.field.datetimeinterval',
    dateReadFormat: 'c',
    dateWriteFormat: Unidata.Config.getDateTimeFormatProxy(),
    defaultValue: null
});
