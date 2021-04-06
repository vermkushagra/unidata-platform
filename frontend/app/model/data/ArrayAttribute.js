Ext.define('Unidata.model.data.ArrayAttribute', {
    extend: 'Unidata.model.data.AbstractAttribute',

    fields: [
        {
            name: 'value',
            type: 'auto',
            defaultValue: '',
            dateFormat: Unidata.Config.getDateTimeFormatServer(),
            convert: function (arr) {
                // NB: if we use function with signature convert(value, record) then record.modified is not working
                var record = arguments[1],
                    delim,
                    serverDelim,
                    dateParsed;

                if (Ext.isArray(arr)) {
                    Ext.Array.each(arr, function (arrItem) {
                        var value = arrItem.value;

                        if (record) {
                            if (record.get('type') === 'Number') {
                                // replace serverDelim to delim if necessary
                                delim       = Unidata.Config.getDecimalDelimiter();
                                serverDelim = Unidata.Config.getServerDecimalDelimiter();

                                if (value) {
                                    value = (delim !== serverDelim) ? +value.toString().replace(delim, serverDelim) : value;
                                }
                            } else if (record.get('type') === 'Date' ||
                                record.get('type') === 'Timestamp' ||
                                record.get('type') === 'Time') {

                                // если value относится к типу Date, то преобразования делать не надо
                                if (!Ext.isDate(value)) {
                                    // пробуем распарсить с временной зоной
                                    dateParsed = Ext.Date.parse(value, 'Y-m-dTH:i:s.uO');

                                    if (dateParsed) {
                                        value = Ext.Date.format(dateParsed, 'Y-m-d\\TH:i:s.u');
                                    }
                                }
                            }

                            arrItem.value = value;
                        }
                    });
                }

                return arr;
            },
            serialize: function (value, record) {
                if (record.get('type') === 'Date' && Ext.isDate(value)) {
                    value = Ext.Date.format(value, Unidata.Config.getDateTimeFormatProxy());
                }

                return value;
            }
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'String'
        },
        {
            name: 'displayValue',
            type: 'string'
        },
        {
            name: 'targetEtalonId',
            type: 'string'
        }
    ]
});
