/**
 * Field to store datetime for time intervals
 * @author Sergey Shishigin
 */
Ext.define('Unidata.field.SimpleAttributeValueField', {
    extend: 'Ext.data.field.Field',

    alias: 'data.field.simpleattributevalue',

    type: 'auto',
    defaultValue: '',
    dateFormat: Unidata.Config.getDateTimeFormatServer(),
    typeFieldName: 'type',
    convert: function (value) {
        // NB: if we use function with signature convert(value, record) then record.modified is not working
        var record = arguments[1],
            delim,
            serverDelim,
            dateParsed,
            typeFieldName = this.typeFieldName;

        if (record) {
            if (record.get(typeFieldName) === 'Number') {
                // replace serverDelim to delim if necessary
                delim       = Unidata.Config.getDecimalDelimiter();
                serverDelim = Unidata.Config.getServerDecimalDelimiter();

                if (value) {
                    value = (delim !== serverDelim) ? +value.toString().replace(delim, serverDelim) : value;
                }
            } else if (record.get(typeFieldName) === 'Date' ||
                record.get(typeFieldName) === 'Timestamp' ||
                record.get(typeFieldName) === 'Time') {

                // если value относится к типу Date, то преобразования делать не надо
                if (!Ext.isDate(value)) {
                    // пробуем распарсить с временной зоной
                    dateParsed = Ext.Date.parse(value, 'Y-m-dTH:i:s.uO');

                    if (dateParsed) {
                        value = Ext.Date.format(dateParsed, 'Y-m-d\\TH:i:s.u');
                    }
                }
            }
        }

        return value;
    },
    serialize: function (value, record) {
        if (record.get(typeFieldName) === 'Date' && Ext.isDate(value)) {
            value = Ext.Date.format(value, Unidata.Config.getDateTimeFormatProxy());
        }

        return value;
    }

});
