Ext.define('Unidata.model.dataquality.Output', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'attributeName',
            type: 'string'
        },
        {
            name: 'canonicalPath',
            type: 'string',
            calculate: function (data) {
                var attributeName = data.attributeName,
                    UPath;

                if (!attributeName) {
                    return null;
                }

                UPath = Ext.create('Unidata.util.upath.UPath');
                UPath.fromUPath(attributeName);

                return UPath.toCanonicalPath();
            },
            persist: false,
            depends: ['attributeName']
        },
        {
            name: 'functionPort',
            type: 'string'
        },
        {
            name: 'portApplicationMode',
            type: 'enum',
            enumList: Unidata.util.DataQuality.portApplicationModeEnumList,
            defaultValue: Unidata.util.DataQuality.portApplicationModeEnumList.MODE_ALL
        }
    ]
});
