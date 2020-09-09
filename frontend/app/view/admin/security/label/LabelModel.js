Ext.define('Unidata.view.admin.security.label.LabelModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.security.label',

    data: {
        currentSecurityLabel: null,
        recordEntity: null,
        readOnly: false,
        nameReadOnly: false
    },

    stores: {
        securityLabels: {
            autoLoad: true,
            model: 'Unidata.model.user.SecurityLabelRole',
            sorters: [
                {
                    property: 'displayName',
                    direction: 'ASC'
                }
            ],
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/security/role/get-all-security-labels',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        },
        listEntitiesLookup: {
            model: 'Unidata.model.search.SearchHit',
            pageSize: 10000,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                limitParam: 'count',
                url: Unidata.Config.getMainUrl() + 'internal/search/meta?fields=type',
                reader: {
                    type: 'json',
                    rootProperty: 'hits',
                    totalProperty: 'total_count'
                }
            }
        }
    },

    formulas: {
        attributeStore: {
            bind: {
                bindTo: '{recordEntity}',
                deep: true
            },
            get: function (recordEntity) {
                var store,
                    data = [],
                    allowLabelForTypes = ['String', 'Integer'];

                // проверяем, можно ли для аттрибута присваивать метки безопасности
                function allowAttribute (attr) {
                    if (attr.get('typeCategory') === 'simpleDataType') {
                        return (Ext.Array.indexOf(allowLabelForTypes, attr.get('simpleDataType')) !== -1);
                    } else if (attr.get('typeCategory') === 'lookupEntityType') {
                        return true;
                    }

                    return false;
                }

                if (Ext.getClassName(recordEntity) === 'Unidata.model.entity.LookupEntity') {
                    data.push(recordEntity.getCodeAttribute().getData());

                    recordEntity.aliasCodeAttributes().each(function (item) {
                        if (allowAttribute(item)) {
                            data.push(item.getData());
                        }
                    });

                }

                if (recordEntity) {
                    recordEntity.simpleAttributes().each(function (item) {
                        if (allowAttribute(item)) {
                            data.push(item.getData());
                        }
                    });
                }

                store = Ext.create('Ext.data.Store', {
                    sorters: [
                        {
                            property: 'displayName',
                            direction: 'ASC'
                        }
                    ],
                    fields: [
                        {name: 'name', type: 'string'},
                        {name: 'displayName', type: 'string'}
                    ],
                    data: data
                });

                return store;
            }
        }
    }
});
