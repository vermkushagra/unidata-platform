Ext.define('Unidata.model.data.SimpleAttribute', {
    extend: 'Unidata.model.data.AbstractAttribute',

    fields: [
        {
            name: 'value',
            type: 'auto',
            defaultValue: '',
            dateFormat: Unidata.Config.getDateTimeFormatServer(),
            convert: function (value) {
                // NB: if we use function with signature convert(value, record) then record.modified is not working
                var record = arguments[1],
                    delim,
                    serverDelim,
                    dateParsed;

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
                }

                return value;
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
        },
        {
            name: 'valueId',
            allowNull: true,
            type: 'string'
        },
        {
            name: 'unitId',
            allowNull: true,
            type: 'string'
        },
        {
            name: 'winner',
            allowNull: true,
            type: 'boolean'
        }
    ]
});
