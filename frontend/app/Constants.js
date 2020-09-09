Ext.define('Unidata.Constants', {
    singleton: true,
    config: {
        typeCategories: ['simpleDataType', 'enumDataType', 'lookupEntityType', 'linkDataType'],
        simpleDataTypes: [
            {
                name: 'String',
                displayName: Unidata.i18n.t('glossary:string')
            },
            {
                name: 'Integer',
                displayName: Unidata.i18n.t('glossary:integer')
            },
            {
                name: 'Number',
                displayName: Unidata.i18n.t('glossary:number')
            },
            {
                name: 'Boolean',
                displayName: Unidata.i18n.t('glossary:boolean')
            },
            {
                name: 'Date',
                displayName: Unidata.i18n.t('glossary:date')
            },
            {
                name: 'Timestamp',
                displayName: Unidata.i18n.t('glossary:dateTime')
            },
            {
                name: 'Time',
                displayName: Unidata.i18n.t('glossary:time')
            },
            {
                name: 'Blob',
                displayName: Unidata.i18n.t('glossary:file')
            },
            {
                name: 'Clob',
                displayName: Unidata.i18n.t('glossary:textFile')
            }
        ],
        arrayDataTypes: [
            {
                name: 'String',
                displayName: Unidata.i18n.t('glossary:string')
            },
            {
                name: 'Integer',
                displayName: Unidata.i18n.t('glossary:integer')
            },
            {
                name: 'Number',
                displayName: Unidata.i18n.t('glossary:number')
            },
            {
                name: 'Date',
                displayName: Unidata.i18n.t('glossary:date')
            },
            {
                name: 'Timestamp',
                displayName: Unidata.i18n.t('glossary:dateTime')
            },
            {
                name: 'Time',
                displayName: Unidata.i18n.t('glossary:time')
            }
        ],
        relTypes: [
            {key: 'References', value: Unidata.i18n.t('glossary:referenceRelation'), alias: 'reference'},
            {key: 'Contains', value: Unidata.i18n.t('glossary:containsRelation')},
            {key: 'ManyToMany', value: Unidata.i18n.t('glossary:manyToManyRelation'), alias: 'm2m'} // тип связи закоментирован т.к. по факту он у нас не реализован
        ],
        anyDataTypeName: 'Any',
        operationTypes: [
            {
                name: 'DIRECT',
                displayName: Unidata.i18n.t('search>query.operationTypeDirect')
            },
            {
                name: 'CASCADED',
                displayName: Unidata.i18n.t('search>query.operationTypeCascaded')
            }
        ]
    },
    constructor: function (config) {
        this.initConfig(config);
    }
});
