Ext.define('Unidata.model.dataquality.Input', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'attributeName',
            type: 'string',
            allowNull: true,
            defaultValue: null
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
        }
    ],

    hasOne: [
        {
            name: 'attributeConstantValue',
            model: 'data.SimpleAttribute',
            deepDirty: true
        }
    ]
});
